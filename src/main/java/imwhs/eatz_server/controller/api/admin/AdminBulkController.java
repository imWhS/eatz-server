package imwhs.eatz_server.controller.api.admin;

import imwhs.eatz_server.service.admin.AdminBulkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 대량의 엔티티 데이터를 관리하기 위한 관리자 권한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/admin/bulk")
@RequiredArgsConstructor
@RestController
public class AdminBulkController {

    private final AdminBulkService bulkService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.OK)
    public void uploadBulkRecipes(
            @RequestParam("jsonFile") MultipartFile jsonFile, @RequestParam("authorId") Long authorId) {
        bulkService.saveBulkRecipes(jsonFile, authorId);
    }

}
