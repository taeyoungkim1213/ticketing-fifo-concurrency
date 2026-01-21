package com.hexagonal.coupon.adapter.out.persistence;

import com.hexagonal.coupon.application.port.out.CreateMemberCouponPort;
import com.hexagonal.coupon.application.port.out.DeleteMemberCouponPort;
import com.hexagonal.coupon.application.port.out.FindCouponOfMemberPort;
import com.hexagonal.coupon.application.port.out.LoadAllCouponsOfMemberPort;
import com.hexagonal.coupon.application.port.out.UseMemberCouponPort;
import com.hexagonal.coupon.domain.MemberCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class MemberCouponPersistenceAdapter implements LoadAllCouponsOfMemberPort, CreateMemberCouponPort, FindCouponOfMemberPort, UseMemberCouponPort, DeleteMemberCouponPort {

    private final MemberCouponRepository memberCouponRepository;
    private final MemberCouponMapper memberCouponMapper;

    @Override
    public List<MemberCoupon> loadAllCouponsOfMember(Long memberId) {
        return memberCouponRepository.findAllByMemberId(memberId)
                .stream()
                .map(memberCouponMapper::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public MemberCoupon createMemberCoupon(Long memberId, Long ticketId) {
        MemberCouponJpaEntity memberCouponJpaEntity = MemberCouponJpaEntity.withMemberIdAndCouponId(memberId, ticketId);
        memberCouponRepository.save(memberCouponJpaEntity);
        return memberCouponMapper.mapToDomainEntity(memberCouponJpaEntity);
    }

    @Override
    public Optional<MemberCoupon> findCouponOfMember(Long memberId, Long ticketId) {
        Optional<MemberCouponJpaEntity> memberCoupon = memberCouponRepository.findByMemberIdAndCouponId(memberId, ticketId);
        return memberCoupon.map(memberCouponMapper::mapToDomainEntity);
    }

    @Override
    public void useMemberCoupon(MemberCoupon memberCoupon) {
        MemberCouponJpaEntity memberCouponJpaEntity = memberCouponMapper.mapToJpaEntity(memberCoupon);
        memberCouponRepository.save(memberCouponJpaEntity);
    }

    @Override
    public void deleteAllByCouponId(Long ticketId) {
        memberCouponRepository.deleteAllByCouponId(ticketId);
    }
}
