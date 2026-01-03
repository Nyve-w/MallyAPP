package com.example.mally;

import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    public int score = 0;

    public static final int STACK_COUNT = 4;
    public static final int DECK_COUNT = 7;

    public static class Stack extends java.util.Stack<Card> {}
    public static class Deck extends java.util.Stack<Card> {}

    public Stack[] stacks = new Stack[STACK_COUNT];
    public Deck[] decks = new Deck[DECK_COUNT];

    public Vector<Card> pioche = new Vector<>();
    public Vector<Card> returnedPioche = new Vector<>();

    public Game() {
        for (int i = 0; i < STACK_COUNT; i++) stacks[i] = new Stack();
        for (int i = 0; i < DECK_COUNT; i++) decks[i] = new Deck();
    }

    // 🔥 INITIALISATION DU JEU (OBLIGATOIRE)
    public void initNewGame() {
        score = 0;
        for (Stack s : stacks) s.clear();
        for (Deck d : decks) d.clear();
        pioche.clear();
        returnedPioche.clear();

        List<Card> allCards = new ArrayList<>();

        for (Card.CardType type : Card.CardType.values()) {
            for (int v = 1; v <= 13; v++) {
                allCards.add(new Card(type, v, false));
            }
        }

        Collections.shuffle(allCards);

        // Distribution des 7 decks
        for (int i = 0; i < DECK_COUNT; i++) {
            for (int j = 0; j <= i; j++) {
                Card c = allCards.remove(0);
                c.setReturned(j == i);
                decks[i].add(c);
            }
        }

        // Reste → pioche
        for (Card c : allCards) {
            c.setReturned(false);
            pioche.add(c);
        }
    }

    public int canMoveCardToStack(Card card) {
        for (int i = 0; i < STACK_COUNT; i++) {
            Stack s = stacks[i];
            if (s.isEmpty()) {
                if (card.getValue() == 1) return i;
            } else {
                Card top = s.lastElement();
                if (top.getType() == card.getType()
                        && top.getValue() == card.getValue() - 1)
                    return i;
            }
        }
        return -1;
    }

    public int canMoveCardToDeck(Card card) {
        for (int i = 0; i < DECK_COUNT; i++) {
            Deck d = decks[i];
            if (d.isEmpty()) {
                if (card.getValue() == 13) return i;
            } else {
                Card top = d.lastElement();
                if (top.getColor() != card.getColor()
                        && top.getValue() == card.getValue() + 1)
                    return i;
            }
        }
        return -1;
    }

    //POUR LE SCORE DANS LE JEU
    public void addScore(int value) {
        score += value;
    }
    public boolean isGameFinished() {
        for (int i = 0; i < STACK_COUNT; i++) {
            if (stacks[i].size() != 13) {
                return false;
            }
        }
        return true;
    }


}

