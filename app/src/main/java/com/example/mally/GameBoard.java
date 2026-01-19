package com.example.mally;

public class GameBoard {
    public static class GameCell {
        public int realValue;
        public int assumedValue;
        public boolean isInitial = false;
        public boolean [] marks = { false, false, false, false, false, false, false, false, false };

        public GameCell( int realValue ) {
            this.realValue = realValue;
        }

        public GameCell( int realValue, int isInitial ) {
            this.realValue = realValue;
            this.isInitial = isInitial == 1;
            if ( this.isInitial ) this.assumedValue = realValue;
        }
    }

    public GameLevel level;
    public boolean bigNumber = true;
    public int currentCellX = -1;
    public int currentCellY = -1;

    // --- NOUVEAU : Score ---
    public int score = 0;

    public GameCell [][] cells;

    private GameBoard( GameLevel level, GameCell [][] cells ) {
        this.level = level;
        this.cells = cells;
    }

    public int getSelectedValue() {
        if ( this.currentCellX == -1 || this.currentCellY == -1 ) return 0;
        return this.cells[ this.currentCellY ][ this.currentCellX ].assumedValue;
    }

    /**
     * Insère une valeur et met à jour le score selon les règles.
     */
    public void pushValue( int value ) {
        if ( this.currentCellX == -1 || this.currentCellY == -1 ) return;
        GameCell currentCell = this.cells[ this.currentCellY ][ this.currentCellX ];
        if ( currentCell.isInitial ) return;

        if ( this.bigNumber ) {
            // Si la valeur est correcte (correspond à la vraie valeur)
            if (value == currentCell.realValue && currentCell.assumedValue != value) {
                score += 100; // Bonus pour bonne réponse
            } else if (value != currentCell.realValue) {
                score -= 10; // Pénalité pour erreur
            }
            currentCell.assumedValue = value;
        } else {
            currentCell.marks[value-1] = ! currentCell.marks[value-1];
        }
    }

    /**
     * Utilise l'aide pour révéler la case sélectionnée.
     */
    public void useHelp() {
        if ( this.currentCellX == -1 || this.currentCellY == -1 ) return;
        GameCell currentCell = this.cells[ this.currentCellY ][ this.currentCellX ];
        if ( currentCell.isInitial ) return;

        currentCell.assumedValue = currentCell.realValue;
        score -= 50; // Grosse pénalité pour utilisation de l'aide
    }

    public void clearCell() {
        if ( this.currentCellX == -1 || this.currentCellY == -1 ) return;
        GameCell currentCell = this.cells[ this.currentCellY ][ this.currentCellX ];
        if ( currentCell.isInitial ) return;

        currentCell.assumedValue = 0;
        currentCell.marks = new boolean[] { false, false, false, false, false, false, false, false, false };
    }

    public static GameBoard getGameBoard( GameLevel level ) {
        if ( level != GameLevel.MEDIUM ) throw new RuntimeException( "Not actually implemented" );
        // Grille d'exemple (inchangée)
        return new GameBoard( level, new GameCell[][] {
                { new GameCell(9,1), new GameCell(2,0), new GameCell(8,0), new GameCell(7,1), new GameCell(5,0), new GameCell(4,0), new GameCell(1,1), new GameCell(3,0), new GameCell(6,0) },
                { new GameCell(6,0), new GameCell(7,0), new GameCell(1,0), new GameCell(8,1), new GameCell(2,0), new GameCell(3,1), new GameCell(5,0), new GameCell(4,0), new GameCell(9,0) },
                { new GameCell(3,0), new GameCell(5,1), new GameCell(4,1), new GameCell(9,0), new GameCell(1,1), new GameCell(6,0), new GameCell(2,0), new GameCell(7,1), new GameCell(8,0) },
                { new GameCell(4,1), new GameCell(9,1), new GameCell(6,0), new GameCell(2,0), new GameCell(3,0), new GameCell(7,0), new GameCell(8,1), new GameCell(5,1), new GameCell(1,0) },
                { new GameCell(8,0), new GameCell(1,1), new GameCell(5,0), new GameCell(4,1), new GameCell(6,0), new GameCell(9,1), new GameCell(7,0), new GameCell(2,1), new GameCell(3,0) },
                { new GameCell(7,0), new GameCell(3,1), new GameCell(2,1), new GameCell(5,0), new GameCell(8,0), new GameCell(1,0), new GameCell(9,0), new GameCell(6,1), new GameCell(4,1) },
                { new GameCell(5,0), new GameCell(4,1), new GameCell(3,0), new GameCell(1,0), new GameCell(9,1), new GameCell(2,0), new GameCell(6,1), new GameCell(8,1), new GameCell(7,0) },
                { new GameCell(2,0), new GameCell(6,0), new GameCell(9,0), new GameCell(3,1), new GameCell(7,0), new GameCell(8,1), new GameCell(4,0), new GameCell(1,0), new GameCell(5,0) },
                { new GameCell(1,0), new GameCell(8,0), new GameCell(7,1), new GameCell(6,0), new GameCell(4,0), new GameCell(5,1), new GameCell(3,0), new GameCell(9,0), new GameCell(2,1) }
        });
    }
}