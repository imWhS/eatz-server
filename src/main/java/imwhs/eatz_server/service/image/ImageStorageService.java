package imwhs.eatz_server.service.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageStorageService {

    String uploadProfileImage(MultipartFile file);

    String save(MultipartFile file, String directory, String fileName);

    String getImagePath(String folder, String imageName);

}
