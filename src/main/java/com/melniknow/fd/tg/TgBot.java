package com.melniknow.fd.tg;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TgBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (MainChats.getNames().contains(update.getMessage().getFrom().getUserName()) &&
                    !MainChats.getChatIds().contains(update.getMessage().getChatId())) {

                MainChats.addChatId((update.getMessage().getChatId()));
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "MyBot";
    }

    @Override
    public String getBotToken() {
        return "6011689070:AAGVAHeC_axVeJLPJhzoDWfyIOO4W6HFTtc";
    }
}
