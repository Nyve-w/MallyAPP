package com.example.mally;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.textfield.TextInputEditText;
public class hangmanGame extends AppCompatActivity {
    //DECLARATION DES VARIABLES
    private MallyWord[] wordBank; // Notre banque de mots
    private java.util.Random random = new java.util.Random();
    private TextView hangman_indice;
    Button btnReset;
    GridLayout grid;
    private TextView wordDisplay;
    private ImageView hangman_img;
    private String word;
    private String monIndice ;

    private char[] displayedWord;

    private boolean gameOver = false;
    private VideoView videoView;
    private FrameLayout container;
    private int score=0;

    private TextView tvScore, tvLives;
    private int lives = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman_game);
        mathcId();
        createKeyboard();
        initWords();
        pickNewWord();
        updateUI(); // Crée cette petite méthode pour rafraîchir l'affichage
        updateWordDisplay();
        btnReset.setOnClickListener(v -> resetGame());



    }
    //1.MATCHING ID
    private void mathcId(){
        videoView = findViewById(R.id.videoView);
        container = findViewById(R.id.winOverlay);
        hangman_indice = findViewById(R.id.hangman_inputText);
        tvScore = findViewById(R.id.tvScore);
        tvLives = findViewById(R.id.tvLives);
        hangman_img = findViewById(R.id.hangman_image);
        hangman_img.setImageResource(R.drawable.hangman_icon);
        wordDisplay = findViewById(R.id.wordDisplay);
        btnReset = findViewById(R.id.btnReset);
        grid = findViewById(R.id.gridLayout);


        //FOR SET
        hangman_indice.setText(monIndice);
    }

    //2.CREATION DES BOUTTONS
    private void createKeyboard(){
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (char letter : letters) {
            Button button = new Button(this);
            button.setText(String.valueOf(letter));
            button.setBackgroundResource(R.drawable.btn_color);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; // pour que chaque bouton prenne un poids égal
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // chaque colonne prend 1 poids
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);
            button.setOnClickListener(v -> {
                playSound(R.raw.button_sound);
                checkLetter(letter);
                button.setEnabled(false);
                button.setAlpha(0.5f);
            });

            grid.addView(button);
        }
        //DISPLAY WORD


    }



    //LETTRE

    //1.
    private void checkLetter(char letter) {
        if (gameOver) return;
        boolean found = false;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                displayedWord[i] = letter;
                found = true;
            }
        }

        if (found) {
            score+=20;
            //Toast.makeText(this, "🔥 Keep going!", LENGTH_SHORT).show();
            updateWordDisplay();
            updateUI();
            if (isWordFound()) {
                gameOver = true;
                disableAllButtons();
                hangman_img.setImageResource(R.drawable.hangman_win_icon);
                //Toast.makeText(this,hangmanScore,LENGTH_SHORT).show();
                playSound(R.raw.gamewin_hangman);
                putVideoWin();
                //Toast.makeText(this, "You win", LENGTH_SHORT).show();

            }
        } else {
            lives--;
            playSound(R.raw.bad_answwer);
            updateHangmanImage();
            updateUI();
            //Toast.makeText(this, "💀 You must improve yourself!", LENGTH_SHORT).show();
            if (lives<=0) {
                gameOver = true;
                disableAllButtons();
                Toast.makeText(this, "💀 You lost! Word was: " + word, LENGTH_LONG).show();
                putVideoLose();
                return;
            }

        }
    }
    //2.INITIALISATION DES MOTS
    private void initWords() {
        java.util.List<MallyWord> list = new java.util.ArrayList<>();
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(getAssets().open("mots.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    list.add(new MallyWord(parts[0], parts[1]));
                }
            }
            wordBank = list.toArray(new MallyWord[0]);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            // Fallback au cas où le fichier a un souci
            wordBank = new MallyWord[]{new MallyWord("ERREUR", "Fichier introuvable")};
        }
    }
    //3.CHOIX DU MOT
    private void pickNewWord() {
        // 1. Choisir un index au hasard
        int index = random.nextInt(wordBank.length);
        MallyWord selected = wordBank[index];

        // 2. Mettre à jour les variables globales
        word = selected.word.toUpperCase();
        monIndice = "Indice : " + selected.hint;

        // 3. Préparer les tirets
        displayedWord = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            displayedWord[i] = '_';
        }

        // 4. Mettre à jour l'affichage de l'indice
        hangman_indice.setText(monIndice);
    }




    private void updateWordDisplay() {
        StringBuilder sb = new StringBuilder();
        for (char c : displayedWord) {
            sb.append(c).append(" ");
        }
        wordDisplay.setText(sb.toString().trim());
    }

    //CHANGEMENT DES IMAGES POUR UN DRAW EN CAS DE MAUVAISE REPONSE
    private void updateHangmanImage() {
        int imageRes;
        int errorNumber = 6 - lives;

        switch (errorNumber) {
            case 1:
                imageRes = R.drawable.ic_baseline_back_24;
                break;
            case 2:
                imageRes = R.drawable.ic_baseline_games_24;
                break;
            case 3:
                imageRes = R.drawable.ic_baseline_home_24;
                break;
            case 4:
                imageRes = R.drawable.ic_baseline_menu_24;
                break;
            case 5:
                imageRes = R.drawable.ic_baseline_lock_24;
                break;
            case 6:
                imageRes = R.drawable.hangman_lose_icon;
                break;
            default:
                imageRes = R.drawable.hangman_lose_icon;
        }

        hangman_img.setImageResource(imageRes);
    }


    //COMPARAISON DES MOTS POUR RENVOYER _ QUI CORRESPONDS à LA TAILLE
    private boolean isWordFound() {
        for (char c : displayedWord) {
            if (c == '_') return false;
        }
        return true;
    }

    //METTRE DES VIDEOS

    //1.EN CAS DE VICTOIRE
          public void putVideoWin() {
        container.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse("android.resource://"
                + getPackageName() + "/" + R.raw.video_winners_game);

        videoView.setVideoURI(uri);
        videoView.start();
        MediaController controller = new MediaController(this);
        controller.setAnchorView(container);
        videoView.setMediaController(controller);
    }
    //2.EN CAS DE DEFAITE
          public void putVideoLose() {
        container.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse("android.resource://"
                + getPackageName() + "/" + R.raw.hangman_lose);

        videoView.setVideoURI(uri);
        videoView.start();
        MediaController controller = new MediaController(this);
        controller.setAnchorView(container);
        videoView.setMediaController(controller);

    }

    //JOUER LE SON
    private void playSound(int soundRes) {
        MediaPlayer mp = MediaPlayer.create(this, soundRes);
        mp.setOnCompletionListener(MediaPlayer::release); // Libère la mémoire après lecture
        mp.start();
    }

    //DESACTIVATION DES BOUTTONS
    private void disableAllButtons() {
        GridLayout grid = findViewById(R.id.gridLayout);
        for (int i = 0; i < grid.getChildCount(); i++) {
            View child = grid.getChildAt(i);
            child.setEnabled(false); // Désactive le clic
            child.setAlpha(0.5f);    // Grise le bouton visuellement
        }
    }


    //MISE A JOUR DES VIES EN CAS DE BAD REPONSE
    private void updateUI() {
        tvScore.setText("Score: " + score);

        StringBuilder hearts = new StringBuilder("Vies: ");
        for (int i = 0; i < lives; i++) {
            hearts.append("❤️");
        }
        tvLives.setText(hearts.toString());
    }


    //RELANCER LA PARTIE
    private void resetGame() {
        // 1. Remettre les variables à l'état initial
        gameOver = false;
        lives = 6;
        // On ne reset pas le score pour que le joueur puisse accumuler des points !

        // 2. Cacher la vidéo et arrêter le son
        container.setVisibility(View.GONE);
        videoView.stopPlayback();

        // 3. Réinitialiser le mot (tu pourras plus tard piocher dans une liste)
        pickNewWord();
        // 4. Update UI
        updateWordDisplay();
        updateUI();
        hangman_img.setImageResource(R.drawable.hangman_icon);

        // 5. Réactiver tous les boutons du clavier
        GridLayout grid = findViewById(R.id.gridLayout);
        for (int i = 0; i < grid.getChildCount(); i++) {
            View child = grid.getChildAt(i);
            child.setEnabled(true);
            child.setAlpha(1.0f);
        }
    }

    //CLASSE POUR LES MOTS
    class MallyWord {
        String word;
        String hint;

        MallyWord(String word, String hint) {
            this.word = word;
            this.hint = hint;
        }
    }
}