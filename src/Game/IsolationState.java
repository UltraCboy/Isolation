// CSC 462 : Christian Honicker

package Game;

import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public class IsolationState implements Comparable<IsolationState> {
	// General variables
	private int[][] board;
	private Point myPos;
	private Point theirPos;
	private int playerNum;
	// Variables used in search
	private Integer h = null; // Heuristic value
	protected Point move;
	protected Point remove;
	private int rand = new java.util.Random().nextInt(256); // Seed used for breaking ties
	protected IsolationState parent = null; // If this state was derived from a previous one
	private Integer childrenH = null;
	private boolean useH2;
	
	/**
	 * Creates an instance.
	 * @param g A GameBoard to read tile data from
	 * @param p Player number
	 * @param h False -> H1, True -> H2
	 */
	public IsolationState(GameBoard g, int p, boolean h) {
		board = g.getTiles();
		myPos = g.getPlayerCoords(p);
		theirPos = g.getPlayerCoords(p == 1 ? 2 : 1);
		useH2 = h;
		playerNum = p;
	}
	
	/**
	 * Creates an instance.
	 * @param s Parent state
	 * @param b Tile array
	 * @param my My position
	 * @param th Their position
	 * @param mv The move performed to reach this state
	 * @param rm The remove performed to reach this state
	 */
	private IsolationState(IsolationState s, int[][] b, Point my, Point th, Point mv, Point rm) {
		parent = s;
		board = Main.copy2DArray(b); myPos = my; theirPos = th;
		move = mv; remove = rm;
		useH2 = s.useH2;
		playerNum = s.playerNum;
	}
	
	/**
	 * @param mine True if its the player's turn, false if it's the opponent's
	 * @return An array of all possible moves the turn player can perform
	 */
	public List<List<Point>> getLegalMoves(boolean mine) {
		// Get moves
		List<Point> moves = new ArrayList<Point>();
		Point pos = mine ? myPos : theirPos;
		for(int r = -1; r <= 1; r++) {
			for(int c = -1; c <= 1; c++) {
				if(r == 0 && c == 0) continue;
				try {
					if (board[pos.x + r][pos.y + c] == 0)
						moves.add(new Point(pos.x + r, pos.y + c));
				} catch(ArrayIndexOutOfBoundsException e) { continue; }
			}
		}
		// Get removes
		List<Point> removes = new ArrayList<Point>();
		for (int r = 0; r < board.length; r++)
			for (int c = 0; c < board[r].length; c++)
				if(board[r][c] == 0 || board[r][c] == playerNum) removes.add(new Point(r, c));
		// Combine
		List<List<Point>> out = new ArrayList<List<Point>>();
		for(Point m : moves) 
			for(Point r : removes)
				if(m.x != r.x || m.y != r.y) {
					List<Point> foo = new ArrayList<Point>();
					foo.add(m); foo.add(r);
					out.add(foo);
				}
		return out;
	}
	
	/**
	 * 
	 * @param move Tile to move the pawn to
	 * @param remove Tile to remove
	 * @param mine True if its the player's turn, false if it's the opponent's
	 * @return The resulting board state
	 */
	public IsolationState simulateMove(Point move, Point remove, boolean mine) {
		// Make the new move
		IsolationState out = new IsolationState(this, board, 
				mine ? move : myPos, mine ? theirPos : move, move, remove);
		Point pos = mine ? myPos : theirPos;
		out.board[move.x][move.y] = out.board[pos.x][pos.y];
		if((pos.x == 0 && pos.y == board[0].length / 2) || (pos.x == board.length && pos.y == board[0].length / 2 - 1))
			out.board[pos.x][pos.y] = -1;
		out.board[pos.x][pos.y] = 0;
		out.board[remove.x][remove.y] = -1;
		// Calculate heuristic
		out.h = useH2 ? hFunction2(out) : hFunction1(out);
		return out;
	}
	
	/**
	 * @param p A location on the board
	 * @return The number of free cells surrounding p
	 */
	private int Cells(Point p) {
		int out = 0;
		for(int r = -1; r <= 1; r++) {
			for(int c = -1; c <= 1; c++) {
				if(r == 0 && c == 0) continue;
				try {
					if (board[p.x + r][p.y + c] == 0) out++;
				} catch(ArrayIndexOutOfBoundsException e) { continue; }
			}
		}
		return out;
	}
	
	/**
	 * Don't want to type out the whole expression; refer to H1 in the assignment document
	 * @param out A child of this state
	 * @return Heuristic value
	 */
	private int hFunction1(IsolationState out) {
		int hMove = 0; int hRemove = 0;
		hMove = out.Cells(out.myPos) - Cells(myPos);
		if(hMove > 0) hMove = 1;
		else if(hMove < 0) hMove = -1;
		hRemove = out.Cells(out.theirPos);
		if(hRemove == 0) hRemove = 100;
		else if(out.Cells(out.myPos) == 0) hRemove = -100;
		else if(hRemove < Cells(theirPos)) hRemove = 22;
		else hRemove = 0;
		return hMove + hRemove;
	}
	
	/**
	 * Don't want to type out the whole expression; refer to H1 in the assignment document
	 * @param out A child of this state
	 * @return Heuristic value
	 */
	private int hFunction2(IsolationState out) {
		int hMove = out.Cells(out.myPos) - Cells(myPos);
		int hRemove = 0;
		if(out.Cells(out.theirPos) == 0) hRemove = 100;
		else if(out.Cells(out.myPos) == 0) hRemove = -100;
		else
			for(int i = 0; i < board.length; i++)
				for(int j = 0; j < board[0].length; j++) {
					Point p = new Point(i, j);
					if(out.Cells(p) < 3) hRemove++;
				}
		return hMove + hRemove;
	}
	
	public void setParentHeuristic(int depth) {
		if (parent.childrenH == null) parent.childrenH = parent.h;
		if (childrenH == null)
			parent.childrenH = depth % 2 == 0 ? Math.min(parent.childrenH, h) : Math.max(parent.childrenH, h);
		else
			parent.childrenH = depth % 2 == 0 ? Math.min(parent.childrenH, childrenH) : Math.max(parent.childrenH, childrenH);
	}
	
	@Override
	public int compareTo(IsolationState other) {
		// Sort by childrenH first, then h, then rand
		if ((childrenH != null && other.childrenH != null) && childrenH != other.childrenH) {
			return childrenH - other.childrenH;
		} else {
			if (h == other.h) {
				return rand - other.rand;
			}
			return h - other.h;
		}
	}
}
