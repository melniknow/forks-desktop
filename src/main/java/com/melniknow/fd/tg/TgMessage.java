package com.melniknow.fd.tg;

public class TgMessage {
    private String text;

    public TgMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
