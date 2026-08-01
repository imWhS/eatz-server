package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.domain.ReportResourceType;
import imwhs.eatz_server.dto.report.*;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v0/reports")
@RestController
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ReportCreationInfoResponse registerReport(
            @AuthenticatedEatzUserId(required = false) Long userId,
            @Valid @RequestBody ReportCreateRequest request) {
        return reportService.register(
                userId,
                request.getResourceId(),
                request.getResourceType(),
                request.getCategoryId(),
                request.getResourceContent(),
                request.getDescription());
    }

    @PostMapping("/{id}/resolve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markReportAsResolved(@AuthenticatedEatzUserId Long adminId, @PathVariable Long id) {
        reportService.markAsResolved(adminId, id);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public ReportCategoryCreationInfoResponse registerReportCategory(
            @AuthenticatedEatzUserId Long adminId,
            @Valid @RequestBody ReportCategoryCreateRequest request) {
        return reportService.registerReason(adminId, request.getCode(), request.getDescription());
    }

    @PutMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCategory(
            @AuthenticatedEatzUserId Long adminId,
            @PathVariable Long id,
            @Valid @RequestBody ReportCategoryUpdateRequest request) {
        reportService.updateCategory(adminId, id, request.getCode(), request.getDescription());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ReportDto> getAllReportsByResource(
            @RequestParam ReportResourceType resource,
            boolean resolved,
            Pageable pageable) {
        return reportService.getAllByResourceType(resource, resolved, pageable);
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<ReportCategoryDto> getAllCategories() {
        return reportService.getAllCategories();
    }

}
