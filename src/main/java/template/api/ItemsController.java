package template.api;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import template.api.model.ItemDTO;
import template.service.ItemsService;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class ItemsController implements ItemsApi {

    private final ItemsService service;

    @Override
    public ResponseEntity<ItemDTO> getItem(Long id) {
        return service.getItem(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ItemDTO>> getItems() {
        return ResponseEntity.ok(service.getItems().stream().toList());
    }

    @Override
    public ResponseEntity<Void> putItem(Long itemId, ItemDTO itemDTO) {
        if (!hasValidId(itemId, itemDTO)) {
            return ResponseEntity.badRequest().build();
        }

        service.putItem(itemId, itemDTO);
        return ResponseEntity.ok().build();
    }

    private boolean hasValidId(Long itemId, ItemDTO itemDTO) {
        return itemDTO.getId() == null || Objects.equals(itemId, itemDTO.getId());
    }

}
