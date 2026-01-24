package com.example.mally;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class GameViewSudoku extends View implements GestureDetector.OnGestureListener {

    // --- Attributs graphiques ---
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GameBoard gameBoard;

    // --- Base de données ---
    private GameDatabaseHelper dbHelper;

    // --- Audio ---
    private MediaPlayer backgroundMusic; // Pour la musique d'ambiance
    private SoundPool soundPool;         // Pour les effets sonores courts (clics)
    private int clickSoundId;            // L'ID du son chargé
    private boolean soundLoaded = false;

    // --- Dimensions ---
    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;
    private float buttonWidth;
    private float buttonRadius;
    private float buttonMargin;
    private float buttonsTop;

    // --- Bitmaps ---
    private Bitmap eraserBitmap;
    private Bitmap pencilBitmap;
    private Bitmap littlePencilBitmap;

    // --- Gestion des touches ---
    private GestureDetector gestureDetector;

    // --- Données utilisateur ---
    private String playerName = "Joueur";
    private boolean gameFinished = false;

    // --- Constructeurs ---
    public GameViewSudoku(Context context) {
        super(context);
        this.init();
    }

    public GameViewSudoku(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        gameBoard = GameBoard.getGameBoard(GameLevel.MEDIUM);
        gestureDetector = new GestureDetector(getContext(), this);
        dbHelper = new GameDatabaseHelper(getContext());

        // --- Initialisation de l'audio ---
        initAudio();

        askForUserName();
    }

    /**
     * Initialise la musique et les bruitages
     */
    private void initAudio() {
        Context context = getContext();

        // 1. Configuration de la musique de fond
        // Assurez-vous d'avoir un fichier 'game_music.mp3' dans res/raw
        try {
            // On vérifie si la ressource existe pour éviter le crash si vous n'avez pas mis le fichier
            int musicResId = getResources().getIdentifier("game_music", "raw", context.getPackageName());
            if (musicResId != 0) {
                backgroundMusic = MediaPlayer.create(context, musicResId);
                backgroundMusic.setLooping(true); // La musique tourne en boucle
                backgroundMusic.setVolume(0.5f, 0.5f); // Volume à 50%
                backgroundMusic.start(); // Lecture immédiate
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Configuration des bruitages (SoundPool)
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5) // Max 5 sons en même temps
                .setAudioAttributes(audioAttributes)
                .build();

        // Chargement du son de clic (assurez-vous d'avoir 'click_sound.mp3' dans res/raw)
        int clickResId = getResources().getIdentifier("click_sound", "raw", context.getPackageName());
        if (clickResId != 0) {
            clickSoundId = soundPool.load(context, clickResId, 1);

            // On attend que le son soit chargé avant de pouvoir le jouer
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundLoaded = true;
                }
            });
        }
    }

    /**
     * Joue le son de clic
     */
    private void playClickSound() {
        if (soundLoaded && soundPool != null) {
            // play(soundID, leftVolume, rightVolume, priority, loop, rate)
            soundPool.play(clickSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    /**
     * Important : Arrêter le son quand la vue est détruite pour éviter les fuites de mémoire
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (backgroundMusic != null) {
            if (backgroundMusic.isPlaying()) {
                backgroundMusic.stop();
            }
            backgroundMusic.release();
            backgroundMusic = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private void askForUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sudoku");
        builder.setMessage("Entrez votre pseudo pour le score :");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("JOUER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerName = input.getText().toString();
                if (playerName.isEmpty()) playerName = "Anonyme";
                invalidate();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void showRulesDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Règles du Sudoku")
                .setMessage("1. Chaque ligne doit contenir les chiffres de 1 à 9 sans répétition.\n\n" +
                        "2. Chaque colonne doit contenir les chiffres de 1 à 9 sans répétition.\n\n" +
                        "3. Chaque bloc carré de 3x3 doit contenir les chiffres de 1 à 9.\n\n" +
                        "Score :\n" +
                        "+100 pts par chiffre correct.\n" +
                        "-10 pts par erreur.")
                .setPositiveButton("Compris", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void checkWinCondition() {
        if (gameFinished) return;

        boolean isFull = true;
        boolean isCorrect = true;

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (gameBoard.cells[y][x].assumedValue == 0) {
                    isFull = false;
                    break;
                }
                if (gameBoard.cells[y][x].assumedValue != gameBoard.cells[y][x].realValue) {
                    isCorrect = false;
                }
            }
        }

        if (isFull && isCorrect) {
            gameFinished = true;
            dbHelper.saveSudokuScore(playerName, gameBoard.score);

            // Arrêter la musique quand on gagne (optionnel)
            if(backgroundMusic != null && backgroundMusic.isPlaying()) backgroundMusic.pause();

            new AlertDialog.Builder(getContext())
                    .setTitle("Félicitations !")
                    .setMessage("Vous avez terminé la grille !\nScore final : " + gameBoard.score)
                    .setPositiveButton("Menu Principal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (getContext() instanceof Activity) {
                                ((Activity) getContext()).finish();
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        gridWidth = w;
        gridSeparatorSize = (w / 9f) / 20f;
        cellWidth = gridWidth / 9f;

        // Espace augmenté pour la zone bleue
        float verticalGap = w * 0.15f;
        buttonsTop = (9 * cellWidth) + verticalGap;

        buttonWidth = w / 7f;
        buttonRadius = buttonWidth / 10f;
        buttonMargin = (w - 6 * buttonWidth) / 7f;

        if (getResources().getIdentifier("eraser", "drawable", getContext().getPackageName()) != 0) {
            eraserBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eraser);
            eraserBitmap = Bitmap.createScaledBitmap(eraserBitmap, (int) (buttonWidth * 0.8f), (int) (buttonWidth * 0.8f), false);
        }

        if (getResources().getIdentifier("pencil", "drawable", getContext().getPackageName()) != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pencil);
            pencilBitmap = Bitmap.createScaledBitmap(bitmap, (int) (buttonWidth * 0.8f), (int) (buttonWidth * 0.8), false);
            littlePencilBitmap = Bitmap.createScaledBitmap(bitmap, (int) (buttonWidth / 3), (int) (buttonWidth / 3), false);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGridCells(canvas);
        drawGridLines(canvas);
        drawSelectionCursor(canvas);
        drawControlBar(canvas);
    }

    private void drawGridCells(Canvas canvas) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellWidth * 0.7f);

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int backgroundColor = Color.WHITE;

                if (gameBoard.currentCellX != -1 && gameBoard.currentCellY != -1) {
                    if ((x / 3 == gameBoard.currentCellX / 3 && y / 3 == gameBoard.currentCellY / 3) ||
                            (x == gameBoard.currentCellX && y != gameBoard.currentCellY) ||
                            (x != gameBoard.currentCellX && y == gameBoard.currentCellY)) {
                        backgroundColor = 0xFF_FF_F0_F0;
                    }
                }

                if (gameBoard.cells[y][x].isInitial) {
                    backgroundColor = (backgroundColor == 0xFF_FF_F0_F0) ? 0xFF_F4_F0_F0 : 0xFF_F0_F0_F0;
                }

                if (gameBoard.getSelectedValue() > 0 &&
                        gameBoard.cells[y][x].assumedValue == gameBoard.getSelectedValue()) {
                    backgroundColor = 0xFF_C7_DA_F8;
                }

                paint.setColor(backgroundColor);
                canvas.drawRect(x * cellWidth, y * cellWidth, (x + 1) * cellWidth, (y + 1) * cellWidth, paint);

                if (gameBoard.cells[y][x].assumedValue != 0) {
                    paint.setColor(gameBoard.cells[y][x].isInitial ? Color.BLACK : 0xFF3F51B5);
                    paint.setTextSize(cellWidth * 0.7f);
                    canvas.drawText("" + gameBoard.cells[y][x].assumedValue,
                            x * cellWidth + cellWidth / 2,
                            y * cellWidth + cellWidth * 0.75f, paint);
                } else {
                    paint.setTextSize(cellWidth * 0.33f);
                    paint.setColor(0xFFA0A0A0);
                    for (int i = 0; i < 9; i++) {
                        if (gameBoard.cells[y][x].marks[i]) {
                            float txtX = x * cellWidth + cellWidth * (0.2f + (i % 3) * 0.3f);
                            float txtY = y * cellWidth + cellWidth * (0.3f + (i / 3) * 0.3f);
                            canvas.drawText("" + (i + 1), txtX, txtY, paint);
                        }
                    }
                }
            }
        }
    }

    private void drawGridLines(Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(gridSeparatorSize / 2);
        for (int i = 0; i <= 9; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, cellWidth * 9, paint);
            canvas.drawLine(0, i * cellWidth, cellWidth * 9, i * cellWidth, paint);
        }
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(gridSeparatorSize);
        for (int i = 0; i <= 3; i++) {
            canvas.drawLine(i * (cellWidth * 3), 0, i * (cellWidth * 3), cellWidth * 9, paint);
            canvas.drawLine(0, i * (cellWidth * 3), cellWidth * 9, i * (cellWidth * 3), paint);
        }
    }

    private void drawSelectionCursor(Canvas canvas) {
        if (gameBoard.currentCellX != -1 && gameBoard.currentCellY != -1) {
            paint.setColor(0xFF_30_3F_9F);
            paint.setStrokeWidth(gridSeparatorSize * 1.5f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(gameBoard.currentCellX * cellWidth,
                    gameBoard.currentCellY * cellWidth,
                    (gameBoard.currentCellX + 1) * cellWidth,
                    (gameBoard.currentCellY + 1) * cellWidth,
                    paint);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(1);
        }
    }

    private void drawControlBar(Canvas canvas) {
        paint.setColor(0xFFE8EAF6);
        canvas.drawRect(0, buttonsTop, gridWidth, getHeight(), paint);

        paint.setColor(0xFF303F9F);
        paint.setTextSize(buttonWidth * 0.4f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Joueur: " + playerName, buttonMargin, buttonsTop + buttonWidth * 0.4f, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Score: " + gameBoard.score, gridWidth - buttonMargin, buttonsTop + buttonWidth * 0.4f, paint);

        float startY = buttonsTop + buttonWidth * 0.6f;
        float currentX = buttonMargin;
        float currentY = startY;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(buttonWidth * 0.7f);

        for (int i = 1; i <= 9; i++) {
            paint.setColor(Color.WHITE);
            RectF rectF = new RectF(currentX, currentY, currentX + buttonWidth, currentY + buttonWidth);
            canvas.drawRoundRect(rectF, buttonRadius, buttonRadius, paint);

            paint.setColor(Color.BLACK);
            canvas.drawText("" + i, rectF.centerX(), rectF.centerY() + (buttonWidth * 0.25f), paint);

            if (i == 5) {
                currentX = buttonMargin;
                currentY += buttonWidth + buttonMargin;
            } else {
                currentX += buttonWidth + buttonMargin;
            }
        }

        currentX = buttonMargin;
        currentY += buttonWidth + buttonMargin;

        drawToolButton(canvas, currentX, currentY, "BACK", null, 0xFFFFCDD2);
        currentX += buttonWidth + buttonMargin;
        drawToolButton(canvas, currentX, currentY, "E", eraserBitmap, 0xFFFFFFFF);
        currentX += buttonWidth + buttonMargin;
        Bitmap currentPencil = gameBoard.bigNumber ? pencilBitmap : littlePencilBitmap;
        drawToolButton(canvas, currentX, currentY, "P", currentPencil, gameBoard.bigNumber ? 0xFFFFFFFF : 0xFFC5CAE9);
        currentX += buttonWidth + buttonMargin;
        drawToolButton(canvas, currentX, currentY, "HELP", null, 0xFFC8E6C9);
    }

    private void drawToolButton(Canvas canvas, float x, float y, String text, Bitmap icon, int color) {
        RectF rect = new RectF(x, y, x + buttonWidth, y + buttonWidth);
        paint.setColor(color);
        canvas.drawRoundRect(rect, buttonRadius, buttonRadius, paint);

        if (icon != null) {
            int margin = (int) (buttonWidth * 0.1f);
            canvas.drawBitmap(icon, x + margin, y + margin, paint);
        } else {
            paint.setColor(Color.BLACK);
            paint.setTextSize(buttonWidth * 0.3f);
            canvas.drawText(text, rect.centerX(), rect.centerY() + (buttonWidth * 0.1f), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) { return true; }
    @Override
    public void onShowPress(MotionEvent e) {}
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override
    public void onLongPress(MotionEvent e) {}
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (e.getY() < gridWidth) {
            int cellX = (int) (e.getX() / cellWidth);
            int cellY = (int) (e.getY() / cellWidth);
            gameBoard.currentCellX = cellX;
            gameBoard.currentCellY = cellY;

            // Son facultatif quand on touche la grille
            // playClickSound();

            invalidate();
            return true;
        }

        if (e.getY() < buttonsTop) return true;

        float startY = buttonsTop + buttonWidth * 0.6f;
        float currentX = buttonMargin;
        float currentY = startY;

        // --- Clics sur les chiffres (1-9) ---
        for (int i = 1; i <= 9; i++) {
            RectF rect = new RectF(currentX, currentY, currentX + buttonWidth, currentY + buttonWidth);
            if (rect.contains(e.getX(), e.getY())) {

                // --- JOUE LE SON ICI ---
                playClickSound();
                // -----------------------

                gameBoard.pushValue(i);
                checkWinCondition();
                invalidate();
                return true;
            }
            if (i == 5) {
                currentX = buttonMargin;
                currentY += buttonWidth + buttonMargin;
            } else {
                currentX += buttonWidth + buttonMargin;
            }
        }

        // --- Clics sur les outils ---
        currentX = buttonMargin;
        currentY += buttonWidth + buttonMargin;

        RectF backRect = new RectF(currentX, currentY, currentX + buttonWidth, currentY + buttonWidth);
        if (backRect.contains(e.getX(), e.getY())) {
            playClickSound(); // Son aussi pour Back
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
            return true;
        }

        currentX += buttonWidth + buttonMargin;
        RectF eraserRect = new RectF(currentX, currentY, currentX + buttonWidth, currentY + buttonWidth);
        if (eraserRect.contains(e.getX(), e.getY())) {
            playClickSound(); // Son aussi pour la gomme
            gameBoard.clearCell();
            invalidate();
            return true;
        }

        currentX += buttonWidth + buttonMargin;
        RectF pencilRect = new RectF(currentX, currentY, currentX + buttonWidth, currentY + buttonWidth);
        if (pencilRect.contains(e.getX(), e.getY())) {
            playClickSound(); // Son pour le mode crayon
            gameBoard.bigNumber = !gameBoard.bigNumber;
            invalidate();
            return true;
        }

        currentX += buttonWidth + buttonMargin;
        RectF helpRect = new RectF(currentX, currentY, currentX + buttonWidth, currentY + buttonWidth);
        if (helpRect.contains(e.getX(), e.getY())) {
            playClickSound(); // Son pour l'aide
            showRulesDialog();
            return true;
        }

        return true;
    }
}