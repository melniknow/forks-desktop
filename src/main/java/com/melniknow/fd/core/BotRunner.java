package com.melniknow.fd.core;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.BetMaker;
import com.melniknow.fd.utils.MathUtils;

import java.util.concurrent.TimeUnit;

public class BotRunner implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);

                var forks = Parser.getForks(Context.parserParams);
                var calculated = MathUtils.calculate(forks);

                if (calculated != null) {
                    var completed = BetMaker.make(calculated);

                    if (completed != null) {
                        Logger.writePrettyMessageAboutFork(completed);
                        TelegramSender.send(completed);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                Logger.writeToLogSession(e.getMessage());
            }
        }
    }
}
