package src.main.java.com.xingyang;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Core logic of the Yut Game
 */
public class YutGame {
    // Players in the game
    private List<Player> players;
    // Index of the current player
    private int currentPlayerIndex;
    // Set of Yut sticks
    private YutSet yutSet;
    // Whether the game is over
    private boolean gameOver;
    // Winner of the game
    private Player winner;
    // Result of the last toss
    private YutSet.YutResult lastResult;
    // Whether there is an extra turn (due to Yut or Mo)
    private boolean hasExtraTurn;
    
    // All points on the board
    private BoardPoint[] boardPoints;
    
    // Last capture information
    private CaptureInfo lastCaptureInfo;
    
    /**
     * Capture information class, records details of a capture
     */
    public static class CaptureInfo {
        private Piece capturingPiece; // Piece that made the capture
        private Piece capturedPiece;  // Captured piece
        private int position;         // Position where the capture occurred
        public long captureTime;     // Time of capture
        
        public CaptureInfo(Piece capturingPiece, Piece capturedPiece, int position) {
            this.capturingPiece = capturingPiece;
            this.capturedPiece = capturedPiece;
            this.position = position;
            this.captureTime = System.currentTimeMillis();
        }
        
        public Piece getCapturingPiece() {
            return capturingPiece;
        }
        
        public Piece getCapturedPiece() {
            return capturedPiece;
        }
        
        public int getPosition() {
            return position;
        }
        
        // Check if the capture happened recently (within 5 seconds)
        public boolean isRecent() {
            return System.currentTimeMillis() - captureTime < 5000;
        }
    }
    
    /**
     * A point on the Yut Game board
     */
    public static class BoardPoint {
        private int x, y;  // Coordinates
        private List<Piece> pieces = new ArrayList<>();  // Pieces on this point
        
        public BoardPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public List<Piece> getPieces() {
            return pieces;
        }
        
        public void addPiece(Piece piece) {
            pieces.add(piece);
        }
        
        public void removePiece(Piece piece) {
            pieces.remove(piece);
        }
    }
    
    public YutGame(int numPlayers) {
        // Create players (up to 4)
        players = new ArrayList<>();
        Color[] playerColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        
        for (int i = 0; i < Math.min(numPlayers, 4); i++) {
            players.add(new Player("Player" + (i + 1), playerColors[i], false));
        }
        
        // If fewer than 2 human players, add computer players
        if (numPlayers < 2) {
            for (int i = numPlayers; i < 2; i++) {
                players.add(new Player("AI" + (i - numPlayers + 1), playerColors[i], true));
            }
        }
        
        // Initialize Yut sticks
        yutSet = new YutSet();
        
        // Initialize board points
        initializeBoardPoints();
        
        // Game setup
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        hasExtraTurn = false;
    }
    
    /**
     * Initialize the points on the board
     */
    private void initializeBoardPoints() {
        boardPoints = new BoardPoint[30]; // Includes start, end, and all path points
        
        // Create standard path points (around the board)
        // Coordinates are used for drawing and piece placement
        boardPoints[0] = new BoardPoint(300, 500); // Start
        boardPoints[1] = new BoardPoint(400, 500);
        boardPoints[2] = new BoardPoint(500, 500);
        boardPoints[3] = new BoardPoint(600, 500);
        boardPoints[4] = new BoardPoint(700, 500);
        boardPoints[5] = new BoardPoint(700, 400); // Bottom right
        boardPoints[6] = new BoardPoint(700, 300);
        boardPoints[7] = new BoardPoint(700, 200);
        boardPoints[8] = new BoardPoint(700, 100);
        boardPoints[9] = new BoardPoint(700, 0);
        boardPoints[10] = new BoardPoint(600, 0); // Top right
        boardPoints[11] = new BoardPoint(500, 0);
        boardPoints[12] = new BoardPoint(400, 0);
        boardPoints[13] = new BoardPoint(300, 0);
        boardPoints[14] = new BoardPoint(200, 0);
        boardPoints[15] = new BoardPoint(200, 100); // Top left
        boardPoints[16] = new BoardPoint(200, 200);
        boardPoints[17] = new BoardPoint(200, 300);
        boardPoints[18] = new BoardPoint(200, 400);
        boardPoints[19] = new BoardPoint(200, 500);
        
        // Special path points (center diagonals)
        boardPoints[20] = new BoardPoint(300, 400); // Center to bottom right
        boardPoints[21] = new BoardPoint(400, 300);
        boardPoints[22] = new BoardPoint(500, 200);
        boardPoints[23] = new BoardPoint(600, 100);
        
        // Second diagonal
        boardPoints[24] = new BoardPoint(300, 100); // Center to top right
        boardPoints[25] = new BoardPoint(400, 200);
        boardPoints[26] = new BoardPoint(500, 300);
        boardPoints[27] = new BoardPoint(600, 400);
        
        // Center point
        boardPoints[28] = new BoardPoint(450, 250);
        
        // End point
        boardPoints[29] = new BoardPoint(300, 500);
    }
    
