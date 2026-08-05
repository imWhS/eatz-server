package imwhs.eatz_server.service.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class AuthRateLimiterService {

    // IP를 Key로, Bucket을 Value로 저장하는 메모리 캐시를 정의합니다. 이 캐시는 하루가 지나면 자동 clear 됩니다.

    // 메모리 누수를 방지하기 위해 Caffeine 로컬 캐시를 사용해
    // 특정 IP가 하루 동안 접근하지 않으면 메모리에서 clear 되도록 설정합니다.
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build();

    // IP 주소에 해당하는 bucket을 Caffeine 로컬 캐시에서 가져옵니다.
    public Bucket resolveBucket(String ip) {
        return cache.get(ip, this::newBucket);
    }

    // 처음으로 요청한 IP 주소에 해당하는 bucket을 생성합니다. Rate limit 규칙을 적용합니다.
    private Bucket newBucket(String ip) {
        // bucket refill 속도: 하루 최대 5번까지 허용합니다.
        Refill refill = Refill.intervally(5, Duration.ofDays(1));
        // bucket 최대 크기를 5로 설정합니다.
        Bandwidth limit = Bandwidth.classic(5, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
