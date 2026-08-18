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

    /**
     * [1] 会員名称がSで始まる1968年1月1日以前に生まれた会員を検索

        会員ステータスも取得する
        生年月日の昇順で並べる
        会員が1968/01/01以前であることをアサート

        ※"以前" の解釈は、"その日ぴったりも含む" で。 
     */
    public void test_silver_one() throws Exception {
        // arrange

        // act
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
            // TODO tsuji 会員名称がSで始まる、の条件がない by jflute (2026/08/18)
            
            // #1on1: 1968年1月1日以前の条件Good, "その日ぴったりも含む" なのでLessEqualでOK (2026/08/18)
            cb.query().setBirthdate_LessEqual(LocalDate.of(1968, 1, 1));

            // #1on1: 生年月日の昇順で並べる、OK (2026/08/18)
            cb.query().addOrderBy_Birthdate_Asc();

            // #1on1: 会員ステータスも取得する？ (2026/08/18)
            // select mb.*    // ここに status のカラムが入ってない
            //   from MEMBER mb
            //     inner join MEMBER_STATUS stat
            //       on mb.MEMBER_STATUS_CODE = stat.MEMBER_STATUS_CODE
            //
            // 関連テーブルをデータ取得する(Java側に持ってくる)時、
            // joinだけでは持って来れない。joinはSQL上で関連テーブルを持ってきただけ。
            // joinだけしてselect句に入れなかったら、結局SQL上で捨ててる。
            // なので、DBFluteのConditionBeanで、select句に並べるというメソッド呼ばないと。
            //
            // e.g.
            //  cb.setupSelect_MemberStatus();
            //   ↓
            //  select mb.*, stat.*    // ここに status のカラムが入るようになる
            //    from MEMBER mb
            //      inner join MEMBER_STATUS stat
            //        on mb.MEMBER_STATUS_CODE = stat.MEMBER_STATUS_CODE
            // ConditionBeanで言うと、↓のinnerJoin()は不要。setupSelectだけでOK。
            //  cb.query().queryMemberStatus().innerJoin();
            // select句に並べると言うことは、joinは絶対に必要になるということなので。
            //
            // joinは他の句(select, where, order by)のための手段(or 準備)であって、
            // joinだけして嬉しいことってほとんどない、と言える。
            // (inner joinで絞り込みを発生させることもできるが、通常のケースではあまり活用しない)
            // (絞り込みするならあくまでwhere句で明示的に絞る方が好まれやすい)
            // そう考えると、joinは目的にはならなくて、他の目的(select, where, order by)の手段になる。
            //
            // $ActiveRecordの影響を受けてしまった
            // ActiveRecordでも、joinした上で何かしらしてくれているはず。それを知っておくことも大事かなと。
            // $lazyloadのケース
            // DBFluteはlazyloadをサポートしていないので、合致するものがない。
            //
            // 事前にjoinしてデータ取得するケース:
            // eager(load)の指定が、ほぼsetupSelectに近そう。
            // $join==eager(load)みたいなイメージになっちゃってた。
            // まさしく、join自体は目的にならなくて...のお話に通じるところ。
            // eager(load)はjoinって覚えちゃう人はいるけど...
            // eager(load)はselect(句)って覚えちゃう人はいなさそう...
            // なぜ？
            // joinの圧が強い。二つのテーブルをがっちゃんこして何かを生み出すという強い機能。
            // でも、"何かを生み出す" ための土台であって、joinが主役ではない。
            // 主役は、(関連テーブルの)select句、where句、order by句。
            // joinという言葉の独り歩き。「joinして取っちゃいな」→ 主役は「取っちゃいな」
            //
            // これで逆に、eager(load)の本質がわかった。
            // 今後また別のO/Rマッパーとかをさわるときに、eager(load)に相当するものなんだろう？
            // っていう思考になることできる。
            
            // #1on1: $lazyload以外でDBFluteで意図的に入れてない機能は？ (2026/08/18)
            //
            // pp 検索されたEntityに値をsetして書き換えても、updateするまではDBに行かない
            // e.g.
            //  LocalDate birthDate = member.getBirthdate();
            //  member.setBirthdate(LocalDate.now());
            //
            // DBFluteとしては、↑のようなコードを書いても、
            // Java上(メモリ上)のEntityの値を書き換えただけで、DBに対して何もしない。
            // これ、update()を追加したら初めてDBに反映される。
            //  memberBhv.update(member);
            //
            // JPAは(やり方によるけど)EntityにsetするだけでDBに反映される。
            // この場合のEntityはDBの分身であるという感覚で実装する。
            // RDB隠蔽の感覚。
            //
            // DBFluteは、どれだけCBとかラップしても、あくまで意識はRDBアクセス。
            // そういうコンセプト。
            //
            // O/Rマッパー、大きくこの二つのコンセプト:
            // o RDB隠蔽
            // o RDB意識
            // (o RDB無意識SQL発行意識のみ)
            //
            // SQLをどう組み立てるか？直接書くか？は、
            // 開発者インターフェースの手段のお話だけしてる感じで若干二の次。
            // 全体としてどういうコンセプトなのか？ってのはRDB隠蔽orNotの方が重要かなと。
            // (もっと根本のコンセプトを見ていきたいところ)
        });

        // assert
        memberList.forEach(member -> {
            log(member);
            LocalDate birthDate = member.getBirthdate();
            
            LocalDate targetDate = LocalDate.of(1968, 1, 1);
            assertTrue(birthDate.isEqual(targetDate) || birthDate.isBefore(targetDate));

            // #1on1: LazyLoadの話。$ActiveRecordだとこれで取れる...だがn+1... (2026/08/18)
            //  e.g. ... = member.getMemberStatus(); // SQLがここで発行される
            // DBFluteは、setupSelectしてない関連テーブルをgetしたら、emptyになるだけ。
            // n+1問題の現場でのお話が、そのままDBFluteがLazyLoadをサポートしない理由。
            //
            // 意図したn+1は、まだそのときのスピード実装のメリットを得ることができたとも言える。
            // 意図しないn+1は、もしかしたらn+1しなくても別に問題なく早く実装できたかもしれない。
            // だとしたら、n+1で良いこと何もなかったとも言える。
            //
            // この話は、(jfluteの知る限り)2005年あたりくらいから、ずっと変わらず続いてる話なので、
            // 20年経って、あらゆるものが進化したはずなので、(人類は)同じ問題と戦っていると言える。
            //
            // o 結局、根本の問題はあまり変わらないもの (あらゆることでこういうことはある)
            //  → 装飾は進化したけど、装飾を取り払うと元の問題同じだったりする感覚 by jflute
            //
            // o 伝承がうまくいかない
            //  → n+1問題なんてありふれた話なのに、また発生する...
            //  → こういうことを伝承して学んでいく機会が少ないジレンマ
        });
    }
    
    // #1on1: $2年目になって、わかることも増えてきたけど、わからないことがわかるようになってきて... (2026/08/18)
    // $わからないことが増えた。
    // → わからないことの大きさがわかるようになってきた。
    // $学ぶことの選別どうしよう？迷っている。将来もあやふや。
    // $最初3年間はちゃんとものを作れるスキルを身につけるだったが...
    // $AIも出てきて何がものを作れるスキルなのか？
    // $目の前のお仕事次第でもある
    //
    // TODO tsuji [再び読み物課題] まず何より、目の前の道具を使いこなしてください by jflute (2026/08/18)
    // https://jflute.hatenadiary.jp/entry/20180223/mastercurrent
    // チャーハン中心でもショートケーキ中心でも、抽象化して相乗効果を狙う学びの意識。
    // これを続けて欲しい。
    // 目の前のことの理解を中途半端で済ませてると損をする。

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
