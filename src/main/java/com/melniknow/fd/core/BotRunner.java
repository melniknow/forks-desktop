package com.melniknow.fd.core;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.BetMaker;
import com.melniknow.fd.utils.MathUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class BotRunner implements Runnable {
    public boolean lastDealSuccess = false;
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (lastDealSuccess) {
                    TimeUnit.SECONDS.sleep(Context.parserParams.pauseAfterSuccess().intValue());
                    lastDealSuccess = false;
                } else {
                    TimeUnit.MILLISECONDS.sleep(1000);
                }

                // Получаем все вилки по нашим параметрам
                var forks = Parser.getForks(Context.parserParams);

                if (forks == null) {
                    Logger.writeToLogSession("Ошибка при получении вилок с сервера");
                } else if (!forks.isEmpty()) {
                    // Фильтруем вилки, находим вилку с лучшей доходностью и проводим для неё расчёты
                    var calculated = MathUtils.calculate(forks);

                    if (calculated != null) {
                        // Ставим ставки
                        var completed = BetMaker.make(calculated);

                        Logger.writePrettyMessageAboutFork(completed);
                        TelegramSender.send(completed);

                        lastDealSuccess = true;

                        var eventId = completed.calculatedFork().fork().eventId().longValue();
                        var count = Context.eventIdToCountSuccessForks.get(eventId);

                        Context.eventIdToCountSuccessForks.put(eventId, count == null ? 1 : ++count);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                Logger.writeToLogSession(e.getLocalizedMessage());
                Context.log.info("[BotRunner] Ошибка - " + e.getLocalizedMessage() + " " + Arrays.toString(e.getStackTrace()));
            }
        }
    }
}
