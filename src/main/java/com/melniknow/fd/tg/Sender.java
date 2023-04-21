package com.melniknow.fd.tg;

import com.melniknow.fd.core.BetsUtils;
import io.mikael.urlbuilder.UrlBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Sender {
    public static final HttpClient client = HttpClient.newHttpClient();

    public static void send(BetsUtils.CompleteBetsFork fork) {
        var data = getDataMessage(fork);

        var uri = UrlBuilder.fromString("https://api.telegram.org/bot6061363285:AAGhtAmbN4A37_2IS7kx2zIvpZG8rRgcoGg/sendMessage")
            .addParameter("chat_id", "-1001704593015")
            .addParameter("parse_mode", "HTML")
            .addParameter("text", data).toUri();

        var request = HttpRequest.newBuilder(uri).build();
        client.sendAsync(request, (HttpResponse.BodyHandler<String>) responseInfo -> null);
    }

    private static String getDataMessage(BetsUtils.CompleteBetsFork completedFork) {
        var fork = completedFork.calculatedFork().fork();
        return String.format(
            "Поставлена вилка! " + "\u26A1" + "\u26A1" + "\u26A1" + "\n\n" +
                "<i>Спорт:</i> <b>" + fork.sport() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.betType() + "</b>" + "\n\n" +

                "Букмекер 1\n" +
                "<i>Имя:</i> <b>" + fork.bkName1() + "</b>" + "\n" +
                "<i>Событие:</i> <b>" + fork.event1() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.bet1() + "</b>" + "\n" +
                "<i>Ссылка:</i> <b>" + fork.link1() + "</b>" + "\n" +
                "<i>Коэффициент:</i> <b>" + fork.ratio1() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.type1() + "</b>" + "\n\n" +

                "Букмекер 2\n" +
                "<i>Имя:</i> <b>" + fork.bkName2() + "</b>" + "\n" +
                "<i>Событие:</i> <b>" + fork.event2() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.bet2() + "</b>" + "\n" +
                "<i>Ссылка:</i> <b>" + fork.link2() + "</b>" + "\n" +
                "<i>Коэффициент:</i> <b>" + fork.ratio2() + "</b>" + "\n" +
                "<i>Тип ставки:</i> <b>" + fork.type2() + "</b>" + "\n\n" +

                "<i>Информация по вилке:</i> <b>" + completedFork.info() + "</b>" + "\n",
            StandardCharsets.UTF_8
        );
    }
}
