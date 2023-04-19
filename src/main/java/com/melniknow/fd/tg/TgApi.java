package com.melniknow.fd.tg;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TgApi {
    Executor executor = Executors.newFixedThreadPool(2);
    TradeSender sender;

    public TgApi() {
        this.sender = new TradeSender();
        executor.execute(sender);
    }

    public void SendToAll(TgMessage message) {
        sender.SendToAll(message);
    }

}
