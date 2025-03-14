package template.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import template.api.model.ItemDTO;

import java.util.List;

@RestController
public class ItemsController implements ItemsApi {

    @Override
    public ResponseEntity<List<ItemDTO>> getItems() {
        var itemA = new ItemDTO().id(1).content("Item A");
        var itemB = new ItemDTO().id(2).content("Item B");
        var itemC = new ItemDTO().id(3).content("Item C");

        return ResponseEntity.ok(List.of(itemA, itemB, itemC));
    }

}
