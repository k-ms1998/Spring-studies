package hello.core.discount;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
//@Qualifier("mainDiscountPolicy")
//@Primary
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy{
    private double discountRate = 0.2;

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP){
            return (int)(price *discountRate);
        }

        return 0;
    }
}
