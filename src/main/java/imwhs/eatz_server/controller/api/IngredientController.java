package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.ingredient.*;
import imwhs.eatz_server.dto.liked.LikedIngredientBasicDto;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.ingredient.IngredientQueryService;
import imwhs.eatz_server.service.ingredient.IngredientService;
import imwhs.eatz_server.service.liked.LikedIngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v0/ingredients")
@RequiredArgsConstructor
@RestController
public class IngredientController {

    private final IngredientService ingredientService;
    private final IngredientQueryService ingredientQueryService;
    private final LikedIngredientService likedIngredientService;

    /**
     * 새 재료를 등록합니다.
     * @param request 재료 생성 및 등록을 요청하기 위해 필요한 정보
     * @return 등록 완료된 재료의 생성 정보를 담은 응답 DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IngredientCreationInfoResponse registerIngredient(
            @AuthenticatedEatzUserId Long userId,
            @RequestBody IngredientCreateRequest request) {
        return ingredientService.register(userId, request.getName(), request.getParentId(), request.getChildIds());
    }

    @PostMapping("/{id}/likeds")
    @ResponseStatus(HttpStatus.OK)
    public LikedIngredientBasicDto likeIngredient(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        return likedIngredientService.like(id, userId);
    }

    @DeleteMapping("/{id}/likeds")
    @ResponseStatus(HttpStatus.OK)
    public LikedIngredientBasicDto unlikeIngredient(@AuthenticatedEatzUserId Long userId, @PathVariable Long id) {
        return likedIngredientService.unlike(id, userId);
    }

    /**
     * 특정 상위 재료의 기본 정보와 해당 상위 재료에 포함되어 있는 모든 재료의 기본 정보 목록을 가져옵니다.
     * <p> 로그인 사용자인 경우, 사용자의 재료 별 보관함 추가 여부, 좋아요 여부도 함께 가져옵니다. </p>
     * @param id 상위 재료의 ID
     * @return 특정 상위 재료의 기본 정보와 해당 상위 재료에 포함되어 있는 모든 재료의 기본 정보 목록
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IngredientBasicsInParentDto getIngredientBasicsInParent(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId) {
        return ingredientQueryService.getAllBasicsInParent(id, userId);
    }

//    /**
//     * 재료 정보 및 해당 재료가 소속된 상위 재료, 해당 재료의 모든 하위 재료 목록을 함께 가져옵니다.
//     * <p> 로그인 사용자인 경우, 사용자의 재료 별 보관함 추가 여부, 좋아요 여부도 함께 가져옵니다. </p>
//     * @param name 재료의 이름
//     * @return 재료 정보 및 해당 재료가 소속된 상위 재료, 해당 재료의 모든 하위 재료 목록을 담은 Dto
//     */
//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public IngredientEssentialWithChildrenDto getIngredientWithChildren(@RequestParam String name) {
//        return ingredientQueryService.getEssentialWithChildrenByName(name);
//    }

    /**
     * 상위 재료가 존재하지 않는 모든 최상위 계층(root) 재료 목록을 가져옵니다.
     * <p> 로그인 사용자인 경우, 사용자의 재료 별 보관함 추가 여부, 좋아요 여부도 함께 가져옵니다. </p>
     * @return 최상위 계층(Root)의 재료 목록과 페이징 정보
     */
    @GetMapping("/roots")
    @ResponseStatus(HttpStatus.OK)
    public Page<IngredientBasicDto> getAllRootBasics(@AuthenticatedEatzUserId Long userId, Pageable pageable) {
        return ingredientQueryService.getAllRootBasics(userId, pageable);
    }

    /**
     * ID에 해당하는 재료 및 재료의 모든 하위 계층(hierarchy) 재료 정보를 가져옵니다.
     * @param id 재료의 ID
     * @return 재료 및 재료의 모든 하위 계층(hierarchy) 재료 정보
     */
    @GetMapping("/{id}/tree")
    @ResponseStatus(HttpStatus.OK)
    public IngredientHierarchyDto getHierarchy(@PathVariable Long id) {
        return ingredientQueryService.getHierarchy(id);
    }

    /**
     * 검색어에 해당하는 이름을 가진 재료를 검색합니다.
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 검색된 재료의 기본 정보 목록과 페이징 정보
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<IngredientBasicDto> searchIngredients(
            @AuthenticatedEatzUserId Long userId,
            @RequestParam("keyword") String keyword, Pageable pageable) {
        return ingredientQueryService.searchBasics(keyword, userId, pageable);
    }

}
