package template.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static template.util.TestItems.createTestItems;

public class ItemsServiceTest {

    @Test
    void shouldReturnItems() {
        //given service
        var service = new ItemsService();

        //when items are requested
        var items = service.getItems();

        //then items are returned
        assertEquals(createTestItems(), items);
    }

}
