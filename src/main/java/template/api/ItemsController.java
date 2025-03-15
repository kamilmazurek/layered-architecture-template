package template.api;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import lombok.AllArgsConstructor;
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

    private final Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @Override
    public ResponseEntity<List<ItemDTO>> getItems() {
        return ResponseEntity.ok(service.getItems().stream().map(this::toDTO).toList());
    }

    private ItemDTO toDTO(Item item) {
        return mapper.map(item, ItemDTO.class);
    }

}
