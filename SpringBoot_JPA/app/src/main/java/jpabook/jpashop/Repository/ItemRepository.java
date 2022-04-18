package jpabook.jpashop.Repository;

import jpabook.jpashop.Domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em; //@RequiredArgsConstructor 덕분에 자동으로 관계 주입 해줌

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

    public List<Item> findByName(String name) {
        return em.createQuery("select i from Item i where i.name = :itemName", Item.class)
                .setParameter("itemName", name)
                .getResultList();
    }

}
