package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;

public interface IBookmaker {
    void openLink(Bookmaker bookmaker, Parser.BetInfo info);
    void clickOnBetType(Bookmaker bookmaker, Parser.BetInfo info, Sports sport);
    void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, Sports sport);
    void placeBet();
}
