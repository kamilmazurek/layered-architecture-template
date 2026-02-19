package template.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static template.repository.Queries.ALTER_SEQUENCE_QUERY;
import static template.repository.Queries.CURRENT_SEQ_VAL_QUERY;
import static template.repository.Queries.LOCK_QUERY;
import static template.repository.Queries.MERGE_QUERY;

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
        //Works with H2 for development and testing, but may need adjustments for production databases
        entityManager.createNativeQuery(LOCK_QUERY).getResultList();
        jpaRepository.save(item);
    }

    @Transactional
    public void upsert(Long id, ItemEntity item) {
        //Works with H2 for development and testing, but may need adjustments for production databases
        entityManager.createNativeQuery(LOCK_QUERY).getResultList();
        entityManager.createNativeQuery(MERGE_QUERY).setParameter(1, id).setParameter(2, item.getName()).executeUpdate();
        syncSequence(id);
    }

    private void syncSequence(Long insertedId) {
        var currentSeqVal = (Number) entityManager.createNativeQuery(CURRENT_SEQ_VAL_QUERY).getSingleResult();

        if (insertedId < currentSeqVal.longValue()) {
            return;
        }

        entityManager.createNativeQuery(String.format(ALTER_SEQUENCE_QUERY, insertedId + 1)).executeUpdate();
    }

    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

}
