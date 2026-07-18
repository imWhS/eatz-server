package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeOutboundCountService {

    private final EatzUserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RedisService redisService;

    /**
     * Redis에 저장된 레시피 별 임시 outbound 수 증분량을 Recipe 엔티티의 outboundCount에도 반영한 후 Redis에서 삭제합니다.
     */
    @Transactional(rollbackFor = Exception.class)
    public void flushOutboundCountIncrements() {
        log.info("레시피 별 outbound 수 증분량 flush를 시작할게요.");

        // 위 방식은 getKeysByPattern가 redis에 존재하는 '모든 key'를 대상으로 패턴 매칭을 수행하기 때문에 사용하지 않습니다.
        // 실제 증분량을 반영해야 할 레시피 ID 집합을 조회해서, 해당 집합의 요소로서 포함된 레시피만 순회합니다.
        String incrementFlushTargetRecipeIdsKey = generateRedisIncrementFlushTargetRecipeIdsKey();
        Set<String> recipeIds = redisService.getSetMembers(incrementFlushTargetRecipeIdsKey);

        for (String recipeId : recipeIds) {
            Long id = Long.parseLong(recipeId);
            String incrementKey = generateRedisIncrementKey(id);
            int viewersIncrement = redisService.getAsInt(incrementKey);
            log.info("레시피의 outbound 수를 flush 할게요. | 레시피 ID: {} | 증분량: {}", recipeId, viewersIncrement);
            recipeRepository.addOutboundCount(id, viewersIncrement);
            redisService.delete(incrementKey);
            redisService.removeFromSet(incrementFlushTargetRecipeIdsKey, recipeId);
        }
    }

    public void markAsOutboundTodayByAuthenticated(Long recipeId, Long userId, String timeZone) {
        userRepository.validateExists(userId);
        String viewerId = "authenticated:" + userId;
        markAsOutboundToday(recipeId, viewerId, timeZone);
    }

    public void markAsOutboundTodayByGuest(Long recipeId, String deviceId, String timeZone) {
        String viewerId = "guest:" + deviceId;
        markAsOutboundToday(recipeId, viewerId, timeZone);
    }

    private void markAsOutboundToday(Long recipeId, String viewerId, String timeZone) {
        recipeRepository.validateExists(recipeId);
        String viewersKey = generateRedisViewersKey(recipeId);

        // Redis의 '레시피의 outbound를 요청한 사용자 집합'에 사용자의 ID를 추가합니다.
        long viewers = redisService.addElementToSet(viewersKey, viewerId);

        // 다음 날 자정에 해당 레시피에 대한 outbound 관련 기록이 초기화되도록 TTL을 설정합니다.
        ZonedDateTime now = getNow(timeZone);
        ZonedDateTime startOfNextDay = getStartOfNextDay(timeZone);
        redisService.setExpire(viewersKey, Duration.between(now, startOfNextDay));

        // 레시피의 상세 정보를 조회한 사용자 집합에 존재하지 않던 새로운 값(사용자 ID)의 추가 여부
        boolean isMarked = (viewers == 1L);
        if (isMarked) {
            // 요청한 날짜 기준으로, 사용자가 레시피의 상세 정보를 중복으로 요청하지 않은 경우 임시 조회 수 증분량을 증가시킵니다.
            setOutboundCountIncrement(recipeId);
            log.info("사용자의 오늘 날짜 기준 레시피 최초 outbound 요청 기록을 저장할게요. | " +
                    "요청한 사용자 ID: {} | " +
                    "레시피 ID: {}", viewerId, recipeId);
        }
    }

    private void setOutboundCountIncrement(Long recipeId) {
        String incrementKey = generateRedisIncrementKey(recipeId);
        redisService.increase(incrementKey);

        String incrementFlushTargetRecipeIdsKey = generateRedisIncrementFlushTargetRecipeIdsKey();
        redisService.addElementToSet(incrementFlushTargetRecipeIdsKey, String.valueOf(recipeId));
    }

    private ZonedDateTime getNow(String timeZone) {
        return ZonedDateTime.now(ZoneId.of(timeZone));
    }

    private ZonedDateTime getStartOfNextDay(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        return now.toLocalDate().plusDays(1).atStartOfDay(zoneId);
    }

    /**
     * 특정 레시피의 상세 정보를 조회한 viewer의 ID를 저장하기 위한 집합의 Redis key를 생성합니다.
     * 해당 key에는 집합 자료 구조가 매핑되며, 집합에는 viewer의 ID가 저장되어서, '동일한 사용자의 레시피 중복 조회를 필터링'하기 위해 사용합니다.
     * @param recipeId 레시피의 ID
     * @return 레시피의 상세 정보를 조회한 viewer의 ID를 저장하기 위해 사용할 Redis key
     */
    @NonNull
    private static String generateRedisViewersKey(Long recipeId) {
        return "recipe:outbound_count:" + recipeId + ":viewers";
    }

    /**
     * 특정 레시피의 상세 정보 조회 수 증분량을 저장하기 위해 사용할 Redis key를 생성합니다.
     * @param recipeId 레시피의 ID
     * @return 레시피의 상세 정보 조회 수 증분량을 저장하기 위해 사용할 Redis key
     */
    @NonNull
    private static String generateRedisIncrementKey(Long recipeId) {
        return "recipe:outbound_count:" + recipeId + ":increment";
    }

    /**
     * 조회 수가 증가해서, 임시 조회 수 증분량을 반영(flush)해야 할 레시피의 ID를 저장하기 위한 집합의 Redis key를 생성합니다.
     * @return 임시 조회 수 증분량을 반영(flush)해야 할 레시피의 ID를 저장하기 위한 집합의 Redis key
     */
    @NonNull
    private static String generateRedisIncrementFlushTargetRecipeIdsKey() {
        return "recipe:outbound_count:increment_flush_target_recipeIds";
    }

}
