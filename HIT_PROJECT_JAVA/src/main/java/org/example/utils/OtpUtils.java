package org.example.utils;

import java.util.Random;

public class OtpUtils {

    public static String generateOtp() {
        int otpValue = 100000 + new Random().nextInt(900000);
        return String.valueOf(otpValue);
    }
}