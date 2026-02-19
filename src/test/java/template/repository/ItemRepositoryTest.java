package template.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static template.util.TestItems.createTestItemEntities;

class ItemRepositoryTest {

    private final String LOCK_QUERY_STRING = "SELECT 1 FROM ITEM_SEQ_LOCK FOR UPDATE";

    @Test
    void shouldFindItemById() {
        // given entity
        var entity = ItemEntity.builder().id(1L).name("Item A").build();

        // and JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        // and entity manager
        var entityManager = mock(EntityManager.class);

        // and repository
        var repository = new ItemRepository(entityManager, jpaRepository);

        // when item is requested
        var result = repository.findById(1L);

        // then expected item is returned
        assertEquals(Optional.of(entity), result);

        // and repository was queried for data
        verify(jpaRepository).findById(1L);
    }

    @Test
    void shouldFindAllItems() {
        // given entities
        var entities = createTestItemEntities();

        // and JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);
        when(jpaRepository.findAll()).thenReturn(entities);

        // and entity manager
        var entityManager = mock(EntityManager.class);

        // and repository
        var repository = new ItemRepository(entityManager, jpaRepository);

        // when items are requested
        var result = repository.findAll();

        // then items are returned
        assertEquals(entities, result);

        // and repository was queried for data
        verify(jpaRepository).findAll();
    }

    @Test
    void shouldCreateItem() {
        // given entity
        var entity = ItemEntity.builder().name("Item A").build();

        // and JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);

        // and entity manager
        var entityManager = mock(EntityManager.class);
        var lockQuery = createLockQuery(entityManager);

        // and repository
        var repository = new ItemRepository(entityManager, jpaRepository);

        // when item is created
        repository.create(entity);

        // then entity manager is used for lock
        verify(entityManager).createNativeQuery(LOCK_QUERY_STRING);
        verify(lockQuery).getResultList();

        // and item is saved in JPA repository
        verify(jpaRepository).save(entity);
    }

    @Test
    void shouldUpsertItem() {
        // given entity
        var entity = ItemEntity.builder().name("Item A").build();

        // and JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);

        // and entity manager with helper methods for queries
        var entityManager = mock(EntityManager.class);
        var lockQuery = createLockQuery(entityManager);
        var mergeQuery = createMergeQuery(entityManager);
        var seqQuery = createSequenceQuery(entityManager, 0L);
        createAlterSequenceQuery(entityManager);

        // and repository
        var repository = new ItemRepository(entityManager, jpaRepository);

        // when item is upserted
        repository.upsert(1L, entity);

        // then lock query is executed
        verify(entityManager).createNativeQuery(LOCK_QUERY_STRING);
        verify(lockQuery).getResultList();

        // and merge query is executed
        verify(entityManager).createNativeQuery("MERGE INTO item (id, name) KEY(id) VALUES (?, ?)");
        verify(mergeQuery).setParameter(1, 1L);
        verify(mergeQuery).setParameter(2, entity.getName());

        // and sequence query is executed
        verify(seqQuery).getSingleResult();
        verify(entityManager).createNativeQuery(startsWith("ALTER SEQUENCE ITEM_SEQ RESTART WITH"));
    }

    @Test
    void shouldDeleteItem() {
        // given JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);

        // and entity manager
        var entityManager = mock(EntityManager.class);

        // and repository
        var repository = new ItemRepository(entityManager, jpaRepository);

        // when item is deleted
        repository.deleteById(1L);

        // then item is deleted from JPA repository
        verify(jpaRepository).deleteById(1L);
    }

    private Query createLockQuery(EntityManager em) {
        var query = mock(Query.class);
        when(em.createNativeQuery(LOCK_QUERY_STRING)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(1));
        return query;
    }

    private Query createMergeQuery(EntityManager em) {
        var mergeQueryString = "MERGE INTO item (id, name) KEY(id) VALUES (?, ?)";
        var query = mock(Query.class);
        when(em.createNativeQuery(mergeQueryString)).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        return query;
    }

    private Query createSequenceQuery(EntityManager em, long currentValue) {
        var seqQueryString = "SELECT CAST(BASE_VALUE AS BIGINT) FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_NAME = 'ITEM_SEQ'";
        var query = mock(Query.class);
        when(em.createNativeQuery(seqQueryString)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(currentValue);
        return query;
    }

    private Query createAlterSequenceQuery(EntityManager em) {
        var alterSeqPrefix = "ALTER SEQUENCE ITEM_SEQ RESTART WITH";
        var query = mock(Query.class);
        when(em.createNativeQuery(startsWith(alterSeqPrefix))).thenReturn(query);
        return query;
    }

}