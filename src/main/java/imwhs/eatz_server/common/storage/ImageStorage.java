package imwhs.eatz_server.common.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * ImageStorage 인터페이스입니다.
 * <p>이미지 파일 저장소를 구현하기 위해 사용합니다.
 */
public interface ImageStorage {

    /**
     * 이미지 파일을 저장합니다.
     * @param directory 파일을 저장할 디렉터리 경로
     * @param fileName 파일을 저장할 이름. 파일 저장소에서의 이름을 지정합니다.
     * @param file 저장하려는 이미지 파일
     * @return 파일이 저장된 상대 경로
     */
    String save(String directory, String fileName, MultipartFile file);

}
