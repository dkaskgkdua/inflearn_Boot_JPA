package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;
    // jpa 스펙 상 엔티티나 임베디드 타입은 자바 기본 생성자를
    // public, protected까지 허용 (리플렉션 같은 기술을 사용하게 지원)
    protected Address() {
    }
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}

/**
 * 값 타입은 변경이 되면 안됨.
 * 생성할 때만 값이 세팅이 되고 Setter 제공 안하는게 좋음.
 * */

