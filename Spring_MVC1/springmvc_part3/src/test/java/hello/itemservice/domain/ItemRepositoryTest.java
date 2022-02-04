package hello.itemservice.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ItemRepositoryTest {
    ItemRepository itemRepository = new ItemRepository();

    @AfterEach
    void afterEach() {
        itemRepository.clearStore();
    }

    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId());
        Assertions.assertThat(findItem).isEqualTo(savedItem);

    }

    @Test
    void findAll() {
        //given
        Item itemA = new Item("itemA", 10000, 10);
        Item itemB = new Item("itemB", 20000, 20);

        itemRepository.save(itemA);
        itemRepository.save(itemB);

        //when
        List<Item> result = itemRepository.findAll();

        //then
        Assertions.assertThat(result).contains(itemA, itemB);

    }

    @Test
    void updateItem() {
        //given
        Item itemA = new Item("itemA", 10, 10000);
        itemRepository.save(itemA);
        Long itemId = itemA.getId();

        //when
        Item itemB = new Item("itemB", 20000, 20);
        itemRepository.update(itemA.getId(), itemB);

        //then
        Item updatedItem = itemRepository.findById(itemId);
        Assertions.assertThat(updatedItem.getItemName()).isEqualTo(itemB.getItemName());
        Assertions.assertThat(updatedItem.getPrice()).isEqualTo(itemB.getPrice());
        Assertions.assertThat(updatedItem.getQuantity()).isEqualTo(itemB.getQuantity());


    }
}
