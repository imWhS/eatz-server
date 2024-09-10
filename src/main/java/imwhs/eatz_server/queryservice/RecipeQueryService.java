package imwhs.eatz_server.queryservice;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.PagedResponseDto;
import imwhs.eatz_server.dto.RecipeResponseDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeQueryService {

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;


    /**
     * 페이지 번호 및 크기 기본 값.
     * 응답 메시지에 포함시킬 레시피에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * ID로 레시피 조회.
     * @param id 조회할 레시피의 ID
     * @return 조회된 레시피 정보를 담고 있는 RecipeResponseDto
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우
     */
    public RecipeResponseDto findRecipeById(Long id) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + id + "인 레시피가 존재하지 않습니다."));

        return new RecipeResponseDto(recipe);
    }

    /**
     * 모든 레시피 조회.
     * @param pageNumber 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param pageSize 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto
     */
    public PagedResponseDto<RecipeResponseDto> findAllRecipes(Integer pageNumber, Integer pageSize) {
        int index = (pageNumber == null || pageNumber < 0) ? DEFAULT_PAGE_NUMBER : pageNumber;
        int limit = (pageSize == null || pageSize < 0) ? DEFAULT_PAGE_SIZE : pageSize;

        PageRequest pageRequest = PageRequest.of(index, limit);
        Page<Recipe> pagedRecipes = recipeRepository.findAllByDeletedAtIsNull(pageRequest);
        Page<RecipeResponseDto> pagedUserResponseDtos = pagedRecipes.map(RecipeResponseDto::new);

        return PagedResponseDto.of(pagedUserResponseDtos);
    }

    /**
     * 특정 사용자가 등록한 모든 레시피 조회.
     * @param userId 레시피를 등록한 사용자 ID
     * @param pageNumber 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param pageSize 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우
     */
    public PagedResponseDto<RecipeResponseDto> findAllRecipesByUserId(Long userId, Integer pageNumber, Integer pageSize) {
        int number = (pageNumber == null || pageNumber < 0) ? DEFAULT_PAGE_NUMBER : pageNumber;
        int size = (pageSize == null || pageSize < 0) ? DEFAULT_PAGE_SIZE : pageSize;

        EatzUser user = userRepository.findById(userId).orElseThrow(() ->
                new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));

        PageRequest pageRequest = PageRequest.of(number, size);
        Page<Recipe> pagedRecipes = recipeRepository.findByUserAndDeletedAtIsNull(user, pageRequest);
        Page<RecipeResponseDto> pagedRecipeResponseDtos = pagedRecipes.map(RecipeResponseDto::new);

        return PagedResponseDto.of(pagedRecipeResponseDtos);
    }

}
