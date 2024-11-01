package imwhs.eatz_server.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 리스너 클래스를 통해 JPA Auditing 기능을 활성화합니다.
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Setter
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 해당 엔티티를 논리적으로 삭제 처리합니다.
     */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 해당 엔티티가 논리적으로 삭제 처리되었는지 확인합니다.
     */
    public boolean isMarkedAsDeleted() {
        return deletedAt != null;
    }

}
