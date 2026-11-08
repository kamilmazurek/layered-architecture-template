package template.api;

import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import template.api.model.ItemDTO;
import template.service.Item;
import template.service.ItemId;
import template.service.ItemService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class ItemController implements ItemsApi {

    private final ItemService service;

    private final ModelMapper mapper;

    @Override
    public ResponseEntity<ItemDTO> getItem(String id) {
        return findItem(id).map(this::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ItemDTO>> getItems() {
        return ResponseEntity.ok(service.get().stream().map(this::toDTO).toList());
    }

    @Override
    public ResponseEntity<Void> postItem(ItemDTO itemDTO) {
        if (itemDTO.getId() != null) {
            return ResponseEntity.badRequest().build();
        }

        service.create(toDomainObject(itemDTO));
        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<Void> putItem(String itemId, ItemDTO itemDTO) {
        if (!hasValidId(itemId, itemDTO)) {
            return ResponseEntity.badRequest().build();
        }

        service.upsert(toDomainObject(itemDTO));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteItem(String id) {
        if (findItem(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.delete(new ItemId(id));
        return ResponseEntity.ok().build();
    }

    private Optional<Item> findItem(String id) {
        return Optional.ofNullable(id).filter(ItemId::isValid).map(ItemId::new).flatMap(service::get);
    }

    private boolean hasValidId(String itemId, ItemDTO itemDTO) {
        return itemDTO.getId() != null && Objects.equals(itemId, itemDTO.getId());
    }

    @VisibleForTesting
    ItemDTO toDTO(Item item) {
        return mapper.map(item, ItemDTO.class);
    }

    @VisibleForTesting
    Item toDomainObject(ItemDTO itemDTO) {
        return mapper.map(itemDTO, Item.class);
    }

}
