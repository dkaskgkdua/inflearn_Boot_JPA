package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * !!...데이터를 수정하는 방법...!!
     *
     * 변경감지 기능 사용
     * repo 에서 update를 따로 호출 안해도 저장이 됨.
     * 이유는 영속성 컨텍스트에서 엔티티를 다시 조회 한 후에 데이터를 수정하게 되면
     * 트랜잭션 커밋 시점에 변경 감지(Dirty Checking)이 동작해서 UPDATE 쿼리가 실행됨
     *
     * merge의 경우 jpa에서 아래와 같은 코드 기능을 해주는 것이다.
     * ex) Item mergeItem = em.merge(item);
     * merge는 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능이다.
     * 그리고 그 엔티티를 반환한다. 그리고 그 엔티티는 영속성 컨텍스트에서 관리한다.
     * 이후 뭔가 작업하려면 반환된 값을 사용해야 한다는 말이다.
     *
     * 주의!!
     * 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경가능하지만, 병합을 사용하면
     * 모든 속성이 변경된다. 병합시 값이 없으면 'null'로 업데이트될 위험도 있음..
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
