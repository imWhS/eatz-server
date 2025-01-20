package imwhs.eatz_server.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {

    String save(String directory, String fileName, MultipartFile file);

}
