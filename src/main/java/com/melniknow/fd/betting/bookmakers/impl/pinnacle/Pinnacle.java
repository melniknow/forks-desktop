package com.melniknow.fd.betting.bookmakers.impl.pinnacle;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Sports;
import io.mikael.urlbuilder.UrlBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Pinnacle implements IBookmaker {
    private BigDecimal sumBet = null;
    private static Long since = 0L;

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {

    }

    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sports sport) {
        var bookmakerData = Context.betsParams.get(bookmaker);
        var auth = new String(Base64.encodeBase64((bookmakerData.login() + ":" + bookmakerData.password()).getBytes()));

        return getBalanceAsRub(auth, bookmakerData.proxyIp(),
            bookmakerData.proxyPort(), bookmakerData.proxyLogin(), bookmakerData.proxyPassword());
    }

    @Override
    public BigDecimal enterSumAndGetCf(Bookmaker bookmaker, BigDecimal betCoef, Parser.BetInfo info) {
        sumBet = new BigDecimal("1");

        var bookmakerData = Context.betsParams.get(bookmaker);
        var auth = new String(Base64.encodeBase64((bookmakerData.login() + ":" + bookmakerData.password()).getBytes()));


        return getCf(auth, info, bookmakerData.proxyIp(),
            bookmakerData.proxyPort(), bookmakerData.proxyLogin(), bookmakerData.proxyPassword());
    }

    @Override
    public void placeBet(Bookmaker bookmaker, BigDecimal betCoef, BigDecimal curCf, Parser.BetInfo info) throws InterruptedException {
        var bookmakerData = Context.betsParams.get(bookmaker);
        var auth = new String(Base64.encodeBase64((bookmakerData.login() + ":" + bookmakerData.password()).getBytes()));

        var betId = placeBet(auth, bookmakerData.proxyIp(),
            bookmakerData.proxyPort(), bookmakerData.proxyLogin(), bookmakerData.proxyPassword(), info, sumBet);

        checkBet(betId, auth, bookmakerData.proxyIp(),
            bookmakerData.proxyPort(), bookmakerData.proxyLogin(), bookmakerData.proxyPassword());
    }

    public static BigDecimal getBalanceAsRub(String base64Auth, String proxyHost, int proxyPort, String proxyLogin, String proxyPasswd) {
        var credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(proxyLogin, proxyPasswd));

        var proxy = new HttpHost(proxyHost, proxyPort, "http");

        try (var httpclient = HttpClients.custom()
            .setProxy(proxy)
            .setDefaultCredentialsProvider(credsProvider)
            .build()) {

            var config = RequestConfig.custom().build();

            var uri = UrlBuilder.fromString("https://api.pinnacle.com/v1/client/balance").toUri();
            var httpget = new HttpGet(uri);

            httpget.setConfig(config);
            httpget.setHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + base64Auth));

            var json = JsonParser.parseString(httpclient.execute(httpget, response ->
                EntityUtils.toString(response.getEntity()))).getAsJsonObject();

            if (json.getAsJsonPrimitive("currency").getAsString().equals("RUB")) {
                return json.getAsJsonPrimitive("availableBalance").getAsBigDecimal();
            } else {
                return Context.currencyToRubCourse.get(Currency.valueOf(json.getAsJsonPrimitive("currency").getAsString()))
                    .multiply(json.getAsJsonPrimitive("availableBalance").getAsBigDecimal());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void checkBet(String betId, String auth, String proxyIp, Integer proxyPort,
                  String proxyLogin, String proxyPassword) throws InterruptedException {
        while (true) {
            var provider = new BasicCredentialsProvider();

            provider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(proxyLogin, proxyPassword));

            var proxy = new HttpHost(proxyIp, proxyPort, "http");

            TimeUnit.SECONDS.sleep(1);

            try (var httpclient = HttpClients.custom()
                .setProxy(proxy)
                .setDefaultCredentialsProvider(provider)
                .build()) {

                var config = RequestConfig.custom().build();

                var uri = UrlBuilder.fromString("https://api.pinnacle.com/v3/bets?uniqueRequestIds=" + betId).toUri();
                var httpget = new HttpGet(uri);

                httpget.setConfig(config);
                httpget.setHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth));

                var json = JsonParser.parseString(httpclient.execute(httpget, response ->
                    EntityUtils.toString(response.getEntity()))).getAsJsonObject();

                var betObj = json.getAsJsonArray("straightBets").get(0).getAsJsonObject();

                if (betObj.getAsJsonPrimitive("uniqueRequestId").getAsString().equalsIgnoreCase(betId)
                    && betObj.getAsJsonPrimitive("betStatus").getAsString().equals("ACCEPTED")) {
                    return;
                } else if (betObj.getAsJsonPrimitive("uniqueRequestId").getAsString().equalsIgnoreCase(betId)
                    && betObj.getAsJsonPrimitive("betStatus").getAsString().equals("PENDING_ACCEPTANCE"))
                    Logger.writeToLogSession("Ставка обрабатывается [Pinnacle]");

                else throw new RuntimeException("Ставка не принята [Pinnacle]");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    String placeBet(String auth, String proxyIp, Integer proxyPort,
                    String proxyLogin, String proxyPassword, Parser.BetInfo info, BigDecimal sumBet) {
        var betId = UUID.randomUUID().toString().toUpperCase();

        var event = info.BK_event_meta();
        var market = info.BK_market_meta();

        var keys = market.getAsJsonPrimitive("key").getAsString().split(";");
        var betType = market.getAsJsonPrimitive("market_name").getAsString();
        var dest = market.getAsJsonPrimitive("dest").getAsString();

        var realBetType = switch (betType) {
            case "totals" -> "TOTAL_POINTS";
            case "moneyline" -> "MONEYLINE";
            case "spreads" -> "SPREAD";
            default ->
                throw new RuntimeException("Неподдерживаемый betType [Pinnacle] - " + betType);
        };

        var realDest = switch (dest) {
            case "over" -> "OVER";
            case "under" -> "UNDER";
            case "home" -> "TEAM1";
            case "away" -> "TEAM2";
            default -> throw new RuntimeException("Неподдерживаемый dest [Pinnacle] - " + dest);
        };

        var period = keys[1];
        var provider = new BasicCredentialsProvider();

        provider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(proxyLogin, proxyPassword));

        var proxy = new HttpHost(proxyIp, proxyPort, "http");

        try (var httpclient = HttpClients.custom()
            .setProxy(proxy)
            .setDefaultCredentialsProvider(provider)
            .build()) {

            var config = RequestConfig.custom().build();

            var params = new JsonObject();
            params.addProperty("oddsFormat", "DECIMAL");
            params.addProperty("uniqueRequestId", betId);
            params.addProperty("acceptBetterLine", true);
            params.addProperty("stake", sumBet);
            params.addProperty("winRiskStake", "RISK");
            params.addProperty("lineId", market.getAsJsonPrimitive("lineId").getAsBigInteger());

            if (market.has("altLineId"))
                params.addProperty("altLineId", market.getAsJsonPrimitive("altLineId").getAsBigInteger());
            else
                params.add("altLineId", JsonNull.INSTANCE);

            params.addProperty("pitcher1MustStart", true);
            params.addProperty("pitcher2MustStart", true);
            params.addProperty("fillType", "NORMAL");
            params.addProperty("sportId", event.getAsJsonPrimitive("sport_id").getAsInt());
            params.addProperty("eventId", market.getAsJsonPrimitive("matchup_id").getAsBigInteger());
            params.addProperty("periodNumber", Integer.valueOf(period));
            params.addProperty("betType", realBetType);

            if (realDest.equals("TEAM1") || realDest.equals("TEAM2"))
                params.addProperty("team", realDest);
            else
                params.addProperty("team", "DRAW");

            if (realDest.equals("OVER") || realDest.equals("UNDER"))
                params.addProperty("side", realDest);
            else
                params.add("side", JsonNull.INSTANCE);

            var requestEntity = new StringEntity(
                params.toString(),
                ContentType.APPLICATION_JSON);

            var post = new HttpPost("https://api.pinnacle.com/v2/bets/straight");
            post.setHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth));
            post.setConfig(config);
            post.setEntity(requestEntity);

            var json = JsonParser.parseString(httpclient.execute(post, response ->
                EntityUtils.toString(response.getEntity()))).getAsJsonObject();

            var status = json.getAsJsonPrimitive("status").getAsString();

            if (status.equals("PENDING_ACCEPTANCE") || status.equals("ACCEPTED")) return betId;
            throw new RuntimeException("Pinnacle API вернул код ошибки " + status);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BigDecimal getCf(String auth, Parser.BetInfo info, String proxyIp, Integer proxyPort, String proxyLogin, String proxyPassword) {
        var event = info.BK_event_meta();
        var market = info.BK_market_meta();

        var marketName = market.getAsJsonPrimitive("market_name").getAsString();

        var credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(proxyLogin, proxyPassword));

        var proxy = new HttpHost(proxyIp, proxyPort, "http");

        try (var httpclient = HttpClients.custom()
            .setProxy(proxy)
            .setDefaultCredentialsProvider(credsProvider)
            .build()) {

            var config = RequestConfig.custom().build();
            var url = "https://api.pinnacle.com/v1/odds?sportId=" + event.getAsJsonPrimitive("sport_id").getAsInt() +
                "&leagueIds=" + event.getAsJsonPrimitive("league_id").getAsInt() +
                "&oddsFormat=Decimal&since=" + since + "&isLive=true" +
                "&eventIds=" + market.getAsJsonPrimitive("matchup_id").getAsBigInteger();

            var post = new HttpGet(url);
            post.setHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth));
            post.setConfig(config);

            var json = httpclient.execute(post, response ->
                EntityUtils.toString(response.getEntity()));

            var jsonRoot = JsonParser.parseString(json).getAsJsonObject();

            since = jsonRoot.getAsJsonPrimitive("last").getAsLong();

            var preArray = jsonRoot
                .getAsJsonArray("leagues").get(0).getAsJsonObject()
                .getAsJsonArray("events").get(0).getAsJsonObject()
                .getAsJsonArray("periods").get(0).getAsJsonObject();

            switch (marketName) {
                case "spreads" -> {
                    var hdp = new BigDecimal(market.getAsJsonPrimitive("key").getAsString().split(";")[3]);
                    var array = preArray.getAsJsonArray("spreads");
                    for (JsonElement jsonElement : array) {
                        if (jsonElement.getAsJsonObject().getAsJsonPrimitive("hdp").getAsBigDecimal().equals(hdp)) {
                            if (market.getAsJsonPrimitive("dest").getAsString().equals("home")) {
                                return jsonElement.getAsJsonObject().getAsJsonPrimitive("home").getAsBigDecimal();
                            } else if (market.getAsJsonPrimitive("dest").getAsString().equals("away")) {
                                return jsonElement.getAsJsonObject().getAsJsonPrimitive("away").getAsBigDecimal();
                            } else {
                                throw new RuntimeException("Неизвестный dest [Pinnacle] - " + market.getAsJsonPrimitive("dest").getAsString());
                            }
                        }
                    }
                }
                case "moneyline" -> {
                    var data = preArray.getAsJsonObject("moneyline");
                    if (market.getAsJsonPrimitive("dest").getAsString().equals("home")) {
                        return data.getAsJsonPrimitive("home").getAsBigDecimal();
                    } else if (market.getAsJsonPrimitive("dest").getAsString().equals("away")) {
                        return data.getAsJsonPrimitive("away").getAsBigDecimal();
                    } else {
                        throw new RuntimeException("Неизвестный dest [Pinnacle] - " + market.getAsJsonPrimitive("dest").getAsString());
                    }
                }
                case "totals" -> {
                    var array = preArray.getAsJsonArray("totals");
                    var points = new BigDecimal(market.getAsJsonPrimitive("key").getAsString().split(";")[3]);
                    for (JsonElement jsonElement : array) {
                        if (jsonElement.getAsJsonObject().getAsJsonPrimitive("points").getAsBigDecimal().equals(points)) {
                            if (market.getAsJsonPrimitive("dest").getAsString().equals("over")) {
                                return jsonElement.getAsJsonObject().getAsJsonPrimitive("over").getAsBigDecimal();
                            } else if (market.getAsJsonPrimitive("dest").getAsString().equals("under")) {
                                return jsonElement.getAsJsonObject().getAsJsonPrimitive("under").getAsBigDecimal();
                            } else {
                                throw new RuntimeException("Неизвестный dest [Pinnacle] - " + market.getAsJsonPrimitive("dest").getAsString());
                            }
                        }
                    }
                }
                default ->
                    throw new RuntimeException("Не поддерживаемый marketName [Pinnacle] - " + marketName);
            }
            throw new RuntimeException("Ошибка в получении котировок");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
