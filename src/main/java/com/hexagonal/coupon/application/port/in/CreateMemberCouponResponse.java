package com.hexagonal.coupon.application.port.in;

import com.hexagonal.coupon.common.SelfValidating;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
public class CreateMemberCouponResponse extends SelfValidating<CreateMemberCouponResponse> {

    @NotNull
    private final Long memberTicketId;
    @NotNull
    private final Long ticketId;
    @NotNull
    private final LocalDateTime createDateTime;

    public CreateMemberCouponResponse(Long memberTicketId, Long ticketId, LocalDateTime createDateTime) {
        this.memberTicketId = memberTicketId;
        this.ticketId = ticketId;
        this.createDateTime = createDateTime;
        this.validateSelf();
    }
}
