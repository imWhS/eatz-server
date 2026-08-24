package imwhs.eatz_server.service;

import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * RedisService 클래스입니다.
 * <p>
 *      Redis를 기반으로 데이터를 관리하는 클래스입니다.
 * </p>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    /**
     * Key와 값이 한 쌍으로 구성된 데이터를 저장합니다.
     * @param key 데이터의 key. Redis에서 데이터의 식별자로서 사용됩니다.
     * @param value Key에 매핑해 데이터로 저장할 값
     */
    @Transactional(rollbackFor = Exception.class)
    public void setForValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Key와 값이 한 쌍으로 구성된 데이터를 저장하고, 만료 시간(TTL)을 설정합니다.
     * @param key 데이터의 key. Redis에서 데이터의 식별자로서 사용됩니다.
     * @param value Key에 매핑해 데이터로 저장할 값
     * @param timeout 데이터의 만료 시간(TTL)
     */
    @Transactional(rollbackFor = Exception.class)
    public void setForValue(String key, String value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * Key와 값이 한 쌍으로 구성된 집합에 값을 추가하고, 만료 시간(TTL)을 설정합니다.
     * @param key 데이터의 key. Redis에서 데이터의 식별자로서 사용됩니다.
     * @param value 집합에 추가할 값
     * @param timeout 데이터의 만료 시간(TTL)
     * @return 집합에 존재하지 않던 새로운 값을 추가했으면 1, 이미 집합에 동일한 값이 존재하면 0
     */
    @Transactional(rollbackFor = Exception.class)
    public long addElementToSet(String key, String value, Duration timeout) {
        Long addedValueCount = redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, timeout);
        if (addedValueCount == null) {
            throw new EatzInvalidRequestArgumentException("해당 key(" + key + ")에 매핑된 값이 없어요.");
        }
        return addedValueCount;
    }

    /**
     * Key와 값이 한 쌍으로 구성된 집합에 값을 추가합니다.
     * @param key 데이터의 key. Redis에서 데이터의 식별자로서 사용됩니다.
     * @param value 집합에 추가할 값
     * @return 집합에 존재하지 않던 새로운 값을 추가했으면 1, 이미 집합에 동일한 값이 존재하면 0
     */
    @Transactional(rollbackFor = Exception.class)
    public long addElementToSet(String key, String value) {
        Long addedValueCount = redisTemplate.opsForSet().add(key, value);
        if (addedValueCount == null) {
            throw new EatzInvalidRequestArgumentException("해당 key(" + key + ")에 매핑된 값이 없어요.");
        }
        return addedValueCount;
    }

    /**
     * 특정 key에 매핑된 정수 값을 1 증가시킵니다.
     * <ul>
     *     <li> 해당 key의 값은 문자열 타입의 정수여야 합니다. </li>
     *     <li> Key가 존재하지 않을 경우, 해당 key에 대한 값으로 0을 매핑, 저장한 후 1 증가시킵니다. </li>
     * </ul>
     * @param key 데이터의 key
     * @return 1 증가된 값
     */
    @Transactional(rollbackFor = Exception.class)
    public long increase(String key) {
        Long value = redisTemplate.opsForValue().increment(key, 1);
        if (Objects.isNull(value)) {
            throw new IllegalStateException("Redis에서 해당 key(" + key + ")에 대한 데이터를 정상적으로 조회하지 못했어요.");
        }
        return value;
    }

    /**
     * 특정 key에 매핑된 정수 값을 1 증가시키고, 만료 시간(TTL)을 설정합니다.
     * <ul>
     *     <li> 해당 key의 값은 문자열 타입의 정수여야 합니다. </li>
     *     <li> Key가 존재하지 않을 경우, 해당 key에 대한 값으로 0을 매핑, 저장한 후 1 증가시킵니다. </li>
     * </ul>
     * @param key 데이터의 key
     * @param timeout 데이터의 만료 시간(TTL)
     * @return 1 증가된 값
     */
    @Transactional(rollbackFor = Exception.class)
    public long increase(String key, Duration timeout) {
        long value = increase(key);
        setExpire(key, timeout);
        return value;
    }

    /**
     * key 및 해당 key와 매핑된 값이 저장된 데이터를 Redis에서 삭제합니다.
     * @param key 삭제할 데이터의 Key
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 패턴과 일치하는 모든 key 및 해당 key와 매핑된 값이 저장된 데이터를 찾아 Redis에서 일괄 삭제합니다.
     * @param pattern 패턴
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByPattern(String pattern) {
        Set<String> keys = getKeysByPattern(pattern);
        if (keys != null && !keys.isEmpty()) { redisTemplate.delete(keys); }
    }

    /**
     * 특정 key에 만료 시간(TTL)을 설정하거나 업데이트합니다.
     * @param key 데이터의 Key
     * @param duration 만료 시간
     */
    @Transactional(rollbackFor = Exception.class)
    public void setExpire(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    /**
     * 특정 key에 대한 데이터의 존재 여부를 확인합니다.
     * @param key 데이터의 key
     * @return key에 대한 데이터의 존재 여부
     */
    public boolean exists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        if (Objects.isNull(exists)) {
            throw new IllegalStateException("Redis에 해당 key(" + key + ")에 대한 데이터가 없어요.");
        }
        return exists;
    }

    /**
     * 주어진 패턴과 일치하는 모든 Key의 집합을 조회합니다.
     * <p> O(N)으로 동작하므로, "*"와 같이 넓은 범위의 패턴 사용은 권장하지 않습니다.</p>
     * @param pattern 조회할 key의 패턴 (Ex. "email:*")
     * @return 패턴과 일치하는 key들의 집합. 일치하는 결과가 없으면 빈 집합을 반환합니다.
     */
    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * Key에 매핑된 문자열 타입의 값을 조회합니다.
     * @param key 조회할 데이터의 key
     * @return Key에 매핑된 문자열 타입의 정수 값. Key가 존재하지 않으면 null을 반환합니다.
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Key에 매핑된 문자열 타입의 정수 값을 정수 타입으로 조회합니다.
     * @param key 조회할 데이터의 key
     * @return Key에 매핑된 문자열 타입의 정수 값
     * @throws RuntimeException Key에 매핑된 값이 존재하지 않거나, 정수 타입으로 변환하지 못한 경우
     */
    public int getAsInt(String key) {
        String value = get(key);
        if (value == null) { throw new EatzInvalidRequestArgumentException("해당 key(" + key + ")에 매핑된 값이 없어요."); }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "해당 key(\" + key + \")에 매핑된 값(\" + value + \")이 문자열 타입의 정수가 아니에요.");
        }
    }

    public long getSetSize(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        return size == null ? 0L : size;
    }

    public Set<String> getSetMembers(String key) {
        Set<String> members = redisTemplate.opsForSet().members(key);
        return members == null ? Collections.emptySet() : members;
    }

    public void removeFromSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    /**
     * 특정 key의 만료 시간(TTL)을 '초' 시간 단위로 조회합니다.
     * @param key 데이터의 key
     * @return key의 만료 시간(TTL)
     */
    public long getTtl(String key) {
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (Objects.isNull(expire)) {
            throw new IllegalStateException("Redis에서 해당 key(" + key + ")에 대한 데이터의 만료 시간을 확인할 수 없어요.");
        }
        return expire;
    }

}
