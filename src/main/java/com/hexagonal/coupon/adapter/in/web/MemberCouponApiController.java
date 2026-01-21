package com.hexagonal.coupon.adapter.in.web;

import com.hexagonal.coupon.application.port.in.CreateMemberCouponCommand;
import com.hexagonal.coupon.application.port.in.CreateMemberCouponResponse;
import com.hexagonal.coupon.application.port.in.CreateMemberCouponUseCase;
import com.hexagonal.coupon.application.port.in.UseMemberCouponCommand;
import com.hexagonal.coupon.application.port.in.UseMemberCouponResponse;
import com.hexagonal.coupon.application.port.in.UseMemberCouponUseCase;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class MemberCouponApiController {

    private final UseMemberCouponUseCase useMemberCouponUseCase;
    private final CreateMemberCouponUseCase createMemberCouponUseCase;

    @ApiOperation("회원티켓 발급")
    @PostMapping("/members/{memberId}/coupons/{ticketId}")
    CreateMemberCouponResponse createMemberCoupon(@Parameter(description = "회원 ID") @PathVariable Long memberId,
                                                  @Parameter(description = "티켓 ID") @PathVariable Long ticketId) {
        return createMemberCouponUseCase.createMemberCoupon(new CreateMemberCouponCommand(memberId, ticketId));
    }

    @ApiOperation("회원티켓 사용")
    @PatchMapping("/members/{memberId}/coupons/{ticketId}")
    UseMemberCouponResponse useMemberCoupon(@Parameter(description = "회원 ID") @PathVariable Long memberId,
                                            @Parameter(description = "티켓 ID") @PathVariable Long ticketId) {
        return useMemberCouponUseCase.useMemberCoupon(new UseMemberCouponCommand(memberId, ticketId));
    }
}
