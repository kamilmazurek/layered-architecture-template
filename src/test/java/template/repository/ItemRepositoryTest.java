package template.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import template.exception.ItemAlreadyExistsException;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static template.util.TestItems.createTestItemEntities;

class ItemRepositoryTest {

    @Test
    void shouldFindItemById() {
        //given entity
        var entity = ItemEntity.builder().id("item-a").name("Item A").build();

        //and JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);
        when(jpaRepository.findById("item-a")).thenReturn(Optional.of(entity));

        //and repository
        var repository = new ItemRepository(mock(EntityManager.class), jpaRepository);

        //when item is requested
        var result = repository.findById("item-a");

        //then expected item is returned
        assertEquals(Optional.of(entity), result);

        //and repository was queried for data
        verify(jpaRepository).findById("item-a");
    }

    @Test
    void shouldFindAllItems() {
        //given entities
        var entities = createTestItemEntities();

        //and JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);
        when(jpaRepository.findAll()).thenReturn(entities);

        //and repository
        var repository = new ItemRepository(mock(EntityManager.class), jpaRepository);

        //when items are requested
        var result = repository.findAll();

        //then items are returned
        assertEquals(entities, result);

        //and repository was queried for data
        verify(jpaRepository).findAll();
    }

    @Test
    void shouldCreateItem() {
        //given entity
        var entity = ItemEntity.builder().id("item-a").name("Item A").build();

        //and entity manager
        var entityManager = mock(EntityManager.class);

        //and repository
        var repository = new ItemRepository(entityManager, mock(ItemJpaRepository.class));

        //when item is created
        repository.create(entity);

        //then item is persisted and flushed
        verify(entityManager).persist(entity);
        verify(entityManager).flush();
    }

    @Test
    void shouldNotCreateItemWhenIdAlreadyExists() {
        //given entity
        var entity = ItemEntity.builder().id("item-a").name("Item A").build();

        //and entity manager that reports primary key violation
        var entityManager = mock(EntityManager.class);
        doThrow(new ConstraintViolationException("Duplicate key", new SQLException(), "PRIMARY_KEY"))
                .when(entityManager).flush();

        //and repository
        var repository = new ItemRepository(entityManager, mock(ItemJpaRepository.class));

        //when item is created
        var exception = assertThrows(ItemAlreadyExistsException.class, () -> repository.create(entity));

        //then exception with item ID is thrown
        assertEquals("Item with id item-a already exists", exception.getMessage());
    }

    @Test
    void shouldUpsertItem() {
        //given entity
        var entity = ItemEntity.builder().id("item-a").name("Item A").build();

        //and JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);

        //and repository
        var repository = new ItemRepository(mock(EntityManager.class), jpaRepository);

        //when item is upserted
        repository.upsert(entity);

        //then item is saved in JPA repository
        verify(jpaRepository).save(entity);
    }

    @Test
    void shouldDeleteItem() {
        //given JPA repository
        var jpaRepository = mock(ItemJpaRepository.class);
        //and repository
        var repository = new ItemRepository(mock(EntityManager.class), jpaRepository);

        //when item is deleted
        repository.deleteById("item-a");

        //then item is deleted from JPA repository
        verify(jpaRepository).deleteById("item-a");
    }

}
