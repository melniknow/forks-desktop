package com.melniknow.fd.core;

import com.melniknow.fd.utils.BetUtils;
import io.mikael.urlbuilder.UrlBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TelegramSender {
    public static final HttpClient client = HttpClient.newHttpClient();

    public static void send(BetUtils.CompleteBetsFork fork) {
        var data = getDataMessage(fork);

        var url = UrlBuilder.fromString("https://api.telegram.org/bot6061363285:AAGhtAmbN4A37_2IS7kx2zIvpZG8rRgcoGg/sendMessage")
            .addParameter("chat_id", "-1001704593015")
            .addParameter("parse_mode", "HTML")
            .addParameter("text", data).toUri();

        var request = HttpRequest.newBuilder(url).build();
        client.sendAsync(request, (HttpResponse.BodyHandler<String>) responseInfo -> null);
    }

    private static String getDataMessage(BetUtils.CompleteBetsFork completedFork) {
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

                "Букмекер 2\n" +
                "<i>Имя:</i> <b>" + fork.betInfo2().BK_name() + "</b>" + "\n" +
                "<i>Событие:</i> <b>" + fork.betInfo2().BK_event_id() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.betInfo2().BK_bet() + "</b>" + "\n" +
                "<i>Ссылка:</i> <b>" + fork.betInfo2().BK_href() + "</b>" + "\n" +
                "<i>Коэффициент:</i> <b>" + fork.betInfo2().BK_cf() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.betInfo2().BK_bet_type() + "</b>" + "\n\n" +

                "<i>Информация по вилке:</i> <b>" + completedFork.info() + "</b>" + "\n",
            StandardCharsets.UTF_8
        );
    }
}
