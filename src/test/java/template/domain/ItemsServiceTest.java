package template.domain;

import org.junit.jupiter.api.Test;
import template.persistence.ItemsRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static template.util.TestItems.createTestItemEntities;
import static template.util.TestItems.createTestItems;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;

public class ItemsServiceTest {

    @Test
    void shouldReturnItems() {
        //given repository
        var repository = mock(ItemsRepository.class);
        when(repository.findAll()).thenReturn(createTestItemEntities());

        //and service
        var service = new ItemsService(repository);

        //when items are requested
        var items = service.getItems();

        //then items are returned
        assertEquals(createTestItems(), items);

        //and repository was involved in retrieving the data
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldPutItem() {
        //given repository
        var repository = mock(ItemsRepository.class);

        //and service
        var service = new ItemsService(repository);

        //and item
        var item = Item.builder().name("Item A").build();

        //when item is put
        service.putItem(1L, item);

        //then item has been put to repository
        verify(repository, times(1)).save(invokeMethod(service, "toEntity", item));
    }

}
