package imwhs.eatz_server.service.kitchenware;

import imwhs.eatz_server.ImageCategory;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.dto.kitchenware.KitchenwareCreationInfoResponse;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.kitchenware.KitchenwareRepository;
import imwhs.eatz_server.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class KitchenwareService {

    private final KitchenwareRepository kitchenwareRepository;
    private final EatzUserRepository userRepository;
    private final ImageService imageService;

    /**
     * 새 도구를 등록합니다.
     * @param adminId 관리자의 ID
     * @param name 도구의 이름
     * @return 등록 완료된 도구의 생성 정보를 담은 응답 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public KitchenwareCreationInfoResponse register(Long adminId, String name) {
        userRepository.validateExistsAsAdmin(adminId);

        // 사용하려는 재료 이름의 유효성을 검증합니다.
        kitchenwareRepository.validateDuplicates(name);

        // DTO로 재료 엔티티를 생성하고 저장합니다.
        Kitchenware kitchenware = Kitchenware.create(name);
        kitchenwareRepository.save(kitchenware);

        return new KitchenwareCreationInfoResponse(kitchenware);
    }

    /**
     * 새 도구를 등록합니다.
     * @param adminId 관리자의 ID
     * @param name 도구의 이름
     * @param imageUrl 도구의 이미지 URL
     * @return 등록 완료된 도구의 생성 정보를 담은 응답 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public KitchenwareCreationInfoResponse register(Long adminId, String name, String imageUrl) {
        userRepository.validateExistsAsAdmin(adminId);

        // 사용하려는 재료 이름의 유효성을 검증합니다.
        kitchenwareRepository.validateDuplicates(name);

        // TODO: 이미지 업로드

        // DTO로 재료 엔티티를 생성하고 저장합니다.
        Kitchenware kitchenware = Kitchenware.create(name, imageUrl);
        kitchenwareRepository.save(kitchenware);

        return new KitchenwareCreationInfoResponse(kitchenware);
    }

    /**
     * 도구를 업데이트합니다.
     * @param adminId 관리자의 ID
     * @param id 도구의 ID
     * @param name 도구의 새 이름. 필수 항목입니다.
     * @param imageUrl 도구의 새 이미지 URL
     * @return 업데이트 완료된 도구의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long adminId, long id, String name, String imageUrl) {
        userRepository.validateExistsAsAdmin(adminId);

        // ID로 업데이트할 도구의 엔티티를 조회합니다.
        Kitchenware kitchenware = kitchenwareRepository.get(id);

        // 새 이름으로 변경합니다.
        kitchenware.updateName(name);

        // 새 이미지 URL로 변경합니다.
        kitchenware.updateImageUrl(imageUrl);

        // 사용하려는 도구 이름의 유효성을 검증합니다.
        kitchenwareRepository.validateDuplicates(name);
    }

    /**
     * 도구를 삭제합니다.
     * @param id 삭제하려는 도구의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 삭제할 재료의 ID가 전달되지 않은 경우, 더 이상 진행하지 않습니다.
        kitchenwareRepository.validateExists(id);

        // 엔티티를 삭제합니다.
        kitchenwareRepository.deleteById(id);
    }

    /**
     * 도구의 대표 이미지를 업로드한 후, 이미지 URL을 업데이트합니다.
     * @param id 도구의 ID
     * @param adminId 관리자의 ID
     * @param image 도구의 대표 이미지
     * @return 업로드 완료된 도구의 대표 이미지 URL
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateImage(Long id, Long adminId, MultipartFile image) {
        userRepository.validateExistsAsAdmin(adminId);

        // ID로 업데이트할 도구의 엔티티를 조회합니다.
        Kitchenware kitchenware = kitchenwareRepository.get(id);

        String existingImageUrl = kitchenware.getImageUrl();

        String imageUrl = imageService.upload(image, ImageCategory.KITCHENWARE);
        kitchenware.updateImageUrl(imageUrl);
        log.info("재료 {}의 대표 이미지 URL을 업데이트했어요: {}", kitchenware.getName(), imageUrl);

        if (Objects.nonNull(existingImageUrl) && !existingImageUrl.isBlank()) {
            try {
                imageService.delete(existingImageUrl);
            } catch (Exception e) {
                log.error("재료 {}의 기존 대표 이미지를 삭제하지 못했어요.", kitchenware.getName());
            }
        }

        return imageUrl;
    }

    /**
     * 재료의 프로필 이미지 URI(URL)를 제거하고, 이미지를 삭제합니다.
     * @param id 재료의 ID
     * @param adminId 관리자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long id, Long adminId) {
        userRepository.validateExistsAsAdmin(adminId);

        // ID로 업데이트할 도구의 엔티티를 조회합니다.
        Kitchenware kitchenware = kitchenwareRepository.get(id);
        String existingImageUrl = kitchenware.getImageUrl();
        kitchenware.deleteImageUrl();
        if (Objects.nonNull(existingImageUrl) && !existingImageUrl.isBlank()) {
            try {
                imageService.delete(existingImageUrl);
            } catch (Exception e) {
                log.error("재료 {}의 대표 이미지를 삭제하지 못했어요.", kitchenware.getName());
            }
        }
    }

}
