package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.CountResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import imwhs.eatz_server.service.recipe.SavedRecipeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 레시피의 사용자 별 저장 정보를 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/saveds/{id}")
@RequiredArgsConstructor
@RestController
public class SavedController {

private final SavedRecipeQueryService savedRecipeQueryService;

    /**
     * 레시피를 저장한 모든 사용자 목록을 가져옵니다.
     * @param id 레시피 ID
     * @return 레시피를 저장한 모든 사용자 목록
     */
    @GetMapping("/recipes")
    public List<EatzUserEssentialDto> getSavedUsers(@PathVariable Long id) {
        return savedRecipeQueryService.getSavedUserEssentialsByRecipe(id);
    }

    /**
     * 레시피를 저장한 사용자 수를 가져옵니다.
     * @param id 레시피 ID
     * @return 레시피를 저장한 사용자 수를 담은 응답 DTO
     */
    @GetMapping("/recipes/r/count")
    public CountResponse getSavedUserCount(@PathVariable Long id) {
        return savedRecipeQueryService.countSaveds(id);
    }

}
