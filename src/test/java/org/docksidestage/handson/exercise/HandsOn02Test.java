package org.docksidestage.handson.exercise;

import javax.annotation.Resource;

import org.dbflute.bhv.readable.CBCall;
import org.docksidestage.handson.dbflute.cbean.MemberCB;
import org.docksidestage.handson.dbflute.exbhv.MemberBhv;
import org.docksidestage.handson.unit.UnitContainerTestCase;

public class HandsOn02Test extends UnitContainerTestCase {
    @Resource
    private MemberBhv memberBhv;

    public void test_existsTestData() throws Exception {
        // ## Arrange ##

        // ## Act ##
        int count = memberBhv.selectCount(cb -> {});

        // ## Assert ##
        assertTrue(count > 0);
    }
    
    public void test_memberNameStartsWithS() throws Exception {
        // ## Arrange ##
        
        // ## Act ##

    
        // ## Assert ##
    }
}
