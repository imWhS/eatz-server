package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.CreateRecipeDto;
import imwhs.eatz_server.dto.PagedResponseDto;
import imwhs.eatz_server.dto.RecipeResponseDto;
import imwhs.eatz_server.dto.UpdateRecipeDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 기본 페이지 번호 및 크기.
     * 응답 메시지에 포함시킬 레시피에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 새 레시피 등록.
     * @param userId 새 레시피를 등록하려는 사용자의 ID
     * @param dto 등록할 레시피 정보를 담고 있는 CreateRecipeDto
     * @return 등록 완료된 레시피 정보를 담고 있는 RecipeResponseDto
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우
     */
    @Transactional
    public RecipeResponseDto registerRecipe(
            Long userId,
            CreateRecipeDto dto
    ) {
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));

        Recipe recipe = Recipe.create(user, dto.getTitle(), dto.getUrl(), dto.getImageUrl());
        recipeRepository.save(recipe);

        return new RecipeResponseDto(recipe);
    }

    /**
     * ID로 레시피 조회.
     * @param id 조회할 레시피의 ID
     * @return 조회된 레시피 정보를 담고 있는 RecipeResponseDto
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우
     */
    public RecipeResponseDto findRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + id + "인 레시피가 존재하지 않습니다."));

        return new RecipeResponseDto(recipe);
    }

    /**
     * 모든 레시피 조회.
     * @param pageIndex 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param pageLimit 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto
     */
    public PagedResponseDto<RecipeResponseDto> findAllRecipes(Integer pageIndex, Integer pageLimit) {
        int index = (pageIndex == null || pageIndex < 0) ? DEFAULT_PAGE_NUMBER : pageIndex;
        int limit = (pageLimit == null || pageLimit < 0) ? DEFAULT_PAGE_SIZE : pageLimit;

        PageRequest pageRequest = PageRequest.of(index, limit);
        Page<Recipe> recipes = recipeRepository.findAll(pageRequest);
        Page<RecipeResponseDto> dtoPage = recipes.map(RecipeResponseDto::new);

        return PagedResponseDto.of(dtoPage);
    }

    /**
     * 특정 사용자가 등록한 모든 레시피 조회.
     * @param userId 레시피를 등록한 사용자 ID
     * @param pageIndex 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param pageLimit 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우
     */
    public PagedResponseDto<RecipeResponseDto> findAllRecipesByUserId(Long userId, Integer pageIndex, Integer pageLimit) {
        int index = (pageIndex == null || pageIndex < 0) ? DEFAULT_PAGE_NUMBER : pageIndex;
        int limit = (pageLimit == null || pageLimit < 0) ? DEFAULT_PAGE_SIZE : pageLimit;

        EatzUser user = userRepository.findById(userId).orElseThrow(() ->
                new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));

        PageRequest pageRequest = PageRequest.of(index, limit);
        Page<Recipe> recipes = recipeRepository.findByUser(user, pageRequest);
        Page<RecipeResponseDto> dtoPage = recipes.map(RecipeResponseDto::new);

        return PagedResponseDto.of(dtoPage);
    }

    /**
     * 레시피 정보 수정.
     * @param id 수정할 레시피 레시피 ID
     * @param dto 수정할 레시피 정보를 담고 있는 UpdateRecipeDto
     * @return 수정 완료된 레시피 정보를 담고 있는 RecipeResponseDto
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우
     */
    @Transactional
    public RecipeResponseDto updateRecipe(Long id, UpdateRecipeDto dto) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new RecipeNotFoundException("id가 " + id + "인 레시피가 존재하지 않습니다."));

        recipe.update(dto);

        return new RecipeResponseDto(recipe);
    }

}
