package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    /**
     * N + 1 문제
     * order 조회 1번. 결과 수 N개
     * order -> member 지연 로딩조회 N번
     * order -> delivery 지연 로딩조회 N번
     * order 결과가 2개면 1 + 2 + 2번이 실행이 됨
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
        return result;
    }

    /**
     * fetch join 으로 성능 최적화
     * 쿼리 1번으로 전부 다들고 옴.
     * 실무에서 자주 사용하는 기법
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
        return result;
    }
    /** V3와 V4 중 선택 권장 순서
     *  1. 우선 엔티티를 DTO로 변환하는 방법을 선택
     *  2. 필요하면 패치 조인으로 성능을 최적화
     *  3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용
     *  4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 JDBC Template로 직접 쿼리작성
     */

    /**
     * v3 와 비교해서
     * 원하는 컬럼만 뽑아내서 성능은 앞설 수 있지만
     * 재사용하기가 어려움(v3는 공용으로 사용 가능)
     * 그리고 jpql 이 좀 지저분함.(new 명령어 사용)
     * * repository가 화면을 의존하게 되는 현상이 생김...
     * * 그래서 특정한 목적이 있는 것은 공용 repository가 아닌 별도의
     * * repository로 구성 -> OrderSimpleQueryRepository
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
