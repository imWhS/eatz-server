package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.UploadedImageInfoResponse;
import imwhs.eatz_server.dto.kitchenware.KitchenwareCreateRequest;
import imwhs.eatz_server.dto.kitchenware.KitchenwareCreationInfoResponse;
import imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.kitchenware.KitchenwareQueryService;
import imwhs.eatz_server.service.kitchenware.KitchenwareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v0/kitchenwares")
@RequiredArgsConstructor
@RestController
public class KitchenwareController {

    private final KitchenwareService kitchenwareService;
    private final KitchenwareQueryService kitchenwareQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KitchenwareCreationInfoResponse registerKitchenware(
            @AuthenticatedEatzUserId Long userId,
            @Valid @RequestBody KitchenwareCreateRequest request) {
        return kitchenwareService.register(userId, request.getName());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UploadedImageInfoResponse updateKitchenwareImage(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long adminId,
            @RequestParam("image") MultipartFile image) {
        String imageUrl = kitchenwareService.updateImage(id, adminId, image);
        return new UploadedImageInfoResponse(imageUrl);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeKitchenwareImage(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long adminId) {
        kitchenwareService.deleteImage(id, adminId);
    }

    /**
     * 도구 목록을 가져옵니다.
     * 이름 파라미터가 있으면 이름으로 필터링 된 도구 목록을, 없으면 전체 도구 목록을 가져옵니다.
     * <ul>
     *     <li> Ex. GET /api/v0/kitchenwares — 전체 도구 목록 </li>
     *     <li> Ex. GET /api/v0/kitchenwares?name=프라이팬 — '프라이팬'으로 이름이 필터링 된 도구 목록 </li>
     * </ul>
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getKitchenwares(
            @RequestParam(required = false) String name,
            @AuthenticatedEatzUserId Long userId,
            Pageable pageable) {
        if (name != null) {
            // name이 유효한 경우: 이름으로 필터링 후 단일 항목 조회
            KitchenwareBasicDto dto = kitchenwareQueryService.get(name);
            return ResponseEntity.ok(dto);
        } else {
            // name이 유효하지 않은 경우: 전체 목록 조회
            Page<KitchenwareBasicDto> dtos = kitchenwareQueryService.getAllBasics(
                    userId, pageable);
            return ResponseEntity.ok(dtos);
        }
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public KitchenwareBasicDto getKitchenware(@PathVariable Long id) {
        return kitchenwareQueryService.get(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<KitchenwareBasicDto> searchKitchenwares(
            @RequestParam("name") String keyword,
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return kitchenwareQueryService.searchBasics(keyword, userId, pageable);
    }

}
