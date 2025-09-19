package com.mohamed.backend.utils.methods;

import java.util.Random;

public class RandomNumberGenerator {
    public static int generate8DigitNumber() {
        Random random = new Random();
        return 10000000 + random.nextInt(90000000);
    }
}
