package template.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import template.domain.ItemsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static template.util.TestItems.createTestItemDTOs;
import static template.util.TestItems.createTestItems;

public class ItemsControllerTest {

    @Test
    void shouldReturnItems() {
        //given service
        var service = mock(ItemsService.class);
        when(service.getItems()).thenReturn(createTestItems());

        //and controller
        var controller = new ItemsController(service);

        //when items are requested
        var response = controller.getItems();

        //then response containing items is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createTestItemDTOs(), response.getBody());

        //and service was involved in retrieving the data
        verify(service, times(1)).getItems();
    }

}
