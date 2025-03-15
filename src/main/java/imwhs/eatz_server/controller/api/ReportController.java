package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.CreateReportDto;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.ReportDto;
import imwhs.eatz_server.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v0/reports")
@RestController
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> registerReport(@RequestBody CreateReportDto dto) {
        Long reportId = reportService.register(EatzUserAuthUtil.getId(), dto.getEntityId(), dto.getType(), dto.getContent());
        return ResponseEntity.ok(ApiResponse.success(reportId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Paged<ReportDto>>> findReportsByType(@RequestParam EntityType type, Pageable pageable) {
        Page<ReportDto> reports = reportService.findByType(type, pageable);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @PostMapping("/{id}/resolve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resolveReport(@PathVariable Long id) {
        reportService.markAsResolved(EatzUserAuthUtil.getId(), id);
    }

}
