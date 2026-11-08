package template.service;

import com.google.common.annotations.VisibleForTesting;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import template.exception.ItemIdAlreadySetException;
import template.exception.ItemIdMismatchException;
import template.repository.ItemEntity;
import template.repository.ItemRepository;
import template.service.util.ItemIdGenerator;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository repository;

    private final ModelMapper mapper;

    public Optional<Item> get(ItemId id) {
        return repository.findById(id.value()).map(this::toDomainObject);
    }

    public List<Item> get() {
        return repository.findAll().stream().map(this::toDomainObject).toList();
    }

    @Transactional
    public void create(Item item) {
        if (item.getId() != null) {
            throw new ItemIdAlreadySetException(item.getId());
        }

        repository.create(toEntity(item.toBuilder().id(ItemIdGenerator.fromName(item.getName())).build()));
    }

    @Transactional
    public void upsert(Item item) {
        var expectedId = ItemIdGenerator.fromName(item.getName());

        if (!expectedId.equals(item.getId())) {
            throw new ItemIdMismatchException(expectedId, item.getId());
        }

        repository.upsert(toEntity(item));
    }

    public void delete(ItemId id) {
        repository.deleteById(id.value());
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
