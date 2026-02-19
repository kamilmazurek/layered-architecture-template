package template.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import template.exception.ItemIdAlreadySetException;
import template.repository.ItemEntity;
import template.repository.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static template.util.TestItems.createTestItemEntities;
import static template.util.TestItems.createTestItems;

class ItemServiceTest {

    @Test
    void shouldGetItem() {
        //given entity
        var entity = ItemEntity.builder().id(1L).name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        //and service
        var service = new ItemService(mock(EntityManager.class), repository, new ModelMapper());

        //when item is requested
        var result = service.get(entity.getId());

        //then expected item is returned
        var item = service.toDomainObject(entity);
        assertEquals(Optional.of(item), result);

        //and repository was queried for data
        verify(repository).findById(entity.getId());
    }

    @Test
    void shouldNotFindItem() {
        //given repository
        var repository = mock(ItemRepository.class);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        //and service
        var service = new ItemService(mock(EntityManager.class), repository, new ModelMapper());

        //when item is requested
        var result = service.get(1L);

        //then no items are returned
        assertTrue(result.isEmpty());

        //and repository was queried for data
        verify(repository).findById(1L);
    }

    @Test
    void shouldGetItems() {
        //given repository
        var repository = mock(ItemRepository.class);
        when(repository.findAll()).thenReturn(createTestItemEntities());

        //and service
        var service = new ItemService(mock(EntityManager.class), repository, new ModelMapper());

        //when items are requested
        var items = service.get();

        //then items are returned
        assertEquals(createTestItems(), items);

        //and repository was involved in retrieving the data
        verify(repository).findAll();
    }

    @Test
    void shouldCreateItem() {
        //given item
        var item = Item.builder().name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);

        //and entity manager
        var entityManager = mock(EntityManager.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mock(Query.class));

        //and service
        var service = new ItemService(entityManager, repository, new ModelMapper());

        //when item is created
        service.create(item);

        //then item is saved in repository
        var expectedEntity = service.toEntity(item);
        verify(repository).save(expectedEntity);
    }

    @Test
    void shouldNotCreateItem() {
        //given item
        var item = Item.builder().id(1L).name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);

        //and service
        var service = new ItemService(mock(EntityManager.class), repository, new ModelMapper());

        //when item is created
        var exception = assertThrows(ItemIdAlreadySetException.class, () -> service.create(item));

        //then exception is thrown
        var expectedMessage = "Item ID must be null when creating a new item. Expected null so the service can assign a new ID, but received: 1.";
        assertEquals(expectedMessage, exception.getMessage());

        //and item has not been saved in repository
        verify(repository, never()).save(any());
    }

    @Test
    void shouldUpsertItem() {
        //given item
        var item = Item.builder().id(1L).name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);

        //and entity manager
        var entityManager = mock(EntityManager.class);
        when(entityManager.createNativeQuery("SELECT 1 FROM ITEM_SEQ_LOCK FOR UPDATE")).thenReturn(mock(Query.class));

        var queryA = mock(Query.class);
        when(queryA.setParameter(eq(1), any())).thenReturn(queryA);
        when(queryA.setParameter(eq(2), any())).thenReturn(queryA);
        when(entityManager.createNativeQuery("MERGE INTO item (id, name) KEY(id) VALUES (?, ?)")).thenReturn(queryA);

        var queryB = mock(Query.class);
        when(queryB.getSingleResult()).thenReturn(1L);
        var currentSeqValQueryString = "SELECT CAST(BASE_VALUE AS BIGINT) FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_NAME = 'ITEM_SEQ'";
        when(entityManager.createNativeQuery(currentSeqValQueryString)).thenReturn(queryB);

        when(entityManager.createNativeQuery("ALTER SEQUENCE ITEM_SEQ RESTART WITH 2")).thenReturn(mock(Query.class));

        //and service
        var service = new ItemService(entityManager, repository, new ModelMapper());

        //when item is put
        service.upsert(1L, item);

        //then item has been saved in repository
        verify(entityManager, times(4)).createNativeQuery(anyString());
    }

    @Test
    void shouldDeleteItem() {
        //given entity
        var entity = ItemEntity.builder().id(1L).name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        //and service
        var service = new ItemService(mock(EntityManager.class), repository, new ModelMapper());

        //when item is deleted
        service.delete(entity.getId());

        //then item is deleted from repository
        verify(repository).deleteById(entity.getId());
    }

}
