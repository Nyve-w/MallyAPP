package com.example.mally;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View implements GestureDetector.OnGestureListener
{
    private long startTime;
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

    public GameView(Context context, AttributeSet attrs) {
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
        headerForegroundColor = res.getColor( R.color.headerForegroundColor );
        backgroundColor = res.getColor( R.color.backgroundColor );
        redColor = res.getColor( R.color.redColor );
        startTime = System.currentTimeMillis();
        timerHandler.post(timerRunnable);
        dbHelper = new GameDatabaseHelper(getContext());

        // 🔥 ICI tu récupères les données
        bestScore = dbHelper.getBestScore();
        bestTime = dbHelper.getBestTime();


    }


    /**
     * Retaillage des bitmaps en fonction de la taille de la fenêtre.
     */
    @Override
    protected void onSizeChanged( int width, int height, int oldw, int oldh ) {
        super.onSizeChanged( width, height, oldw, oldh );

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
            imgBack = Bitmap.createScaledBitmap(imgBack, (int)deckWidth, (int)deckHeight, true);

        } catch (Exception exception) {
            Log.e("ERROR", "Cannot load card images");
        }

    }
    /**
     * Calcul de la "bounding box" de la stack spécifiée en paramètre.
     */
    private RectF computeStackRect(int index ) {
        float x = deckMargin + (deckWidth + deckMargin) * index;
        float y = getHeight() * 0.17f;
        return new RectF( x, y, x+deckWidth, y+deckHeight );
    }


    /**
     * Calcul de la "bounding box" de la pile retournée associée à la pioche.
     */
    private RectF computeReturnedPiocheRect() {
        float x = deckMargin + (deckWidth + deckMargin) * 5;
        float y = getHeight() * 0.17f;
        return new RectF( x, y, x+deckWidth, y+deckHeight );
    }


    /**
     * Calcul de la "bounding box" de la pile découverte associée à la pioche.
     */
    private RectF computePiocheRect() {
        float x = deckMargin + (deckWidth + deckMargin) * 6;
        float y = getHeight() * 0.17f;
        return new RectF( x, y, x+deckWidth, y+deckHeight );
    }


    /**
     * Calcul de la "bounding box" du deck spécifié en paramètre.
     */
    private RectF computeDeckRect( int index, int cardIndex ) {
        float x = deckMargin + (deckWidth + deckMargin) * index;
        float y = getHeight() * 0.30f + cardIndex * computeStepY();
        return new RectF( x, y, x+deckWidth, y+deckHeight );
    }


    /**
     * Calcul du décalage en y pour toutes les cartes d'un deck.
     */
    public float computeStepY() {
        return ( getHeight()*0.9f - getHeight()*0.3f ) / 17f;
    }
    private Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );


    /**
     * Cette méthode permet de tracer une carte à la position spécifiée en paramètre.
     * @param canvas Le canvas à utiliser.
     * @param card  La carte à dessiner. Si vous passez un pointeur nul,
     *              juste le contour de la carte sera tracé (état initial des stacks par exemple).
     * @param x La position en x de tracé.
     * @param y La position en y de tracé.
     */
    public void drawCard(Canvas canvas, Card card, float x, float y ) {
        float cornerWidth = deckWidth / 10f;

        RectF rectF = new RectF( x, y, x + deckWidth, y + deckHeight );

        // Si card == null alors on ne trace que le contour de la carte
        if ( card == null ) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xff_40_40_40);
            canvas.drawRoundRect(rectF, cornerWidth, cornerWidth, paint);
            paint.setStyle(Paint.Style.FILL);
            return;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor( card.isReturned() ? 0xff_ff_ff_ff : 0xff_a0_c0_a0 );
        canvas.drawRoundRect(rectF, cornerWidth, cornerWidth, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor( 0xff_00_00_00 );
        canvas.drawRoundRect(rectF, cornerWidth, cornerWidth, paint);

        if ( card.isReturned() ) {
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
            paint.setTextSize( deckWidth / 2.4f );
            paint.setFakeBoldText( true );
            paint.setTextAlign( Paint.Align.LEFT );
            paint.setColor( color );
            if ( card.getValue() != 10 ) {
                canvas.drawText(card.getName(), x + deckWidth * 0.1f, y + deckHeight * 0.32f, paint);
            } else {
                canvas.drawText( "1", x + deckWidth * 0.1f, y + deckHeight * 0.32f, paint);
                canvas.drawText( "0", x + deckWidth * 0.3f, y + deckHeight * 0.32f, paint);
            }
            canvas.drawBitmap( imageLittle, x + deckWidth*0.9f - imageLittle.getWidth(),
                    y + deckHeight * 0.1f, paint );
            canvas.drawBitmap( image, x + (deckWidth - image.getWidth())/ 2f,
                    y + (deckHeight*0.9f - image.getHeight()) , paint );
            paint.setFakeBoldText( false );
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


    /**
     * On trace l'aire de jeu
     * @param canvas Le canvas à utiliser.
     */

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // --- Background ---
        paint.setColor(backgroundColor);
        paint.setStyle( Paint.Style.FILL );
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        // --- Draw the Header ---

        float widthDiv10 = getWidth() / 10f;
        float heightDiv10 = getHeight() / 10f;

        paint.setColor( headerBackgroundColor );
        RectF rectF = new RectF(0, 0, getWidth(), getHeight() * 0.15f);
        canvas.drawRect(rectF, paint);

        paint.setColor(redColor);
        paint.setTextAlign( Paint.Align.CENTER );
        paint.setTextSize( (int) (getWidth() / 8.5) );
        canvas.drawText( getResources().getString(R.string.app_name),
                widthDiv10 * 5, (int) (heightDiv10 * 0.8), paint );

        paint.setColor( headerForegroundColor );
        paint.setTextAlign( Paint.Align.LEFT );
        paint.setTextSize( getWidth() / 20f );
        paint.setStrokeWidth(1);

        canvas.drawText(
                "Score : " + game.score,
                getWidth() * 0.05f,
                getHeight() * 0.12f,
                paint
        );
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(getWidth() / 25f);
        paint.setColor(headerForegroundColor);

        canvas.drawText(
                "Best Score: " + bestScore,
                getWidth() * 0.05f,
                getHeight() * 0.09f,
                paint
        );



        paint.setTextAlign( Paint.Align.RIGHT );
        canvas.drawText( "By Mally.fr", (int) (widthDiv10 * 0.03), (int) (heightDiv10 * 1.3), paint );
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(getWidth() / 25f);
        paint.setColor(headerForegroundColor);


        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(
                "Time : " + getFormattedTime(),
                getWidth() * 0.95f,
                getHeight() * 0.12f,
                paint
        );


        // --- Draw the fourth stacks ---
        paint.setStrokeWidth( getWidth() / 200f );

        for (int i = 0; i < Game.STACK_COUNT; i++) {
            Game.Stack stack = game.stacks[i];
            rectF = computeStackRect( i );
            drawCard( canvas, stack.isEmpty() ? null : stack.lastElement(), rectF.left, rectF.top );
        }

        // --- Draw the pioche ---
        rectF = computeReturnedPiocheRect();
        drawCard( canvas, game.returnedPioche.isEmpty() ? null : game.returnedPioche.lastElement(),
                rectF.left, rectF.top );

        rectF = computePiocheRect();
        drawCard(canvas, game.pioche.isEmpty() ? null : game.pioche.lastElement(), rectF.left, rectF.top);

        // --- Draw the seven decks ---
        for ( int i = 0; i < Game.DECK_COUNT; i++ ) {
            Game.Deck deck = game.decks[i];

            if ( deck.isEmpty() ) {
                rectF = computeDeckRect(i, 0);
                drawCard( canvas, null, rectF.left, rectF.top );
            } else {
                for ( int cardIndex = 0; cardIndex < deck.size(); cardIndex++ ) {
                    Card card = deck.get(cardIndex);
                    rectF = computeDeckRect(i, cardIndex);
                    drawCard(canvas, card, rectF.left, rectF.top);
                }
            }

        }
    }
// --- OnGestureDetector interface ----

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);     // Le widget repasse la main au GestureDetector.
    }

    // On réagit à un appui simple sur le widget.
    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        RectF rect;

        // --- Un tap sur les cartes non retournées de la pioche ---
        rect = computePiocheRect();
        if ( rect.contains( e.getX(), e.getY() ) ) {
            if ( ! game.pioche.isEmpty() ) {
                Card card = game.pioche.remove(0);
                card.setReturned( true );
                game.returnedPioche.add( card );
            } else {
                game.pioche.addAll( game.returnedPioche );
                game.returnedPioche.clear();
                for( Card card : game.pioche ) card.setReturned( false );
            }
            checkEndGame();
            postInvalidate();
            return true;
        }

        // --- Un tap sur les cartes retournées de la pioche ---
        rect = computeReturnedPiocheRect();
        if ( rect.contains( e.getX(), e.getY() ) && ! game.returnedPioche.isEmpty() ) {
            final int stackIndex = game.canMoveCardToStack( game.returnedPioche.lastElement() );
            if ( stackIndex > -1 ) {
                Card selectedCard = game.returnedPioche.remove(game.returnedPioche.size() - 1);
                game.stacks[stackIndex].add( selectedCard );
                postInvalidate();
                return true;
            }

            final int deckIndex = game.canMoveCardToDeck( game.returnedPioche.lastElement() );
            if ( deckIndex > -1 ) {
                Card selectedCard = game.returnedPioche.remove( game.returnedPioche.size() - 1 );
                game.decks[deckIndex].add( selectedCard );
                postInvalidate();
                game.addScore(10);
                checkEndGame();
                postInvalidate();
            }
        }

        // --- Un tap sur une carte d'une deck ---
        for( int deckIndex=0; deckIndex<Game.DECK_COUNT; deckIndex++ ) {
            final Game.Deck deck = game.decks[deckIndex];
            if ( ! deck.isEmpty() ) {
                for( int i=deck.size()-1; i>=0; i-- ) {
                    rect = computeDeckRect(deckIndex, i);
                    if ( rect.contains(e.getX(), e.getY()) ) {
                        // Click sur carte non retournée de la deck => on sort
                        Card currentCard = deck.get(i);
                        if ( ! currentCard.isReturned() ) return true;

                        // Peut-on déplacer la carte du sommet de la deck vers une stack ?
                        if ( i == deck.size() - 1 ) {       // On vérifie de bien être sur le sommet
                            int stackIndex = game.canMoveCardToStack(currentCard);
                            if (stackIndex > -1) {
                                Card selectedCard = deck.remove(deck.size() - 1);
                                if ( ! deck.isEmpty() ) deck.lastElement().setReturned(true);
                                game.stacks[stackIndex].add( selectedCard );
                                game.addScore(10);

                                checkEndGame();
                                postInvalidate();
                                return true;
                            }
                        }

                        // Peut-on déplacer la carte de la deck vers une autre deck ?
                        final int deckIndex2 = game.canMoveCardToDeck( currentCard );
                        if (deckIndex2 > -1) {
                            if ( i == deck.size() - 1 ) {
                                // On déplace qu'un carte
                                Card selectedCard = deck.remove(deck.size() - 1);
                                if ( ! deck.isEmpty() ) {
                                    deck.lastElement().setReturned(true);
                                }
                                game.decks[deckIndex2].add( selectedCard );
                            } else {
                                // On déplace plusieurs cartes
                                final ArrayList<Card> selectedCards = new ArrayList<>();
                                for( int ci=deck.size()-1; ci>=i; ci-- ) {
                                    selectedCards.add( 0, deck.remove( ci ) );
                                }
                                if ( ! deck.isEmpty() ) {
                                    deck.lastElement().setReturned(true);
                                }
                                game.decks[deckIndex2].addAll( selectedCards );
                            }
                            postInvalidate();
                            return true;
                        }

                        return true;
                    }
                }
            }
        }

        return true;
    }
    private boolean gameFinished = false;

    private void checkEndGame() {
        if (gameFinished) return;

        if (game.isGameFinished()) {
            gameFinished = true;

            timerHandler.removeCallbacks(timerRunnable);

            // ✅ UNE SEULE SAUVEGARDE
            dbHelper.saveScore(game.score, elapsedTime);

            // 🔄 Recharge après sauvegarde

            bestScore = dbHelper.getBestScore();
            bestTime = dbHelper.getBestTime();

            Log.d("GAME", "🎉 Partie terminée !");
            Log.d("GAME", "Score : " + game.score);
            Log.d("GAME", "Temps : " + getFormattedTime());

            // plus tard : dialog victoire
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
    public Game getGame() {
        return game;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

}




