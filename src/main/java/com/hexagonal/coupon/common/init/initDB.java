package com.hexagonal.coupon.common.init;

import com.hexagonal.coupon.application.port.out.CreateCouponPort;
import com.hexagonal.coupon.application.port.out.CreateMemberPort;
import com.hexagonal.coupon.domain.Coupon;
import com.hexagonal.coupon.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class initDB {

    private final CreateMemberPort createMemberPort;
    private final CreateCouponPort createCouponPort;

    @PostConstruct // @Component + @PostConstruct = 빈 생성 후 자동 실행
    public void init() {
        for (int i = 1; i <= 100; i++) {
            createMemberPort.createMember(new Member((long) i, "이름" + i, "010-1111-1111", "test@naver.com"));
        }

        for (int i = 1; i <= 10; i++) {
            createCouponPort.createCoupon(new Coupon((long) i, "티켓" + i, 1000, 100, 100));
        }

        createCouponPort.createCoupon(new Coupon(11L, "티켓11", 10000, 1000, 1000));
    }
}
