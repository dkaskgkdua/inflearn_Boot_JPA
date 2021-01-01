package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// JOINED = 가장 정규화된 스타일, SINGLETABLE = 한 테이블에  다 넣음,
// TABLE_PER_CLASS = 앨범, 북, 무비 테이블과 같이 나누는 것
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
    /**
     * 실무에서는 @ManyToMany를 사용하지 말자
     * ManyToMany가 편한 것 같지만, 중간 테이블(CATEGORY_ITEM)에 컬럼을
     * 추가할 수없고, 세밀하게 쿼리를 실행하기 어렵기 때문에 실무에서
     * 사용하기는 한계가 있다.
     * 중간 엔티티를 만들고 @ManyToOne, @OneToMany로 매핑해서 사용하자.
     * 위에건 사용가능하다는 예제임.
     * */

    //== 비즈니스 로직 == //

    /**
     * stock 증가
     * @param quantity
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     * @param quantity
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
