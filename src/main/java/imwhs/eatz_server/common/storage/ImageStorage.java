package imwhs.eatz_server.common.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * ImageStorage 인터페이스입니다.
 * <p>이미지 파일 저장소를 구현하기 위해 사용합니다.
 */
public interface ImageStorage {

    /**
     * 이미지를 저장합니다.
     * @param directory 파일을 저장할 디렉터리 경로.
     * @param imageName 파일 저장소에서의 파일 이름. 파일이 서비스에서 이미지로서 사용될 때의 이름을 지정하며, 확장자는 제외해야 합니다.
     * @param file 저장하려는 파일.
     * @return 파일이 저장된 상대 경로.
     */
    String save(String directory, String imageName, MultipartFile file);

    /**
     * 이미지 파일을 삭제합니다.
     * @param file 삭제할 파일의 경로.
     */
    void delete(String file);

}
