// CSC 462 : Christian Honicker

package Game;

import java.awt.Point;
import java.util.Random;

/**
 * An "AI" player that randomly generates its moves.
 */
public class RandomPlayer extends Player{
	private Random r;
	
	/**
	 * Creates an instance.
	 * @param p Player number
	 */	
	public RandomPlayer(int p) { super(p); r = new Random(); }

	@Override
	public void run() {
		while(true) {
			try {
				sleep(1);
				if(wait) continue;
				sleep(100); // Pretend to think
			} catch (InterruptedException e) {}
			
			Point coords = game.getPlayerCoords(playerNum);
			int row, col;
			do {
				row = coords.x + r.nextInt(3) - 1; 
				col = coords.y + r.nextInt(3) - 1;
			} while (!game.isLegalMove(coords.x, coords.y, row, col));
			game.movePlayer(row, col, playerNum);
			do {
				row = r.nextInt(8);
				col = r.nextInt(6);
			} while (!game.isTileRemovable(row, col));
			game.removeTile(row, col);
			
			wait = true;
		}
	}
}
