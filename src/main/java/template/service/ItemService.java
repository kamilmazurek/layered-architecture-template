package template.service;

import com.google.common.annotations.VisibleForTesting;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import template.exception.ItemIdAlreadySetException;
import template.repository.ItemEntity;
import template.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository repository;

    private final ModelMapper mapper;

    public Optional<Item> get(Long id) {
        return repository.findById(id).map(this::toDomainObject);
    }

    public List<Item> get() {
        return repository.findAll().stream().map(this::toDomainObject).toList();
    }

    @Transactional
    public void create(Item item) {
        if (item.getId() != null) {
            throw new ItemIdAlreadySetException(item.getId());
        }
        repository.create(toEntity(item));
    }

    @Transactional
    public void upsert(Long itemId, Item item) {
        repository.upsert(itemId, toEntity(item));
    }

    public void delete(Long id) {
        repository.deleteById(id);
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

