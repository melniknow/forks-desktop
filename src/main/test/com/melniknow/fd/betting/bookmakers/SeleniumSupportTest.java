package com.melniknow.fd.betting.bookmakers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeleniumSupportTest {

    @Test
    void formatNumberTest() {
        assertEquals("1,000", SeleniumSupport.formatNumber("1000"));
        assertEquals("99", SeleniumSupport.formatNumber("99"));
        assertEquals("333", SeleniumSupport.formatNumber("333"));
        assertEquals("9,999,999", SeleniumSupport.formatNumber("9999999"));
        assertEquals("123.55", SeleniumSupport.formatNumber("123.55"));
        assertEquals("2,123.55", SeleniumSupport.formatNumber("2123.55"));
        assertEquals("0.55", SeleniumSupport.formatNumber("0.55"));
        assertEquals("5,555.535", SeleniumSupport.formatNumber("5555.535"));
    }
}