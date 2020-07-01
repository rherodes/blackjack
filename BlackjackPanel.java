//********************************************************************
//  BlackjackPanel.java
//	author: Rick Herodes
//	date:	4/24/2019
//
//  Creates a GUI in which to play a game of blackjack.
//********************************************************************

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class BlackjackPanel extends JPanel
{
	//Variable Declarations
	private final int WIDTH = 1900, HEIGHT = 950, TABLESIZE = 1800;								//table drawing constants

	private ImageIcon[] cards = new ImageIcon[52];												//holds all card pictures, also
	private ImageIcon[] playerCards = new ImageIcon[12], dealerCards = new ImageIcon[12], playerSplitCards = new ImageIcon[12];				//holds player and dealer cards
	private JButton hit, stand, split, doubleDown, surrender;									//buttons for action on cards or to bet
	private JLabel[] results = new JLabel[6];													//holds output saying what's happening
	private JLabel funds, bet;																	//used to show funds and the bet header
	private int money, betAmount;																//used for fund math
	private int cardNumber, playerCardCount, playerCardSplitCount;								//which card should be used and how many cards player has
	private int playerPointTotal, dealerPointTotal, pointTotal, temp, cardCountPlayer, cardCountDealer;		//holds totals for card values and holds individual value
	private int numAcesPlayer, numAcesSplit, numAcesDealer;										//used to choose most effective use of aces
	private boolean splitDone;																	//used to tell if the split has been completed or not
	private JTextField betInput;																//used to input the bet amount
	//private int TextPanelWidth = 0;															//stores the width of the text panel for sizing


	/********************************************************************/

	public BlackjackPanel()
	{
		//create border layout
		setLayout(new BorderLayout());

		//create JPanel with grid layoutfor buttons
		JPanel buttonLayout	= new JPanel();
		buttonLayout.setLayout(new GridLayout(6,1));

		//create JPanel for bets
		JPanel betLayout = new JPanel();
		betLayout.setLayout(new GridLayout(1,2));
		betLayout.setBackground(Color.blue);

		//create bet JLabel
		bet = new JLabel("Bet:");
		bet.setFont(new Font("Helvetica", Font.PLAIN, 48));
		bet.setHorizontalAlignment(JLabel.CENTER);
		bet.setForeground(Color.white);

		//add bet text to betLayout
		betLayout.add(bet);

		//create JTextField for bet amount
		betInput = new JTextField(5);
		betInput.setFont(new Font("Helvetica", Font.PLAIN, 48));
		betInput.setHorizontalAlignment(JLabel.CENTER);
		betInput.addActionListener(new BetListener());
		betInput.setEnabled(true);
		betInput.requestFocus();

		//add betInput next to the bet button
		betLayout.add(betInput);

		//add betLayout to buttonLayout
		buttonLayout.add(betLayout);

		//create and add hit button to button layout
		hit = new JButton("Hit");
		hit.setFont(new Font("Helvetica", Font.PLAIN, 48));
		hit.setBackground(Color.blue);
        hit.setForeground(Color.white);
		hit.addActionListener(new ButtonListener());
		hit.setEnabled(false);
		buttonLayout.add(hit);

		//create and add stand button to button layout
		stand = new JButton("Stand");
		stand.setFont(new Font("Helvetica", Font.PLAIN, 48));
		stand.setBackground(Color.blue);
        stand.setForeground(Color.white);
		stand.addActionListener(new ButtonListener());
		stand.setEnabled(false);
		buttonLayout.add(stand);

		//create and add split button to button layout
		split = new JButton("Split");
		split.setFont(new Font("Helvetica", Font.PLAIN, 48));
		split.setBackground(Color.blue);
        split.setForeground(Color.white);
		split.addActionListener(new ButtonListener());
		split.setEnabled(false);
		buttonLayout.add(split);

		//create and add doubleDown button to button layout
		doubleDown = new JButton("Double Down");
		doubleDown.setFont(new Font("Helvetica", Font.PLAIN, 48));
		doubleDown.setBackground(Color.blue);
        doubleDown.setForeground(Color.white);
		doubleDown.addActionListener(new ButtonListener());
		doubleDown.setEnabled(false);
		buttonLayout.add(doubleDown);

		//create and add surrender button to button layout
		surrender = new JButton("Surrender");
		surrender.setFont(new Font("Helvetica", Font.PLAIN, 48));
		surrender.setBackground(Color.blue);
        surrender.setForeground(Color.white);
		surrender.addActionListener(new ButtonListener());
		surrender.setEnabled(false);
		buttonLayout.add(surrender);

		//put card images into cards array
		for(int i=0; i<cards.length; i++)
		{
			//creates a new card and adds it to the cards array
			cards[i] = new ImageIcon("cards/" + i + ".png",Integer.toString(i));
		}
		//end for cards.length

		//initial shuffle
		shuffle();

		//create JPanel with grid layout for text
		JPanel textLayout	= new JPanel();
		textLayout.setLayout(new GridLayout(7,1));
		textLayout.setBackground(Color.blue);

		//set first item in results to Results
		results[0] = new JLabel("Results");
		results[0].setFont(new Font("Helvetica", Font.PLAIN, 48));
		results[0].setForeground(Color.white);
		results[0].setHorizontalAlignment(JLabel.CENTER);

		//create and add border to results box
		Border border = BorderFactory.createLineBorder(Color.white, 5);
		results[0].setBorder(border);

		//add the results header to the text layout
		textLayout.add(results[0]);

		//fill event text array and add to display
		for(int i=1; i<results.length; i++)
		{
			results[i] = new JLabel("--------------------------");
			results[i].setFont(new Font("Helvetica", Font.PLAIN, 36));
			results[i].setForeground(Color.white);
			results[i].setHorizontalAlignment(JLabel.CENTER);
			textLayout.add(results[i]);
		}
		//end for text array

		//initialize
		money = 500;
		cardNumber = 0;
		playerCardCount = 2;
		playerPointTotal = 0;
		dealerPointTotal = 0;
		splitDone = false;

		//create funds jlabel
		funds = new JLabel("Funds: $" + money);
		funds.setFont(new Font("Helvetica", Font.PLAIN, 48));
		funds.setForeground(Color.white);
		funds.setHorizontalAlignment(JLabel.CENTER);
		funds.setBorder(border);

		//add funds to textLayout
		textLayout.add(funds);

		//add buttonLayout to right side of window
		add(buttonLayout, BorderLayout.EAST);

		//add textLayout, header, and spacing to bottom of window
		add(textLayout, BorderLayout.WEST);

		//set screen size
		setPreferredSize(new Dimension(WIDTH+900, HEIGHT));

	}
	//end BlackjackPanel


	//method used to shuffle the deck
	public void shuffle()
	{
		//creates a new random component
		Random rand = new Random();

		//loops through each card and moves it to a random location
		for (int i=0; i<cards.length; i++)
		{
			//gets random position, then swaps the cards using a temp
			int randPos = rand.nextInt(cards.length);
			ImageIcon temp = cards[i];
			cards[i] = cards[randPos];
			cards[randPos] = temp;
		}
		//end for cards.length
	}
	//end shuffle


	//method to add new message in results
	public void newMessage(String message)
	{
		results[1].setText(results[2].getText());
		results[2].setText(results[3].getText());
		results[3].setText(results[4].getText());
		results[4].setText(results[5].getText());
		results[5].setText(message);
	}
	//end newMessage


	//used for resetting game
	public void reset()
	{
		//disable buttons
		hit.setEnabled(false);
		stand.setEnabled(false);
		split.setEnabled(false);
		doubleDown.setEnabled(false);
		surrender.setEnabled(false);

		//clear and open up betting
		betInput.setText("");
		betInput.setEnabled(true);

		//new message input bid
		newMessage("Enter new bid");

		//set focus to input
		betInput.requestFocus();
	}
	//end reset


	//card totaler, returns point total
	public int totaler(ImageIcon[] total, int type)
	{
		//reset point total
		pointTotal = 0;

		//reset temp
		temp = 0;

		//for each card in player array
		for(int i=0; i<total.length; i++)
		{
			//if card exists
			if(total[i] != null)
			{
				//if description number/4 under 36
				if(Integer.parseInt(total[i].getDescription()) < 36)
				{
					//add temp amount to total
					temp = Integer.parseInt(total[i].getDescription())/4 + 2;
				}
				//else if ace (default to 1, keep ace count)
				else if(Integer.parseInt(total[i].getDescription()) >= 36 && Integer.parseInt(total[i].getDescription()) < 40)
				{
					//set temp to 1
					temp = 1;

					//if player
					if(type == 0)
					{
						//add one to ace count
						numAcesPlayer++;
					}
					//else if dealer
					else if(type == 1)
					{
						//add one to ace count
						numAcesDealer++;
					}
					//else split
					else
					{
						//add one to ace count
						numAcesSplit++;
					}
					//end if
				}
				//else description number over 36
				else
				{
					//set card value to 10
					temp = 10;
				}
				//end if

				//increment total by card value
				pointTotal += temp;
			}
			//end if
		}
		//end for

		//return point total
		return pointTotal;
	}
	//end card totaler


	//compare players cards to dealers cards
	//returns money accordingly and produces messages
	public void compare(ImageIcon[] player)
	{
		//reset point total and card count values
		playerPointTotal = 0;
		cardCountPlayer = 0;
		numAcesPlayer = 0;

		//for each card in player array
		for(int i=0; i<player.length; i++)
		{
			//if card exists
			if(player[i] != null)
			{
				//if description number/4 under 36
				if(Integer.parseInt(player[i].getDescription()) < 36)
				{
					//add temp amount to total
					temp = Integer.parseInt(player[i].getDescription())/4 + 2;
				}
				//else if ace
				else if( Integer.parseInt(player[i].getDescription()) >= 36 && Integer.parseInt(player[i].getDescription()) < 40)
				{
					//add one to points
					temp = 1;

					//increment num aces player
					numAcesPlayer++;
				}
				//else description number over 36
				else
				{
					//set card value to 10
					temp = 10;
				}
				//end if
				//increment total by card value
				playerPointTotal += temp;

				//increment player card amount
				cardCountPlayer++;
			}
			//end if
		}
		//end for

		//if number of aces 1 or more
		if( numAcesPlayer > 0)
		{
			//if making 1 into a 11 would be less than or equal to 21
			if( playerPointTotal + 10 <= 21 )
			{
				playerPointTotal += 10;
			}
			//end if
		}
		//end if

		//if player busts
		if( playerPointTotal > 21 )
		{
			//bust, send message
			newMessage("Bust: Bet forfeited");
		}
		//else if dealer busts
		else if( dealerPointTotal > 21 )
		{
			//if total 21
			if(playerPointTotal == 21)
			{
				//if natural blackjack
				if( cardCountPlayer == 2 )
				{
					//payout 3:2
					money += betAmount + betAmount*1.5;
					funds.setText("Funds: $" + money);

					//new message blackjack
					newMessage("BLACKJACK! Payout is $" + (betAmount + betAmount*1.5) );
				}
				//else 3 card blackjack
				else
				{
					//payout is 1:1
					money += betAmount*2;
					funds.setText("Funds: $" + money);

					//new message, its 3 card so not blackjack
					newMessage("You win! Payout is $" + betAmount*2);
				}
				//end if
			}
			//else total not 21
			else
			{
				//payout 1:1
				money += betAmount*2;
				funds.setText("Funds: $" + money);

				//new message normal win
				newMessage("You win! Payout is $" + betAmount*2);
			}
			//end if
		}
		//else if player points more than dealer
		else if( playerPointTotal > dealerPointTotal )
		{
			//if total 21
			if(playerPointTotal == 21)
			{
				//if natural blackjack
				if( cardCountPlayer == 2 )
				{
					//payout 3:2
					money += betAmount + betAmount*1.5;
					funds.setText("Funds: $" + money);

					//new message blackjack
					newMessage("BLACKJACK! Payout is $" + (betAmount + betAmount*1.5) );
				}
				//else 3 card blackjack
				else
				{
					//payout is 1:1
					money += betAmount*2;
					funds.setText("Funds: $" + money);

					//new message, its 3 card so not blackjack
					newMessage("You win! Payout is $" + betAmount*2);
				}
				//end if
			}
			//else total not 21
			else
			{
				//payout 1:1
				money += betAmount*2;
				funds.setText("Funds: $" + money);

				//new message normal win
				newMessage("You win! Payout is $" + betAmount*2);
			}
			//end if
		}
		//else if point the same
		else if( playerPointTotal == dealerPointTotal)
		{
			//if blackjack
			if( playerPointTotal == 21)
			{
				//if natural blackjack over 3+ card
				if( cardCountPlayer == 2 && cardCountDealer > 2 )
				{
					//payout 3:2
					money += betAmount + betAmount*1.5;
					funds.setText("Funds: $" + money);

					//new message blackjack
					newMessage("BLACKJACK! Payout is $" + (betAmount + betAmount*1.5) );
				}
				//else if dealer has natural
				else if( cardCountDealer == 2 && cardCountPlayer > 2 )
				{
					//losing message
					newMessage("You lose, bet is forfeited");
				}
				//else both natural or multi card
				else
				{
					//return bet
					money += betAmount;
					funds.setText("Funds: $" + money);

					//new message push
					newMessage("Push. Bet ($" + betAmount + ") is returned");
				}
				//end if
			}
			//else not 21
			else
			{
				//return bet
				money += betAmount;
				funds.setText("Funds: $" + money);

				//new message push
				newMessage("Push. Bet ($" + betAmount + ") is returned");
			}
			//end if
		}
		//else player less than dealer
		else
		{
			//new message loss, bet lost
			newMessage("You lose, bet is forfeited");
		}
		//end if

		//disable hit button
		hit.setEnabled(false);
		stand.setEnabled(false);
	}
	//end compare


	//takes care of the dealers hand
	public void dealerPlay()
	{
		//reset dealer point total and card count
		dealerPointTotal = 0;
		cardCountDealer = 2;

		//get second card
		dealerCards[1]	= cards[cardNumber];

		//increment deck position
		cardNumber++;

		//get dealer card total
		//if description number/4 under 36
		if(Integer.parseInt(dealerCards[0].getDescription()) < 36)
		{
			//add card amount to total
			dealerPointTotal += Integer.parseInt(dealerCards[0].getDescription())/4 + 2;
		}
		//else if ace
		else if( Integer.parseInt(dealerCards[0].getDescription()) >= 36 && Integer.parseInt(dealerCards[0].getDescription()) < 40 )
		{
			//if adding 11 makes 21
			if( dealerPointTotal + 11 < 22 && dealerPointTotal + 11 > 16 )
			{
				//increment dealer points by 11
				dealerPointTotal += 11;

				//increment num aces
				numAcesDealer++;
			}
			//else doesnt make 21
			else
			{
				//add one
				dealerPointTotal++;

				//increment num aces
				numAcesDealer++;
			}
			//end if
		}
		//else description number over 36
		else
		{
			//add 10 to point total
			dealerPointTotal += 10;
		}
		//end if

		//if card 2 through 10
		if(Integer.parseInt(dealerCards[1].getDescription()) < 36)
		{
			//add card amount to total
			dealerPointTotal += Integer.parseInt(dealerCards[1].getDescription())/4 + 2;

			//if theres an ace, make sure not 16
			if( numAcesDealer > 0)
			{
				//if not < 16
				if( dealerPointTotal + 10 > 16)
				{
					//add 10 points, the ace would have been 11
					dealerPointTotal += 10;
				}
				//end if
			}
			//end if
		}
		//else if ace
		else if( Integer.parseInt(dealerCards[1].getDescription()) >= 36 && Integer.parseInt(dealerCards[1].getDescription()) < 40)
		{
			//if adding 11 makes 21
			if( dealerPointTotal + 11 < 22 && dealerPointTotal + 11 > 16 )
			{
				//increment dealer points by 11
				dealerPointTotal += 11;

				//increment num aces
				numAcesDealer++;
			}
			//else doesnt make 21
			else
			{
				//add one
				dealerPointTotal++;

				//increment num aces
				numAcesDealer++;
			}
			//end if
		}
		//else description number over 36
		else
		{
			//add 10 to point total
			dealerPointTotal += 10;
		}
		//end if

		//while total at 16 or below
		while( dealerPointTotal <= 16 )
		{
			//add new card to dealer cards array
			dealerCards[cardCountDealer] = cards[cardNumber];

			//increment card count for dealer
			cardCountDealer++;

			//increment deck position
			cardNumber++;

			//if description number/4 under 36
			if(Integer.parseInt(dealerCards[cardCountDealer-1].getDescription()) < 36)
			{
				//add card amount to total
				dealerPointTotal += Integer.parseInt(dealerCards[cardCountDealer-1].getDescription())/4 + 2;
			}
			//else if ace
			else if( Integer.parseInt(dealerCards[cardCountDealer-1].getDescription()) >= 36 && Integer.parseInt(dealerCards[cardCountDealer-1].getDescription()) < 40)
			{
				//if adding 11 makes 21
				if( dealerPointTotal + 11 < 22 && dealerPointTotal + 11 > 16  )
				{
					//increment dealer points by 11
					dealerPointTotal += 11;

					//increment num aces
					numAcesDealer++;
				}
				//else doesnt make 21
				else
				{
					//add one
					dealerPointTotal++;

					//increment num aces
					numAcesDealer++;
				}
				//end if
			}
			//else description number over 36
			else
			{
				//add 10 to point total
				dealerPointTotal += 10;
			}
			//end if

			if(dealerPointTotal + 10 < 22 && dealerPointTotal + 10 > 16 && numAcesDealer > 0)
			{
				//make ace worth 10
				dealerPointTotal += 10;
			}
			//end if
		}
		//end while

		//repaint cards
		repaint();
	}
	//end dealerPlay


	//used for drawing shapes to the screen
	public void paintComponent(Graphics page)
	{
		//creates paint component for adding drawings to
		super.paintComponent(page);

		//make background black
		page.setColor(Color.black);
		page.fillRect(0,0,WIDTH+500,HEIGHT+500);

		//draw green table
		page.setColor(Color.green);
		page.fillOval( (((WIDTH+320)-TABLESIZE)/2) + 200,-1*(TABLESIZE/2),TABLESIZE,TABLESIZE);

		//DRAW CARDS LOOP
		//for card in cards
		for(int i=0; i<playerCards.length; i++)
		{
			//if card not null
			if(playerCards[i] != null)
			{
				playerCards[i].paintIcon(this,page,WIDTH/2+75+i*25,TABLESIZE/2-100);
			}
			//end if

			//if card not null
			if(dealerCards[i] != null)
			{
				dealerCards[i].paintIcon(this,page,WIDTH/2+50+i*25,150);
			}
			//end if

			//if card not null
			if(playerSplitCards[i] != null)
			{
				playerSplitCards[i].paintIcon(this,page,WIDTH/2-75+i*25,TABLESIZE/2-100);
			}
			//end if
		}
		//end for

		//draw dealer heading to screen
		page.setColor(Color.white);
		page.setFont(new Font("Helvetica", Font.PLAIN, 48));
		page.drawString("Dealer",(WIDTH/2)+300,50);
	}
	//end paintComponent


	//*****************************************************************
	//  Listener for the buttons.
	//*****************************************************************
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			//If hit button pressed
			if( event.getSource() == hit )
			{
				//disable double down, split and surrender buttons
				doubleDown.setEnabled(false);
				split.setEnabled(false);
				surrender.setEnabled(false);

				//if split done is false (split not started/doesnt exist)
				if(splitDone == false)
				{
					//add new card to player cards array
					playerCards[playerCardCount] = cards[cardNumber];

					//increment card number
					cardNumber++;

					//increment playerCardCount
					playerCardCount++;

					//if totaler returns over 21 then stop
					if( totaler(playerCards,0) == 21 )
					{
						//disable hit button
						hit.setEnabled(false);
						stand.setEnabled(false);

						//play dealer hand then compare
						dealerPlay();
						compare(playerCards);

						//reset game
						reset();
					}
					//else if over 21
					else if( totaler(playerCards,0) > 21)
					{
						//disable hit button
						hit.setEnabled(false);
						stand.setEnabled(false);

						//bust, send message
						newMessage("Bust: Bet forfeited");

						//reset game
						reset();
					}
					//end if
				}
				//else split started
				else
				{
					//add new card to player cards array
					playerSplitCards[playerCardSplitCount] = cards[cardNumber];

					//increment card number
					cardNumber++;

					//increment playerCardCount
					playerCardSplitCount++;

					//if totaler returns over 21 then stop
					if( totaler(playerSplitCards,0) == 21 )
					{
						//disable hit button
						hit.setEnabled(false);
						stand.setEnabled(false);

						//play dealer hand then compare
						dealerPlay();
						compare(playerSplitCards);

						//reset game
						reset();
					}
					//else if over 21
					else if( totaler(playerSplitCards,0) > 21)
					{
						//disable hit button
						hit.setEnabled(false);
						stand.setEnabled(false);

						//bust, send message
						newMessage("Bust: Bet forfeited");

						//reset game
						reset();
					}
					//end if
				}
				//end if

				//paint new cards
				repaint();
			}
			//end if

			//If stand button pressed
			if( event.getSource() == stand )
			{
				//disable hit, double down, split, stand, and surrender buttons
				hit.setEnabled(false);
				split.setEnabled(false);
				doubleDown.setEnabled(false);
				surrender.setEnabled(false);
				stand.setEnabled(false);

				//if there is a split hand
				if(playerSplitCards[0] != null && splitDone == false )
				{
					//enable hit and stand
					hit.setEnabled(true);
					stand.setEnabled(true);

					//set split done to 1
					splitDone	= true;
				}
				//else split done/doesnt exist
				else
				{
					//play dealer hand then compare
					dealerPlay();
					compare(playerCards);

					//if split exists
					if( splitDone == true )
					{
						//compare to dealer
						compare(playerSplitCards);
					}

					//reset game
					reset();
				}
				//end if
			}
			//end if

			//If split button pressed
			if( event.getSource() == split )
			{
				//disable doubleDown and surrender buttons
				doubleDown.setEnabled(false);
				surrender.setEnabled(false);

				//cards split message
				newMessage("Cards split");

				//subtract from funds and double bet
				money -= betAmount;
				funds.setText("Funds: $" + money);
				betAmount *= 2;

				//split cards
				playerSplitCards[0]	= playerCards[1];

				//add new card to first hand
				playerCards[1]	= cards[cardNumber];

				//increment deck position
				cardNumber++;

				//add new card to second hand
				playerSplitCards[1]	= cards[cardNumber];

				//increment deck position
				cardNumber++;

				//create new message with new bet amount
				newMessage("Bet doubled: $" + betAmount);

				//repaint
				repaint();
			}
			//end if

			//If doubleDown button pressed
			if( event.getSource() == doubleDown )
			{
				//subtract from funds and double bet
				money -= betAmount;
				funds.setText("Funds: $" + money);
				betAmount *= 2;

				//create new message with new bet amount
				newMessage("Bet doubled: $" + betAmount);
				newMessage("One more card drawn");

				//disable split, hit, stand, double down and surrender buttons
				hit.setEnabled(false);
				stand.setEnabled(false);
				split.setEnabled(false);
				surrender.setEnabled(false);
				doubleDown.setEnabled(false);

				//Get next card
				playerCards[2] = cards[cardNumber];

				//increment cardNumber
				cardNumber++;

				//play dealer hand then compare
				dealerPlay();
				compare(playerCards);
//no need for split code, no doubles after a split

				//repaint new card
				repaint();

				//reset game
				reset();
			}
			//end if

			//If surrender button pressed
			if( event.getSource() == surrender )
			{
				//message that half bet returned
				newMessage("Hand surrendered");
				newMessage("$"+(int)(betAmount*0.5)+" returned to player");

				//return half the bet
				money += (int)(betAmount*0.5);
				funds.setText("Funds: $" + money);

				//resets game
				reset();
			}
			//end if
		}
		//end actionPerformed
	}
	//end ButtonListener

	//*****************************************************************
	//  Listener for the JTextField.
	//*****************************************************************
	private class BetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			//if bet is 0
			if( Integer.parseInt(betInput.getText()) == 0 )
			{
				//message error 0
				newMessage("Invalid bet, 0 is not valid");

				//reset to new bet
				reset();
			}
			//else if bet is less than funds
			else if( Integer.parseInt(betInput.getText()) <= money )
			{
				//store bet amount
				betAmount	= Integer.parseInt(betInput.getText());

				//decrement funds by bet amount
				money 		-= betAmount;
				funds.setText("Funds: $" + money);
				betInput.setEnabled(false);

				//reset card counts for both split and normal
				playerCardCount			= 2;
				playerCardSplitCount 	= 2;
				cardNumber 				= 0;
				playerPointTotal 		= 0;
				dealerPointTotal 		= 0;
				pointTotal 				= 0;
				cardCountPlayer 		= 0;
				cardCountDealer	 		= 0;
				numAcesPlayer 			= 0;
				numAcesDealer 			= 0;
				numAcesSplit 			= 0;
				splitDone 				= false;

				//for each member of cards array
				for(int i=0; i<playerCards.length; i++)
				{
					//reset player, split, and dealer arrays
					playerCards[i] 			= null;
					playerSplitCards[i] 	= null;
					dealerCards[i] 			= null;
				}
				//end for

				//shuffle cards
				shuffle();

				//pick player and dealer cards
				playerCards[0]	= cards[0];
				dealerCards[0]	= cards[1];
				playerCards[1]	= cards[2];

				//set cardNumber to hold spot in deck
				cardNumber		= 3;

				//check for natural blackJack
				if( totaler(playerCards,0) == 21 )
				{
					//new message showing bet
					newMessage("Bet placed: $"+betAmount);

					//disable hit button
					hit.setEnabled(false);
					stand.setEnabled(false);

					//play dealer hand then compare
					dealerPlay();
					compare(playerCards);

					//reset game
					reset();
				}
				//else not 21
				else
				{
					//enable hit, stand, surrender and double down buttons
					hit.setEnabled(true);
					stand.setEnabled(true);
					doubleDown.setEnabled(true);
					surrender.setEnabled(true);

					//if cards are the same
					if( Integer.parseInt(playerCards[0].getDescription())/4 == Integer.parseInt(playerCards[1].getDescription())/4 )
					{
						//enable split button
						split.setEnabled(true);
					}
					//end if

					//new message showing bet
					newMessage("Bet placed: $"+betAmount);
				}
				//end if

				//paint cards on table
				repaint();
			}
			//else bet is more than funds
			else
			{
				//print message: insufficient funds
				newMessage("Insufficient funds for bet");
			}
			//end if
		}
		//end actionPerformed
	}
	//end BetListener
}
//end BlackjackPanel
