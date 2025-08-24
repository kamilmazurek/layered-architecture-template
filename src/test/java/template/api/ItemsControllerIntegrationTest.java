package template.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import template.AbstractIntegrationTest;
import template.api.model.ItemDTO;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static template.util.TestItems.createTestItemDTOs;

public class ItemsControllerIntegrationTest extends AbstractIntegrationTest {

    private final ObjectWriter objectWriter = new ObjectMapper().writer();

    @Test
    void shouldReturnItems() throws JsonProcessingException {
        when()
                .get("/items")
                .then()
                .statusCode(200)
                .assertThat()
                .body(equalTo(objectWriter.writeValueAsString(createTestItemDTOs())));
    }

    @Test
    void shouldInsertItemByPutRequest() {
        //given item
        var item = new ItemDTO().id(4L).name("Item D");

        //when PUT request with item is sent
        given()
                .contentType("application/json")
                .body(item)
                .when()
                .put("/items/4")
                .then()
                .statusCode(200);

        //then item can be retrieved by ID
        // TODO: Once GET by ID is implemented, retrieve the item and verify it matches the previously stored one.

        //cleanup
        // TODO: Add cleanup once DELETE by ID is implemented.
    }

    @Test
    void shouldNotAcceptPutRequestWhenItemHasAmbiguousID() {
        given()
                .contentType("application/json")
                .body(new ItemDTO().id(5L).name("Item E"))
                .when()
                .put("/items/6")
                .then()
                .statusCode(400);
    }


}
