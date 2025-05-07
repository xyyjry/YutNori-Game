package com.xingyang;

import java.awt.Color;

/**
 * A piece in the Yut game
 */
public class Piece {
    private int id;
    private int position; // The piece's position on the board
    private boolean isHome; // Whether the piece is at the start
    private boolean isFinished; // Whether the piece has reached the end
    private Color color;
    private Player owner;
    
    public Piece(int id, Player owner, Color color) {
        this.id = id;
        this.owner = owner;
        this.color = color;
        this.position = -1; // -1 indicates the piece is at the start
        this.isHome = true;
        this.isFinished = false;
    }
    
    /**
     * Move the piece
     * @param steps Number of steps to move
     * @return Whether the piece has reached the end
     */
    public boolean move(int steps) {
        if (isFinished) {
            return true; // A piece that has reached the end does not move
        }
        
        if (isHome) {
            // Start from the home position
            isHome = false;
            position = 0; // Starting position
        }
        
        position += steps;
        
        // Check whether the piece has reached the end (position 29 is the endpoint)
        if (position >= 29) {
            isFinished = true;
            position = 29; // Set to endpoint position
            return true;
        }
        
        return false;
    }
    
    /**
     * Return the piece to the start
     */
    public void backToHome() {
        isHome = true;
        isFinished = false;
        position = -1;
    }
    
    /**
     * Get the piece ID
     * @return Piece ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Get the piece's position
     * @return Piece position
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Check if the piece is at the start
     * @return Whether it is at the start
     */
    public boolean isHome() {
        return isHome;
    }
    
    /**
     * Check if the piece has reached the end
     * @return Whether it has reached the end
     */
    public boolean isFinished() {
        return isFinished;
    }
    
    /**
     * Get the piece's color
     * @return Piece color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Get the owner of the piece
     * @return Piece owner
     */
    public Player getOwner() {
        return owner;
    }
    
    /**
     * Set the piece's position
     * @param position New position
     */
    public void setPosition(int position) {
        this.position = position;
        if (position == -1) {
            isHome = true;
            isFinished = false;
        } else if (position >= 29) {
            isFinished = true;
            isHome = false;
        } else {
            isHome = false;
            isFinished = false;
        }
    }
}
