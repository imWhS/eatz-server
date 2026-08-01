package imwhs.eatz_server.controller.api.eatzuser;

import imwhs.eatz_server.dto.plan.*;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.PantryService;
import imwhs.eatz_server.service.eatzuser.EatzUserPlanQueryService;
import imwhs.eatz_server.service.eatzuser.EatzUserPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v0/users/me/plans")
@RestController
public class EatzUserPlanController {

    private final EatzUserPlanService eatzUserPlanService;
    private final EatzUserPlanQueryService eatzUserPlanQueryService;
    private final PantryService pantryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PlanCreationInfoResponse registerPlan(
            @RequestBody PlanCreateRequest request,
            @AuthenticatedEatzUserId Long userId) {
        return eatzUserPlanService.register(userId, request.getRecipeId(), request.getDate(), request.getPriority());
    }

    @PutMapping("/{planId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePlan(
            @PathVariable Long planId,
            @AuthenticatedEatzUserId Long userId,
            @Valid @RequestBody PlanUpdateRequest request) {
        eatzUserPlanService.update(planId, userId, request.getDate(), request.getPriority());
    }

    @DeleteMapping("/{planId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable Long planId, @AuthenticatedEatzUserId Long userId) {
        eatzUserPlanService.delete(planId, userId);
    }

    /**
     * 플랜을 모두 가져옵니다.
     * <ul>
     *     <li> 기간을 지정하면, 해당 기간에 포함되어 있는 사용자의 플랜만 조회 대상에 포함시킬 수 있습니다. </li>
     *     <li> 차단한 사용자의 레시피가 추가되어 있는 플랜은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param startDate 기간의 첫 날짜. 선택 사항입니다.
     * @param endDate 기간의 마지막 날짜. 선택 사항입니다.
     * @return 레시피가 플랜으로 등록되어 있는 모든 날짜 목록
     */
    @GetMapping
    public List<PlanDetailDto> getAllPlans(
            @AuthenticatedEatzUserId Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return eatzUserPlanQueryService.getAllDetailsByUserId(userId, startDate, endDate);
    }

    /**
     * 레시피가 플랜으로 등록되어 있는 모든 날짜 목록을 가져옵니다.
     * <ul>
     *     <li> 기간을 지정하면, 해당 기간에 포함되어 있는 사용자의 플랜만 조회 대상에 포함시킬 수 있습니다. </li>
     *     <li> 차단한 사용자의 레시피가 추가되어 있는 플랜은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param recipeId 레시피의 ID
     * @param startDate 기간의 첫 날짜. 선택 사항입니다.
     * @param endDate 기간의 마지막 날짜. 선택 사항입니다.
     * @return 레시피가 플랜으로 등록되어 있는 모든 날짜 목록
     */
    @GetMapping(value = "/recipes/{recipeId}/dates")
    public PlannedDatesDto getAllPlannedDates(
            @AuthenticatedEatzUserId Long userId,
            @PathVariable Long recipeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return eatzUserPlanQueryService.getPlannedDatesForRecipe(
                userId, recipeId, startDate, endDate);
    }

    @GetMapping("/checklist")
    public ChecklistDto getChecklist(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @AuthenticatedEatzUserId Long userId) {
        return eatzUserPlanQueryService.getChecklist(userId, startDate, endDate);
    }

    @PostMapping("/checklist/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeChecklist(
            @RequestBody ChecklistCompletionRequest request,
            @AuthenticatedEatzUserId Long userId) {
        pantryService.addIngredients(request.getIngredientIds(), userId);
    }

}
