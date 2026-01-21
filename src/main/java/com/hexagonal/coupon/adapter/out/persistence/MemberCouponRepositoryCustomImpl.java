package com.hexagonal.coupon.adapter.out.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.hexagonal.coupon.adapter.out.persistence.QCouponJpaEntity.*;
import static com.hexagonal.coupon.adapter.out.persistence.QMemberCouponJpaEntity.*;
import static com.hexagonal.coupon.adapter.out.persistence.QMemberJpaEntity.*;

@RequiredArgsConstructor
class MemberCouponRepositoryCustomImpl implements MemberCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberCouponJpaEntity> findAllByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(memberCouponJpaEntity)
                .join(memberCouponJpaEntity.member, memberJpaEntity).fetchJoin()
                .join(memberCouponJpaEntity.coupon, couponJpaEntity).fetchJoin()
                .where(memberIdEq(memberId))
                .fetch();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return (memberId != null) ? memberCouponJpaEntity.member.id.eq(memberId) : null;
    }

    @Override
    public Optional<MemberCouponJpaEntity> findByMemberIdAndCouponId(Long memberId, Long ticketId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(memberCouponJpaEntity)
                .join(memberCouponJpaEntity.member, memberJpaEntity).fetchJoin()
                .where(memberIdEq(memberId), couponIdEq(ticketId))
                .fetchOne());
    }

    private BooleanExpression couponIdEq(Long ticketId) {
        return (ticketId != null) ? memberCouponJpaEntity.coupon.id.eq(ticketId) : null;
    }

    @Override
    public void deleteAllByCouponId(Long ticketId) {
        queryFactory.delete(memberCouponJpaEntity)
                .where(memberCouponJpaEntity.coupon.id.eq(ticketId))
                .execute();
    }
}
