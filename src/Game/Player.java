// CSC 462 : Christian Honicker

package Game;

/**
 * A player that makes moves on an Isolation GameBoard.
 */
public abstract class Player extends Thread {
	protected GameBoard game;
	protected int playerNum;
	protected boolean wait = true;
	
	/**
	 * Creates an instance.
	 * @param p Player number
	 */
	public Player(int p) { playerNum = p; }
	
	/**
	 * @param g The GameBoard this Player is playing on
	 */
	public void setGameBoard(GameBoard g) { game = g; }
	
	/**
	 * Informs the Player that it's waiting for a move.
	 */
	public void promptForMove() {
		wait = false;
	}
	
	@Override
	public abstract void run();
}
