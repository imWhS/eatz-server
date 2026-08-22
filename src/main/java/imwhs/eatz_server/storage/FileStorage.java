package imwhs.eatz_server.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 업로드 및 삭제 등 파일 저장소와 관련된 작업을 처리하는 인터페이스입니다.
 */
public interface FileStorage {

    /**
     * 파일을 디렉터리에 저장합니다.
     * @param logicalPath 파일의 논리적 경로. 인프라와 무관한 순수 비즈니스 경로를 의미합니다.
     *                    Ex. "images/users/profiles/profile_123.png"
     * @param file 저장할 파일 객체
     * @return 외부에서 저장된 파일에 바로 접근할 수 있는 URI(URL). accessUri에 해당합니다.
     *         Ex. "uploads/images/users/profiles/profile_123.png" 또는
     *             "https://sample.com/images/users/profiles/profile_123.png"
     */
    String save(String logicalPath, MultipartFile file);

    /**
     * URI에 해당하는 파일을 디렉터리에서 삭제합니다.
     * @param accessUri save()에 의해 생성된, 삭제할 파일에 바로 접근할 수 있는 URI(URL).
     *                  Ex. "uploads/images/users/profiles/profile_123.png" 또는
     *                      "https://sample.com/images/users/profiles/profile_123.png"
     */
    void deleteByUri(String accessUri);

}
