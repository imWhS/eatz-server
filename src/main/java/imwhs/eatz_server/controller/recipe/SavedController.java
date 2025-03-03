package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.service.recipe.SavedRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 레시피의 사용자 별 저장 정보를 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/recipes/{id}/saveds")
@RequiredArgsConstructor
@RestController
public class SavedController {

    private final SavedRecipeService savedRecipeService;

    /**
     * 레시피를 저장한 모든 사용자 목록을 조회합니다.
     * @param id 레시피 ID.
     * @return 레시피를 저장한 모든 사용자 목록.
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<List<EatzUserBasicDto>>> findSavedUsers(@PathVariable Long id) {
        return ResponseEntity.ok().body(ApiResponse.success(savedRecipeService.getSavedUsersByRecipe(id)));
    }

    /**
     * 레시피를 저장한 사용자 수를 조회합니다.
     * @param id 레시피 ID.
     * @return 레시피를 저장한 사용자 수.
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getSavedUserCount(@PathVariable Long id) {
        Long savedUserCount = savedRecipeService.countSaveds(id);
        return ResponseEntity.ok(ApiResponse.success(savedUserCount));
    }

}
