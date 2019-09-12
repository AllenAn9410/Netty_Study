package bank.ATM;

import bank.card.Card;
import org.apache.commons.lang.StringUtils;

public class ICBCATM implements ATM {


    @Override
    public void show() {

    }

    @Override
    public void service() {

    }

    @Override
    public void getCard(Card c) {
        String cardType = c.getCardType();
        if(!StringUtils.isEmpty(cardType) && "icbc".equalsIgnoreCase(cardType)){
            System.out.println("你好"+c.getName()+"开始使用");
        }else{
            System.out.println("BOCATM不支持"+cardType+"类型银行卡");
            return;
        }
    }
}
