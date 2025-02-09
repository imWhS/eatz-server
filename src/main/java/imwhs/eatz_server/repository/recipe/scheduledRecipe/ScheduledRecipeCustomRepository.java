package imwhs.eatz_server.repository.recipe.scheduledRecipe;

import imwhs.eatz_server.dto.recipe.RecipeDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledRecipeCustomRepository {

    /*
    1. 플래너에 레시피 등록
    2. 플래너에 등록한 레시피의 날짜 변경
    3. 플래너에 등록한 레시피의 우선 순위 변경
    4. 플래너에서 레시피 삭제
    5. 사용자가 플래너에 등록한 모든 레시피 조회
        - 레시피 ID
        - 레시피 제목
        - 레시피 설명
        - 레시피 URL
        - 레시피 이미지 URL
        - 좋아요 수
        - 평가 평균 점수, 평가 수
    6. 사용자가 특정 날짜/기간에 등록한 레시피 조회
    7. 플래너에서 특정 재료를 포함하고 있는 레시피 조회
     */

    List<RecipeDto> findScheduledRecipesByUsername(String username);

    


}
