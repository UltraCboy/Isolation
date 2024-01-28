// CSC 462 : Christian Honicker

package Game;

import java.awt.*;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		JFrame master = new JFrame();
		master.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		
		JLabel title = new JLabel("Isolation");
		title.setFont(new Font("Arial", Font.BOLD, 32));
		panel.add(title, BorderLayout.NORTH);
		
		String[] players = {"Human", "Random", "AI (H1)", "AI (H2)"};
		JComboBox selectP1 = new JComboBox(players);
		selectP1.setFont(new Font("Arial", Font.BOLD, 16));
		JComboBox selectP2 = new JComboBox(players);
		selectP2.setFont(new Font("Arial", Font.BOLD, 16));
		panel.add(selectP1, BorderLayout.CENTER); panel.add(selectP2, BorderLayout.CENTER);
		
		JButton button = new JButton("Start");
		button.setFont(new Font("Arial", Font.BOLD, 24));
		button.addActionListener(a -> {
			Player p1 = null, p2 = null;
			switch(selectP1.getSelectedIndex()) {
				case 0: p1 = new HumanPlayer(1); break;
				case 1: p1 = new RandomPlayer(1); break;
				case 2: p1 = new AIPlayer(1, false); break;
				case 3: p1 = new AIPlayer(1, true); break;
			}
			switch(selectP2.getSelectedIndex()) {
				case 0: p2 = new HumanPlayer(2); break;
				case 1: p2 = new RandomPlayer(2); break;
				case 2: p2 = new AIPlayer(2, false); break;
				case 3: p2 = new AIPlayer(2, true); break;
			}
			new GameBoard(p1, p2);
			master.setVisible(false);
			master.dispose();
		});
		panel.add(button, BorderLayout.SOUTH);
		
		master.add(panel);
		master.setSize(new Dimension(200, 200));
		master.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    master.setVisible(true);
	}
	
	public static int[][] copy2DArray(int[][] a) {
		int[][] out = new int[a.length][a[0].length];
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[0].length; j++)
				out[i][j] = a[i][j];
		return out;
	}
}
