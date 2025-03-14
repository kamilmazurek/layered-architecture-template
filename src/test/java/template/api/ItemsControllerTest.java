package template.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import template.api.model.ItemDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemsControllerTest {

    @Test
    void shouldReturnItems() {
        //given items controller
        var controller = new ItemsController();

        //when items are requested
        var response = controller.getItems();

        //then response containing items is returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getExpectedItems(), response.getBody());
    }

    private List<ItemDTO> getExpectedItems() {
        var itemA = new ItemDTO().id(1).content("Item A");
        var itemB = new ItemDTO().id(2).content("Item B");
        var itemC = new ItemDTO().id(3).content("Item C");

        return List.of(itemA, itemB, itemC);
    }

}
