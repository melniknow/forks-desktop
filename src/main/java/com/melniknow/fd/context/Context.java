package com.melniknow.fd.context;

import com.melniknow.fd.core.BetsUtils;
import com.melniknow.fd.oddscorp.BetType;
import com.melniknow.fd.oddscorp.Bookmakers;
import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Context {
    public static volatile BetsUtils.BetsParams betsParams = new BetsUtils.BetsParams(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
    public static volatile Parser.ParserParams parserParams = new Parser.ParserParams(BigDecimal.valueOf(10.0),
        new ArrayList<>() {{
            add(Bookmakers.PINNACLE);
            add(Bookmakers._188BET);
        }},
        0,
        new ArrayList<>() {{
            add(BetType.CORRECT_SCORE);
            add(BetType.HALF_WHO_SCORE);
        }},
        new BigDecimal(1),
        new BigDecimal(40),
        new BigDecimal(1),
        new BigDecimal(100),
        new BigDecimal(2)
    );

    public static volatile String URITokenAuth = "TOKEN";
}
