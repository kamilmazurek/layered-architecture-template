package template.repository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import template.exception.ItemAlreadyExistsException;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ItemRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final ItemJpaRepository jpaRepository;

    public Optional<ItemEntity> findById(String id) {
        return jpaRepository.findById(id);
    }

    public List<ItemEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Transactional
    public void create(ItemEntity item) {
        try {
            entityManager.persist(item);
            entityManager.flush();
        } catch (EntityExistsException | ConstraintViolationException e) {
            throw new ItemAlreadyExistsException(item.getId());
        }
    }

    @Transactional
    public void upsert(ItemEntity item) {
        jpaRepository.save(item);
    }

    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

}
