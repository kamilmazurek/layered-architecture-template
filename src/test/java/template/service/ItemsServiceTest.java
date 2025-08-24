package template.service;

import org.junit.jupiter.api.Test;
import template.api.model.ItemDTO;
import template.persistence.ItemEntity;
import template.persistence.ItemsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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
    void shouldPutItem() {
        //given repository
        var repository = mock(ItemsRepository.class);

        //and service
        var service = new ItemsService(repository);

        //and item
        var itemDTO = new ItemDTO().name("Item A");

        //when item is put
        service.putItem(1L, itemDTO);

        //then item has been put to repository with proper ID
        var item = service.toDomainObject(itemDTO);
        item.setId(1L);
        verify(repository).save(service.toEntity(item));
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
