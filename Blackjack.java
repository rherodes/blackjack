//********************************************************************
//  Blackjack.java
//	author: Rick Herodes
//	date:	4/24/2019
//
//  Creates a game of blackjack.
//********************************************************************

import javax.swing.JFrame;

public class Blackjack
{
	//-----------------------------------------------------------------
	//  Creates and displays the main program frame.
	//-----------------------------------------------------------------
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Blackjack");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new BlackjackPanel());

		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}
}