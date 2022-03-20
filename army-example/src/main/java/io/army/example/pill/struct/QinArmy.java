package io.army.example.pill.struct;

import io.army.struct.TextEnum;

public enum QinArmy implements TextEnum {

    ZORO("Roronoa Zoro"),
    ANZAI("Mitsuyoshi Anzai");

    private final String text;

    QinArmy(String text) {
        this.text = text;
    }


    @Override
    public String text() {
        return this.text;
    }


}
