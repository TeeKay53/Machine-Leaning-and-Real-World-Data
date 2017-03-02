package uk.ac.cam.tp423.MachineLearning.Task4;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by TeeKay on 1/30/2017.
 */
public class Combinatorial {
    public  HashMap<Integer, BigDecimal> computed = new HashMap<>();
    private int chooseFrom;

    public Combinatorial(int n){
        chooseFrom = n;
        computed.put(0, new BigDecimal("1"));
        computed.put(1, new BigDecimal(((Integer) n).toString()));
    }

    public BigDecimal choose(int howMuchChoose){
        if (!computed.containsKey(howMuchChoose)){
            computed.put(howMuchChoose, choose(howMuchChoose - 1).multiply(new BigDecimal((chooseFrom - (howMuchChoose - 1)))).divide(new BigDecimal(howMuchChoose)));
        }
        return computed.get(howMuchChoose);
    }

    public static void main(String[] args) {
        Combinatorial c = new Combinatorial(6);
        for (int i = 0; i <= 6; i++){
            System.out.println(c.choose(i));
        }
    }
}
