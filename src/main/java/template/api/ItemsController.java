package template.api;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import template.api.model.ItemDTO;
import template.domain.Item;
import template.domain.ItemsService;

import java.util.List;

@RestController
@AllArgsConstructor
public class ItemsController implements ItemsApi {

    private final ItemsService service;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public ResponseEntity<List<ItemDTO>> getItems() {
        return ResponseEntity.ok(service.getItems().stream().map(this::toDTO).toList());
    }

    private ItemDTO toDTO(Item item) {
        return mapper.map(item, ItemDTO.class);
    }

}
