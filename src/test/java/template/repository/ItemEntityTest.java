package template.repository;

import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static template.service.ItemId.MAX_LENGTH;

class ItemEntityTest {

    @Test
    void shouldHaveIdColumnLengthMatchingItemIdMaxLength() throws NoSuchFieldException {
        //given ID column definition
        var column = ItemEntity.class.getDeclaredField("id").getAnnotation(Column.class);

        //when column length is read
        var length = column.length();

        //then it matches item ID max length
        assertEquals(MAX_LENGTH, length);
    }

}
