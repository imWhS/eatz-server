package imwhs.eatz_server.dto.plan;

import imwhs.eatz_server.domain.Plan;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class PlanCreationInfoResponse extends CreationInfoResponse {

    public PlanCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public PlanCreationInfoResponse(Plan plan) {
        this(plan.getId(), plan.getCreatedAt());
    }

}
