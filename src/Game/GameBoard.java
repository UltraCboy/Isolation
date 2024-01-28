// CSC 462 : Christian Honicker

package Game;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * An Isolation graphical interface.
 */
public class GameBoard {
	private int rows = 8; private int cols = 6;
	private JButton[][] tiles;
	private int[][] game; // -1 : hole, 0 : tile, 1 : P1, 2 : P2
	private JLabel info;
	private Player p1, p2;
	private Point q1, q2;
	private int gameState = 0;
	private String[] gameStateNames = {
		"<html>P1<br/>to<br/>Move</html>", 
		"<html>P1<br/>to<br/>Remove</html>", 
		"<html>P2<br/>to<br/>Move</html>", 
		"<html>P2<br/>to<br/>Remove</html>",
		"<html>P1<br/>Wins!</html>", 
		"<html>P2<br/>Wins!</html>"
	};
	private int turnCount = 1;
	private JLabel turns;
	
	/**
	 * Constructs a GameBoard.
	 * @param p1 Player 1
	 * @param p2 Player 2
	 */
	public GameBoard(Player p1, Player p2) {
		// Set up UI
		JFrame master = new JFrame();
		master.setLayout(new BorderLayout());
		
		JPanel buttonGrid = new JPanel();
	    tiles = new JButton[rows][cols];
	    for (int i = 0; i < rows; i++) {
	        for (int j = 0; j < cols; j++) { 
        		int foo = i; int bar = j;
	        	tiles[i][j] = new JButton(drawCell(createTile()));
	        	tiles[i][j].addActionListener(a -> {
	        		handleTilePress(foo, bar); 
	        	});
	        	buttonGrid.add(tiles[i][j]);
	        }
	    }
	    buttonGrid.setLayout(new GridLayout(rows, cols));
	    buttonGrid.setPreferredSize(new Dimension(100 * rows, 100 * cols));    
	    master.add(buttonGrid, BorderLayout.CENTER);
	    
	    JPanel side = new JPanel();
	    side.setLayout(new BorderLayout());
	    
	    info = new JLabel();
	    info.setPreferredSize(new Dimension(200, 100));
	    info.setFont(new Font("Arial", Font.BOLD, 48));
	    
	    turns = new JLabel();
	    turns.setPreferredSize(new Dimension(200, 50));
	    turns.setFont(new Font("Arial", Font.BOLD, 24));
	    
	    side.add(info, BorderLayout.CENTER);
	    side.add(turns, BorderLayout.SOUTH);
	    master.add(side, BorderLayout.EAST);
	    
	    master.setSize(800, 800);
	    master.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    master.setVisible(true);
	    
	    // Set up game
	    this.p1 = p1; this.p2 = p2;
	    p1.setGameBoard(this); p2.setGameBoard(this);
	    game = new int[rows][cols];
	    game[rows - 1][cols / 2 - 1] = 1;
	    q1 = new Point(rows - 1, cols / 2 - 1);
	    game[0][cols / 2] = 2;
	    q2 = new Point(0, cols / 2);
	    updateBoard();
	    p1.start(); p2.start();
	    if (!(p1 instanceof HumanPlayer)) p1.promptForMove();
	}
	
	/**
	 * Performs an action when a tile is clicked.
	 * @param row Clicked cell row
	 * @param col Clicked cell column
	 */
	private void handleTilePress(int row, int col) {
		switch(gameState) {
			case 0:
				if(p1 instanceof HumanPlayer)
					moveP1(row, col);
				break;
			case 2:
				if(p2 instanceof HumanPlayer)
					moveP2(row, col);
				break;
			case 1: case 3:
				removeTile(row, col); break;
			default: break;
		}
	}
	
	/**
	 * Attempts to move Player 1's pawn.
	 * @param row Target cell row
	 * @param col Target cell column
	 */
	public void moveP1(int row, int col) {
		if(!isLegalMove(q1.x, q1.y, row, col)) return;
		if(q1.equals(new Point(rows - 1, cols / 2 - 1)))
			game[q1.x][q1.y] =  -1;
		else
			game[q1.x][q1.y] =  0;
		game[row][col] = 1;
		q1.x = row; q1.y = col;
		gameState++;
		updateBoard();
	}
	
	/**
	 * Attempts to move Player 2's pawn.
	 * @param row Target cell row
	 * @param col Target cell column
	 */
	public void moveP2(int row, int col) {
		if(!isLegalMove(q2.x, q2.y, row, col)) return;
		if (q2.equals(new Point(0, cols / 2)))
			game[q2.x][q2.y] =  -1;
		else
			game[q2.x][q2.y] =  0;
		game[row][col] = 2;
		q2.x = row; q2.y = col;
		gameState++;
		updateBoard();
	}
	
