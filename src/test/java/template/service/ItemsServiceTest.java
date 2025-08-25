package template.service;

import org.junit.jupiter.api.Test;
import template.api.model.ItemDTO;
import template.exception.ItemIdAlreadySetException;
import template.persistence.ItemEntity;
import template.persistence.ItemsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static template.util.TestItems.createTestItemDTOs;
import static template.util.TestItems.createTestItemEntities;

public class ItemsServiceTest {

    @Test
    void shouldGetItem() {
        //given entity
        var entity = ItemEntity.builder().id(1L).name("Item A").build();

        //and repository
        var repository = mock(ItemsRepository.class);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        //and service
        var service = new ItemsService(repository);

        //when item is requested
        var result = service.getItem(entity.getId());

        //then expected item is returned
        var item = service.toDomainObject(entity);
        assertEquals(Optional.of(service.toDTO(item)), result);

        //and repository was queried for data
        verify(repository).findById(entity.getId());
    }

    @Test
    void shouldNotFindItem() {
        //given repository
        var repository = mock(ItemsRepository.class);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        //and service
        var service = new ItemsService(repository);

        //when item is requested
        var result = service.getItem(1L);

        //then no items are returned
        assertTrue(result.isEmpty());

        //and repository was queried for data
        verify(repository).findById(1L);
    }

    @Test
    void shouldGetItems() {
        //given repository
        var repository = mock(ItemsRepository.class);
        when(repository.findAll()).thenReturn(createTestItemEntities());

        //and service
        var service = new ItemsService(repository);

        //when items are requested
        var items = service.getItems();

        //then items are returned
        assertEquals(createTestItemDTOs(), items);

        //and repository was involved in retrieving the data
        verify(repository).findAll();
    }

    @Test
    void shouldCreateItem() {
        //given DTO
        var dto = new ItemDTO().name("Item A");

        //and repository
        var repository = mock(ItemsRepository.class);

        //and service
        var service = new ItemsService(repository);

        //when item is created
        service.postItem(dto);

        //then item is saved in repository
        var item = service.toDomainObject(dto);
        var expectedEntity = service.toEntity(item);
        expectedEntity.setId(1L);
        verify(repository).save(expectedEntity);
    }

    @Test
    void shouldNotCreateItem() {
        //given DTO
        var dto = new ItemDTO().name("Item A").id(1L);

        //and repository
        var repository = mock(ItemsRepository.class);

        //and service
        var service = new ItemsService(repository);

        //when item is created
        var exception = assertThrows(ItemIdAlreadySetException.class, () -> service.postItem(dto));

        //then exception is thrown
        var expectedMessage = "Item ID must be null when creating a new item. Expected null so the adapter can assign a new ID, but received: 1.";
        assertEquals(expectedMessage, exception.getMessage());

        //and item has not been saved in repository
        verify(repository, never()).save(any());
    }

    @Test
    void shouldPutItem() {
        //given DTO
        var dto = new ItemDTO().name("Item A");

        //and repository
        var repository = mock(ItemsRepository.class);

        //and service
        var service = new ItemsService(repository);

        //when item is put
        service.putItem(1L, dto);

        //then item has been saved in repository with proper ID
        var item = service.toDomainObject(dto);
        var expectedEntity = service.toEntity(item);
        expectedEntity.setId(1L);
        verify(repository).save(expectedEntity);
    }

    @Test
    void shouldDeleteItem() {
        //given entity
        var entity = ItemEntity.builder().id(1L).name("Item A").build();

        //and repository
        var repository = mock(ItemsRepository.class);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        //and service
        var service = new ItemsService(repository);

        //when item is deleted
        service.deleteItem(entity.getId());

        //then item is deleted from repository
        verify(repository).deleteById(entity.getId());
    }

}
