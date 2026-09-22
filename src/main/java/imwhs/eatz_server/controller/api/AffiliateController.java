package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.domain.Affiliate;
import imwhs.eatz_server.domain.RequirementType;
import imwhs.eatz_server.dto.affiliate.AffiliateCreateRequest;
import imwhs.eatz_server.dto.affiliate.AffiliateDto;
import imwhs.eatz_server.dto.affiliate.AffiliateUpdateByIdRequest;
import imwhs.eatz_server.dto.affiliate.AffiliateUpdateRequest;
import imwhs.eatz_server.service.AffiliateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v0/affiliates")
@RequiredArgsConstructor
@RestController
public class AffiliateController {

    private final AffiliateService affiliateService;

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void registerAffiliate(@Valid @RequestBody AffiliateCreateRequest request) {
        affiliateService.register(request.getRequirementType(), request.getRequirementId(), request.getUrl());
    }

    @GetMapping
    public AffiliateDto getAffiliate(
            @RequestParam RequirementType requirementType,
            @RequestParam Long requirementId
    ) {
        Affiliate affiliate = affiliateService.get(requirementType, requirementId);
        return AffiliateDto.from(affiliate);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateAffiliateById(@PathVariable Long id, @Valid @RequestBody AffiliateUpdateByIdRequest request) {
        affiliateService.updateById(id, request.getUrl());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    public void updateAffiliate(@Valid @RequestBody AffiliateUpdateRequest request) {
        affiliateService.update(request.getRequirementType(), request.getRequirementId(), request.getUrl());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAffiliateById(@PathVariable Long id) {
        affiliateService.deleteById(id);
    }

}

