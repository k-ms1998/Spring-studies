package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.ItemRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void save() {
        Item item = new Item("A");
        Item saveItem = itemRepository.save(item);
        /**
         * Scenario #1: Item.id -> Long && @Generated Value
         * => insert into item (id) values (103);
         *
         * Scenario #2: Item.id -> String w/o Persistable
         * => 1. select item0_.id as id1_0_0_ from item item0_ where item0_.id='A';
         * => 2. insert into item (id) values ('A');
         *
         * Scenario #3: Item.id -> String w/ Persistable
         * => 1. insert into item (id) values ('A');
         */
    }

}