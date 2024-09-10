package imwhs.eatz_server.queryservice;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.EatzUserResponseDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자(EatzUser) 관련 항목을 조회하는 메서드를 제공하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EatzUserQueryService {

    private final EatzUserRepository userRepository;

    private final RecipeRepository recipeRepository;

    /**
     * 페이지 번호 및 크기 기본 값.
     * 응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 식별자로 사용자 조회.
     * <p>
     * id로 특정 EatzUser를 조회합니다.
     */
    public EatzUserResponseDto findById(Long id) {
        EatzUser user = userRepository.findById(id).orElseThrow(
                () -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾지 못했습니다.")
        );

        return new EatzUserResponseDto(user);
    }

    /**
     * 사용자 이름으로 사용자 조회.
     * <p>
     * username으로 특정 EatzUser를 조회합니다.
     */
    public EatzUserResponseDto findByUsername(String username) {
        EatzUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EatzUserNotFoundException("사용자 이름이 " + username + "인 사용자를 찾을 수 없습니다."));
        userRepository.save(user);

        return new EatzUserResponseDto(user);
    }

    /**
     * 레시피를 등록한 사용자 조회.
     * <p>
     * Recipe의 id로 특정 EatzUser를 조회합니다.
     */
    public EatzUserResponseDto findByRecipe(Long recipeId) {
        EatzUser user = userRepository.findByRecipeId(recipeId)
                .orElseThrow(() -> new EatzUserNotFoundException(
                        "id가 " + recipeId + "인 레시피를 등록한 사용자를 찾을 수 없습니다."));

        return new EatzUserResponseDto(user);
    }

    /**
     * 모든 사용자 목록 조회.
     */
    public Page<EatzUserResponseDto> findAll(Integer pageNumber, Integer pageSize) {
        int number = (pageNumber == null || pageNumber < 0) ? DEFAULT_PAGE_NUMBER : pageNumber;
        int size = (pageSize == null || pageSize < 0) ? DEFAULT_PAGE_SIZE : pageSize;

        PageRequest pageRequest = PageRequest.of(number, size);
        Page<EatzUser> pagedUsers = userRepository.findAll(pageRequest);
        return pagedUsers.map(EatzUserResponseDto::new);
    }

}
