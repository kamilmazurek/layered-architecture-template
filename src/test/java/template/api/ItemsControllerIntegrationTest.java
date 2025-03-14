package template.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import template.AbstractIntegrationTest;
import template.api.model.ItemDTO;

import static io.restassured.RestAssured.when;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static template.api.TestItems.createTestItems;


public class ItemsControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void shouldReturnItems() {
        //when items are requested
        var response = when().get("/items");

        //then expected items are returned
        assertEquals(createTestItems(), asList(response.as(ItemDTO[].class)));

        //and OK status code is returned
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }

}
