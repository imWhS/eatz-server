package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeViewCountServiceOld {

    private static final String COOKIE_NAME = "ViewedRecipeId";
    private static final int COOKIE_EXPIRE_TIME = 1800; // 30분
    private static final int SYNC_VIEW_COUNT_FIXED_RATE = 60000; // 1분
    private static final String REDIS_KEY_PREFIX_RECIPE_VIEW_COUNT_OF = "recipe-view-count-of:";

    private final RecipeRepository recipeRepository;
    private final RedisService redisService;

    @Transactional(rollbackFor = Exception.class)
    public void increaseViewCount(HttpServletRequest request, HttpServletResponse response, Long recipeId) {
        if (recipeId == null || recipeId <= 0) {
            log.warn("올바르지 않은 레시피 ID이에요: {}", recipeId);
            return;
        }

        Optional<Cookie> existingCookie = getCookie(request, COOKIE_NAME);

        if (existingCookie.isEmpty()) {
            log.info("조회한 레시피 ID를 저장하기 위한 쿠키를 생성합니다. | 레시피 id: {}", recipeId);
            updateCookie(response, recipeId, String.valueOf(recipeId));
        } else {
            log.info("기존 쿠키 값에 레시피 ID를 추가합니다. | 레시피 id: {}\", recipeId");
            String value = existingCookie.get().getValue();
            String[] viewedRecipes = value.split(",");

            // 이미 쿠키에 해당 레시피 조회 여부가 기록된 경우
            if (Arrays.asList(viewedRecipes).contains(String.valueOf(recipeId))) {
                log.info("이미 쿠키에 해당 레시피 id 조회 여부가 기록되어 있기 때문에, 조회 수에 반영하지 않습니다. | 레시피 id: {}\", recipeId");
                return;
            }

            // 쿠키에서 해당 레시피 조회 여부가 기록되어 있지 않은 경우
            value += "," + recipeId;

            updateCookie(response, recipeId, value);
        }

        log.info("Redis에 저장된 임시 조회 수를 증가 처리합니다. | key: {}", REDIS_KEY_PREFIX_RECIPE_VIEW_COUNT_OF + recipeId);
        redisService.increase(REDIS_KEY_PREFIX_RECIPE_VIEW_COUNT_OF + recipeId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(fixedRate = SYNC_VIEW_COUNT_FIXED_RATE)
    public void syncViewCount() {
        Set<String> keys = redisService.getKeysByPattern(REDIS_KEY_PREFIX_RECIPE_VIEW_COUNT_OF + "*");
        if (keys.isEmpty()) return;

        for (String key : keys) {
            String recipeIdStr = key.replace(REDIS_KEY_PREFIX_RECIPE_VIEW_COUNT_OF, "");

            if (!recipeIdStr.matches("\\d+")) {
                log.warn("올바르지 않은 key를 감지했어요: {}", key);
                continue;
            }

            Long recipeId = Long.parseLong(recipeIdStr);

            Integer viewCount = Integer.parseInt(redisService.get(key));
            recipeRepository.findById(recipeId).ifPresent(recipe -> {
                recipe.increaseViewCount(viewCount);
                log.info("레시피 추가 조회 수를 데이터베이스에 반영합니다. | 레시피 id: {} | 추가 조회 수: {} | 반영 후 조회 수: {}", recipeId, viewCount, recipe.getViewCount());
            });

            redisService.delete(key);
        }
    }

    private static void updateCookie(HttpServletResponse response, Long recipeId, String value) {
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setMaxAge(COOKIE_EXPIRE_TIME);
        cookie.setPath("/api/v0/recipes/" + recipeId);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
    }

}
