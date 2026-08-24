package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class ReportCategory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String description;

    @Setter
    @Column(nullable = false)
    private boolean isActive = true;

    public static ReportCategory create(String code, String description) {
        return new ReportCategory(null, code, description, true);
    }

    public void updateCode(String code) {
        validateCode(code);
        this.code = code;
    }
    public void updateDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    public static void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 코드가 비어 있어요."); }
    }

    public static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 설명이 비어 있어요."); }
    }
}
