package src.main.java.com.xingyang;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Player in the Yut game
 */
public class Player {
    private String name;
    private Color color;
    private List<Piece> pieces;
    private boolean isComputer;
    
    // Number of pieces each player has
    private static final int PIECE_COUNT = 4;
    
    public Player(String name, Color color, boolean isComputer) {
        this.name = name;
        this.color = color;
        this.isComputer = isComputer;
        
        // Create pieces
        pieces = new ArrayList<>();
        for (int i = 0; i < PIECE_COUNT; i++) {
            pieces.add(new Piece(i, this, color));
        }
    }
    
    /**
     * Get player's name
     * @return Player's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get player's color
     * @return Player's color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Check if the player is a computer
     * @return Whether the player is a computer
     */
    public boolean isComputer() {
        return isComputer;
    }
    
    /**
     * Get all the player's pieces
     * @return List of pieces
     */
    public List<Piece> getPieces() {
        return pieces;
    }
    
    /**
     * Get list of movable pieces
     * @return List of movable pieces
     */
    public List<Piece> getMovablePieces() {
        List<Piece> movablePieces = new ArrayList<>();
        
        for (Piece piece : pieces) {
            if (!piece.isFinished()) {
                movablePieces.add(piece);
            }
        }
        
        return movablePieces;
    }
    
    /**
     * Get list of pieces at home (not yet moved)
     * @return List of home pieces
     */
    public List<Piece> getHomePieces() {
        List<Piece> homePieces = new ArrayList<>();
        
        for (Piece piece : pieces) {
            if (piece.isHome()) {
                homePieces.add(piece);
            }
        }
        
        return homePieces;
    }
    
    /**
     * Get list of pieces on the board
     * @return List of pieces on the board
     */
    public List<Piece> getBoardPieces() {
        List<Piece> boardPieces = new ArrayList<>();
        
        for (Piece piece : pieces) {
            if (!piece.isHome() && !piece.isFinished()) {
                boardPieces.add(piece);
            }
        }
        
        return boardPieces;
    }
    
    /**
     * Get list of finished pieces
     * @return List of finished pieces
     */
    public List<Piece> getFinishedPieces() {
        List<Piece> finishedPieces = new ArrayList<>();
        
        for (Piece piece : pieces) {
            if (piece.isFinished()) {
                finishedPieces.add(piece);
            }
        }
        
        return finishedPieces;
    }
    
    /**
     * Check if the player has won
     * @return Whether the player has won
     */
    public boolean hasWon() {
        return getFinishedPieces().size() == PIECE_COUNT;
    }
}
