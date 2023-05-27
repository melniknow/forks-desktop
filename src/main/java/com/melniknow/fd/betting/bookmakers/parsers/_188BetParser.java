package com.melniknow.fd.betting.bookmakers.parsers;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers._188bet.BetsSupport;
import com.melniknow.fd.core.Parser;

public class _188BetParser implements IParser {

    private final Parser.BetInfo info;

    private String marketName;
    private String partOfGame;
    private String selectionName;

    public _188BetParser(Parser.BetInfo info) {
        this.info = info;
    }

    @Override
    public ClickBox parse() {
        var bkBet = info.BK_bet();
        if (bkBet.contains("HANDICAP")) {
            loadSelectionName(false);
            marketName = info.BK_market_meta().get("marketName").getAsString();
            partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);
            marketName = marketName.split(" - ")[0];

        } else if (bkBet.contains("WIN")) {
            loadSelectionName(false);
            marketName = info.BK_market_meta().get("marketName").getAsString();
            partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);
            marketName = marketName.split(" - ")[0];
            if (bkBet.contains("WIN__2X") || bkBet.contains("WIN__1X") || bkBet.contains("WIN__12")) {
                marketName = info.BK_market_meta().get("marketName").getAsString();
                loadSelectionName(true);
                partOfGame = "";
            }

        } else if (bkBet.contains("TOTALS")) {
            marketName = info.BK_market_meta().get("marketName").getAsString();
            partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);
            loadSelectionName(true);
            marketName = marketName.split(" - ")[0];
        } else if (bkBet.contains("GAME")) {
            marketName = info.BK_market_meta().get("marketName").getAsString();
            loadSelectionName(true);
            partOfGame = "";
        } else {
            throw new RuntimeException("Not supported betType [188Bet]: " + bkBet);
        }

        Context.log.info("info.BK_bet() = " + bkBet);
        Context.log.info("marketName = " + marketName);
        Context.log.info("partOfGame = " + partOfGame);
        Context.log.info("selectionName = " + selectionName);

        return new ClickBox(marketName, partOfGame, selectionName);
    }

    private void loadSelectionName(boolean fromMeta) {
        if (fromMeta) {
            selectionName = info.BK_market_meta().get("selectionName").getAsString();
        } else {
            var bkBet = info.BK_bet();
            if (bkBet.contains("P1")) {
                selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
            } else if (bkBet.contains("P2")) {
                selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
            } else if (bkBet.contains("WIN__PX")) {
                selectionName = "Draw";
            } else {
                throw new RuntimeException("Not supported Handicap [188Bet]");
            }
        }
    }
}
