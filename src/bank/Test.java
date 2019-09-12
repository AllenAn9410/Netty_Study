package bank;

import bank.ATM.ATM;
import bank.ATM.BOCATM;
import bank.ATM.ICBCATM;
import bank.card.ICBCCard;

public class Test {
    public static void main(String[] args){
        ICBCCard icbc = new ICBCCard();
        icbc.setName("苏琪");
        icbc.setBalance(88888888);
        icbc.setCardNum("sq123");
        icbc.setCardType("icbc");
        ATM a = new ICBCATM();
        a.getCard(icbc);
        ATM a2 = new BOCATM();
        a2.getCard(icbc);




    }
}
