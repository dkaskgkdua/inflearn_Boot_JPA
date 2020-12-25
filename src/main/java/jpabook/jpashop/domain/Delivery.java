package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    // ORDINAL - 숫자 형식으로 들어감(1.. 2..), STRING - 문자형식으로 들어감
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}
