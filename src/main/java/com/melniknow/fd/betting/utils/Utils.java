package com.melniknow.fd.betting.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static BigDecimal getTotalsByStr(String str) {
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return BigDecimal.valueOf(Double.parseDouble(m.group(1)));
        }
        throw new RuntimeException("Not found a totals");
    }
}
