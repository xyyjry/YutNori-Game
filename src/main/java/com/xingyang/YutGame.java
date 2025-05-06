package src.main.java.com.xingyang;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Core logic for the Yut game
 */
public class YutGame {
    // Game players
    private List<Player> players;
    // Current player index
    private int currentPlayerIndex;
    // Set of Yut sticks
    private YutSet yutSet;
    // Whether the game is over
    private boolean gameOver;
    // Winner
    private Player winner;
    // Last toss result
    private YutSet.YutResult lastResult;
    // Extra turn flag (Yut or Mo grants extra toss)
    private boolean hasExtraTurn;

    // All positions on the board
    private BoardPoint[] boardPoints;

    // Last capture information
    private CaptureInfo lastCaptureInfo;

    /**
     * Class to record detailed capture information
     */
    public static class CaptureInfo {
        private Piece capturingPiece; // Capturing piece
        private Piece capturedPiece;  // Captured piece
        private int position;         // Capture position
        public long captureTime;      // Capture time

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

        // Check if capture occurred recently (within 5 seconds)
        public boolean isRecent() {
            return System.currentTimeMillis() - captureTime < 5000;
        }
    }

    /**
     * A point on the Yut game board
     */
    public static class BoardPoint {
        private int x, y;  // Coordinates
        private List<Piece> pieces = new ArrayList<>();  // Pieces at this point

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

        // If fewer than 2 players, add AI players
        if (numPlayers < 2) {
            for (int i = numPlayers; i < 2; i++) {
                players.add(new Player("AI" + (i - numPlayers + 1), playerColors[i], true));
            }
        }

        // Initialize Yut sticks
        yutSet = new YutSet();

        // Initialize board points
        initializeBoardPoints();

        // Game settings
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        hasExtraTurn = false;
    }

    /**
     * Initialize the board points
     */
    private void initializeBoardPoints() {
        boardPoints = new BoardPoint[30]; // Includes start, end, and path points

        // Standard path (main circular route on the board)
        boardPoints[0] = new BoardPoint(200, 550); // Start
        boardPoints[1] = new BoardPoint(300, 550);
        boardPoints[2] = new BoardPoint(400, 550);
        boardPoints[3] = new BoardPoint(500, 550);
        boardPoints[4] = new BoardPoint(600, 550);
        boardPoints[5] = new BoardPoint(700, 550); // Bottom-right corner
        boardPoints[6] = new BoardPoint(700, 450);
        boardPoints[7] = new BoardPoint(700, 350);
        boardPoints[8] = new BoardPoint(700, 250);
        boardPoints[9] = new BoardPoint(700, 150);
        boardPoints[10] = new BoardPoint(700, 50); // Top-right corner
        boardPoints[11] = new BoardPoint(600, 50);
        boardPoints[12] = new BoardPoint(500, 50);
        boardPoints[13] = new BoardPoint(400, 50);
        boardPoints[14] = new BoardPoint(300, 50);
        boardPoints[15] = new BoardPoint(200, 50); // Top-left corner
        boardPoints[16] = new BoardPoint(200, 150);
        boardPoints[17] = new BoardPoint(200, 250);
        boardPoints[18] = new BoardPoint(200, 350);
        boardPoints[19] = new BoardPoint(200, 450);

        // Special path (diagonal through center)
        boardPoints[20] = new BoardPoint(283, 467); // Center to bottom-right
        boardPoints[21] = new BoardPoint(366, 384);
        boardPoints[22] = new BoardPoint(533, 217);
        boardPoints[23] = new BoardPoint(614, 134);

        // Second diagonal path
        boardPoints[24] = new BoardPoint(283, 133); // Center to top-right
        boardPoints[25] = new BoardPoint(366, 216);
        boardPoints[26] = new BoardPoint(533, 383);
        boardPoints[27] = new BoardPoint(616, 466);

        // Center point
        boardPoints[28] = new BoardPoint(450, 300);

        // End point
        boardPoints[29] = new BoardPoint(200, 550);
    }

    /**
     * Get current player
     * @return current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Toss the Yut sticks
     * @return toss result
     */
    public YutSet.YutResult tossYut() {
        // Toss sticks and get result
        lastResult = yutSet.toss();

        // Yut or Mo grants extra turn
        hasExtraTurn = (lastResult == YutSet.YutResult.YUT || lastResult == YutSet.YutResult.MO);

        return lastResult;
    }

    /**
     * Move a piece
     * @param piece the piece to move
     * @return true if captured opponent or reached end
     */
    public boolean movePiece(Piece piece) {
        if (lastResult == null) {
            return false; // Must toss first
        }

        // Get old position
        int oldPosition = piece.getPosition();

        // Move piece
        boolean reachedEnd = piece.move(lastResult.getSteps());

        // Update board piece position
        if (oldPosition >= 0 && oldPosition < boardPoints.length) {
            boardPoints[oldPosition].removePiece(piece);
        }

        int newPosition = piece.getPosition();
        if (newPosition >= 0 && newPosition < boardPoints.length) {
            boardPoints[newPosition].addPiece(piece);

            // Check for opponent pieces on same spot
            List<Piece> piecesAtPosition = boardPoints[newPosition].getPieces();
            boolean captured = false;

            for (Piece otherPiece : new ArrayList<>(piecesAtPosition)) {
                if (otherPiece != piece && otherPiece.getOwner() != piece.getOwner()) {
                    // Capture opponent
                    otherPiece.backToHome();
                    boardPoints[newPosition].removePiece(otherPiece);
                    captured = true;

                    // Record capture info
                    lastCaptureInfo = new CaptureInfo(piece, otherPiece, newPosition);
                }
            }

            // Capture grants extra turn
            if (captured) {
                hasExtraTurn = true;
            }
        }

        // Reset last toss result
        lastResult = null;

        // Check if game over
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
            // Stay on current player
            hasExtraTurn = false;
        } else {
            // Switch to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    /**
     * Get last toss result
     * @return last result
     */
    public YutSet.YutResult getLastResult() {
        return lastResult;
    }

    /**
     * Whether there's an extra turn
     * @return true if extra turn
     */
    public boolean hasExtraTurn() {
        return hasExtraTurn;
    }

    /**
     * Get board points
     * @return array of board points
     */
    public BoardPoint[] getBoardPoints() {
        return boardPoints;
    }

    /**
     * Get board point at specific position
     * @param position index
     * @return board point
     */
    public BoardPoint getBoardPoint(int position) {
        if (position >= 0 && position < boardPoints.length) {
            return boardPoints[position];
        }
        return null;
    }

    /**
     * Get all players
     * @return list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Check if game is over
     * @return true if over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Get winner
     * @return winner
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Get Yut stick set
     * @return Yut set
     */
    public YutSet getYutSet() {
        return yutSet;
    }

    /**
     * Get last capture information
     * @return capture info
     */
    public CaptureInfo getLastCaptureInfo() {
        return lastCaptureInfo;
    }
}
