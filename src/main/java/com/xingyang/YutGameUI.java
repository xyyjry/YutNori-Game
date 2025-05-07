package com.xingyang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Yut game UI interface
 */
public class YutGameUI extends JFrame {
    private YutGame game;
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JPanel playerInfoPanel;
    private JPanel controlPanel;
    private JButton tossButton;
    private JButton nextTurnButton;
    private JLabel statusLabel;
    private JLabel resultLabel;
    
    // Selected piece
    private Piece selectedPiece;
    
    // Board size
    private static final int BOARD_SIZE = 600;
    private static final int POINT_SIZE = 30;
    private static final int PIECE_SIZE = 20;
    
    // Piece images
    private List<Image> pieceImages;
    
    // Toss animation related variables
    private boolean isAnimating = false;
    private Timer animationTimer;
    private int animationStep = 0;
    private final int TOTAL_ANIMATION_STEPS = 15;
    private final Random random = new Random();
    private YutStick[] animatingSticks;
    
    public YutGameUI() {
        // Create game instance (2 players)
        game = new YutGame(2);
        
        // Initialize UI
        initializeUI();
        
        // Load piece images
        loadPieceImages();
    }
    
    /**
     * Load piece images
     */
    private void loadPieceImages() {
        pieceImages = new ArrayList<>();
        
        try {
            // Use simple drawing as a replacement for images
            pieceImages = null;
        } catch (Exception e) {
            System.err.println("Failed to load piece images: " + e.getMessage());
        }
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        setTitle("Yut Game (Yut Nori)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800); // Increase window size to provide enough space for all content
        setLocationRelativeTo(null);
        
        // Set overall layout
        mainPanel = new JPanel(new BorderLayout(20, 20)); // Add spacing between components
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Set elegant background gradient
        mainPanel.setBackground(new Color(245, 240, 230));
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Yut Game (Yut Nori)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); 
        titleLabel.setForeground(new Color(120, 60, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Korean traditional folk game");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16)); 
        subtitleLabel.setForeground(new Color(150, 90, 30));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        titlePanel.add(Box.createVerticalStrut(15));
        
        // Create board panel - take up more space in the center
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(700, 700));
        
        // Create more exquisite border
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 60, 30), 3),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Create right-side information panel, use vertical layout
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        rightPanel.setPreferredSize(new Dimension(350, 700)); // Increase right panel width
        
