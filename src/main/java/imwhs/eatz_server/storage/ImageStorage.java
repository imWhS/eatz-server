package imwhs.eatz_server.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * ImageStorage 인터페이스입니다.
 * <p>이미지 파일 저장소를 구현하기 위해 사용합니다.
 */
public interface ImageStorage {

    /**
     * 이미지를 저장합니다.
     * @param filePath 저장할 파일의 확장자를 포함한 상대 디렉터리.
     *                 Ex. "image/users/profiles/profile_123.png"
     * @param file 저장하려는 파일
     * @return 파일이 저장된 상대 디렉터리. 클라이언트 요청용 URL 경로입니다.
     */
    String save(String filePath, MultipartFile file);

    /**
     * 이미지 파일을 삭제합니다.
     * @param file 삭제할 파일의 디렉터리
     */
    void delete(String file);

}
