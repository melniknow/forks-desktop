package com.melniknow.fd.context;

import com.melniknow.fd.core.MathUtils;
import com.melniknow.fd.oddscorp.Bookmakers;
import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Context {
    public static volatile MathUtils.BetsParams betsParams = new MathUtils.BetsParams(BigDecimal.ONE, BigDecimal.ONE);
    public static volatile Parser.ParserParams parserParams = new Parser.ParserParams(BigDecimal.valueOf(10.0), new ArrayList<>(),
        false, new ArrayList<>());

    public static volatile Parser.OddScorpParams oddScorpParams = new Parser.OddScorpParams("afdjd8fd8rjkfu7fa7f3",
        new ArrayList<>() {{
            add(Bookmakers.PINNACLE);
        }}, new BigDecimal(1));
}
