package com.example.mally;

import android.graphics.Color;
import java.io.Serializable;

public class Card implements Serializable {

    public enum CardType {
        COEUR, CARREAU, PIQUE, TREFLE
    }

    private CardType type;
    private int value;
    private boolean returned;

    public Card(CardType type, int value, boolean returned) {
        this.type = type;
        setValue(value);
        this.returned = returned;
    }

    public Card(CardType type, int value) {
        this(type, value, false);
    }

    public CardType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value < 1 || value > 13)
            throw new IllegalArgumentException("Invalid card value");
        this.value = value;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public int getColor() {
        return (type == CardType.COEUR || type == CardType.CARREAU)
                ? Color.RED : Color.BLACK;
    }

    public String getName() {
        switch (value) {
            case 1: return "A";
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            default: return String.valueOf(value);
        }
    }
}
