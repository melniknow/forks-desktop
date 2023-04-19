package com.melniknow.fd.tg;

import java.util.ArrayList;
import java.util.List;

public class MainChats {
    private static final ArrayList<String> names = new ArrayList<>(List.of("melniknow", "Rusakob", "StanislavR84", "arkadyrudenko"));
    private static final ArrayList<Long> chatIds = new ArrayList<>();

    static public synchronized ArrayList<Long> getChatIds() {
        return new ArrayList<>(chatIds);
    }

    static public synchronized void addChatId(Long chatId) {
        chatIds.add(chatId);
    }

    static public synchronized ArrayList<String> getNames() {
        return new ArrayList<>(names);
    }
}
