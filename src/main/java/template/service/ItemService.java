package template.service;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import template.api.model.ItemDTO;
import template.exception.ItemIdAlreadySetException;
import template.repository.ItemEntity;
import template.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ItemRepository repository;

    private final ModelMapper mapper;

    public List<ItemDTO> getItems() {
        return repository.findAll().stream().map(this::toDomainObject).map(this::toDTO).toList();
    }

    public Optional<ItemDTO> getItem(Long id) {
        return repository.findById(id).map(this::toDomainObject).map(this::toDTO);
    }

    @Transactional
    public void postItem(ItemDTO itemDTO) {
        var item = toDomainObject(itemDTO);

        if (item.getId() != null) {
            throw new ItemIdAlreadySetException(item.getId());
        }

        // Works with H2 for development and testing, but may require adjustments for production depending on the database
        entityManager.createNativeQuery("SELECT 1 FROM ITEM_SEQ_LOCK FOR UPDATE").getResultList();
        var itemEntity = toEntity(item);
        repository.save(itemEntity);
    }

    @Transactional
    public void putItem(Long itemId, ItemDTO itemDTO) {
        var item = toDomainObject(itemDTO);
        // Works with H2 for development and testing, but may require adjustments for production depending on the database
        entityManager.createNativeQuery("SELECT 1 FROM ITEM_SEQ_LOCK FOR UPDATE").getResultList();
        var mergeQuery = "MERGE INTO item (id, name) KEY(id) VALUES (?, ?)";
        entityManager.createNativeQuery(mergeQuery).setParameter(1, itemId).setParameter(2, item.getName()).executeUpdate();
        syncSequence(itemId);
    }

    private void syncSequence(Long insertedId) {
        var currentSeqValQuery = "SELECT CAST(BASE_VALUE AS BIGINT) FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_NAME = 'ITEM_SEQ'";
        var currentSeqVal = (Number) entityManager.createNativeQuery(currentSeqValQuery).getSingleResult();

        if (insertedId < currentSeqVal.longValue()) {
            return;
        }

        long newStart = insertedId + 1;
        entityManager.createNativeQuery("ALTER SEQUENCE ITEM_SEQ RESTART WITH " + newStart).executeUpdate();
    }

    public void deleteItem(Long id) {
        repository.deleteById(id);
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

