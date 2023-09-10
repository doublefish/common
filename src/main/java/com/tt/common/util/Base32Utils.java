package com.tt.common.util;

import lombok.var;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 32进制
 * 0-9：0-9，10-31：A-Z（排除I、L、O、U）
 *
 * @author Shuang Yu
 */
public class Base32Utils {

    static final long ZERO = 0L;
    static final long BASE = 32L;

    private Base32Utils() {
    }

    /**
     * 转换
     */
    public static String toString(long number) {
        var chars = new ArrayList<Character>();
        var n = number;
        if (n < ZERO) {
            chars.add('-');
            n = Math.abs(n);
        }
        while (true) {
            var r = n % BASE;
            chars.add(toChar(r));
            n = n / BASE;
            // 余数小于基数中止
            if (n < BASE) {
                if (n > ZERO) {
                    chars.add(toChar(n));
                }
                break;
            }
        }
        Collections.reverse(chars);
        var builder = new StringBuilder();
        for (var c : chars) {
            builder.append(c.charValue());
        }
        return builder.toString();
    }

    /**
     * toChar
     *
     * @param number 0-31
     */
    public static char toChar(long number) {
        if (number == 0) {
            return '0';
        } else if (number == 1) {
            return '1';
        } else if (number == 2) {
            return '2';
        } else if (number == 3) {
            return '3';
        } else if (number == 4) {
            return '4';
        } else if (number == 5) {
            return '5';
        } else if (number == 6) {
            return '6';
        } else if (number == 7) {
            return '7';
        } else if (number == 8) {
            return '8';
        } else if (number == 9) {
            return '9';
        } else if (number == 10) {
            return 'A';
        } else if (number == 11) {
            return 'B';
        } else if (number == 12) {
            return 'C';
        } else if (number == 13) {
            return 'D';
        } else if (number == 14) {
            return 'E';
        } else if (number == 15) {
            return 'F';
        } else if (number == 16) {
            return 'G';
        } else if (number == 17) {
            return 'H';
        } else if (number == 18) {
            return 'J';
        } else if (number == 19) {
            return 'K';
        } else if (number == 20) {
            return 'M';
        } else if (number == 21) {
            return 'N';
        } else if (number == 22) {
            return 'P';
        } else if (number == 23) {
            return 'Q';
        } else if (number == 24) {
            return 'R';
        } else if (number == 25) {
            return 'S';
        } else if (number == 26) {
            return 'T';
        } else if (number == 27) {
            return 'V';
        } else if (number == 28) {
            return 'W';
        } else if (number == 29) {
            return 'X';
        } else if (number == 30) {
            return 'Y';
        } else if (number == 31) {
            return 'Z';
        }
        throw new RuntimeException("The number value must be between 0 and 31.");
    }
}
