package com.ingenico.epay.util;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class RandomIbanGenerator implements IbanGenerator {

    @Override
    public String generate() {
        {
            String start = "NL";
            Random value = new Random();
            int r1 = value.nextInt(10);
            int r2 = value.nextInt(10);
            start += Integer.toString(r1) + Integer.toString(r2) + "-";
            int count = 0;
            int n = 0;
            for (int i = 0; i < 12; i++) {
                if (count == 4) {
                    start += "-";
                    count = 0;
                } else
                    n = value.nextInt(10);
                start += Integer.toString(n);
                count++;

            }
            return start;
        }
    }

}
