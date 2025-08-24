package template.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import template.AbstractIntegrationTest;
import template.api.model.ItemDTO;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;

public class ItemsControllerIntegrationTest extends AbstractIntegrationTest {

    private final ObjectWriter objectWriter = new ObjectMapper().writer();

    @Test
    void shouldGetItem() throws JsonProcessingException {
        when()
                .get("/items/1")
                .then()
                .statusCode(200)
                .assertThat()
                .body(equalTo(objectWriter.writeValueAsString(new ItemDTO().id(1L).name("Item A"))));
    }

    @Test
    void shouldNotFindItem() {
        when()
                .get("/items/4")
                .then()
                .statusCode(404)
                .assertThat()
                .body(emptyString());
    }

    @Test
    void shouldInsertItemByPutRequest() {
        //given item
        var item = new ItemDTO().id(5L).name("Item D");

        //when PUT request with item is sent
        given()
                .contentType("application/json")
                .body(item)
                .when()
                .put("/items/5")
                .then()
                .statusCode(200);

        //then item can be retrieved by ID
        // TODO: Once GET by ID is implemented, retrieve the item and verify it matches the previously stored one.

        //cleanup
        // TODO: Add cleanup once DELETE by ID is implemented.
        // TODO: Change Item id to 5 and /items/5 to /items/4
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
