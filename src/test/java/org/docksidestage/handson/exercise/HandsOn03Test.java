package org.docksidestage.handson.exercise;

import java.time.LocalDate;

import javax.annotation.Resource;

import org.dbflute.cbean.result.ListResultBean;
import org.docksidestage.handson.dbflute.exbhv.MemberBhv;
import org.docksidestage.handson.dbflute.exbhv.MemberStatusBhv;
import org.docksidestage.handson.dbflute.exentity.Member;
import org.docksidestage.handson.dbflute.exentity.MemberAddress;
import org.docksidestage.handson.dbflute.exentity.MemberSecurity;
import org.docksidestage.handson.dbflute.exentity.MemberStatus;
import org.docksidestage.handson.unit.UnitContainerTestCase;

public class HandsOn03Test extends UnitContainerTestCase {

    @Resource
    private MemberBhv memberBhv;

    @Resource
    private MemberStatusBhv memberStatusBhv;

    public void test_silver_one() throws Exception {
        // arrange

        // act
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
            cb.query().setBirthdate_LessEqual(LocalDate.of(1968, 1, 1));
            cb.query().addOrderBy_Birthdate_Asc();
            cb.query().queryMemberStatus().innerJoin();
        });

        // assert
        memberList.forEach(member -> {
            log(member);
            LocalDate birthDate = member.getBirthdate();
            LocalDate targetDate = LocalDate.of(1968, 1, 1);
            assertTrue(birthDate.isEqual(targetDate) || birthDate.isBefore(targetDate));
        });
    }

    public void test_sliver_second() throws Exception {
        // arrange

        // act
//        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
//            cb.query().addOrderBy_Birthdate_Desc();
//            cb.query().addOrderBy_MemberId_Asc();
//            cb.query().queryMemberStatus().innerJoin();
//            cb.query().queryMemberSecurityAsOne().innerJoin();
//        });
//
//        // assert
//        memberList.forEach(member -> {
//            log(member);
//            assertTrue(member.getMemberStatus().isPresent() && member.getMemberSecurityAsOne().isPresent());
//        });

        // memo: わからずだったので回答を写経
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
            cb.setupSelect_MemberStatus();
            cb.setupSelect_MemberSecurityAsOne();
            cb.query().addOrderBy_Birthdate_Desc();
            cb.query().addOrderBy_MemberId_Asc();
        });

        assertHasAnyElement(memberList);
        memberList.forEach(member -> {
            assertTrue(member.getMemberStatus().isPresent());
            assertTrue(member.getMemberSecurityAsOne().isPresent()); // 1:1の時はAsOneがつく???
        });
    }
}
