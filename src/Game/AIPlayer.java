// CSC 462 : Christian Honicker

package Game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An AI player that selects its moves based on a Minimax Search.
 */
public class AIPlayer extends Player {
	int maxDepth = 10;
	int maxMovesPerLevel = 10000;
	boolean useH2;
	
	/**
	 * Creates an instance.
	 * @param p Player number
	 * @param h False if this AI should use Heuristic 1, True if this AI should use Heuristic 2
	 */
	public AIPlayer(int p, boolean h) { super(p); useH2 = h;}
	
	@Override
	public void run() {
		while(true) {
			try {
				sleep(1);
				if(wait) continue;
			} catch (InterruptedException e) {}
			
			List<List<IsolationState>> states = new ArrayList<List<IsolationState>>();
			List<IsolationState> root = new ArrayList<IsolationState>();
			root.add(new IsolationState(game, playerNum, useH2));
			states.add(root);
			int depth = 0;
			
			// Generate the search tree
			while (depth < maxDepth) {
				boolean myTurn = depth % 2 == 0;
				List<IsolationState> nextLevel = new ArrayList<IsolationState>();
				int movesTried = 0;
				for(IsolationState s : states.get(depth)) {
					List<List<Point>> moves = s.getLegalMoves(myTurn);
					for(List<Point> m : moves) {
						if(movesTried >= maxMovesPerLevel) break;
						nextLevel.add(s.simulateMove(m.get(0), m.get(1), myTurn));
						movesTried++;
					}
				}
				Collections.sort(nextLevel);
				if(myTurn) Collections.reverse(nextLevel);
				states.add(nextLevel);
				depth++;
			}
			
			// Search by starting at the leaves & work up
			while(depth > 1) {
				for(IsolationState s : states.get(depth))
					s.setParentHeuristic(depth);
				depth--;
			}
			Collections.sort(states.get(1));
			Collections.reverse(states.get(1));
			IsolationState best = states.get(1).get(0);
			while (best.parent.parent != null) best = best.parent;
			game.movePlayer(best.move.x, best.move.y, playerNum);
			game.removeTile(best.remove.x, best.remove.y);
			
			wait = true;
		}
	}
}