    /**
     * Get the current player
     * @return Current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    /**
     * Toss the Yut sticks
     * @return Toss result
     */
    public YutSet.YutResult tossYut() {
        // Toss sticks and get result
        lastResult = yutSet.toss();
        
        // Yut or Mo gives an extra turn
        hasExtraTurn = (lastResult == YutSet.YutResult.YUT || lastResult == YutSet.YutResult.MO);
        
        return lastResult;
    }
    
    /**
     * Move a piece
     * @param piece The piece to move
     * @return True if captured or reached the end
     */
    public boolean movePiece(Piece piece) {
        if (lastResult == null) {
            return false; // Must toss first
        }
        
        // Get previous position
        int oldPosition = piece.getPosition();
        
        // Move the piece
        boolean reachedEnd = piece.move(lastResult.getSteps());
        
        // Update board piece positions
        if (oldPosition >= 0 && oldPosition < boardPoints.length) {
            boardPoints[oldPosition].removePiece(piece);
        }
        
        int newPosition = piece.getPosition();
        if (newPosition >= 0 && newPosition < boardPoints.length) {
            boardPoints[newPosition].addPiece(piece);
            
            // Check for opponent pieces at the same position
            List<Piece> piecesAtPosition = boardPoints[newPosition].getPieces();
            boolean captured = false;
            
            for (Piece otherPiece : new ArrayList<>(piecesAtPosition)) {
                if (otherPiece != piece && otherPiece.getOwner() != piece.getOwner()) {
                    // Capture the opponent piece
                    otherPiece.backToHome();
                    boardPoints[newPosition].removePiece(otherPiece);
                    captured = true;
                    
                    // Record capture info
                    lastCaptureInfo = new CaptureInfo(piece, otherPiece, newPosition);
                }
            }
            
            // Capturing gives an extra turn
            if (captured) {
                hasExtraTurn = true;
            }
        }
        
        // Reset last result
        lastResult = null;
        
        // Check for game end
        if (getCurrentPlayer().hasWon()) {
            gameOver = true;
            winner = getCurrentPlayer();
        }
        
        return reachedEnd || hasExtraTurn;
    }
    
    /**
     * Proceed to next turn
     */
    public void nextTurn() {
        if (hasExtraTurn) {
            // Extra turn, do not switch player
            hasExtraTurn = false;
        } else {
            // Switch to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }
    
    /**
     * Get the last toss result
     * @return Last toss result
     */
    public YutSet.YutResult getLastResult() {
        return lastResult;
    }
    
    /**
     * Check if there is an extra turn
     * @return True if extra turn exists
     */
    public boolean hasExtraTurn() {
        return hasExtraTurn;
    }
    
    /**
     * Get board points
     * @return Board point array
     */
    public BoardPoint[] getBoardPoints() {
        return boardPoints;
    }
    
    /**
     * Get a specific point on the board
     * @param position Index of the position
     * @return Board point
     */
    public BoardPoint getBoardPoint(int position) {
        if (position >= 0 && position < boardPoints.length) {
            return boardPoints[position];
        }
        return null;
    }
    
    /**
     * Get all players
     * @return List of players
     */
    public List<Player> getPlayers() {
        return players;
    }
    
    /**
     * Check if the game is over
     * @return True if over
     */
    public boolean isGameOver() {
        return gameOver;
    }
    
    /**
     * Get the winner
     * @return Winner player
     */
    public Player getWinner() {
        return winner;
    }
    
    /**
     * Get the Yut set
     * @return Yut set
     */
    public YutSet getYutSet() {
        return yutSet;
    }
    
    /**
     * Get the last capture info
     * @return Capture info
     */
    public CaptureInfo getLastCaptureInfo() {
        return lastCaptureInfo;
    }
}
