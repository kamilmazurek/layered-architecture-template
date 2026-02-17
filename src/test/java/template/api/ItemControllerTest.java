package template.api;

import org.junit.jupiter.api.Test;
import template.api.model.ItemDTO;
import template.service.ItemService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static template.util.TestItems.createTestItemDTOs;

class ItemControllerTest {

    @Test
    void shouldGetItem() {
        //given item
        var item = new ItemDTO().id(1L).name("Item A");

        //and service
        var service = mock(ItemService.class);
        when(service.getItem(1L)).thenReturn(Optional.of(item));

        //and controller
        var controller = new ItemController(service);

        //when item is requested
        var response = controller.getItem(1L);

        //then response containing expected item is returned
        assertEquals(item, response.getBody());

        //and OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in retrieving the data
        verify(service).getItem(1L);
    }

    @Test
    void shouldNotFindItem() {
        //given service
        var service = mock(ItemService.class);
        when(service.getItem(1L)).thenReturn(Optional.empty());

        //and controller
        var controller = new ItemController(service);

        //when item is requested
        var response = controller.getItem(1L);

        //then response contains no item
        assertNull(response.getBody());

        //and Not Found status is returned
        assertEquals(NOT_FOUND, response.getStatusCode());

        //and service was involved in retrieving the data
        verify(service).getItem(1L);
    }

    @Test
    void shouldGetItems() {
        //given service
        var service = mock(ItemService.class);
        when(service.getItems()).thenReturn(createTestItemDTOs());

        //and controller
        var controller = new ItemController(service);

        //when items are requested
        var response = controller.getItems();

        //then response containing expected items is returned
        assertEquals(createTestItemDTOs(), response.getBody());

        //and OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in retrieving the data
        verify(service).getItems();
    }

    @Test
    void shouldPostItem() {
        //given item
        var item = new ItemDTO().name("Item A");

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service);

        //when POST request with item is handled
        var response = controller.postItem(item);

        //then OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in saving the data
        verify(service).postItem(item);
    }

    @Test
    void shouldNotAcceptPostRequestWhenItemHasID() {
        //given item
        var item = new ItemDTO().id(1L).name("Item A");

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service);

        //when POST request with item containing ID is received
        var response = controller.postItem(item);

        //then Bad Request status is returned
        assertEquals(BAD_REQUEST, response.getStatusCode());

        //and service was not involved in saving the data
        verify(service, never()).postItem(any());
    }

    @Test
    void shouldPutItem() {
        //given item
        var item = new ItemDTO().name("Item A");

        //and service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service);

        //when item is put
        var response = controller.putItem(1L, item);

        //then OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in saving data
        verify(service).putItem(1L, item);
    }

    @Test
    void shouldDeleteItem() {
        //given item
        var item = new ItemDTO().id(1L).name("Item A");

        //and service
        var service = mock(ItemService.class);
        when(service.getItem(item.getId())).thenReturn(Optional.of(item));

        //and controller
        var controller = new ItemController(service);

        //when DELETE request is handled
        var response = controller.deleteItem(item.getId());

        //then OK status is returned
        assertEquals(OK, response.getStatusCode());

        //and service was involved in deleting the data
        verify(service).deleteItem(item.getId());
    }

    @Test
    void shouldNotFindItemToDelete() {
        //given service
        var service = mock(ItemService.class);

        //and controller
        var controller = new ItemController(service);

        //and item id
        var itemId = 1L;

        //when DELETE request is handled
        var response = controller.deleteItem(itemId);

        //then Not Found status is returned
        assertEquals(NOT_FOUND, response.getStatusCode());

        //and service was not involved in deleting the data
        verify(service, never()).deleteItem(any());
    }

}
