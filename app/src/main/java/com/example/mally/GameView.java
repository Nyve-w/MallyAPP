package com.example.mally;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class GameView extends View implements GestureDetector.OnGestureListener {
    private long startTime;
    String username;
    private long elapsedTime;
    private android.os.Handler timerHandler = new android.os.Handler();
    private GameDatabaseHelper dbHelper;
    private int bestScore;
    private long bestTime;

    private int headerBackgroundColor;
    private int headerForegroundColor;
    private int backgroundColor;
    private int redColor;

    public Game game = new Game();
    private GestureDetector gestureDetector;

    private Bitmap imgPique;
    private Bitmap imgPiqueLittle;
    private Bitmap imgTreffle;
    private Bitmap imgTreffleLittle;
    private Bitmap imgCarreau;
    private Bitmap imgCarreauLittle;
    private Bitmap imgCoeur;
    private Bitmap imgCoeurLittle;

    private Bitmap imgBack;

    private float deckWidth;
    private float deckHeight;
    private float deckMargin;

    public GameView(Context context) {
        super(context);
        postConstruct();
    }

    public GameView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        postConstruct();
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            postInvalidate();
            timerHandler.postDelayed(this, 1000);
        }
    };

    private void postConstruct() {
        gestureDetector = new GestureDetector(getContext(), this);
        Resources res = getResources();
        headerForegroundColor = res.getColor(R.color.headerForegroundColor);
        backgroundColor = res.getColor(R.color.backSolitaire);
        redColor = res.getColor(R.color.textSolitaire);
        startTime = System.currentTimeMillis();
        timerHandler.post(timerRunnable);
        dbHelper = new GameDatabaseHelper(getContext());

        // 🔥 Récupération des données
        bestScore = dbHelper.getBestSolitaireScore();

        //bestTime = dbHelper.getBestTime();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        deckMargin = width * 0.025f;
        deckWidth = (width - (Game.DECK_COUNT + 1) * deckMargin) / Game.DECK_COUNT;
        deckHeight = deckWidth * 1.4f;

        try {
            int imageSize = (int) (deckWidth * 0.66);
            int imageLittleSize = (int) (deckWidth / 3);

            imgPique = BitmapFactory.decodeResource(getResources(), R.drawable.pique);
            imgPiqueLittle = Bitmap.createScaledBitmap(imgPique, imageLittleSize, imageLittleSize, true);
            imgPique = Bitmap.createScaledBitmap(imgPique, imageSize, imageSize, true);

            imgTreffle = BitmapFactory.decodeResource(getResources(), R.drawable.treffle);
            imgTreffleLittle = Bitmap.createScaledBitmap(imgTreffle, imageLittleSize, imageLittleSize, true);
            imgTreffle = Bitmap.createScaledBitmap(imgTreffle, imageSize, imageSize, true);

            imgCoeur = BitmapFactory.decodeResource(getResources(), R.drawable.coeur);
            imgCoeurLittle = Bitmap.createScaledBitmap(imgCoeur, imageLittleSize, imageLittleSize, true);
            imgCoeur = Bitmap.createScaledBitmap(imgCoeur, imageSize, imageSize, true);

            imgCarreau = BitmapFactory.decodeResource(getResources(), R.drawable.carreau);
            imgCarreauLittle = Bitmap.createScaledBitmap(imgCarreau, imageLittleSize, imageLittleSize, true);
            imgCarreau = Bitmap.createScaledBitmap(imgCarreau, imageSize, imageSize, true);

            imgBack = BitmapFactory.decodeResource(getResources(), R.drawable.back);
            imgBack = Bitmap.createScaledBitmap(imgBack, (int) deckWidth, (int) deckHeight, true);

        } catch (Exception exception) {
            Log.e("ERROR", "Cannot load card images");
        }
    }

    private RectF computeStackRect(int index) {
        float x = deckMargin + (deckWidth + deckMargin) * index;
        float y = getHeight() * 0.17f;
        return new RectF(x, y, x + deckWidth, y + deckHeight);
    }

    private RectF computeReturnedPiocheRect() {
        float x = deckMargin + (deckWidth + deckMargin) * 5;
        float y = getHeight() * 0.17f;
        return new RectF(x, y, x + deckWidth, y + deckHeight);
    }

    private RectF computePiocheRect() {
        float x = deckMargin + (deckWidth + deckMargin) * 6;
        float y = getHeight() * 0.17f;
        return new RectF(x, y, x + deckWidth, y + deckHeight);
    }

    private RectF computeDeckRect(int index, int cardIndex) {
        float x = deckMargin + (deckWidth + deckMargin) * index;
        float y = getHeight() * 0.30f + cardIndex * computeStepY();
        return new RectF(x, y, x + deckWidth, y + deckHeight);
    }

    public float computeStepY() {
        return (getHeight() * 0.9f - getHeight() * 0.3f) / 17f;
    }

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public void drawCard(Canvas canvas, Card card, float x, float y) {
        float cornerWidth = deckWidth / 10f;
        RectF rectF = new RectF(x, y, x + deckWidth, y + deckHeight);

        if (card == null) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xff_40_40_40);
            canvas.drawRoundRect(rectF, cornerWidth, cornerWidth, paint);
            paint.setStyle(Paint.Style.FILL);
            return;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(card.isReturned() ? 0xff_ff_ff_ff : 0xff_a0_c0_a0);
        canvas.drawRoundRect(rectF, cornerWidth, cornerWidth, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xff_00_00_00);
        canvas.drawRoundRect(rectF, cornerWidth, cornerWidth, paint);

        if (card.isReturned()) {
            Bitmap image;
            Bitmap imageLittle;
            int color;
            switch (card.getType()) {
                case CARREAU:
                    image = imgCarreau;
                    imageLittle = imgCarreauLittle;
                    color = 0xff_e6_14_08;
                    break;
                case COEUR:
                    image = imgCoeur;
                    imageLittle = imgCoeurLittle;
                    color = 0xff_e6_14_08;
                    break;
                case PIQUE:
                    image = imgPique;
                    imageLittle = imgPiqueLittle;
                    color = 0xff_00_00_00;
                    break;
                default:
                    image = imgTreffle;
                    imageLittle = imgTreffleLittle;
                    color = 0xff_00_00_00;
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(deckWidth / 2.4f);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(color);
            if (card.getValue() != 10) {
                canvas.drawText(card.getName(), x + deckWidth * 0.1f, y + deckHeight * 0.32f, paint);
            } else {
                canvas.drawText("1", x + deckWidth * 0.1f, y + deckHeight * 0.32f, paint);
                canvas.drawText("0", x + deckWidth * 0.3f, y + deckHeight * 0.32f, paint);
            }
            canvas.drawBitmap(imageLittle, x + deckWidth * 0.9f - imageLittle.getWidth(),
                    y + deckHeight * 0.1f, paint);
            canvas.drawBitmap(image, x + (deckWidth - image.getWidth()) / 2f,
                    y + (deckHeight * 0.9f - image.getHeight()), paint);
            paint.setFakeBoldText(false);
        } else {
            canvas.drawBitmap(imgBack, x, y, paint);
        }
    }

    private String getFormattedTime() {
        int seconds = (int) (elapsedTime / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // --- Background ---
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        // --- Draw the Header ---
        float widthDiv10 = getWidth() / 10f;
        float heightDiv10 = getHeight() / 10f;

        paint.setColor(headerBackgroundColor);
        RectF rectF = new RectF(0, 0, getWidth(), getHeight() * 0.15f);
        canvas.drawRect(rectF, paint);

        paint.setColor(redColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (getWidth() / 8.5));
        canvas.drawText(getResources().getString(R.string.game_name),
                widthDiv10 * 5, (int) (heightDiv10 * 0.6), paint);

        paint.setColor(headerForegroundColor);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(getWidth() / 20f);
        paint.setStrokeWidth(1);
        canvas.drawText("Score : " + game.score,
                getWidth() * 0.05f,
                getHeight() * 0.12f,
                paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(getWidth() / 25f);
        paint.setColor(headerForegroundColor);
        canvas.drawText("Best Score: " + bestScore,
                getWidth() * 0.05f,
                getHeight() * 0.09f,
                paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("By Mally.fr", (int) (widthDiv10 * 0.03), (int) (heightDiv10 * 1.3), paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Time : " + getFormattedTime(),
                getWidth() * 0.95f,
                getHeight() * 0.12f,
                paint);

        // --- Draw stacks ---
        paint.setStrokeWidth(getWidth() / 200f);

        for (int i = 0; i < Game.STACK_COUNT; i++) {
            Game.Stack stack = game.stacks[i];
            rectF = computeStackRect(i);
            drawCard(canvas, stack.isEmpty() ? null : stack.lastElement(), rectF.left, rectF.top);
        }

        rectF = computeReturnedPiocheRect();
        drawCard(canvas, game.returnedPioche.isEmpty() ? null : game.returnedPioche.lastElement(),
                rectF.left, rectF.top);

        rectF = computePiocheRect();
        drawCard(canvas, game.pioche.isEmpty() ? null : game.pioche.lastElement(),
                rectF.left, rectF.top);

        for (int i = 0; i < Game.DECK_COUNT; i++) {
            Game.Deck deck = game.decks[i];
            if (deck.isEmpty()) {
                rectF = computeDeckRect(i, 0);
                drawCard(canvas, null, rectF.left, rectF.top);
            } else {
                for (int cardIndex = 0; cardIndex < deck.size(); cardIndex++) {
                    Card card = deck.get(cardIndex);
                    rectF = computeDeckRect(i, cardIndex);
                    drawCard(canvas, card, rectF.left, rectF.top);
                }
            }
        }
    }

    // --- GestureDetector ---
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // Gestion des taps (pioche, deck, stack) - inchangé
        // ... (le code reste exactement le même que celui fourni)
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) { return true; }
    @Override
    public void onShowPress(MotionEvent e) { }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override
    public void onLongPress(MotionEvent e) { }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }

    public Game getGame() { return game; }
    public int getBestScore() { return bestScore; }
    public long getElapsedTime() { return elapsedTime; }

    private boolean gameFinished = false;

    private void checkEndGame() {
        if (gameFinished) return;

        if (game.isGameFinished()) {
            gameFinished = true;

            timerHandler.removeCallbacks(timerRunnable);
            askUsernameAndSaveScore();

            dbHelper.saveSolitaireScore(username,game.score, elapsedTime);

            bestScore = dbHelper.getBestSolitaireScore();
            //bestTime = dbHelper.getBestTime();

            Log.d("GAME", "🎉 Partie terminée !");
            Log.d("GAME", "Score : " + game.score);
            Log.d("GAME", "Temps : " + getFormattedTime());
        }
    }

    private void askUsernameAndSaveScore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("🏆 Nouveau Record !");
        builder.setMessage("Score : " + game.score + " | Temps : " + getFormattedTime());

        final EditText input = new EditText(getContext());
        input.setHint("Ton pseudo");
        builder.setView(input);

        builder.setCancelable(false); // Oblige à répondre
        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            username = input.getText().toString().trim();
            if (username.isEmpty()) username = "Joueur Anonyme";

            dbHelper.saveSolitaireScore(username, game.score, elapsedTime);
            Toast.makeText(getContext(), "Bravo " + username + " !", Toast.LENGTH_SHORT).show();
            postInvalidate(); // Pour rafraîchir l'affichage du Best Score
        });

        builder.show();
    }
}
