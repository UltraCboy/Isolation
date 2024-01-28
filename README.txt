CSC 462 Artificial Intelligence
Homework 3: Adversarial Search / Isolation
Christian Honicker

How To Run Isolation
--------------------
- Open the terminal from the /bin folder
- Run: java Game.Main
- A small popuup window with the title "Isolation" will appear.
	- This window will have two dropdown boxes with selectable player types.
		- Human: a human player that makes moves by clicking on the interface
		- Random: an "AI" player that makes random moves
		- AI (H1): an AI player that makes moves via an Adversarial Search
		- AI (H2): like AI (H1), but uses a different heuristic method
	- Once your players are selected, click "Start" to start the game.
- Once the game is started, a window containing a game board will appear.
	- The circles represent player pawns; P1 is red, P2 is blue
	- A yellow tile can be stood on by a pawn, a gray tile cannot
	- The right side panel indicates whos turn it is, along with the turn count.
- Each turn, the game will prompt for whichever player's turn it is to move their pawn, then remove a tile.
	- If the turn player is a Human, these moves are performed by clicking on the tile you wish to move to or remove.
	- If the turn player is an AI, these moves will be performed automatically.
- Once one player is isolated, the side panel will show who won.
	- Close the game and re-execute the program to play again.

Algorithm Information
---------------------
I'm not going to outline the full algorithm, but I will outline how it differs from the one in the assignment sheet & anything else I feel needs sharing.
- The AI has a depth limit of 10 (the root counts as depth 0)
- The AI does not employ ab-pruning, but does use a pruning technique
	- The AI has a width limit per tree level, meaning it will only consider a certain number of moves before moving down a level
	- In order to make sure the best moves are considered first, each level is sorted by their heuristic value before moving down
	- This AI has a width limit of 10,000 moves
		- This and the depth limit were selected in order to find a balance between intelligence, memory, & runtime
- Some minor modifications were made to the heuristic functions
	- Both H1 and H2 have a clause that if a move would cause the AI to lose the game, -100 is added to heuristic value
	- This is put in place to prevent the AI from self-isolating
	- Even with this modification, self-isolation may still happen, but I do believe it happens less often than without it

Known Bugs
----------
- Rarely, when an AI attempts to make a move, it will throw the following exception:
	- java.lang.IllegalArgumentException: Comparison method violates its general contract!
	- This is likely due to how the AI randomizes the order of moves it perceives to have the same value.
	- Currently, the only way to fix this is to close the program and re-execute it.

Experiment
----------
I chose to run Experiment 2: Heuristic vs Heuristic. This document will contain an abridged version of the result, but you can find the complete results at the following link:
- https://docs.google.com/spreadsheets/d/1L9SYwAiy0ZkoJ5iOmXmv4dyiGZNbWtP-3SQJNp1Fv-c/edit?usp=sharing

Below is a table of the results of each group of games:
- Column represents Player 1, Row represents Player 2
- The numbers in the cells are in the order of:
	- Player 1 Win Rate, Average Turns per Game, Turns Standard Deviation
- Each cells is the summary of 50 games

  |        H1        |         H2       |
--+------------------+------------------|
H1| 46%, 21.66, 4.75 | 24%, 22.42, 5.93 |
--+------------------+------------------|
H2| 88%, 21.66, 6.14 | 60%, 32.50, 6.01 |
--+------------------+------------------|

I mainly want to talk about win rate; turns per game is interesting in its own right, but win rate is what we're more interested in here.

If win rate was independent of who goes first & which heuristic was used, we would expect the first turn player to win about 50% of the time—the odds of a coin flip.
To determine the odds of a particular number of wins occurring, we can calculate binomial probability:
- b(x) = (50 choose x) * 0.5^50

We'll calculate b(x) for each combination of heuristics:
- H1 vs H1: b(0.46 * 50) = b(23) ~= 0.096
- H1 vs H2: b(0.88 * 50) = b(44) ~= 1/70 million
- H2 vs H1: b(0.24 * 50) = b(12) ~= 1/9000
- H2 vs H2: b(0.60 * 50) = b(30) ~= 0.042

If we consider a significance level of 0.05:
- H1 vs H1 is not significant
- H1 vs H2 and H2 vs H1 are definitely significant
- H2 vs H2 is barely significant, but not enough so to be able to draw a conclusion for sure
	- A lower significant level, such as the fairly common 0.01, would not consider this significant

From these results we can draw the following conclusions:
- The player who goes first probably doesn't have a significant advantage, but we can't say for sure without more testing.
- Heuristic 1 is significantly better than Heuristic 2 in its usage for an Isolation-playing AI. When playing against each other, H1 will beat H2 about 80% of the time.