package template.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static template.api.TestItems.createTestItems;

public class ItemsControllerTest {

    @Test
    void shouldReturnItems() {
        //given items controller
        var controller = new ItemsController();

        //when items are requested
        var response = controller.getItems();

        //then response containing items is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createTestItems(), response.getBody());
    }

}