        // Create player info panel
        playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.Y_AXIS));
        playerInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 80, 40), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        playerInfoPanel.setBackground(new Color(255, 250, 240));
        playerInfoPanel.setMaximumSize(new Dimension(300, 400)); // Limit player info panel height
        
        // Update player info
        updatePlayerInfo();
        
        // Create control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 80, 40), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        controlPanel.setBackground(new Color(255, 250, 240));
        
        // Control panel title
        JLabel controlTitle = new JLabel("Game Control");
        controlTitle.setFont(new Font("Arial", Font.BOLD, 16));
        controlTitle.setForeground(new Color(120, 60, 0));
        controlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(controlTitle);
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Game status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        statusLabel = new JLabel("Game Started");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(50, 50, 50));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        resultLabel.setForeground(new Color(160, 80, 0));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(resultLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(300, 40));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        tossButton = createStyledButton("Toss Sticks", new Color(225, 180, 135), new Color(80, 40, 0));
        nextTurnButton = createStyledButton("End Turn", new Color(180, 200, 220), new Color(0, 40, 80));
        
        // Set button state
        nextTurnButton.setEnabled(false);
        
        // Add button listeners
        tossButton.addActionListener(e -> handleToss());
        nextTurnButton.addActionListener(e -> handleNextTurn());
        
        buttonPanel.add(tossButton);
        buttonPanel.add(nextTurnButton);
        
        // Add components to control panel and add spacing
        controlPanel.add(statusPanel);
        controlPanel.add(Box.createVerticalStrut(20));
        controlPanel.add(buttonPanel);
        
        // Add to right panel
        rightPanel.add(playerInfoPanel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(controlPanel);
        rightPanel.add(Box.createVerticalGlue()); // Add flexible space
        
        // Add to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        setContentPane(mainPanel);
        
        // Update game status
        updateGameStatus();
    }
    
    /**
     * Create styled button
     */
    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(foreground, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add mouse hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(background.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
        
        return button;
    }
    
    /**
     * Handle toss button
     */
    private void handleToss() {
        // Disable toss button until a piece is selected or the turn ends
        tossButton.setEnabled(false);
        
        // Show toss in progress message - ensure the text is clearly displayed
        statusLabel.setText("Tossing sticks...");
        resultLabel.setText("");
        
        // Notify the user
        JOptionPane.showMessageDialog(this, "Starting the toss!", "Toss Animation", JOptionPane.INFORMATION_MESSAGE);
        
        // Start toss animation
        startTossAnimation();
    }
    
    /**
     * Start toss animation
     */
    private void startTossAnimation() {
        // Initialize animation variables
        isAnimating = true;
        animationStep = 0;
        
        // Create copies of sticks for animation
        YutStick[] origSticks = game.getYutSet().getSticks();
        animatingSticks = new YutStick[origSticks.length];
        for (int i = 0; i < origSticks.length; i++) {
            animatingSticks[i] = new YutStick();
        }
        
        // Create animation timer, updating every 60ms
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        animationTimer = new Timer(60, e -> {
            animationStep++;
            
            // During animation, randomly change the stick state to create flipping effect
            if (animationStep < TOTAL_ANIMATION_STEPS) {
                for (YutStick stick : animatingSticks) {
                    // 70% chance to change state, making it look like flipping
                    if (random.nextDouble() < 0.7) {
                        if (stick.isFlatSide()) {
                            stick.setFlatSide(false);
                        } else {
                            stick.setFlatSide(true);
                        }
                    }
                }
                
                // Redraw board to show animation effects
                boardPanel.repaint();
            } else {
                // Animation ends, perform actual toss
                finishTossAnimation();
            }
        });
        
        animationTimer.start();
    }

    /**
     * Finish the toss animation and display the result
     */
    private void finishTossAnimation() {
        // Stop the animation
        isAnimating = false;
        animationTimer.stop();
        
        // Perform the actual toss
        YutSet.YutResult result = game.tossYut();
        
        // Display the result
        String resultText = "Result: " + result.getName() + " (" + result.getSteps() + " steps)";
        resultLabel.setText(resultText);
        
        // Enable the next turn button
        nextTurnButton.setEnabled(true);
        
        // If no movable pieces, automatically end the turn
        if (game.getCurrentPlayer().getMovablePieces().isEmpty()) {
            handleNextTurn();
            return;
        }
        
        // Prompt the player to select a piece
        statusLabel.setText("Please select a piece to move");
        
        // Repaint the board
        boardPanel.repaint();
    }
    
    /**
     * Handle the next turn button
     */
    private void handleNextTurn() {
        // Switch to the next player's turn
        game.nextTurn();
        
        // Reset piece selection
        selectedPiece = null;
        
        // Update the game status
        updateGameStatus();
        
        // If the current player is a computer, perform the move automatically
        if (game.getCurrentPlayer().isComputer()) {
            performComputerTurn();
        }
    }
    
    /**
     * Handle the computer's turn
     */
    private void performComputerTurn() {
        // Disable buttons
        tossButton.setEnabled(false);
        nextTurnButton.setEnabled(false);
        
        // Display that the computer is thinking
        statusLabel.setText("Computer is thinking...");
        
        // Use a timer delay so the player can see the computer's move
        Timer timer = new Timer(1000, e -> {
            // Computer tosses
            YutSet.YutResult result = game.tossYut();
            
            // Display the result
            String resultText = "Result: " + result.getName() + " (" + result.getSteps() + " steps)";
            resultLabel.setText(resultText);
            
            // Refresh the interface
            boardPanel.repaint();
            
            // Add another delay for piece selection
            Timer moveTimer = new Timer(1000, e2 -> {
                // Computer decides: select a piece to move
                List<Piece> movablePieces = game.getCurrentPlayer().getMovablePieces();
                
                if (!movablePieces.isEmpty()) {
                    // Simple AI: prioritize pieces on the board, if none, choose pieces at home
                    Piece pieceToMove = null;
                    
                    // First try to find a piece on the board
                    for (Piece piece : movablePieces) {
                        if (!piece.isHome() && !piece.isFinished()) {
                            pieceToMove = piece;
                            break;
                        }
                    }
                    
                    // If none found, choose a piece at home
                    if (pieceToMove == null) {
                        for (Piece piece : movablePieces) {
                            if (piece.isHome()) {
                                pieceToMove = piece;
                                break;
                            }
                        }
                    }
                    
                    if (pieceToMove != null) {
                        // Move the selected piece
                        boolean extraTurn = game.movePiece(pieceToMove);
                        
                        // Refresh the interface
                        boardPanel.repaint();
                        updatePlayerInfo();
                        
                        // Check if the game is over
                        if (game.isGameOver()) {
                            handleGameOver();
                            return;
                        }
                        
                        // If there is an extra turn, continue the computer's turn
                        if (extraTurn) {
                            performComputerTurn();
                            return;
                        }
                    }
                }
                
                // End the turn
                handleNextTurn();
            });
            
            moveTimer.setRepeats(false);
            moveTimer.start();
        });
        
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Handle game over
     */
    private void handleGameOver() {
        // Disable buttons
        tossButton.setEnabled(false);
        nextTurnButton.setEnabled(false);
        
        // Display the winner
        Player winner = game.getWinner();
        if (winner != null) {
            JOptionPane.showMessageDialog(this, 
                winner.getName() + " wins!", 
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Ask if the user wants to restart the game
        int option = JOptionPane.showConfirmDialog(
            this,
            "Game over, do you want to restart?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        }
    }
    
    /**
     * Restart the game
     */
    private void restartGame() {
        game = new YutGame(2);
        selectedPiece = null;
        updateGameStatus();
        updatePlayerInfo();
        boardPanel.repaint();
    }
    
    /**
     * Update the game status
     */
    private void updateGameStatus() {
        Player currentPlayer = game.getCurrentPlayer();
        
        // Update the status label
        statusLabel.setText("Current player: " + currentPlayer.getName());
        
        // Update the button states
        tossButton.setEnabled(!currentPlayer.isComputer());
        nextTurnButton.setEnabled(false);
        
        // Clear the result label
        resultLabel.setText("");
        
        // Update player information
        updatePlayerInfo();
        
        // Repaint the board
        boardPanel.repaint();
        
        // If the current player is a computer, automatically perform the move
        if (currentPlayer.isComputer()) {
            performComputerTurn();
        }
    }
    
    /**
     * Update the player information panel
     */
    private void updatePlayerInfo() {
        // Clear existing content
        playerInfoPanel.removeAll();
        
        // Add the title
        JLabel titleLabel = new JLabel("Player Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(120, 60, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerInfoPanel.add(titleLabel);
        playerInfoPanel.add(Box.createVerticalStrut(10));
        
        // Create information cards for each player
        for (Player player : game.getPlayers()) {
            // Create player card panel - using rounded borders and gradient background
            JPanel playerCard = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Create a rounded rectangle
                    RoundRectangle2D roundRect = new RoundRectangle2D.Float(
                        0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    
                    // Create a lighter version of the player's color for the background
                    Color playerColor = player.getColor();
                    Color lightColor = new Color(
                        Math.min(255, playerColor.getRed() + 140),
                        Math.min(255, playerColor.getGreen() + 140),
                        Math.min(255, playerColor.getBlue() + 140),
                        60
                    );
                    
                    // Draw the gradient background
                    GradientPaint gradient = new GradientPaint(
                        0, 0, Color.WHITE, 
                        0, getHeight(), lightColor
                    );
                    g2d.setPaint(gradient);
                    g2d.fill(roundRect);
                    
                    // Draw the border
                    g2d.setColor(player == game.getCurrentPlayer() ? 
                                player.getColor() : 
                                new Color(200, 200, 200));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(roundRect);
                    
                    super.paintComponent(g);
                }
            };
            playerCard.setLayout(new BoxLayout(playerCard, BoxLayout.Y_AXIS));
            playerCard.setOpaque(false);
            playerCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            playerCard.setMaximumSize(new Dimension(280, 100)); // Limit the card height
            
            // Player header row - contains color indicator and name
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
            headerPanel.setOpaque(false);
            headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Player color indicator
            JPanel colorIcon = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw a colored circle
                    Color baseColor = player.getColor();
                    g2d.setColor(baseColor);
                    g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                    
                    // Add a highlight effect
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.fillOval(2, 2, getWidth()/3, getHeight()/3);
                    
                    // Add a border
                    g2d.setColor(baseColor.darker());
                    g2d.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                }
            };
            colorIcon.setPreferredSize(new Dimension(20, 20));
            colorIcon.setMaximumSize(new Dimension(20, 20));
            colorIcon.setOpaque(false);
            
            // Player name label
            JLabel nameLabel = new JLabel(player.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            // If it's the current player, add an indicator and change color
            if (player == game.getCurrentPlayer()) {
                nameLabel.setForeground(new Color(180, 0, 0));
                
                // Add the current player indicator arrow
                JLabel arrowLabel = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Draw the arrow
                        g2d.setColor(new Color(200, 0, 0));
                        int[] xPoints = {0, 8, 0};
                        int[] yPoints = {0, 6, 12};
                        g2d.fillPolygon(xPoints, yPoints, 3);
                    }
                };
                arrowLabel.setPreferredSize(new Dimension(10, 12));
                headerPanel.add(arrowLabel);
                headerPanel.add(Box.createHorizontalStrut(5));
            }
            
            headerPanel.add(colorIcon);
            headerPanel.add(Box.createHorizontalStrut(8));
            headerPanel.add(nameLabel);
            headerPanel.add(Box.createHorizontalGlue());
            
            // Piece status panel
            JPanel statusPanel = new JPanel(new GridLayout(2, 2, 8, 4));
            statusPanel.setOpaque(false);
            statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            
            // Get the piece counts
            int homeCount = player.getHomePieces().size();
            int boardCount = player.getBoardPieces().size();
            int finishedCount = player.getFinishedPieces().size();
            
            // Create status labels with uniform styling
            JLabel homeLabel = createStatusLabel("Home", homeCount, new Color(120, 120, 120));
            JLabel boardLabel = createStatusLabel("Board", boardCount, new Color(0, 0, 180));
            JLabel finishedLabel = createStatusLabel("Finished", finishedCount, new Color(0, 150, 0));
            JLabel totalLabel = createStatusLabel("Total", 4, new Color(100, 100, 100));
            
            statusPanel.add(homeLabel);
            statusPanel.add(boardLabel);
            statusPanel.add(finishedLabel);
            statusPanel.add(totalLabel);
            
            // Add a separator
            JSeparator separator = new JSeparator();
            separator.setForeground(new Color(200, 200, 200));
            separator.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Add components to the player card
            playerCard.add(headerPanel);
            playerCard.add(Box.createVerticalStrut(2));
            playerCard.add(separator);
            playerCard.add(Box.createVerticalStrut(4));
            playerCard.add(statusPanel);
            
            // Add to the player info panel with spacing
            playerInfoPanel.add(playerCard);
            playerInfoPanel.add(Box.createVerticalStrut(12));
        }
        
        // Refresh the panel
        playerInfoPanel.revalidate();
        playerInfoPanel.repaint();
    }  

       /**
     * Create a status label for the pieces
     */
    private JLabel createStatusLabel(String text, int count, Color valueColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.setOpaque(false);
        
        JLabel textLabel = new JLabel(text + ":");
        textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        textLabel.setForeground(new Color(100, 100, 100));
        
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countLabel.setForeground(valueColor);
        
        panel.add(textLabel);
        panel.add(countLabel);
        
        // Wrap it in a JLabel for use in GridLayout
        JLabel wrapperLabel = new JLabel();
        wrapperLabel.setLayout(new BorderLayout());
        wrapperLabel.add(panel, BorderLayout.CENTER);
        
        return wrapperLabel;
    }
    
    /**
     * The board panel
     */
    private class BoardPanel extends JPanel {
        
        public BoardPanel() {
            setBackground(new Color(245, 222, 179)); // Light wood-colored background
            
            // Add a mouse listener to handle piece selection and movement
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Pieces can only be selected after a throw
                    if (game.getLastResult() == null || game.getCurrentPlayer().isComputer()) {
                        return;
                    }
                    
                    // Check if a piece was clicked
                    Piece clickedPiece = getPieceAt(e.getX(), e.getY());
                    
                    if (clickedPiece != null && clickedPiece.getOwner() == game.getCurrentPlayer() && !clickedPiece.isFinished()) {
                        // Select the piece
                        selectedPiece = clickedPiece;
                        statusLabel.setText("Selected piece " + clickedPiece.getId() + ", click again to move");
                        repaint();
                    } else if (selectedPiece != null) {
                        // A piece has already been selected, move it
                        boolean extraTurn = game.movePiece(selectedPiece);
                        
                        // Add a move message
                        String moveMessage = "Piece " + selectedPiece.getId() + " has moved";
                        
                        // Check if there's capture information
                        if (game.getLastCaptureInfo() != null && 
                            game.getLastCaptureInfo().isRecent() &&
                            game.getLastCaptureInfo().getCapturingPiece() == selectedPiece) {
                            
                            // Show capture info
                            Piece capturedPiece = game.getLastCaptureInfo().getCapturedPiece();
                            Player opponent = capturedPiece.getOwner();
                            
                            moveMessage += " - Captured " + opponent.getName() + "'s piece " + capturedPiece.getId() + "!";
                            
                            // Show capture message in a dialog
                            final String finalMessage = moveMessage;
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(
                                    YutGameUI.this,
                                    finalMessage,
                                    "Captured a Piece!",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                            });
                        }
                        
                        // Reset selected piece
                        selectedPiece = null;
                        
                        // Update UI
                        repaint();
                        updatePlayerInfo();
                        
                        // Check if the game is over
                        if (game.isGameOver()) {
                            handleGameOver();
                            return;
                        }
                        
                        // If there's an extra turn, enable the throw button
                        if (extraTurn) {
                            tossButton.setEnabled(true);
                            nextTurnButton.setEnabled(false);
                            statusLabel.setText(moveMessage + " - Extra turn! Please toss again");
                        } else {
                            // Enable end turn button
                            tossButton.setEnabled(false);
                            nextTurnButton.setEnabled(true);
                            statusLabel.setText(moveMessage + " - Please end your turn");
                        }
                    }
                }
            });
        }
        
        /**
         * Get the piece at the specified coordinates
         * @param x X coordinate
         * @param y Y coordinate
         * @return The piece at the coordinates, or null if none
         */
        private Piece getPieceAt(int x, int y) {
            YutGame.BoardPoint[] boardPoints = game.getBoardPoints();
            
            // Check each position on the board
            for (int i = 0; i < boardPoints.length; i++) {
                YutGame.BoardPoint point = boardPoints[i];
                if (point == null) continue;
                
                // Calculate the center of the point
                int centerX = point.getX();
                int centerY = point.getY();
                
                // Increase the clickable area - the original PIECE_SIZE may be too small
                int clickRadius = PIECE_SIZE * 2;
                
                // Check the pieces at this point
                List<Piece> piecesAtPoint = point.getPieces();
                if (piecesAtPoint.isEmpty()) continue;
                
                // Calculate offset for the piece's position
                int offsetX = 0;
                int offsetY = 0;
                
                for (Piece piece : piecesAtPoint) {
                    // Calculate the piece's position
                    int pieceX = centerX + offsetX;
                    int pieceY = centerY + offsetY;
                    
                    // Check if the click is on the piece - enlarge the check area
                    if (Math.abs(x - pieceX) <= clickRadius && Math.abs(y - pieceY) <= clickRadius) {
                        // Print debug info
                        System.out.println("Clicked position: (" + x + ", " + y + ")");
                        System.out.println("Piece position: (" + pieceX + ", " + pieceY + ")");
                        return piece;
                    }
                    
                    // Adjust for the next piece's offset
                    offsetX += 10;
                    offsetY += 10;
                    if (offsetX > 30) { // Avoid large offset
                        offsetX = 0;
                        offsetY = 0;
                    }
                }
            }
            
            // Check pieces in the player's home area
            for (Player player : game.getPlayers()) {
                List<Piece> homePieces = player.getHomePieces();
                
                if (homePieces.isEmpty()) continue;
                
                // Home position (top-left and bottom-right corners)
                int homeX, homeY;
                if (player == game.getPlayers().get(0)) {
                    homeX = 100;
                    homeY = 500;
                } else {
                    homeX = 700;
                    homeY = 100;
                }
                
                int offsetX = 0;
                int offsetY = 0;
                
                for (Piece piece : homePieces) {
                    int pieceX = homeX + offsetX;
                    int pieceY = homeY + offsetY;
                    
                    // Increase the clickable area
                    int clickRadius = PIECE_SIZE * 2;
                    if (Math.abs(x - pieceX) <= clickRadius && Math.abs(y - pieceY) <= clickRadius) {
                        // Print debug info
                        System.out.println("Clicked home piece: (" + x + ", " + y + ")");
                        System.out.println("Piece position: (" + pieceX + ", " + pieceY + ")");
                        return piece;
                    }
                    
                    offsetX += 15;
                    offsetY += 15;
                }
            }
            
            return null;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the game board
            drawBoard(g2d);

            // Draw all the pieces
            drawPieces(g2d);

            // Highlight the selected piece if there is one
            if (selectedPiece != null) {
                highlightSelectedPiece(g2d);
            }

            // Draw Yut sticks
            drawYutSticks(g2d);

            // If there was a recent capture event, display special effects
            if (game.getLastCaptureInfo() != null && game.getLastCaptureInfo().isRecent()) {
                drawCaptureEffect(g2d, game.getLastCaptureInfo());
            }
        }


        /**
         * Draw the game board
         */
        private void drawBoard(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();

            // Enable high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // Draw the background of the board - light tone
            g2d.setColor(new Color(245, 235, 225));
            g2d.fillRect(0, 0, width, height);

            // Create wood texture effect - more natural wood color and gradient
            createWoodTexture(g2d, 0, 0, width, height);

            // Add a soft inner shadow border effect
            drawBoardBorder(g2d, 10, 10, width-20, height-20);

            // Draw the center area
            int centerX = width / 2;
            int centerY = height / 2;
            int boardSize = Math.min(width, height) - 100;
            int boardX = centerX - boardSize/2;
            int boardY = centerY - boardSize/2;

            // Draw the center game area - dark wood texture
            createWoodTexture(g2d, boardX, boardY, boardSize, boardSize,
                             new Color(180, 150, 110), new Color(140, 100, 60));

            // Draw the outer frame
            g2d.setColor(new Color(120, 60, 30));
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawRect(boardX, boardY, boardSize, boardSize);

            // Draw center cross and diagonal lines - more elegant lines
            drawBoardLines(g2d, boardX, boardY, boardSize);

            // Draw all points
            drawBoardPoints(g2d);

            // Draw start and end markers
            drawStartEndMarkers(g2d);

            // Draw player home areas
            drawPlayerHomeAreas(g2d);
        }

        /**
         * Create realistic wood texture
         */
        private void createWoodTexture(Graphics2D g2d, int x, int y, int width, int height) {
            createWoodTexture(g2d, x, y, width, height,
                             new Color(220, 200, 165), new Color(180, 160, 125));
        }

        /**
         * Create custom colored wood texture
         */
        private void createWoodTexture(Graphics2D g2d, int x, int y, int width, int height,
                                       Color lightColor, Color darkColor) {
            // Create main gradient background
            GradientPaint mainGradient = new GradientPaint(
                x, y, lightColor,
                x, y + height, darkColor,
                false);
            g2d.setPaint(mainGradient);
            g2d.fillRect(x, y, width, height);

            // Save current clipping region
            Shape oldClip = g2d.getClip();
            g2d.setClip(x, y, width, height);

            // Add random wood grain lines
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

            // Use a fixed random seed for consistency
            java.util.Random rand = new java.util.Random(12345);

            int numGrains = width / 5;

            for (int i = 0; i < numGrains; i++) {
                int grainX = x + rand.nextInt(width);
                int grainWidth = 1 + rand.nextInt(3);
                int curve = 10 + rand.nextInt(20);

                // Color randomly changes between light and dark
                Color grainColor;
                if (rand.nextBoolean()) {
                    grainColor = new Color(
                        Math.max(0, darkColor.getRed() - 20 - rand.nextInt(30)),
                        Math.max(0, darkColor.getGreen() - 20 - rand.nextInt(30)),
                        Math.max(0, darkColor.getBlue() - 20 - rand.nextInt(30)),
                        50 + rand.nextInt(100)
                    );
                } else {
                    grainColor = new Color(
                        Math.min(255, lightColor.getRed() + 20 + rand.nextInt(30)),
                        Math.min(255, lightColor.getGreen() + 20 + rand.nextInt(30)),
                        Math.min(255, lightColor.getBlue() + 20 + rand.nextInt(30)),
                        50 + rand.nextInt(100)
                    );
                }

                g2d.setColor(grainColor);

                // Use quadratic Bezier curves to create curved wood grain
                for (int j = 0; j < height; j += 20 + rand.nextInt(40)) {
                    int controlX = grainX + (rand.nextBoolean() ? curve : -curve);

                    g2d.setStroke(new BasicStroke(grainWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawLine(grainX - 2, y + j, grainX + 2, y + j + 20 + rand.nextInt(30));
                }
            }

            // Restore composite mode and clipping region
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.setClip(oldClip);

            // Add gloss effect
            GradientPaint shineGradient = new GradientPaint(
                x, y, new Color(255, 255, 255, 30),
                x, y + height/2, new Color(255, 255, 255, 0)
            );
            g2d.setPaint(shineGradient);
            g2d.fillRect(x, y, width, height/2);
        }

        /**
         * Draw the board border
         */
        private void drawBoardBorder(Graphics2D g2d, int x, int y, int width, int height) {
            // Draw fine 3D border
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Outer border - dark brown
            g2d.setColor(new Color(100, 50, 20));
            g2d.drawRect(x-2, y-2, width+4, height+4);

            // Inner border - light brown
            g2d.setColor(new Color(170, 120, 70));
            g2d.drawRect(x, y, width, height);

            // Border decorations - four corners
            int cornerSize = 20;
            drawCornerDecoration(g2d, x-2, y-2, cornerSize, 0); // Top left
            drawCornerDecoration(g2d, x+width-cornerSize+2, y-2, cornerSize, 1); // Top right
            drawCornerDecoration(g2d, x-2, y+height-cornerSize+2, cornerSize, 2); // Bottom left
            drawCornerDecoration(g2d, x+width-cornerSize+2, y+height-cornerSize+2, cornerSize, 3); // Bottom right
        }

        /**
         * Draw corner decorations for the board
         */
        private void drawCornerDecoration(Graphics2D g2d, int x, int y, int size, int corner) {
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(140, 80, 40));

            int curve = size / 2;

            switch (corner) {
                case 0: // Top left
                    g2d.drawArc(x, y, size, size, 0, 90);
                    break;
                case 1: // Top right
                    g2d.drawArc(x-size, y, size, size, 0, 90);
                    break;
                case 2: // Bottom left
                    g2d.drawArc(x, y-size, size, size, 270, 90);
                    break;
                case 3: // Bottom right
                    g2d.drawArc(x-size, y-size, size, size, 180, 90);
                    break;
            }
        }

        /**
         * Draw the board lines
         */
        private void drawBoardLines(Graphics2D g2d, int x, int y, int size) {
            // Set line style
            g2d.setColor(new Color(100, 50, 30, 180));

            // Center cross
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(x, y + size/2, x + size, y + size/2);
            g2d.drawLine(x + size/2, y, x + size/2, y + size);

            // Diagonal lines - dashed style
            float[] dashPattern = {5, 3};
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
                                         BasicStroke.JOIN_ROUND, 10.0f, dashPattern, 0));
            g2d.drawLine(x, y, x + size, y + size);
            g2d.drawLine(x, y + size, x + size, y);
        }

        /**
         * Draw all the points on the board
         */
        private void drawBoardPoints(Graphics2D g2d) {
            // Get the points on the board
            YutGame.BoardPoint[] boardPoints = game.getBoardPoints();

            for (int i = 0; i < boardPoints.length; i++) {
                YutGame.BoardPoint point = boardPoints[i];
                if (point == null) continue;

                // Highlight key positions with different colors
                if (i == 0 || i == 29) {
                    // Start and end points in gold
                    drawBoardPoint(g2d, point, new Color(218, 165, 32), POINT_SIZE + 4, true);
                } else if (i == 5 || i == 10 || i == 15) {
                    // Corner points in brown
                    drawBoardPoint(g2d, point, new Color(101, 67, 33), POINT_SIZE + 2, true);
                } else if (i == 28) {
                    // Center point in red
                    drawBoardPoint(g2d, point, new Color(178, 34, 34), POINT_SIZE + 2, true);
                } else {
                    // Regular points in dark red-brown
                    drawBoardPoint(g2d, point, new Color(120, 60, 30), POINT_SIZE, false);
                }
            }
        }

        /**
         * Draw start and end markers
         */
        private void drawStartEndMarkers(Graphics2D g2d) {
            g2d.setFont(new Font("Arial", Font.BOLD, 16));

            // Draw start and end markers with large font and background color
            drawTextWithBackground(g2d, "Start", 280, 535, Color.WHITE, new Color(178, 34, 34, 200));
            drawTextWithBackground(g2d, "End", 380, 535, Color.WHITE, new Color(0, 100, 0, 200));
        }

        /**
         * Draw player home areas
         */
        private void drawPlayerHomeAreas(Graphics2D g2d) {
            drawHomeArea(g2d, game.getPlayers().get(0), 60, 460);

            if (game.getPlayers().size() > 1) {
                drawHomeArea(g2d, game.getPlayers().get(1), 720, 60);
            }
        }

        /**
         * Draw player home area
         * @param g2d Graphics context
         * @param player Player
         * @param x X coordinate
         * @param y Y coordinate
         */
        private void drawHomeArea(Graphics2D g2d, Player player, int x, int y) {
            // Set home area size
            int homeSize = 120;

            // Create rounded rectangle area
            RoundRectangle2D homeRect = new RoundRectangle2D.Double(
                x, y, homeSize, homeSize, 20, 20);

            // Create a lighter version of the player's color
            Color playerColor = player.getColor();
            Color lightPlayerColor = new Color(
                Math.min(255, playerColor.getRed() + 140),
                Math.min(255, playerColor.getGreen() + 140),
                Math.min(255, playerColor.getBlue() + 140),
                60
            );

            // Draw background - using gradient
            GradientPaint gradient = new GradientPaint(
                x, y, Color.WHITE,
                x + homeSize, y + homeSize, lightPlayerColor,
                false
            );
            g2d.setPaint(gradient);
            g2d.fill(homeRect);

            // Add wood texture effect
            createWoodTexture(g2d, x, y, homeSize, homeSize,
                             new Color(230, 220, 200, 120),
                             new Color(200, 190, 170, 80));

            // Draw border - current player gets a more prominent border
            if (player == game.getCurrentPlayer()) {
                // Glow border effect
                g2d.setColor(new Color(playerColor.getRed(),
                                       playerColor.getGreen(),
                                       playerColor.getBlue(),
                                       150));
                g2d.setStroke(new BasicStroke(4));
                g2d.draw(homeRect);

                // Add glowing effect
                g2d.setColor(new Color(playerColor.getRed(),
                                       playerColor.getGreen(),
                                       playerColor.getBlue(),
                                       60));
                g2d.setStroke(new BasicStroke(8));
                g2d.draw(homeRect);
            } else {
                // Non-current player gets a lighter border
                g2d.setColor(new Color(playerColor.getRed(),
                                       playerColor.getGreen(),
                                       playerColor.getBlue(),
                                       120));
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(homeRect);
            }

            // Draw player label
            String label = player.getName() + " Home";
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(label);

            // Create label rectangle
            RoundRectangle2D labelRect = new RoundRectangle2D.Double(
                x + homeSize/2 - textWidth/2 - 8,
                y - 25,
                textWidth + 16,
                25,
                10,
                10
            );

            // Draw label background
            GradientPaint labelGradient = new GradientPaint(
                x, y - 25, new Color(255, 255, 255, 220),
                x, y, playerColor,
                false
            );
            g2d.setPaint(labelGradient);
            g2d.fill(labelRect);

            // Draw label border
            g2d.setColor(playerColor.darker());
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(labelRect);

            // Draw label text
            g2d.setColor(new Color(50, 50, 50));
            g2d.drawString(label, x + homeSize/2 - textWidth/2, y - 8);

            // If it's the current player, add additional indicator
            if (player == game.getCurrentPlayer()) {
                int indicatorSize = 8;
                int[] xPoints = {x + homeSize/2 - indicatorSize, x + homeSize/2, x + homeSize/2 + indicatorSize};
                int[] yPoints = {y - 30, y - 25, y - 30};
                g2d.setColor(playerColor.darker());
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
        }

        /**
         * Draw points on the board
         */
        private void drawBoardPoint(Graphics2D g2d, YutGame.BoardPoint point, Color color, int size, boolean highlight) {
            int x = point.getX();
            int y = point.getY();

            // Draw shadow for the point
            if (highlight) {
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fill(new Ellipse2D.Double(x - size/2 + 2, y - size/2 + 2, size, size));
            }

            // Draw the point
            g2d.setColor(color);
            g2d.fill(new Ellipse2D.Double(x - size/2, y - size/2, size, size));

            // Draw point border
            g2d.setColor(Color.WHITE);
            g2d.draw(new Ellipse2D.Double(x - size/2, y - size/2, size, size));

            // If it's a highlighted point, add inner ring
            if (highlight) {
                g2d.setColor(new Color(255, 255, 255, 120));
                g2d.draw(new Ellipse2D.Double(x - size/4, y - size/4, size/2, size/2));
            }
        }

        /**
         * Draw text with background color
         */
        private void drawTextWithBackground(Graphics2D g2d, String text, int x, int y, Color textColor, Color bgColor) {
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            // Draw background rectangle
            g2d.setColor(bgColor);
            g2d.fillRoundRect(x - 5, y - textHeight + 5, textWidth + 10, textHeight, 10, 10);

            // Draw text
            g2d.setColor(textColor);
            g2d.drawString(text, x, y);
        }

        /**
         * Draw all pieces
         * @param g2d Graphics context
         */
        private void drawPieces(Graphics2D g2d) {
            // Draw pieces on the board
            YutGame.BoardPoint[] boardPoints = game.getBoardPoints();

            for (int i = 0; i < boardPoints.length; i++) {
                YutGame.BoardPoint point = boardPoints[i];
                if (point == null) continue;

                List<Piece> piecesAtPoint = point.getPieces();
                if (piecesAtPoint.isEmpty()) continue;

                // Piece offset for displaying multiple pieces on the same point
                int offsetX = 0;
                int offsetY = 0;

                for (Piece piece : piecesAtPoint) {
                    drawPiece(g2d, piece, point.getX() + offsetX, point.getY() + offsetY);

                    // Adjust offset for next piece
                    offsetX += 10;
                    offsetY += 10;
                    if (offsetX > 30) { // Prevent excessive offset
                        offsetX = 0;
                        offsetY = 0;
                    }
                }
            }

            // Draw pieces in home area
            for (Player player : game.getPlayers()) {
                List<Piece> homePieces = player.getHomePieces();

                if (homePieces.isEmpty()) continue;

                // Home position (top-left and bottom-right)
                int homeX, homeY;
                if (player == game.getPlayers().get(0)) {
                    homeX = 100;
                    homeY = 500;
                } else {
                    homeX = 700;
                    homeY = 100;
                }

                int offsetX = 0;
                int offsetY = 0;

                for (Piece piece : homePieces) {
                    drawPiece(g2d, piece, homeX + offsetX, homeY + offsetY);

                    offsetX += 15;
                    offsetY += 15;
                }
            }
        }

        /**
         * Draw a single piece
         * @param g2d Graphics context
         * @param piece Piece
         * @param x X coordinate
         * @param y Y coordinate
         */
        private void drawPiece(Graphics2D g2d, Piece piece, int x, int y) {
            // Save original transformation
            AffineTransform originalTransform = g2d.getTransform();

            // Set high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Check if it is the selected piece, if yes, add slight floating animation effect
            boolean isSelected = (piece == selectedPiece);
            int yOffset = 0;

            if (isSelected) {
                // Create slight floating effect
                long time = System.currentTimeMillis() % 1000;
                yOffset = -2 - (int)(Math.sin(time * Math.PI / 500) * 3);
            }

            // Draw shadow
            drawPieceShadow(g2d, x, y - yOffset);

            // Create base color and darker color version for the piece
            Color baseColor = piece.getColor();
            Color lighterColor = new Color(
                Math.min(255, baseColor.getRed() + 50),
                Math.min(255, baseColor.getGreen() + 50),
                Math.min(255, baseColor.getBlue() + 50)
            );
            Color darkerColor = new Color(
                Math.max(0, baseColor.getRed() - 70),
                Math.max(0, baseColor.getGreen() - 70),
                Math.max(0, baseColor.getBlue() - 70)
            );

            // Draw basic piece shape - 3D sphere effect
            drawPieceSphere(g2d, x, y + yOffset, PIECE_SIZE, baseColor, lighterColor, darkerColor);

            // Draw piece ID marker
            drawPieceId(g2d, piece, x, y + yOffset);

            // If it's the selected piece, add selection effect
            if (isSelected) {
                drawSelectionEffect(g2d, x, y + yOffset);
            }

            // Restore original transformation
            g2d.setTransform(originalTransform);
        }
        /**
         * Draw the piece shadow
         */
        private void drawPieceShadow(Graphics2D g2d, int x, int y) {
            // Draw the oval shadow
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillOval(x - PIECE_SIZE/2 - 2, y - PIECE_SIZE/4 + 2,
                          PIECE_SIZE + 4, PIECE_SIZE/2 + 2);
            g2d.setComposite(AlphaComposite.SrcOver);
        }

        /**
         * Draw a 3D sphere effect for the piece
         */
        private void drawPieceSphere(Graphics2D g2d, int x, int y, int size,
                                      Color baseColor, Color lighterColor, Color darkerColor) {
            // Create the sphere gradient
            float radius = size / 2.0f;

            // Use a regular GradientPaint instead of RadialGradientPaint
            GradientPaint gradient = new GradientPaint(
                x - size/4, y - size/4, lighterColor,
                x + size/2, y + size/2, darkerColor,
                false);

            // Draw the sphere
            g2d.setPaint(gradient);
            Ellipse2D sphere = new Ellipse2D.Double(x - radius, y - radius, size, size);
            g2d.fill(sphere);

            // Add edge highlight to enhance the 3D effect
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(sphere);

            // Add a small highlight spot
            int highlightSize = size / 5;
            g2d.setColor(new Color(255, 255, 255, 160));
            g2d.fill(new Ellipse2D.Double(
                x - radius/2 - highlightSize/2,
                y - radius/2 - highlightSize/2,
                highlightSize, highlightSize));
        }

        /**
         * Draw the piece ID marker
         */
        private void drawPieceId(Graphics2D g2d, Piece piece, int x, int y) {
            // Create the ID marker background
            int idSize = (int)(PIECE_SIZE * 0.65);

            // Create a white background circle
            g2d.setColor(Color.WHITE);
            g2d.fill(new Ellipse2D.Double(x - idSize/2, y - idSize/2, idSize, idSize));

            // Border
            g2d.setColor(new Color(100, 100, 100, 150));
            g2d.setStroke(new BasicStroke(0.8f));
            g2d.draw(new Ellipse2D.Double(x - idSize/2, y - idSize/2, idSize, idSize));

            // Draw the ID number, using the piece's owner color
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            String idStr = String.valueOf(piece.getId());
            int textWidth = fm.stringWidth(idStr);
            int textHeight = fm.getAscent();

            // Use a darker version of the owner's color as the text color
            Color textColor = new Color(
                Math.max(0, piece.getColor().getRed() - 40),
                Math.max(0, piece.getColor().getGreen() - 40),
                Math.max(0, piece.getColor().getBlue() - 40)
            );

            g2d.setColor(textColor);
            g2d.drawString(idStr, x - textWidth/2, y + textHeight/2 - 1);
        }

        /**
         * Draw the piece selection effect
         */
        private void drawSelectionEffect(Graphics2D g2d, int x, int y) {
            // Dynamic pulse effect
            long time = System.currentTimeMillis() % 2000;
            float alpha = 0.4f + 0.4f * (float)Math.sin(time * Math.PI / 1000);

            // Create the outer glow effect
            int glowSize = PIECE_SIZE + 10;
            Color glowColor = new Color(255, 223, 0, (int)(180 * alpha));

            // Use a simple ellipse and semi-transparent color
            g2d.setColor(glowColor);
            g2d.fill(new Ellipse2D.Double(x - glowSize/2, y - glowSize/2, glowSize, glowSize));

            // Dynamic selection indicator
            g2d.setColor(new Color(255, 223, 0, 200));
            g2d.setStroke(new BasicStroke(2.0f));

            double angleDelta = time / 500.0 * Math.PI;
            for (int i = 0; i < 4; i++) {
                double angle = angleDelta + i * Math.PI / 2;
                int dotX = (int)(x + Math.cos(angle) * (PIECE_SIZE/2 + 4));
                int dotY = (int)(y + Math.sin(angle) * (PIECE_SIZE/2 + 4));

                g2d.fill(new Ellipse2D.Double(dotX - 3, dotY - 3, 6, 6));
            }
        }

        /**
         * Highlight the selected piece
         */
            private void highlightSelectedPiece(Graphics2D g2d) {
            if (selectedPiece == null) return;

            // Get the selected piece's position
            int position = selectedPiece.getPosition();
            int x = 0, y = 0;

            if (selectedPiece.isHome()) {
                // If the piece is at home
                Player owner = selectedPiece.getOwner();
                if (owner == game.getPlayers().get(0)) {
                    x = 100;
                    y = 500;
                } else {
                    x = 700;
                    y = 100;
                }

                // Adjust the position based on the piece's ID
                x += selectedPiece.getId() * 15;
                y += selectedPiece.getId() * 15;
            } else {
                // Piece on the board
                YutGame.BoardPoint point = game.getBoardPoint(position);
                if (point != null) {
                    x = point.getX();
                    y = point.getY();

                    // If there are multiple pieces at the point, find this piece's position
                    List<Piece> piecesAtPoint = point.getPieces();
                    int index = piecesAtPoint.indexOf(selectedPiece);
                    if (index > 0) {
                        x += index * 10;
                        y += index * 10;
                    }
                }
            }

            // Draw possible move paths
            drawPossibleMove(g2d, x, y);
        }
        /**
        * Draw possible movement path
        */
        private void drawPossibleMove(Graphics2D g2d, int startX, int startY) {
    if (selectedPiece == null || game.getLastResult() == null) return;
    
    // Get the step count from the current throw result
    int steps = game.getLastResult().getSteps();
    
    // Calculate target position
    int targetPosition = selectedPiece.getPosition() + steps;
    
    // Check if the position is valid
    if (targetPosition >= 0 && targetPosition < game.getBoardPoints().length) {
        YutGame.BoardPoint targetPoint = game.getBoardPoint(targetPosition);
        
        if (targetPoint != null) {
            int endX = targetPoint.getX();
            int endY = targetPoint.getY();
            
            // Draw arrow indicating move direction
            drawMovementArrow(g2d, startX, startY, endX, endY);
            
            // Highlight the target point
            drawTargetHighlight(g2d, endX, endY);
        }
    }
}

        /**
         * Draw movement arrow
         */
        private void drawMovementArrow(Graphics2D g2d, int startX, int startY, int endX, int endY) {
    // Set arrow style
    g2d.setColor(new Color(255, 215, 0, 120));
    g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                 10.0f, new float[]{7, 5}, 0));
    
    // Calculate arrow direction
    double angle = Math.atan2(endY - startY, endX - startX);
    
    // Adjust arrow length to avoid overlapping pieces
    int arrowLength = (int)Math.sqrt((endX - startX) * (endX - startX) + 
                                    (endY - startY) * (endY - startY));
    
    int adjustedStartX = startX + (int)(15 * Math.cos(angle));
    int adjustedStartY = startY + (int)(15 * Math.sin(angle));
    
    int adjustedEndX = endX - (int)(15 * Math.cos(angle));
    int adjustedEndY = endY - (int)(15 * Math.sin(angle));
    
    // Draw arrow line
    g2d.drawLine(adjustedStartX, adjustedStartY, adjustedEndX, adjustedEndY);
    
    // Draw arrowhead
    int arrowSize = 12;
    int[] xPoints = new int[3];
    int[] yPoints = new int[3];
    
    xPoints[0] = adjustedEndX;
    yPoints[0] = adjustedEndY;
    
    xPoints[1] = (int)(adjustedEndX - arrowSize * Math.cos(angle - Math.PI/6));
    yPoints[1] = (int)(adjustedEndY - arrowSize * Math.sin(angle - Math.PI/6));
    
    xPoints[2] = (int)(adjustedEndX - arrowSize * Math.cos(angle + Math.PI/6));
    yPoints[2] = (int)(adjustedEndY - arrowSize * Math.sin(angle + Math.PI/6));
    
    g2d.setStroke(new BasicStroke(1.5f));
    g2d.setColor(new Color(255, 215, 0, 180));
    g2d.fillPolygon(xPoints, yPoints, 3);
    g2d.setColor(new Color(200, 150, 0, 200));
    g2d.drawPolygon(xPoints, yPoints, 3);
}

        /**
        * Draw highlight effect for the target point
        */
        private void drawTargetHighlight(Graphics2D g2d, int x, int y) {
    // Create pulsating effect
    long time = System.currentTimeMillis() % 1500;
    float scale = 0.8f + 0.4f * (float)Math.sin(time * Math.PI / 750);
    
    int highlightSize = (int)(POINT_SIZE * 1.8 * scale);
    
    // Use simple color gradient ellipse
    g2d.setColor(new Color(255, 200, 0, 100));
    g2d.fill(new Ellipse2D.Double(
        x - highlightSize/2, 
        y - highlightSize/2, 
        highlightSize, 
        highlightSize
    ));
    
    // Draw inner circle
    g2d.setColor(new Color(255, 215, 0, 60));
    g2d.setStroke(new BasicStroke(2.0f));
    g2d.draw(new Ellipse2D.Double(
        x - POINT_SIZE/2 - 3, 
        y - POINT_SIZE/2 - 3, 
        POINT_SIZE + 6, 
        POINT_SIZE + 6
    ));
}

        /**
        * Draw Yut sticks
        * @param g2d graphics context
        */
        private void drawYutSticks(Graphics2D g2d) {
    // Get Yut stick states - use animation state if animating
    YutStick[] sticks = isAnimating ? animatingSticks : game.getYutSet().getSticks();
    
    // Display throw state (whether animating or not)
    g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
    g2d.setColor(new Color(200, 0, 0));
    
    if (isAnimating) {
        drawTextWithBackground(g2d, "Throwing...", 650, 220, Color.WHITE, new Color(200, 0, 0, 180));
    } else {
        // Display last throw result if available
        if (game.getLastResult() != null) {
            drawTextWithBackground(g2d, 
                "Result: " + game.getLastResult().getName() + " (" + game.getLastResult().getSteps() + " steps)",
                650, 220, Color.WHITE, new Color(0, 100, 0, 180));
        }
    }
    
    // Draw each stick - position adjusted to the left side
    for (int i = 0; i < sticks.length; i++) {
        // Stick position - shifted to the left
        int x = 650;
        int y = 300 + i * 50; // increase spacing
        
        // Add shaking effect if animating
        if (isAnimating) {
            x += random.nextInt(11) - 5; // random offset between -5 and 5
            y += random.nextInt(11) - 5;
            
            // Draw motion blur effect
            g2d.setColor(new Color(100, 100, 100, 40));
            for (int j = 0; j < 3; j++) {
                int blurX = x + random.nextInt(7) - 3;
                int blurY = y + random.nextInt(7) - 3;
                
                if (sticks[i].isFlatSide()) {
                    g2d.fillRect(blurX - 20, blurY - 5, 40, 10);
                } else {
                    g2d.fillOval(blurX - 20, blurY - 10, 40, 20);
                }
            }
        }
        
        // Draw based on stick state
        if (sticks[i].isFlatSide()) {
            // Flat side up - draw flat shape
            g2d.setColor(new Color(222, 184, 135)); // light brown
            g2d.fillRect(x - 20, y - 5, 40, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x - 20, y - 5, 40, 10);
            
            // Add texture lines
            g2d.setColor(new Color(180, 140, 100));
            g2d.drawLine(x - 15, y - 2, x + 15, y - 2);
            g2d.drawLine(x - 10, y + 2, x + 10, y + 2);
        } else {
            // Round side up - draw oval shape
            g2d.setColor(new Color(160, 82, 45)); // dark brown
            g2d.fillOval(x - 20, y - 10, 40, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x - 20, y - 10, 40, 20);
            
            // Add wood grain texture
            g2d.setColor(new Color(120, 60, 20, 100));
            g2d.drawArc(x - 15, y - 8, 30, 16, 30, 120);
            g2d.drawArc(x - 10, y - 5, 20, 10, 210, 120);
        }
        
        // Draw rotation indicator if animating
        if (isAnimating) {
            drawRotationIndicator(g2d, x, y, i);
        }
    }
}
        /**
         * Draw rotating indicator arrow
         */
        private void drawRotationIndicator(Graphics2D g2d, int x, int y, int index) {
            // Calculate animation position
            double angle = (animationStep * 24 + index * 90) * Math.PI / 180;
            int arrowSize = 8;
            
            // Draw rotating arrow around the stick
            g2d.setColor(new Color(255, 100, 0, 180));
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int x1 = (int)(x + Math.cos(angle) * 25);
            int y1 = (int)(y + Math.sin(angle) * 15);
            int x2 = (int)(x + Math.cos(angle + Math.PI/8) * 25);
            int y2 = (int)(y + Math.sin(angle + Math.PI/8) * 15);
            
            g2d.drawLine(x1, y1, x2, y2);
            
            // Draw arrowhead
            int[] xPoints = {
                x2,
                (int)(x2 - arrowSize * Math.cos(angle - Math.PI/4)),
                (int)(x2 - arrowSize * Math.cos(angle + Math.PI/2))
            };
            int[] yPoints = {
                y2,
                (int)(y2 - arrowSize * Math.sin(angle - Math.PI/4)),
                (int)(y2 - arrowSize * Math.sin(angle + Math.PI/2))
            };
            
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
        
        /**
         * Draw capture effect
         * @param g2d Graphics context
         * @param captureInfo Capture information
         */
        private void drawCaptureEffect(Graphics2D g2d, YutGame.CaptureInfo captureInfo) {
            // Get coordinates of the capture position
            YutGame.BoardPoint point = game.getBoardPoint(captureInfo.getPosition());
            if (point == null) return;
            
            int x = point.getX();
            int y = point.getY();
            
            // Calculate time-based animation percentage (fade out within 5 seconds)
            long timeSinceCapture = System.currentTimeMillis() - captureInfo.captureTime;
            float percentage = 1.0f - (timeSinceCapture / 5000.0f);
            
            // Set transparency
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, percentage));
            
            // Draw capture effect - flashing rings
            int size = POINT_SIZE * 3;
            for (int i = 0; i < 3; i++) {
                float pulse = (float) (0.5 + 0.5 * Math.sin((System.currentTimeMillis() + i * 200) / 200.0));
                int currentSize = (int) (size * (1.0 + 0.5 * pulse));
                
                g2d.setColor(new Color(255, 215, 0, (int) (150 * percentage * (1 - i * 0.2))));
                g2d.setStroke(new BasicStroke(3.0f - i, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawOval(x - currentSize / 2, y - currentSize / 2, currentSize, currentSize);
            }
            
            // Draw capture text
            g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
            drawTextWithBackground(g2d, "Captured!", x, y - 40, 
                                 Color.WHITE, new Color(200, 0, 0, (int) (180 * percentage)));
            
            // Reset transparency
            g2d.setComposite(AlphaComposite.SrcOver);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            YutGameUI gameUI = new YutGameUI();
            gameUI.setVisible(true);
        });
    }
}