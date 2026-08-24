package imwhs.eatz_server.storage;

import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 서버가 위치한 환경의 로컬 저장소 및 로컬 저장소 내 파일을 관리합니다.
 */
@Slf4j
@Component
public class LocalFileStorage implements FileStorage {

    /**
     * 서버가 위치한 환경의 로컬 저장소(storage) 디렉토리의 절대 경로
     * <ul>
     *     <li> 절대 경로를 사용해야 하기 때문에 루트를 포함합니다. </li>
     *     <li> Ex. /Users/wonhee/eatz_workspace/ </li>
     * </ul>
     */
    @Value("${eatz.storage.root-directory-path}")
    private String rootDirectoryPath;

    /**
     * 서버가 위치한 환경의 로컬 저장소(storage)에 '업로드된 파일'이 저장될 디렉토리의 이름
     * <ul>
     *     <li> 파일 리소스 serving을 요청할 때의 URI 접두어로도 사용합니다. </li>
     *     <li> Ex. uploads </li>
     * </ul>
     */
    @Value("${eatz.storage.base-directory}")
    private String baseDirectory;

    @Override
    public String save(String logicalPath, MultipartFile file) {
        if (logicalPath == null ||  logicalPath.isBlank() || file == null) { throw new EatzInvalidRequestArgumentException(); }

        String path = removeLeadingSlash(logicalPath);

        // 파일이 저장될 로컬 저장소의 절대 경로를 정의합니다.
        Path storagePath = createStoragePath(path);

        // 파일이 저장될 부모 디렉토리의 절대 경로를 정의합니다.
        Path parentDirectoryPath = storagePath.getParent();

        try {
            // 부모 디렉토리 경로까지의 디렉토리가 하나라도 존재하지 않으면 일괄 생성합니다.
            if (!Files.exists(parentDirectoryPath)) {
                log.info("디렉토리를 일괄 생성할게요: {}", parentDirectoryPath);
                Files.createDirectories(parentDirectoryPath);
            }

            // 메모리에 임시 저장된 파일을 부모 디렉토리로 이동(복사)합니다.
            file.transferTo(storagePath);
            String accessUri = createAccessUri(logicalPath);
            log.info("파일을 로컬 저장소에 저장했어요. | {}", accessUri);
            return accessUri;
        } catch (IOException e) {
            log.error("{} 파일을 로컬 저장소의 {}에 저장할 수 없어요: {}",
                    storagePath.getFileName(), storagePath, e.getMessage());
            throw new RuntimeException("파일을 로컬 저장소에 저장하지 못했어요. " + e.getLocalizedMessage());
        }
    }

    @Override
    public void deleteByUri(String accessUri) {
        if (accessUri == null || accessUri.isBlank()) { return; }

        String uri = removeLeadingSlash(accessUri);

        // 삭제할 파일이 위치한 로컬 저장소의 절대 경로를 정의합니다.
        Path storagePath = Paths.get(rootDirectoryPath).resolve(uri);

        try {
            Files.deleteIfExists(storagePath);
        } catch (IOException e) {
            // Checked Exception 대신 Unchecked Exception으로 감싸서 예외를 발생시킵니다.
            log.error("{} 파일을 로컬 저장소의 {}에서 삭제할 수 없어요: {}",
                    storagePath.getFileName(), storagePath, e.getMessage());
            throw new RuntimeException("파일을 삭제하지 못했어요. " + e.getLocalizedMessage());
        }
    }

    /**
     * 파일의 상대 경로를 로컬 저장소에서의 절대 경로로 변환한 후, 이를 포함하는 Path 타입을 생성합니다.
     * @param logicalPath 파일의 이름 및 디렉토리
     */
    private Path createStoragePath(String logicalPath) {
        Path rootBasePath = getRootBasePath();
        return rootBasePath.resolve(logicalPath);
    }

    @NonNull
    private static String removeLeadingSlash(String filePath) {
        return filePath.startsWith("/") ? filePath.substring(1) : filePath;
    }

    @NonNull
    private Path getRootBasePath() {
        return Paths.get(rootDirectoryPath).resolve(baseDirectory);
    }

    /**
     * 외부에서 로컬 저장소에 위치한 파일에 바로 접근할 수 있는 URI 경로를 생성합니다.
     * Ex. /uploads/images/users/profiles/profile_123.png
     */
    @NonNull
    private String createAccessUri(String fileDirectory) {
        return "/" + baseDirectory + "/" + fileDirectory;
    }

}
