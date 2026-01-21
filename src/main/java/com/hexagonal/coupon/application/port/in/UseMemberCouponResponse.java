package com.hexagonal.coupon.application.port.in;

import com.hexagonal.coupon.common.SelfValidating;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
public class UseMemberCouponResponse extends SelfValidating<UseMemberCouponResponse> {

    @NotNull
    private final Long memberTicketId;
    @NotNull
    private final Long ticketId;
    @NotNull
    private final LocalDateTime useDateTime;

    public UseMemberCouponResponse(Long memberTicketId, Long ticketId, LocalDateTime useDateTime) {
        this.memberTicketId = memberTicketId;
        this.ticketId = ticketId;
        this.useDateTime = useDateTime;
        this.validateSelf();
    }
}
