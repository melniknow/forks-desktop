package com.melniknow.fd.tg;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class TradeSender implements Runnable {
    private final BlockingQueue<TgMessage> queue = new LinkedBlockingDeque<>();
    private final TelegramLongPollingBot bot = new TgBot();

    @Override
    public void run() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {

        }
        try {
            while (true) {
                var message = queue.take();
                SendMessage(message);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void SendToAll(TgMessage message) {
        queue.add(message);
    }

    private void SendMessage(TgMessage message) {
        for (var chatId : MainChats.getChatIds()) {
            SendMessage newMessage = new SendMessage();
            newMessage.setChatId(chatId);
            newMessage.setText(message.getText());
            try {
                bot.execute(newMessage);
            } catch (TelegramApiException e) {

            }
        }
    }
}
