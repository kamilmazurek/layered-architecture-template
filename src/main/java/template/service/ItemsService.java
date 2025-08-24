package template.service;

import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import template.api.model.ItemDTO;
import template.persistence.ItemEntity;
import template.persistence.ItemsRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemsService {

    private final ItemsRepository repository;

    private final ModelMapper mapper = new ModelMapper();

    public List<ItemDTO> getItems() {
        return repository.findAll().stream().map(this::toDomainObject).map(this::toDTO).toList();
    }

    public Optional<ItemDTO> getItem(Long id) {
        return repository.findById(id).map(this::toDomainObject).map(this::toDTO);
    }

    public void putItem(Long itemId, ItemDTO itemDTO) {
        var item = toDomainObject(itemDTO);
        item.setId(itemId);
        repository.save(toEntity(item));
    }

    @VisibleForTesting
    ItemDTO toDTO(Item item) {
        return mapper.map(item, ItemDTO.class);
    }

    @VisibleForTesting
    Item toDomainObject(ItemDTO itemDTO) {
        return mapper.map(itemDTO, Item.class);
    }

    @VisibleForTesting
    Item toDomainObject(ItemEntity itemEntity) {
        return mapper.map(itemEntity, Item.class);
    }

    @VisibleForTesting
    ItemEntity toEntity(Item item) {
        return mapper.map(item, ItemEntity.class);
    }

}

