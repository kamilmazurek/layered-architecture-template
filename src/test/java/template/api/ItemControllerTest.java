package template.api;

import org.junit.jupiter.api.Test;
import org.modelmapper.MappingException;
import template.api.model.ItemDTO;
import template.config.TemplateConfig;
import template.exception.InvalidItemIdException;
import template.service.Item;
import template.service.ItemId;
import template.service.ItemService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static template.util.TestItems.createTestItemDTOs;
import static template.util.TestItems.createTestItems;

class ItemControllerTest {

    @Test
    void shouldGetItem() {
        //given item
        var item = Item.builder().id(new ItemId("item-a")).name("Item A").build();

        //and service
        var service = mock(ItemService.class);
        when(service.get(new ItemId("item-a"))).thenReturn(Optional.of(item));

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when item is requested
        var response = controller.getItem("item-a");

        //then response containing expected item is returned
        assertEquals(controller.toDTO(item), response.getBody());

        //and OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in retrieving the data
        verify(service).get(new ItemId("item-a"));
    }

    @Test
    void shouldNotFindItem() {
        //given service
        var service = mock(ItemService.class);
        when(service.get(new ItemId("item-a"))).thenReturn(Optional.empty());

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when item is requested
        var response = controller.getItem("item-a");

        //then response contains no item
        assertNull(response.getBody());

        //and Not Found status is returned
        assertEquals(NOT_FOUND, response.getStatusCode());

        //and service was involved in retrieving the data
        verify(service).get(new ItemId("item-a"));
    }

    @Test
    void shouldNotFindItemWithInvalidId() {
        //given service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when item with invalid ID is requested
        var response = controller.getItem("Item A");

        //then Not Found status is returned
        assertEquals(NOT_FOUND, response.getStatusCode());

        //and service was not involved in retrieving the data
        verify(service, never()).get(any());
    }

    @Test
    void shouldGetItems() {
        //given service
        var service = mock(ItemService.class);
        when(service.get()).thenReturn(createTestItems());

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when items are requested
        var response = controller.getItems();

        //then response containing expected items is returned
        assertEquals(createTestItemDTOs(), response.getBody());

        //and OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in retrieving the data
        verify(service).get();
    }

    @Test
    void shouldPostItem() {
        //given item
        var item = new ItemDTO().name("Item A");

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when POST request with item is handled
        var response = controller.postItem(item);

        //then OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in saving the data
        verify(service).create(controller.toDomainObject(item));
    }

    @Test
    void shouldNotAcceptPostRequestWhenItemHasID() {
        //given item
        var item = new ItemDTO().id("item-a").name("Item A");

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when POST request with item containing ID is received
        var response = controller.postItem(item);

        //then Bad Request status is returned
        assertEquals(BAD_REQUEST, response.getStatusCode());

        //and service was not involved in saving the data
        verify(service, never()).create(any());
    }

    @Test
    void shouldPutItem() {
        //given item
        var item = new ItemDTO().id("item-a").name("Item A");

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when item is put
        var response = controller.putItem("item-a", item);

        //then OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in saving data
        verify(service).upsert(controller.toDomainObject(item));
    }

    @Test
    void shouldNotAcceptPutRequestWhenItemHasNoID() {
        //given item
        var item = new ItemDTO().name("Item A"); // ID is null

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when PUT request is received but DTO lacks an ID
        var response = controller.putItem("item-a", item);

        //then Bad Request status is returned
        assertEquals(BAD_REQUEST, response.getStatusCode());

        //and service was not involved in saving the data
        verify(service, never()).upsert(any());
    }

    @Test
    void shouldNotAcceptPutRequestWhenItemHasAmbiguousID() {
        //given item
        var item = new ItemDTO().id("item-a").name("Item A");

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when PUT request with ambiguous ID is received
        var response = controller.putItem("item-b", item);

        //then Bad Request status is returned
        assertEquals(BAD_REQUEST, response.getStatusCode());

        //and service was not involved in saving the data
        verify(service, never()).upsert(any());
    }

    @Test
    void shouldNotPutItemWithInvalidId() {
        //given item with invalid ID
        var item = new ItemDTO().id("Item A").name("Item A");

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when PUT request with invalid ID is received
        var exception = assertThrows(MappingException.class, () -> controller.putItem("Item A", item));

        //then exception is caused by invalid ID
        assertInstanceOf(InvalidItemIdException.class, exception.getCause());

        //and service was not involved in saving the data
        verify(service, never()).upsert(any());
    }

    @Test
    void shouldDeleteItem() {
        //given item
        var item = Item.builder().id(new ItemId("item-a")).name("Item A").build();

        //and service
        var service = mock(ItemService.class);
        when(service.get(item.getId())).thenReturn(Optional.of(item));

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //when DELETE request is handled
        var response = controller.deleteItem("item-a");

        //then OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in deleting the data
        verify(service).delete(item.getId());
    }

    @Test
    void shouldNotFindItemToDelete() {
        //given service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service, new TemplateConfig().modelMapper());

        //and item id
        var itemId = "item-a";

        //when DELETE request is handled
        var response = controller.deleteItem(itemId);

        //then Not Found status is returned
        assertEquals(NOT_FOUND, response.getStatusCode());

        //and service was not involved in deleting the data
        verify(service, never()).delete(any());
    }

}
