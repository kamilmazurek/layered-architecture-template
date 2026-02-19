package template.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ItemRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final ItemJpaRepository jpaRepository;

    public Optional<ItemEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    public List<ItemEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Transactional
    public void create(ItemEntity item) {
        // Works with H2 for development and testing, but may require adjustments for production depending on the database
        entityManager.createNativeQuery("SELECT 1 FROM ITEM_SEQ_LOCK FOR UPDATE").getResultList();
        jpaRepository.save(item);
    }

    @Transactional
    public void upsert(Long id, ItemEntity item) {
        // Works with H2 for development and testing, but may require adjustments for production depending on the database
        entityManager.createNativeQuery("SELECT 1 FROM ITEM_SEQ_LOCK FOR UPDATE").getResultList();
        var mergeQuery = "MERGE INTO item (id, name) KEY(id) VALUES (?, ?)";
        entityManager.createNativeQuery(mergeQuery).setParameter(1, id).setParameter(2, item.getName()).executeUpdate();
        syncSequence(id);
    }

    private void syncSequence(Long insertedId) {
        var currentSeqValQuery = "SELECT CAST(BASE_VALUE AS BIGINT) FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_NAME = 'ITEM_SEQ'";
        var currentSeqVal = (Number) entityManager.createNativeQuery(currentSeqValQuery).getSingleResult();

        if (insertedId < currentSeqVal.longValue()) {
            return;
        }

        long newStart = insertedId + 1;
        entityManager.createNativeQuery("ALTER SEQUENCE ITEM_SEQ RESTART WITH " + newStart).executeUpdate();
    }

    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

}
