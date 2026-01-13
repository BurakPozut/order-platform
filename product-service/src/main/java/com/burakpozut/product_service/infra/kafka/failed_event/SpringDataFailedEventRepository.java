package com.burakpozut.product_service.infra.kafka.failed_event;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataFailedEventRepository extends JpaRepository<FailedEventJpaEntity, UUID> {
    List<FailedEventJpaEntity> findByStatusOrderByCreatedAtAsc(FailedEventStatus status);

    List<FailedEventJpaEntity> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    List<FailedEventJpaEntity> findByEntityTypeAndEntityIdAndStatus(String entityType, UUID entityId,
            FailedEventStatus status);
}