package template.service;

import org.junit.jupiter.api.Test;
import template.config.TemplateConfig;
import template.exception.InvalidItemNameException;
import template.exception.ItemIdAlreadySetException;
import template.exception.ItemIdMismatchException;
import template.repository.ItemEntity;
import template.repository.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static template.util.TestItems.createTestItemEntities;
import static template.util.TestItems.createTestItems;

class ItemServiceTest {

    @Test
    void shouldGetItem() {
        //given entity
        var entity = ItemEntity.builder().id("item-a").name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);
        when(repository.findById("item-a")).thenReturn(Optional.of(entity));

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when item is requested
        var result = service.get(new ItemId("item-a"));

        //then expected item is returned
        var item = service.toDomainObject(entity);
        assertEquals(Optional.of(item), result);

        //and repository was queried for data
        verify(repository).findById("item-a");
    }

    @Test
    void shouldNotFindItem() {
        //given repository
        var repository = mock(ItemRepository.class);
        when(repository.findById("item-a")).thenReturn(Optional.empty());

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when item is requested
        var result = service.get(new ItemId("item-a"));

        //then no items are returned
        assertTrue(result.isEmpty());

        //and repository was queried for data
        verify(repository).findById("item-a");
    }

    @Test
    void shouldGetItems() {
        //given repository
        var repository = mock(ItemRepository.class);
        when(repository.findAll()).thenReturn(createTestItemEntities());

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when items are requested
        var items = service.get();

        //then items are returned
        assertEquals(createTestItems(), items);

        //and repository was involved in retrieving the data
        verify(repository).findAll();
    }

    @Test
    void shouldCreateItemWithIdDerivedFromName() {
        //given item
        var item = Item.builder().name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when item is created
        service.create(item);

        //then item with ID derived from its name is saved in repository
        verify(repository).create(ItemEntity.builder().id("item-a").name("Item A").build());
    }

    @Test
    void shouldNotCreateItemWithId() {
        //given item
        var item = Item.builder().id(new ItemId("item-a")).name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when item is created
        var exception = assertThrows(ItemIdAlreadySetException.class, () -> service.create(item));

        //then exception is thrown
        var expectedMessage = "Item ID must be null when creating a new item. Expected null so the ID can be derived from the item name, but received: item-a.";
        assertEquals(expectedMessage, exception.getMessage());

        //and item has not been saved in repository
        verify(repository, never()).create(any());
    }

    @Test
    void shouldNotCreateItemWithInvalidName() {
        //given item with name that produces no ID
        var item = Item.builder().name("!!!").build();

        //and repository
        var repository = mock(ItemRepository.class);

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when item is created
        assertThrows(InvalidItemNameException.class, () -> service.create(item));

        //then item has not been saved in repository
        verify(repository, never()).create(any());
    }

    @Test
    void shouldUpsertItem() {
        //given item
        var item = Item.builder().id(new ItemId("item-a")).name("Item A").build();

        //and repository
        var repository = mock(ItemRepository.class);

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when item is put
        service.upsert(item);

        //then item has been saved in repository
        verify(repository).upsert(service.toEntity(item));
    }

    @Test
    void shouldNotUpsertItemWhenIdIsNotDerivedFromName() {
        //given item
        var item = Item.builder().id(new ItemId("item-a")).name("Item B").build();

        //and repository
        var repository = mock(ItemRepository.class);

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when item is put
        var exception = assertThrows(ItemIdMismatchException.class, () -> service.upsert(item));

        //then exception is thrown
        var expectedMessage = "Item ID must be derived from the item name. Expected: item-b, but received: item-a.";
        assertEquals(expectedMessage, exception.getMessage());

        //and item has not been saved in repository
        verify(repository, never()).upsert(any());
    }

    @Test
    void shouldDeleteItem() {
        //given item id
        var itemId = new ItemId("item-a");

        //and repository
        var repository = mock(ItemRepository.class);

        //and service
        var service = new ItemService(repository, new TemplateConfig().modelMapper());

        //when item is deleted
        service.delete(itemId);

        //then item is deleted from repository
        verify(repository).deleteById("item-a");
    }

}
