package com.aryan.omybott.util;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomKeyGenerator {

    private static final SecureRandom SECURED_RANDOM = new SecureRandom();

    public static String randomBase64(int length) {
        // UUID.randomUUID().toString().replaceAll("-", "");
        byte[] buf = new byte[length/2];
        SECURED_RANDOM.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

}
