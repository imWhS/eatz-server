package imwhs.eatz_server.domain;

import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "affiliate", uniqueConstraints = {
        @UniqueConstraint(name = "uk_requirement_type_requirement_id", columnNames = {"requirement_type", "requirement_id"})
})
@Entity
public class Affiliate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementType requirementType;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private Long requirementId;

    @Column(nullable = false, length = 2000)
    private String url;

    @Column(nullable = false)
    private String provider;

    public static Affiliate create(RequirementType requirementType, Long requirementId, String url, String provider) {
        validateUrl(url);
        validateProvider(provider);

        Affiliate affiliate = new Affiliate();
        affiliate.requirementType = requirementType;
        affiliate.requirementId = requirementId;
        affiliate.url = url;
        affiliate.provider = provider;
        return affiliate;
    }

    public void updateUrl(String url) {
        validateUrl(url);
        this.url = url;
    }

    public void updateProvider(String provider) {
        validateProvider(provider);
        this.provider = provider;
    }

    private static void validateUrl(String url) {
        if (Objects.isNull(url) || url.isBlank()) {
            throw new EatzInvalidRequestArgumentException("URL이 비어 있거나, 올바르지 않아요.");
        }
    }

    private static void validateProvider(String provider) {
        if (Objects.isNull(provider) || provider.isBlank()) {
            throw new EatzInvalidRequestArgumentException("제공자가 비어 있거나, 올바르지 않아요.");
        }
    }

}
