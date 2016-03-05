package com.github.kindrat.radagovuasdk.client.dto;

public enum State {
    ACTIVE("Чинний");

    private final String value;

    State(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
