package com.melniknow.fd.core;

import com.melniknow.fd.utils.BetUtils;
import io.mikael.urlbuilder.UrlBuilder;

import java.math.RoundingMode;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TelegramSender {
    public static final HttpClient client = HttpClient.newHttpClient();

    public static void send(BetUtils.CompleteBetsFork fork) {
        var data = getForkAsMessage(fork);

        var url = UrlBuilder.fromString("https://api.telegram.org/bot6061363285:AAGhtAmbN4A37_2IS7kx2zIvpZG8rRgcoGg/sendMessage")
            .addParameter("chat_id", "-1001704593015")
            .addParameter("parse_mode", "HTML")
            .addParameter("text", data).toUri();

        var request = HttpRequest.newBuilder(url).build();
        client.sendAsync(request, (HttpResponse.BodyHandler<String>) responseInfo -> null);
    }

    public static String getForkAsMessage(BetUtils.CompleteBetsFork completedFork) {
        var fork = completedFork.calculatedFork().fork();
        return String.format(
            "Поставлена вилка! " + "\u26A1" + "\u26A1" + "\u26A1" + "\n\n" +
                "<i>Спорт:</i> <b>" + fork.sport() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.betType() + "</b>" + "\n\n" +

                "Букмекер 1\n" +
                "<i>Имя:</i> <b>" + fork.betInfo1().BK_name() + "</b>" + "\n" +
                "<i>Событие:</i> <b>" + fork.betInfo1().BK_event_id() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.betInfo1().BK_bet() + "</b>" + "\n" +
                "<i>Ссылка:</i> <b>" + fork.betInfo1().BK_href() + "</b>" + "\n" +
                "<i>Коэффициент:</i> <b>" + fork.betInfo1().BK_cf() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.betInfo1().BK_bet_type() + "</b>" + "\n\n" +
                "<i>Сумма ставки:</i> <b>" + completedFork.bet1Rub() + "руб</b>" + "\n\n" +
                "<i>Баланс:</i> <b>" + completedFork.betRubBalance1().setScale(2, RoundingMode.DOWN) + "руб</b>" + "\n\n" +

                "Букмекер 2\n" +
                "<i>Имя:</i> <b>" + fork.betInfo2().BK_name() + "</b>" + "\n" +
                "<i>Событие:</i> <b>" + fork.betInfo2().BK_event_id() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.betInfo2().BK_bet() + "</b>" + "\n" +
                "<i>Ссылка:</i> <b>" + fork.betInfo2().BK_href() + "</b>" + "\n" +
                "<i>Коэффициент:</i> <b>" + fork.betInfo2().BK_cf() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.betInfo2().BK_bet_type() + "</b>" + "\n\n" +
                "<i>Сумма ставки:</i> <b>" + completedFork.bet2Rub() + "руб</b>" + "\n\n" +
                "<i>Баланс:</i> <b>" + completedFork.betRubBalance2().setScale(2, RoundingMode.DOWN) + "руб</b>" + "\n\n" +

                "<i>Доход ₽:</i> <b>" + completedFork.income() + "</b>" + "\n",
            StandardCharsets.UTF_8
        );
    }

    public static String getForkAsMessageInTextArea(BetUtils.CompleteBetsFork completedFork) {
        var fork = completedFork.calculatedFork().fork();
        return String.format(
            "-----------------------------------------------------------------------------\n" +
            "--------------------------- NEW FORK --------------------------------\n" +
            "-----------------------------------------------------------------------------\n" +
            "Поставлена вилка! " + "\u26A1" + "\u26A1" + "\u26A1" + "\n\n" +
                "Спорт: " + fork.sport() + "\n" +
                "Тип ставки: " + fork.betType() + "\n\n" +

                "Букмекер 1\n" +
                "Имя: " + fork.betInfo1().BK_name() + "\n" +
                "Событие: " + fork.betInfo1().BK_event_id() + "\n" +
                "Тип ставки: " + fork.betInfo1().BK_bet() + "\n" +
                "Ссылка: " + fork.betInfo1().BK_href() + "\n" +
                "Коэффициент: " + fork.betInfo1().BK_cf() + "\n" +
                "Тип ставки: " + fork.betInfo1().BK_bet_type() + "\n\n" +
                "Сумма ставки: " + completedFork.bet1Rub() + "руб" + "\n\n" +
                "Баланс: " + completedFork.betRubBalance1().setScale(2, RoundingMode.DOWN) + "руб" + "\n\n" +

                "Букмекер 2\n" +
                "Имя: " + fork.betInfo2().BK_name() + "\n" +
                "Событие: " + fork.betInfo2().BK_event_id() + "\n" +
                "Тип ставки: " + fork.betInfo2().BK_bet() + "\n" +
                "Ссылка: " + fork.betInfo2().BK_href() + "\n" +
                "Коэффициент: " + fork.betInfo2().BK_cf() + "\n" +
                "Тип ставки: " + fork.betInfo2().BK_bet_type() + "\n\n" +
                "Сумма ставки: " + completedFork.bet2Rub() + "руб" + "\n\n" +
                "Баланс: " + completedFork.betRubBalance2().setScale(2, RoundingMode.DOWN) + "руб" + "\n\n" +

                "Доход ₽: " + completedFork.income() + "\n" +
                "------------------------------------------------------------------------\n" +
                "--------------------------- END OF FORK ------------------------\n" +
                "------------------------------------------------------------------------\n",
            StandardCharsets.UTF_8
        );
    }
}
