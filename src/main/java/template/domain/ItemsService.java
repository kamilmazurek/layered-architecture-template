package template.domain;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import template.persistence.ItemEntity;
import template.persistence.ItemsRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemsService {

    private final ItemsRepository repository;

    private final ModelMapper mapper = new ModelMapper();

    public List<Item> getItems() {
        return repository.findAll().stream().map(this::toDomainObject).toList();
    }

    private Item toDomainObject(ItemEntity itemEntity) {
        return mapper.map(itemEntity, Item.class);
    }

}

