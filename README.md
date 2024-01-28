# Adversarial Search Project: Isolation

## Description

This is an implementation of the minimax algorithm in order to play the board game Isolation, a 2-player tile-based game where the goal is to make it so that your opponent is unable to move.

This project was created for a university-level Artificial Intelligence class, submitted on October 20, 2023, and was uploaded afterwards for public use.

## How to Run

- Open the terminal from the project folder.
- Run `java -jar Isolation.jar`
- A popup window will appear, allowing you to select who is operating which player.
  - Top selection box is for Player 1, bottom selection box is for Player 2.
- After you've made your player selections, press "Start" to start the game.

## How to Play

- After hitting start, a 6 by 8 grid of tiles will appear.
  - Yellow tiles are tiles pawns can stand on, gray tiles are holes.
  - The red pawn is Player 1, and the blue pawn is Player 2.
- The right-side panel displays whose turn it is, what action they need to take, and how many turns have passed.
- Player 1 goes first, then turns alternate.
- A player's turn is divided into two actions.
  - Move: the player moves their pawn one space up, down, left, right, or diagonally.
    - A player can only move to a yellow tile, not to a gray one.
    - A player cannot move to a tile that already has a pawn on it.
    - A player must move on their turn; they cannot choose to stay in place.
    - On each player's first turn, their starting tile is automatically removed.
  - Remove: the player removes one tile from the board.
    - A player cannot remove a tile that's already removed.
    - A player cannot remove a tile that has a pawn on it.
    - A player must remove a tile on their turnl they cannot choose to not remove a tile.
- If the turn player is controlled by a human, they must click on the tile they wish to move to or remove. If the turn player is controlled by an AI, they will perform these actions automatically.
- The game ends when a player is unable to move on their turn; the isolated player loses, and their opponent wins.
- Once the game ends, re-execute the program to play again.

## AI Algorithm Information

This program features 3 different AI players.

- Random: makes moves at complete random.
- AI (H1): decides on moves via a minimax algorithm, with a max depth of 10 and a max width per level of 10,000. It calculates the heuristic value of a board state based on how many cells are available for it & the opponent to move to.
- AI (H2): same as AI (H1), except it does not consider how many cells are available for the opponent to move to, but instead considers how many cells on the whole board have 3 or more available adjacent cells.

More detailed breakdowns of each AI algorithm may become available at a later date.

## Experiment

An experiment was performed on AI (H1) and AI (H2) to see if one was demonstrably better than the other. A total of 200 games were simulated, with each AI playing 100 games as Player 1 and 100 games as Player 2
(each AI played 50 games against itself). You can find the full game reports at this spreadsheet: https://docs.google.com/spreadsheets/d/1L9SYwAiy0ZkoJ5iOmXmv4dyiGZNbWtP-3SQJNp1Fv-c/edit?usp=sharing

Below is a table summarizing the data collected from this experiment.

- Column represents Player 1, row represents Player 2.
- The numbers in the cells are in the order of: Player 1 win rate, Average Turns Per Game, Turns Standard Deviation

|      |        H1        |         H2       |
|------|------------------|------------------|
|**H1**| 46%, 21.66, 4.75 | 24%, 22.42, 5.93 |
|**H2**| 88%, 21.66, 6.14 | 60%, 32.50, 6.01 |

A more detailed analysis of the experiment may become available at a later date.

## Known Bugs

- Rarely, when AI (H1) or AI (H2) attempts to make a move, it will throw the following exception: `java.lang.IllegalArgumentException: Comparison method violates its general contract!`
	- This is likely due to how the AI selects randomly between moves it perceives to have the same value.
	- Currently, the only way to fix this is to close the program and re-execute it.
