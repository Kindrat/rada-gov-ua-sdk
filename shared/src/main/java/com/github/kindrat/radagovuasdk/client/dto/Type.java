package com.github.kindrat.radagovuasdk.client.dto;

public enum Type {
    DECREE("Постанова"),
    LAW("Закон"),
    APPEAL("Звернення");

    private final String value;

    Type(String value) {
        this.value = value;
    }
}
