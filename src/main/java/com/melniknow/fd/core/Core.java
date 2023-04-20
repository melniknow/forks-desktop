package com.melniknow.fd.core;

import com.melniknow.fd.oddscorp.Parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Core implements Runnable {
    @Override
    public void run() { // Ебать как важно очень пиздато обработать прерывания в этом методе, иначе ставки будут делаться в фоне...
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("I'm work");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            var params = new Parser.Params(BigDecimal.valueOf(10.0), new ArrayList<>(),
                new ArrayList<>(), false, new ArrayList<>());

            var forks = Parser.getForks(params);
        }
    }
}