	/**
	 * Attempts to move a player's pawn.
	 * @param row Target cell row
	 * @param col Target cell column
	 * @param p Player number
	 */
	public void movePlayer(int row, int col, int p) {
		if(p == 1) moveP1(row, col);
		else if (p == 2) moveP2(row, col);
	}
	
	/**
	 * Attempts to remove a tile.
	 * @param row Target cell row
	 * @param col Target cell column
	 */
	public void removeTile(int row, int col) {
		if(isTileRemovable(row, col)) {
			game[row][col] = -1;
			gameState++; gameState %= 4;
			turnCount++;
			checkForIsolation();
			updateBoard();
			if(gameState == 0) p1.promptForMove();
			if(gameState == 2) p2.promptForMove();
		}
	}
	
	/**
	 * @param fromRow Starting cell row
	 * @param fromCol Starting cell column
	 * @param toRow Target cell row
	 * @param toCol Target cell column
	 * @return True if a pawn can move from starting cell to target cell, otherwise False
	 */
	public boolean isLegalMove(int fromRow, int fromCol, int toRow, int toCol) {
		try {
			if (game[toRow][toCol] != 0) return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		int deltaRow = Math.abs(fromRow - toRow);
		int deltaCol = Math.abs(fromCol - toCol);
		if (deltaRow > 1 || deltaCol > 1) return false;
		return true;
	}
	
	/**
	 * @param row Target cell row
	 * @param col Target cell column
	 * @return True if the target cell can be removed, otherwise false
	 */
	public boolean isTileRemovable(int row, int col) {
		return game[row][col] == 0;
	}
	
	/**
	 * Checks to see if either player is isolated—that is, if they have no legal moves
	 */
	private void checkForIsolation() {
		boolean p1Iso = true, p2Iso = true;
		for(int r = -1; r <= 1; r++) {
			for(int c = -1; c <= 1; c++) {
				if(r == 0 && c == 0) continue;
				if(isLegalMove(q1.x, q1.y, q1.x + r, q1.y + c)) p1Iso = false;
				if(isLegalMove(q2.x, q2.y, q2.x + r, q2.y + c)) p2Iso = false;
			}
		}
		if(p1Iso) {
			turnCount--;
			if(p2Iso) gameState = gameState / 2 == 0 ? 4 : 5; // The winner is whoever's turn it was
			else gameState = 5; // P2 Wins
		} else if(p2Iso) {
			turnCount--;
			gameState = 4; // P1 Wins
		}
	}
	
	/**
	 * Graphically updates a single tile.
	 * @param row Target cell row
	 * @param col Target cell column
	 */
	private void updateTile(int row, int col) {
		BufferedImage img;
		if (game[row][col] < 0) img = createHole();
		else {
			img = createTile();
			if (game[row][col] == 1) img = addP1(img);
			if (game[row][col] == 2) img = addP2(img);
		}
		tiles[row][col].setIcon(drawCell(img));
	}
	
	/**
	 * @param p Player number
	 * @return That player's pawn's coordinates
	 */
	public Point getPlayerCoords(int p) {
		return p == 2 ? q2 : q1;
	}
	
	/**
	 * @return A deep copy of the game board
	 */
	public int[][] getTiles() {
		return Main.copy2DArray(game);
	}
	
	/**
	 * Graphically updates the entire board, including the side panel.
	 */
	public void updateBoard() {
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				updateTile(i, j);
		info.setText(gameStateNames[gameState]);
		turns.setText("Turn count: " + turnCount);
	}
	
	/**
	 * @param i A BufferedImage
	 * @return An ImageIcon to be used on a JButton
	 */
	private ImageIcon drawCell(BufferedImage i) {
	    return new ImageIcon(i);
	}
	
	/**
	 * @return A standard tile graphic.
	 */
	private BufferedImage createTile() {
	    BufferedImage i = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
	    Graphics drawer = i.getGraphics();
	    drawer.setColor(new Color(240, 220, 20));
	    drawer.fillRect(0, 0, 100, 100);
	    return i;
	}
	
	/**
	 * @return A hole graphic.
	 */
	private BufferedImage createHole() {
	    BufferedImage i = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
	    Graphics drawer = i.getGraphics();
	    drawer.setColor(new Color(127, 127, 127));
	    drawer.fillRect(0, 0, 100, 100);
	    return i;
	}
	
	/**
	 * @param base A tile image.
	 * @return A copy of the image with a red pawn added.
	 */
	private BufferedImage addP1(BufferedImage base) {
	    Graphics drawer = base.getGraphics();
	    drawer.setColor(new Color(255, 0, 0));
	    drawer.fillOval(38, 40, 25, 25);
	    return base;
	}

	/**
	 * @param base A tile image.
	 * @return A copy of the image with a blue pawn added.
	 */
	private BufferedImage addP2(BufferedImage base) {
	    Graphics drawer = base.getGraphics();
	    drawer.setColor(new Color(0, 0, 255));
	    drawer.fillOval(38, 40, 25, 25);
	    return base;
	}
}
