package imwhs.eatz_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
     * 특정 key로 데이터를 저장합니다.
     * <p>key에 저장하려는 데이터의 종류를 나타낼 수 있는 접두어를 추가해야 합니다. ex. email:example@example.com</p>
     * @param key
     * @param value
     */
    @Transactional
    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 특정 key로 데이터를 저장함과 동시에 만료 시간을 설정합니다.
     * @param key
     * @param value
     * @param timeout
     */
    @Transactional
    public void setValue(String key, String value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 특정 key로 데이터를 조회합니다.
     * @param key
     * @return
     */
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 특정 key로 데이터를 삭제합니다.
     * @param key
     */
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 특정 key에 대한 데이터에 만료 시간을 설정합니다.
     * @param key
     * @param timeout
     */
    public void expireValue(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Redis에 특정 key가 데이터와 함께 존재하는지 확인합니다.
     * @param key
     * @return
     */
    public boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

}
