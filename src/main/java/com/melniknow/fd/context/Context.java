package com.melniknow.fd.context;

import com.melniknow.fd.core.MathUtils;
import com.melniknow.fd.oddscorp.BetType;
import com.melniknow.fd.oddscorp.Bookmakers;
import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class Context {
    public static volatile MathUtils.BetsParams betsParams = new MathUtils.BetsParams(BigDecimal.ONE, BigDecimal.ONE);
    public static volatile Parser.ParserParams parserParams = new Parser.ParserParams(BigDecimal.valueOf(10.0),
        new ArrayList<Bookmakers>() {{
            add(Bookmakers.PINNACLE);
            add(Bookmakers._188BET);
        }},
        0,
        new ArrayList<BetType>() {{
            add(BetType.CORRECT_SCORE);
            add(BetType.HALF_WHO_SCORE);
        }});

    public static volatile String URITokenAuth = "TOKEN";
}
