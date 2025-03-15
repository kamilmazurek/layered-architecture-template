package template.domain;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemsService {

    public List<Item> getItems() {
        var itemA = Item.builder().id(1).name("Item A").build();
        var itemB = Item.builder().id(2).name("Item B").build();
        var itemC = Item.builder().id(3).name("Item C").build();

        return List.of(itemA, itemB, itemC);
    }

}
