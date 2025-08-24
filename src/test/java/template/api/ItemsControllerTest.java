package template.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import template.api.model.ItemDTO;
import template.service.ItemsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static template.util.TestItems.createTestItemDTOs;

public class ItemsControllerTest {

    @Test
    void shouldGetItem() {
        //given item
        var item = new ItemDTO().id(1L).name("Item A");

        //and service
        var service = mock(ItemsService.class);
        when(service.getItem(1L)).thenReturn(Optional.of(item));

        //and controller
        var controller = new ItemsController(service);

        //when item is requested
        var response = controller.getItem(1L);

        //then response containing expected item is returned
        assertEquals(item, response.getBody());

        //and OK status is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());

        //and service was involved in retrieving the data
        verify(service).getItem(1L);
    }

    @Test
    void shouldNotFindItem() {
        //given service
        var service = mock(ItemsService.class);
        when(service.getItem(1L)).thenReturn(Optional.empty());

        //and controller
        var controller = new ItemsController(service);

        //when item is requested
        var response = controller.getItem(1L);

        //then response contains no item
        assertNull(response.getBody());

        //and Not Found status is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        //and service was involved in retrieving the data
        verify(service).getItem(1L);
    }


    @Test
    void shouldGetItems() {
        //given service
        var service = mock(ItemsService.class);
        when(service.getItems()).thenReturn(createTestItemDTOs());

        //and controller
        var controller = new ItemsController(service);

        //when items are requested
        var response = controller.getItems();

        //then response containing items is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createTestItemDTOs(), response.getBody());

        //and service was involved in retrieving the data
        verify(service).getItems();
    }

    @Test
    void shouldPutItem() {
        //given service
        var service = mock(ItemsService.class);

        //and controller
        var controller = new ItemsController(service);

        //and item
        var item = new ItemDTO().name("Item A");

        //when item is put
        var response = controller.putItem(1L, item);

        //then OK status is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());

        //and service was involved in saving data
        verify(service).putItem(1L, item);
    }

    @Test
    void shouldDeleteItem() {
        //given service
        var service = mock(ItemsService.class);

        //and controller
        var controller = new ItemsController(service);

        //and item
        var item = new ItemDTO().id(1L).name("Item A");
        when(service.getItem(item.getId())).thenReturn(Optional.of(item));

        //when DELETE request is handled
        var response = controller.deleteItem(item.getId());

        //then OK status is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());

        //and service was involved in deleting the data
        verify(service).deleteItem(item.getId());
    }

    @Test
    void shouldNotFindItemToDelete() {
        //given service
        var service = mock(ItemsService.class);

        //and controller
        var controller = new ItemsController(service);

        //and item id
        var itemId = 1L;

        //when DELETE request is handled
        var response = controller.deleteItem(itemId);

        //and Not Found status is returned
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        //and service was not involved in deleting the data
        verify(service, never()).deleteItem(any());
    }

}
