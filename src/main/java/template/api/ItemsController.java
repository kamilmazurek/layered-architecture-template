package template.api;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import template.api.model.ItemDTO;
import template.domain.Item;
import template.domain.ItemsService;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class ItemsController implements ItemsApi {

    private final ItemsService service;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public ResponseEntity<ItemDTO> getItem(Long id) {
        return service.getItem(id).map(this::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ItemDTO>> getItems() {
        return ResponseEntity.ok(service.getItems().stream().map(this::toDTO).toList());
    }

    @Override
    public ResponseEntity<Void> putItem(Long itemId, ItemDTO itemDTO) {
        if (!hasValidId(itemId, itemDTO)) {
            return ResponseEntity.badRequest().build();
        }

        service.putItem(itemId, toDomainObject(itemDTO));
        return ResponseEntity.ok().build();
    }

    private boolean hasValidId(Long itemId, ItemDTO itemDTO) {
        return itemDTO.getId() == null || Objects.equals(itemId, itemDTO.getId());
    }

    private ItemDTO toDTO(Item item) {
        return mapper.map(item, ItemDTO.class);
    }

    private Item toDomainObject(ItemDTO itemDTO) {
        return mapper.map(itemDTO, Item.class);
    }


}
