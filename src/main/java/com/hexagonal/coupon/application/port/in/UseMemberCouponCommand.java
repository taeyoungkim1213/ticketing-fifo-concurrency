package com.hexagonal.coupon.application.port.in;

import com.hexagonal.coupon.common.SelfValidating;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class UseMemberCouponCommand extends SelfValidating<UseMemberCouponCommand> {

    @NotNull
    private final Long memberId;
    @NotNull
    private final Long ticketId;

    public UseMemberCouponCommand(Long memberId, Long ticketId) {
        this.memberId = memberId;
        this.ticketId = ticketId;
        this.validateSelf();
    }
}
