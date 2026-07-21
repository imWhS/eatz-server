package imwhs.eatz_server.storage;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3FileStorage implements FileStorage {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Override
    public String save(String logicalPath, MultipartFile file) {
        if (logicalPath == null || logicalPath.isBlank() || file == null) { throw new IllegalArgumentException(); }

        String trimmedLogicalPath = removeLeadingSlash(logicalPath);
        Path path = Paths.get(trimmedLogicalPath);
        String accessUrl = createAccessUri(trimmedLogicalPath);

        try (InputStream inputStream = file.getInputStream()) {
            s3Template.upload(bucketName, trimmedLogicalPath, inputStream);
            log.info("파일을 S3에 업로드했어요. | {}", accessUrl);
            return accessUrl;
        } catch (IOException e) {
            log.error("{} 파일을 S3의 {}에 저장할 수 없어요: {}",
                    path.getFileName(), accessUrl, e.getMessage());
            throw new RuntimeException("파일을 S3에 업로드하지 못했어요. " + e.getLocalizedMessage());
        }
    }

    @Override
    public void deleteByUri(String accessUri) {
        if (accessUri == null || accessUri.isBlank()) { return; }

        String uri = removeLeadingSlash(accessUri);
        String s3Key = extractS3Key(uri);

        try {
            s3Template.deleteObject(bucketName, s3Key);
            log.info("S3에서 {} 파일을 삭제했어요.", s3Key);
        } catch (Exception e) {
            log.error("{} 파일을 S3에서 삭제하지 못했어요: {}", s3Key, e.getMessage());
            throw new RuntimeException("S3에서 파일을 삭제하지 못했어요. " + e.getLocalizedMessage());
        }
    }

    @NonNull
    private String createAccessUri(String logicalPath) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, logicalPath);
    }

    @NonNull
    private static String extractS3Key(String accessUri) {
        // 이미 상대 경로(logicalPath)인 경우, logicalPath가 S3의 키와 동일하기 때문에 그대로 반환합니다.
        if (!accessUri.startsWith("http://") && !accessUri.startsWith("https://")) {
            return accessUri.startsWith("/") ? accessUri.substring(1) : accessUri;
        }

        URI uri = URI.create(accessUri);
        String path = uri.getPath();
        return removeLeadingSlash(path);
    }

    @NonNull
    private static String removeLeadingSlash(String filePath) {
        return filePath.startsWith("/") ? filePath.substring(1) : filePath;
    }

}
