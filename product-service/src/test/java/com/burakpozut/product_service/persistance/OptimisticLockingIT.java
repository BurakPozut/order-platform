package com.burakpozut.product_service.persistance;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.burakpozut.product_service.infra.persistance.ProductJpaEntity;

// TODO: UTC
@SpringBootTest
class ProductOptimisticLockingIT {

  @Autowired
  PlatformTransactionManager txManager;
  @Autowired
  EntityManagerFactory emf;

  @Test
  void should_throw_when_updating_stale_version() {
    TransactionTemplate tx = new TransactionTemplate(txManager);

    // 1) Seed a Product row
    // UUID id = UUID.randomUUID();
    // tx.executeWithoutResult(status -> {
    // EntityManager em = emf.createEntityManager();
    // em.joinTransaction();

    // ProductJpaEntity p = new ProductJpaEntity();
    // p.setId(id);
    // p.setName("Book");
    // p.setPrice(new BigDecimal("100.00"));
    // p.setCurrency(Currency.TRY); // change if needed
    // p.setStatus(ProductStatus.ACTIVE); // change if needed
    // p.setInventory(10);

    // // If auditing isn't enabled in tests, set timestamps manually to satisfy NOT
    // // NULL.
    // // If auditing IS enabled, Hibernate/Spring Data will overwrite these anyway.
    // p.setCreatedAt(LocalDateTime.now());
    // p.setUpdatedAt(LocalDateTime.now());

    // em.persist(p);
    // em.flush();
    // em.close();
    // });

    String uuidString = "fcbee20c-7176-4006-adb6-743170388a13";
    UUID uuid = UUID.fromString(uuidString);
    // 2) TX1 loads the entity (detached after EM close)
    ProductJpaEntity tx1Snapshot = tx.execute(status -> {
      EntityManager em = emf.createEntityManager();
      em.joinTransaction();
      ProductJpaEntity e = em.find(ProductJpaEntity.class, uuid);
      em.close(); // detach
      return e;
    });

    // 3) TX2 loads the same entity (detached after EM close)
    ProductJpaEntity tx2Snapshot = tx.execute(status -> {
      EntityManager em = emf.createEntityManager();
      em.joinTransaction();
      ProductJpaEntity e = em.find(ProductJpaEntity.class, uuid);
      em.close(); // detach
      return e;
    });

    // 4) TX1 updates and commits => version increments
    tx.executeWithoutResult(status -> {
      EntityManager em = emf.createEntityManager();
      em.joinTransaction();
      ProductJpaEntity managed = em.merge(tx1Snapshot);
      managed.setInventory(managed.getInventory() + 1);
      managed.setUpdatedAt(LocalDateTime.now()); // safe if auditing not enabled
      em.flush();
      em.close();
    });

    // 5) TX2 tries to update with stale version => should fail
    var ex = assertThrows(RuntimeException.class, () -> {
      tx.executeWithoutResult(status -> {
        EntityManager em = emf.createEntityManager();
        em.joinTransaction();
        ProductJpaEntity managed = em.merge(tx2Snapshot);
        managed.setInventory(managed.getInventory() + 1);
        em.flush();
        em.close();
      });
    });

    // Either Spring wraps it, or it stays as JPA
    if (!(ex instanceof OptimisticLockException) &&
        !(ex instanceof ObjectOptimisticLockingFailureException) &&
        !(ex.getCause() instanceof OptimisticLockException)) {
      throw ex; // unexpected failure type
    }
  }
}
