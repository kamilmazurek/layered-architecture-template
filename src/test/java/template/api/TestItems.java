package template.api;

import template.api.model.ItemDTO;

import java.util.List;

public class TestItems {

    public static List<ItemDTO> createTestItems() {
        var itemA = new ItemDTO().id(1).content("Item A");
        var itemB = new ItemDTO().id(2).content("Item B");
        var itemC = new ItemDTO().id(3).content("Item C");

        return List.of(itemA, itemB, itemC);
    }

}
