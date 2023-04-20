package com.melniknow.fd.core;

import com.melniknow.fd.context.Context;
import com.melniknow.fd.oddscorp.Parser;
import com.melniknow.fd.tg.Sender;

import java.util.concurrent.TimeUnit;

public class Core implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            var forks = Parser.getForks(Context.parserParams);
            var calculated = MathUtils.calculate(Context.betsParams, forks);

            if (!calculated.isEmpty()) Sender.send(calculated.get(0));
        }
    }
}
