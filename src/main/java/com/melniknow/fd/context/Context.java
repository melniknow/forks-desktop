package com.melniknow.fd.context;

import com.melniknow.fd.core.MathUtils;
import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Context {
    public static volatile MathUtils.BetsParams betsParams = new MathUtils.BetsParams();
    public static volatile Parser.ParserParams parserParams = new Parser.ParserParams(BigDecimal.valueOf(10.0), new ArrayList<>(),
        new ArrayList<>(), false, new ArrayList<>());
}
