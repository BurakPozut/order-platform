package com.burakpozut.order_service.infra.persistance.failed_event;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataFailedEventRepository extends JpaRepository<FailedEventJpaEntity, UUID> {
    List<FailedEventJpaEntity> findByStatus(String status);

    List<FailedEventJpaEntity> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    List<FailedEventJpaEntity> findByStatusOrderByCreatedAtAsc(FailedEventStatus status);

    List<FailedEventJpaEntity> findByEntityTypeAndEntityIdAndStatus(String entityType, UUID entityId,
            FailedEventStatus status);

}
