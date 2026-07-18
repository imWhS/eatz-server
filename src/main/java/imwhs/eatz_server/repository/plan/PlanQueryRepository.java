package imwhs.eatz_server.repository.plan;

import imwhs.eatz_server.dto.plan.PlanBasicDto;
import imwhs.eatz_server.dto.plan.PlanDetailDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanQueryRepository {

    /**
     * 사용자가 등록한 모든 플랜의 레시피를 포함한 상세한 정보를 포함하는 목록을 조회합니다.
     * <ul>
     *     <li> 기간을 지정하면, 해당 기간에 포함되어 있는 사용자의 플랜만 조회 대상에 포함합니다. </li>
     *     <li> 차단한 사용자의 레시피가 추가되어 있는 플랜은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 사용자의 ID
     * @param startDate 기간의 첫 날짜. 선택 사항입니다.
     * @param endDate 기간의 마지막 날짜. 선택 사항입니다.
     * @return 모든 플랜의 레시피를 포함한 상세한 정보를 포함하는 목록
     */
    List<PlanDetailDto> findAllDetailsByUserId(
            Long id,
            LocalDateTime startDate,
            LocalDateTime endDate);

    /**
     * 사용자가 레시피를 플랜으로 추가한 날짜 목록을 조회합니다.
     * <ul>
     *     <li> 플래너에 추가된 날짜가 빠른 플랜부터 정렬합니다. </li>
     *     <li> 사용자가 특정 레시피를 플래너의 어떤 날짜에 추가했는지 표시할 때,
     *          플래너의 달력에 특정 레시피가 추가되어 있는 날짜를 표시할 때 사용할 수 있습니다. </li>
     *     <li> 기간을 지정하면, 해당 기간에 포함되어 있는 사용자의 플랜만 조회 대상에 포함합니다. </li>
     *     <li> 차단한 사용자의 레시피가 추가되어 있는 플랜은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param userId 사용자의 ID
     * @param recipeId 레시피의 ID
     * @param startDate 기간의 첫 날짜. 선택 사항입니다.
     * @param endDate 기간의 마지막 날짜. 선택 사항입니다.
     * @return 사용자가 레시피를 플랜으로 추가한 날짜 목록
     */
    List<LocalDateTime> findScheduledDatesByUserIdAndRecipeId(
            Long userId,
            Long recipeId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * 사용자가 등록한 모든 플랜의 레시피를 포함한 기본 정보를 포함하는 목록을 조회합니다.
     * <ul>
     *     <li> 기간을 지정하면, 해당 기간에 포함되어 있는 사용자의 플랜만 조회 대상에 포함합니다. </li>
     *     <li> 차단한 사용자의 레시피가 추가되어 있는 플랜은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 사용자의 ID
     * @param startDate 기간의 첫 날짜. 선택 사항입니다.
     * @param endDate 기간의 마지막 날짜. 선택 사항입니다.
     * @return 모든 플랜의 레시피를 포함한 기본 정보를 포함하는 목록
     */
    List<PlanBasicDto> findAllBasicsByUserId(
            Long id,
            LocalDateTime startDate,
            LocalDateTime endDate);

}
