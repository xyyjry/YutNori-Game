# Yut Nori – Traditional Korean Game

This is a Java Swing implementation of the traditional Korean board game **Yut Nori**.

## Game Rules

**Yut Nori** is a traditional folk game with the following rules:

1. The game uses four wooden sticks as dice. Each stick has a flat side and a round side.
2. Each player has 4 pieces. The goal is to move all pieces from the starting point to the finish.
3. Players take turns throwing the sticks and move their pieces based on the result:

   * **Do (도)**: 1 flat side up – move 1 step
   * **Gae (개)**: 2 flat sides up – move 2 steps
   * **Geol (걸)**: 3 flat sides up – move 3 steps
   * **Yut (윷)**: 4 flat sides up – move 4 steps
   * **Mo (모)**: 0 flat sides up (all round sides) – move 5 steps
4. If the result is **Yut** or **Mo**, the player gets an extra turn.
5. If a piece lands on an opponent’s piece, the opponent’s piece is captured and sent back to start. The player gains an extra turn.
6. The first player to get all four pieces to the finish wins the game.

## Project Structure

* `src.main.java.com.xingyang.YutStick.java` – Class representing a single Yut stick
* `src.main.java.com.xingyang.YutSet.java` – Class representing a full set of 4 Yut sticks
* `src.main.java.com.xingyang.Piece.java` – Class representing a game piece
* `src.main.java.com.xingyang.Player.java` – Class representing a player
* `src.main.java.com.xingyang.YutGame.java` – Core game logic
* `src.main.java.com.xingyang.YutGameUI.java` – Game UI class (Java Swing)
* `src.main.java.com.xingyang.YutLauncher.java` – Game launcher class

## How to Build and Run

### Run with Script

**Linux/Mac:**

```bash
./run.sh
```

**Windows:**

```
run.bat
```

### Manual Compilation and Execution

```bash
mkdir -p target/classes
javac -d target/classes src/main/java/*.java
java -cp target/classes src.main.java.com.xingyang.YutLauncher
```

## Game Controls

1. Click the **“Throw Sticks”** button to roll the Yut sticks.
2. Click the piece you want to move, then click a position on the board to confirm the move.
3. If you have earned an extra turn, the game will prompt you to throw again.
4. Click the **“End Turn”** button to finish your turn.

## System Requirements

* Java 11 or higher
