package org.docksidestage.handson.exercise;

import javax.annotation.Resource;

import org.dbflute.bhv.readable.CBCall;
import org.dbflute.cbean.result.ListResultBean;
import org.docksidestage.handson.dbflute.cbean.MemberCB;
import org.docksidestage.handson.dbflute.exbhv.MemberBhv;
import org.docksidestage.handson.dbflute.exentity.Member;
import org.docksidestage.handson.unit.UnitContainerTestCase;

// #1on1: ビルドツール (2026/06/10)
// 推移的依存を配慮しつつ...
// o 単純にダウンロードしてくれる
// o バージョンを解決してくれる
// 仕組みの話、Mavenセントラルサーバー。

// #1on1: テストデータの用意の仕方 (2026/06/10)
// つじさんのプロジェクトではArrangeでデータめっちゃ突っ込んでるけど？
//
// A. ReplaceSchemaである程度のテストデータ用意していて...足りなければ少しArrangeで追加
// A+. 事前登録ではないけどツール使ってArrangeで簡単に登録できるように
// B. 全くデフォルトのテストデータは用意せず...すべてArrangeで自前で用意する
// C. AとBのあいのこ
// D. そもそもUnitTestをDBを使わずmockする
//
// テストデータを作るツールを使っている？ by つじさん
// それって「ReplaceSchemaである程度のテストデータ用意していて」に近い？
// 登録処理が事前なのか？Arrangeなのか？の違いで合って、用意はどっちもされている。


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
    
    /**
     * 会員名称がSで始まる会員を検索 (これはタイトル、この中にも要件が含まれている)

        会員名称の昇順で並べる (これは実装要件、Arrange or Act でこの通りに実装すること)
        (検索結果の)会員名称がSで始まっていることをアサート (これはアサート要件、Assert でこの通りに実装すること)
        "該当テストデータなし" や "条件間違い" 素通りgreenにならないように素通り防止を (今後ずっと同じ)
     * @throws Exception
     */
    public void test_memberNameStartsWithS() throws Exception {
        // ## Arrange ##
        
        // ## Act ##
    	ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
			cb.query().setMemberName_LikeSearch("S", op -> op.likePrefix());
			cb.query().addOrderBy_MemberName_Asc();
		});
    
        // ## Assert ##
    	for (Member member : memberList) {
			String memberName = member.getMemberName();
			log(memberName);
			assertTrue(memberName.startsWith("S"));
		}
    }
}
