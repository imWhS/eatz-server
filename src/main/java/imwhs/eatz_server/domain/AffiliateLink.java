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
@Table(name = "affiliate_link", uniqueConstraints = {
        @UniqueConstraint(name = "uk_requirement_type_requirement_id", columnNames = {"requirement_type", "requirement_id"})
})
@Entity
public class AffiliateLink {

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

    public static AffiliateLink create(RequirementType requirementType, Long requirementId, String url) {
        AffiliateLink link = new AffiliateLink();
        link.requirementType = requirementType;
        link.requirementId = requirementId;
        link.url = url;
        return link;
    }

    public void updateUrl(String url) {
        if (Objects.isNull(url) || url.isBlank()) {
            throw new EatzInvalidRequestArgumentException("URL이 비어 있거나, 올바르지 않아요.");
        }

        this.url = url;
    }

}
