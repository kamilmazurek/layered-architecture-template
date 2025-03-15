package template.util;

import template.api.model.ItemDTO;
import template.domain.Item;

import java.util.List;

public class TestItems {

    public static List<ItemDTO> createTestItemDTOs() {
        var itemA = new ItemDTO().id(1).name("Item A");
        var itemB = new ItemDTO().id(2).name("Item B");
        var itemC = new ItemDTO().id(3).name("Item C");

        return List.of(itemA, itemB, itemC);
    }

    public static List<Item> createTestItems() {
        var itemA = Item.builder().id(1).name("Item A").build();
        var itemB = Item.builder().id(2).name("Item B").build();
        var itemC = Item.builder().id(3).name("Item C").build();

        return List.of(itemA, itemB, itemC);
    }

}
