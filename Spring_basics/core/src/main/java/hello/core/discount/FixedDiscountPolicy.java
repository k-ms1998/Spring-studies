package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
//@Qualifier("fixedDiscountPolicy")
public class FixedDiscountPolicy implements DiscountPolicy{
    private int fixedAmount = 1000;
    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP) {
            return fixedAmount;
        }
        return 0;
    }
}
