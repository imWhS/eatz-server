package imwhs.eatz_server.service.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageStorageService {

    String upload(MultipartFile file, String directory);

    String getImagePath(String folder, String imageName);

}
