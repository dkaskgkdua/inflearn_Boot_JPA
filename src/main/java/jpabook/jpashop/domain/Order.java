package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    /**
     * ManyToOne(fetch = FetchType.EAGER)  즉시로딩임.
     * -> Order를 조회하는 시점에 Member도 같이 조회하겠다는 것
     * JPQL을 실행할 때 N+1 문제가 자주 발생함.
     * 해서 실무에서는 모든 연관관계를 지연로딩으로 해야함(LAZY)
     * 연관된 엔티티를 함께 DB에서 조회해야 하면, fetch join 또는 엔티티 그래프 기능을 사용
     * @XToOne 의 기본 패치는 EAGER임
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 컬렉션은 필드에서 초기화하는게 좋음.
     * null 문제로부터 안전하고
     * 하이버네이트는 엔티티를 영속화할 때, 컬랙션을 감싸서 하이버네이트가 제공하는
     * 내장 컬렉션으로 변경한다. 만약 임의의 메서드에서 잘못 생성하면
     * 문제가 발생할 수 있다.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    /**
     * 스프링 부트의 설정으로 엔티티(카멜) -> 테이블(언더스코어)로 변환해준다.
     * 1. 카멜 -> 언더스코어
     * 2. .(점) -> 언더스코어
     * 3. 대문자 -> 소문자
     * 혹시나 설정을 바꾸고 싶다면
     * 1. 논리적 생성: 명시적으로 컬럼, 테이블명을 직접 적지 않으면 implicitNamingStrategy 사용
     *  'spring.jpa.hibernate.naming.implicit-strategy'
     * 2. 물리명 적용:
     *  'spring.jpa.hibernate.naming.physical-strategy' 모든 논리명에 적용
     *
     */
    private LocalDateTime orderDate;    // 주문시간


    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [OREDER, CANCEL]


    // == 연관관계 메서드 ==
    // 양방향일 경우 써주면 편리함
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    // == 연관관계 메서드2 ==
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
