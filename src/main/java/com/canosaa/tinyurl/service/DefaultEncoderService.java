package com.canosaa.tinyurl.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class DefaultEncoderService implements EncoderService {

    private final int OFFSET = 0;
    private final char[] BASE_62_SYMBOLS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final char[] BASE_16_SYMBOLS = "0123456789ABCDEF".toCharArray();

    @Override
    public String toBase62(long value) {
        long result = -1;
        long remain;
        var encoded = new StringBuilder();
        while (result != 0) {
            result = value / 62L;
            remain = value % 62L;
            encoded.insert(OFFSET, toBase62Symbol(remain));
            value = result;
        }

        return encoded.toString();
    }

    @Override
    public Long fromBase62(String value) {
        char[] chars = value.toCharArray();
        var decimalNumber = 0L;
        var power = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            var character = chars[i];
            var symbolValue = fromBase62Symbol(character);
            var result = symbolValue * Math.pow(62, power);
            power++;
            decimalNumber += (long) result;
        }

        return decimalNumber;
    }

    public String toBase16(int value) {
        int result = -1;
        int remain;
        var encoded = new StringBuilder();
        while (result != 0) {
            result = value / 16;
            remain = value % 16;
            encoded.insert(OFFSET, toBase16Symbol(remain));
            value = result;
        }
        return encoded.toString();
    }

    private String toBase16Symbol(int number) {
        if (number >= 16) {
            throw new IllegalArgumentException("Must be 15 of lower");
        }

        if (number <= 9) {
            return "" + number;
        } else {
            return "" + BASE_16_SYMBOLS[number];
        }

    }


    private int fromBase62Symbol(char symbol) {
        return new String(BASE_62_SYMBOLS).indexOf(symbol);
    }

    private String toBase62Symbol(long number) {
        if (number >= 62) {
            throw new IllegalArgumentException("Must be 61 of lower");
        }

        if (number <= 9) {
            return "" + number;
        } else {
            return String.valueOf(BASE_62_SYMBOLS[(int) number]);
        }

    }

}
