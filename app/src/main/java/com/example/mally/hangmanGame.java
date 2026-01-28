package com.example.mally;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
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
    private String currentUsername = "Joueur"; // Nom par défaut
    private MallyWord[] wordBank; // Notre banque de mots
    private java.util.Random random = new java.util.Random();
    private TextView hangman_indice;
    private MediaPlayer backgroundMusic;
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
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnHelp = findViewById(R.id.btnHelp);
        createKeyboard();
        initWords();
        pickNewWord();
        askUsernameAtStart();
        startBackgroundMusic();
        updateUI(); // Crée cette petite méthode pour rafraîchir l'affichage
        updateWordDisplay();
        btnReset.setOnClickListener(v -> resetGame());
        btnBack.setOnClickListener(v -> finish());
        btnHelp.setOnClickListener(v -> {
            String rules = "JEU 2. PENDU (HANGMAN)\n\n" +
                    "Objectif du jeu :\n" +
                    "- Deviner le mot caché avant que le pendu ne soit complet.\n\n" +
                    "Comment jouer :\n" +
                    "1. Un mot ou une phrase est choisi aléatoirement.\n" +
                    "2. Le mot est affiché avec des tirets à la place des lettres.\n" +
                    "3. Tu dois proposer une lettre à chaque tour.\n" +
                    "4. Si la lettre est dans le mot, elle apparaît à toutes ses positions.\n" +
                    "5. Si la lettre n’est pas dans le mot, une partie du pendu est dessinée.\n" +
                    "6. Tu as un nombre limité d’erreurs possibles avant que le pendu soit complet.\n\n" +
                    "Gagner :\n" +
                    "- Deviner toutes les lettres du mot avant que le pendu ne soit complet.\n\n" +
                    "Conseils :\n" +
                    "- Commence par les voyelles, elles apparaissent souvent dans les mots.\n" +
                    "- Essaie des lettres fréquentes comme 'S', 'T', 'R', 'N'.\n" +
                    "- Évite de répéter les lettres déjà proposées.";

            new AlertDialog.Builder(this)
                    .setTitle("Aide")
                    .setMessage(rules)
                    .setPositiveButton("OK", null)
                    .show();
        });

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
            // Dans checkLetter, quand isWordFound() est vrai :
            if (isWordFound()) {
                gameOver = true;
                disableAllButtons();
                hangman_img.setImageResource(R.drawable.hangman_win_icon);
                playSound(R.raw.gamewin_hangman);

                // SAUVEGARDE AUTOMATIQUE
                GameDatabaseHelper db = new GameDatabaseHelper(this);
                // On enregistre avec le nom récupéré au début
                db.saveHangmanScore(currentUsername, score);

                putVideoWin();
                Toast.makeText(this, "Victoire ! Score enregistré pour " + currentUsername, Toast.LENGTH_SHORT).show();
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
    private void startBackgroundMusic() {
        // Si le player n'existe pas, on le crée
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(this, R.raw.game_music);
            backgroundMusic.setLooping(true); // IMPORTANT : Pour que la musique tourne en boucle
            backgroundMusic.setVolume(0.5f, 0.5f); // Volume à 50% pour ne pas couvrir les bruitages
        }

        // Si la musique ne joue pas, on la lance
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Quand l'appli revient au premier plan, on relance la musique
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Quand l'appli passe en arrière-plan (ou écran éteint), on met pause
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Quand l'activité est détruite, on libère la mémoire
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
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
        container.setAlpha(0f);
        container.setVisibility(View.VISIBLE);
        container.animate()
                      .alpha(1f)
                      .setDuration(600)
                      .setListener(null);
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
        container.setAlpha(0f);
        container.setVisibility(View.VISIBLE);
        container.animate()
                      .alpha(1f)
                      .setDuration(600)
                      .setListener(null);
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

    //BASE DE DONNEE LOCAL
    @Override
    public void onBackPressed() {
        if (!gameOver) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Quitter la partie ?")
                    .setMessage("Ton score actuel sera perdu !")
                    .setPositiveButton("Quitter", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Rester", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
    private void askUsernameAtStart() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Bienvenue !");
        builder.setMessage("Entre ton pseudo pour cette session :");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Pseudo");
        builder.setView(input);
        builder.setCancelable(false);

        builder.setPositiveButton("C'est parti !", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                currentUsername = name;
            }
            // On affiche le nom dans l'UI si vous avez un TextView pour ça
            Toast.makeText(this, "Bonne chance " + currentUsername + " !", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }
    //METHODE FOR GET USERNAME
    /*private void initAudio() {
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

    }
    private void askForNameAndSave() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("🏆 VICTOIRE !");
        builder.setMessage("Entre ton pseudo pour le Hall of Fame :");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Pseudo");
        builder.setView(input);
        builder.setCancelable(false); // On oblige l'utilisateur à valider

        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            String username = input.getText().toString().trim();
            if (username.isEmpty()) username = "Inconnu";

            // Sauvegarde dans la DB
            GameDatabaseHelper db = new GameDatabaseHelper(this);
            db.saveHangmanScore(username, score); // Utilisation de la nouvelle méthode

            android.widget.Toast.makeText(this, "Score enregistré !", android.widget.Toast.LENGTH_SHORT).show();

            // Une fois le nom entré, on lance la vidéo
            putVideoWin();
        });

        builder.show();
    }
    */


}