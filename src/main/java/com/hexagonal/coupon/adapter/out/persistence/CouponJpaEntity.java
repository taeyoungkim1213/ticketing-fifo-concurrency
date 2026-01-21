package com.hexagonal.coupon.adapter.out.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TICKET")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class CouponJpaEntity extends BaseTimeJpaEntity {

    @Id
    @Column(name = "TICKET_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TICKET_NAME")
    private String name;

    @Column(name = "TICKET_PRICE")
    private int price;

    @Column(name = "TICKET_TOTAL_QUANTITY")
    private int totalQuantity;

    @Column(name = "TICKET_REMAIN_QUANTITY")
    private int remainQuantity;

    static CouponJpaEntity withId(Long ticketId) {
        return new CouponJpaEntity(ticketId, null, 0, 0, 0);
    }
}
