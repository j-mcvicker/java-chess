import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;


/**
 * ChessGUI to draw board and pieces. Also allows actions to be performed on board / game overall
 * @authors Justin McVicker, Kai, Justin Yu, Alazar
 * Note: game squares are organized using a 2D array of [8][8] buttons, and all the data regarding occupied squares, and 
 * 		color of pieces in squares are organized using matching 2D [8][8] int arrays.
 * 	Note: These arrays can be accessed by [row][column], indexes 0-7.
 * 	Note: On the game board, player white appears Red and player black appears Blue
 * 
 * Image for chesspieces is was photo-edited to be unique pieces for this game by Justin Y.
 */
public class ChessGUI {
	private final JPanel gui = new JPanel(new BorderLayout(3, 3));
	private JButton[][] chessBoardSquares = new JButton[8][8];
	
	private Image[][] chessPieceImages = new Image[2][6]; //Black(Row1): Q,K,R,Kn,B,P ; White(Row2): Q,K,R,Kn,B,P
	private JPanel chessBoard;
	private final JLabel message = new JLabel("Chess");
	private static final String COLS = "ABCDEFGH";
	
	//These integers will be used to lookup the corresponding images in chessPieceImages
			// These numbers will essentially be used as ID's for pieces throughout the game, can be checked using occupiedSquares[][]
			// Because the Queen is taking int value 0, the "empty space" icon will be assigned the int value 10 
	public static final int KING = 0, QUEEN = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
	//This will indicate which row to select images from in chessPieceImages
	public static final int BLACK = 0, WHITE = 1;
	//This essentially marks the numerical index positions needed to write the correct fist row = [2, 3, 4, 1, 0, 4, 3, 2] lookup ID for chessPieceImages 
	public static final int[] STARTING_ROW = { ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK };
	
	Controller control; // = new Controller();
	Model model; // = new Model();
	
	boolean whiteTurn = true;
	int turnCounter = 0;
	int undoCounter = 0; //this will count from 0-3 to determine how many moves the person is trying to undo.
	
	JLabel playerLabel;
	String playerString = ""; //will keep track of whose turn it is and display on screen for user
	
	

	ChessGUI(Controller ctrl, Model mdl){
		control = ctrl;
		model = mdl;
		initializeGui();
	}

	public final void initializeGui(){
		// create the images for the chess pieces
		createImages();

		// set up the main GUI
		gui.setBorder(new EmptyBorder(5, 5, 5, 5));
		JToolBar tools = new JToolBar();
		tools.setFloatable(false);
		gui.add(tools, BorderLayout.PAGE_START);
		
		Action newGameAction = new AbstractAction("New Game") {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setupNewGame();
			}
		};
		tools.add(newGameAction);
		
		
		tools.addSeparator();
	
		Action surrenderGameAction = new AbstractAction("Surrender Game") {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				surrenderGame();
			}
		};
		tools.add(surrenderGameAction);
		
		tools.addSeparator();	
		
		Action undoMoveAction = new AbstractAction("Undo Last Move") {
			public void actionPerformed(ActionEvent e)
			{
			//This feature is still being worked on, currently causes errors and needs further decoding
				int previousSpot = model.getPreviousClicks()[5]; //this is the value of whatever was in the square before it is being undone
				int previousSpotCol = model.getPreviousClicks()[1];
				int previousSpotRow = model.getPreviousClicks()[0];
				int previousSpot2 = model.getPreviousClicks()[2]; //this is the value of whatever was in the square before it is being undone
						
				int previousSpotColor = model.getColorOfSquares()[previousSpotRow][previousSpotCol];
				boolean canUndo = model.undoLastMove();
				if(canUndo) {
					move();
					//model.setRestorePositions(); //makes piece dissapear after moving it
					System.out.println(previousSpot+" and "+previousSpot2);
					//System.out.println(previousSpotColor+" and "+mode);
					if(previousSpot != 10){//previousSpot == previousSpot2 && previousSpot != 10){
						replacePiece();
					}
					System.out.println("Current location of pieces:");
					model.printOccupied();
					model.clearPreviousSavedClicks(); //because clicks are changed for undo, resetting them prevents any unusual movement
					
					
					//playerButton.setText(control.whoseTurn()); //Sets the GUI label that lets the user see whose turn it is
					if(control.playerTurn()){
						//playerButton.setBackground(Color.BLUE);
						playerLabel.setForeground(Color.BLUE);
						playerLabel.setBorder(new CompoundBorder(new LineBorder(Color.BLUE,5), new BevelBorder(BevelBorder.RAISED)));
						
					}else{
						//playerButton.setBackground(Color.RED);
						playerLabel.setForeground(Color.RED);
						playerLabel.setBorder(new CompoundBorder(new LineBorder(Color.RED,5), new BevelBorder(BevelBorder.RAISED)));
					}
					playerLabel.setText(control.whoseTurn());
					
				}
			}
		};
		tools.add(undoMoveAction);
		
		tools.addSeparator();
		Action exitGameAction = new AbstractAction("Exit Game") {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				exitGame();
			}
		};
		tools.add(exitGameAction);
		tools.addSeparator();	
		
		
		//This is a panel that will be inserted to the left of the chessboard, containing the player stats.
		JPanel playerStats = new JPanel(new GridLayout(4,0)){
			public final Dimension getPreferredSize(){
				Dimension d = super.getPreferredSize();
				Dimension prefSize = null;
				Component c = getParent();
				if (c == null)
				{
					prefSize = new Dimension((int) d.getWidth(), (int) d.getHeight());
				} 
				
				else if (c != null && c.getWidth() > d.getWidth() && c.getHeight() > d.getHeight())
				{
					prefSize = c.getSize();
				} 
				
				else
				{
					prefSize = d;
				}
				int w = (int) prefSize.getWidth();
				int h = (int) prefSize.getHeight();
				// the smaller of the two sizes
				int s = (w > h ? h : w);
				return new Dimension(s, s);
			}
		}; //end playerStats creation
		playerStats.setBorder(new CompoundBorder(new EmptyBorder(8, 8, 8, 8), new EmptyBorder(1,1,1,1))); //new LineBorder(Color.white)));
		playerStats.setBackground(Color.LIGHT_GRAY);
		
		playerString = control.whoseTurn(); //will keep track of whose turn it is and display on screen for user
		
		playerLabel = new JLabel(playerString);
		playerLabel.setOpaque(true);
		playerLabel.setBorder(new CompoundBorder(new LineBorder(Color.BLUE,5), new BevelBorder(BevelBorder.RAISED)));
		playerLabel.setForeground(Color.BLUE);
		playerLabel.setVerticalTextPosition(SwingConstants.CENTER);
		playerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		playerLabel.setFont(new Font("Serif", Font.PLAIN, 50));
	   
	    /* JButton playerButton = new JButton();
		playerButton.setText(playerString);
		//playerButton.setBackground(Color.BLUE);
		playerButton.setOpaque(true);
		playerButton.setFont(new Font("Serif", Font.PLAIN, 20));  
		
		playerStats.add(playerButton, BorderLayout.LINE_START); */
		playerStats.add(playerLabel);
		
		

		
		//Just figuring out where these things pop up...
		//gui.add(new JLabel("Player 2"), BorderLayout.AFTER_LINE_ENDS); //what for?
		//gui.add(new JLabel("Player 22"), BorderLayout.EAST);
		gui.add(new JLabel("Play the Game!"), BorderLayout.SOUTH);

		chessBoard = new JPanel(new GridLayout(0, 9)) {
			/**
			 * Override the preferred size to return the largest it can, in a
			 * square shape. Must (must, must) be added to a GridBagLayout as
			 * the only component (it uses the parent as a guide to size) with
			 * no GridBagConstaint (so it is centered).
			 */
			@Override
			public final Dimension getPreferredSize(){
				Dimension d = super.getPreferredSize();
				Dimension prefSize = null;
				Component c = getParent();
				if (c == null)
				{
					prefSize = new Dimension((int) d.getWidth(), (int) d.getHeight());
				} 
				
				else if (c != null && c.getWidth() > d.getWidth() && c.getHeight() > d.getHeight())
				{
					prefSize = c.getSize();
				} 
				
				else
				{
					prefSize = d;
				}
				int w = (int) prefSize.getWidth();
				int h = (int) prefSize.getHeight();
				// the smaller of the two sizes
				int s = (w > h ? h : w);
				return new Dimension(s, s);
			}
		}; //end chessBoard creation
		
		chessBoard.setBorder(new CompoundBorder(new EmptyBorder(8, 8, 8, 8), new EmptyBorder(1,1,1,1))); //new LineBorder(Color.white)));
	
		chessBoard.setBackground(Color.white);
		
		JPanel boardConstrain = new JPanel(new GridBagLayout()); //JPanel to hold the chessBoard JPanel
		boardConstrain.setBackground(Color.LIGHT_GRAY);
		boardConstrain.add(playerStats);
		boardConstrain.add(chessBoard);
		
		
		gui.add(boardConstrain);  //main frame JPanel to hold the Panel with gameBoard in it
		
		

		// creates the chess board squares as 64x64pixel icons to be placed in squares
		//note that this was still implemented after using the background color got implemented
		// this is because we want to set the default beginning square size to the same size as the pieces that will be placed on it 64x64px
		Insets buttonMargin = new Insets(0, 0, 0, 0);
		BufferedImage black = null;
		BufferedImage white = null;
		try{
			black = ImageIO.read(new File("Black.png"));
			white = ImageIO.read(new File("White.png"));
		} catch (IOException e) {
			System.out.println("The file could not be found."); 
		}
			
		
		for (int ii = 0; ii < chessBoardSquares.length; ii++)
		{
			for (int jj = 0; jj < chessBoardSquares[ii].length; jj++)
			{
				JButton b = new JButton();
				b.setMargin(buttonMargin);
				String locName = ii+" "+jj; //these two numbers will make up the (row,column) coordinates of the 64 chessboard squares
				b.setText(locName); //this is usually button text, but it will be hidden and used to store board grid location
				b.setIconTextGap(0); //removes any additional text border 
				b.setFont(new Font("Serif", Font.PLAIN, 0));; //making the text size 0 makes it no longer visible on the screen
				
				//allow checker-board pattern to extend original button area
				b.setBorderPainted(false);  //painting the border makes visible button ridges, not needed (or visibly pleasing) in a chessboard
				b.setOpaque(true); //this allows the background color which extends beyond the button icon surface to show through.
						// essentially it is the stretchable visible checker-board pattern behind any icons
				
				
				
				// Setting the alternating white and black checker-board type pattern for the chess game board
				if((jj % 2 == 1 && ii % 2 == 1) || (jj % 2 == 0 && ii % 2 == 0)){
					ImageIcon icon = new ImageIcon(black); //buffered image used to create a 64x64px image
					b.setIcon(icon); //this black icon is used to create the default shape, the same shape as the pieces
					b.setBackground(Color.BLACK); //setting the background in alternating colors for the needed chess-board pattern
					
				} 
				else{
					ImageIcon icon = new ImageIcon(white);
					b.setIcon(icon);
					b.setBackground(Color.WHITE); //the alternate white background for the 
				}
				chessBoardSquares[ii][jj] = b; //sets each button index by row then by column (in each row)
				model.setOccupiedSquares(ii,jj,10); //[ii][jj] = 10; //by default all squares are empty (no icon), so this will assing "empty" to all values
								// empty is set using 10, because '0' is occupied by the Queen piece, and this will matter for move interpretation
				model.setColorOfSquares(ii,jj,2); //[ii][jj] = 2; //sets all square piece colors to 2, which is empty
			}
		}
		

		/*
		 * fill the chess board
		 */
		chessBoard.add(new JLabel(""));
		// fill the top row
		for (int ii = 0; ii < 8; ii++)
		{
			chessBoard.add(new JLabel(COLS.substring(ii, ii + 1), SwingConstants.CENTER));
		}
		
		// fill the black non-pawn piece row
		int intLabel = 1;
		for (int ii = 0; ii < 8; ii++){
			for (int jj = 0; jj < 8; jj++){
				switch (jj)
				{
				case 0:
					chessBoard.add(new JLabel("" + (intLabel), SwingConstants.CENTER));
					intLabel++;
				default:
					chessBoard.add(chessBoardSquares[ii][jj]);
				}
			}
		}
		
		
		
		//Action listener creation
		ActionListener listener = new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (e.getSource() instanceof JButton) {
	                String text = ((JButton) e.getSource()).getText();
	                
	                model.setJustClicked(text);	// Records which square was last clicked, important for all data relays in game
	                
	                System.out.println("The square you just clicked contains: "+model.pieceLookup(model.getOccupiedOnClick(text)));
	                System.out.println("The value of item located in square you just clicked is: "+model.getOccupiedOnClick(text));
	                System.out.println("The color of the item located in square you just clicked: "+ model.colorLookup(model.getColorOnClick(text)));
	                
	                
	            }
	        }
	    };
		
		//Adding actions to the buttons for every time one is clicked.
		for (int ii = 0; ii < chessBoardSquares.length; ii++){
			for (int jj = 0; jj < chessBoardSquares[ii].length; jj++){
				chessBoardSquares[ii][jj].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						if(control.checkIfLegal()){ //enters loop only when checkIfLegal determines the last two clicked squares add up to a legal move
							move();
							model.setTurnCounter(model.getTurnCounter()+1);
							model.setPreviousClicks(model.getJustClicked());
							model.setUndoCounter(0);
							
						}

						//playerButton.setText(control.whoseTurn()); //Sets the GUI label that lets the user see whose turn it is
						if(control.playerTurn()){
							model.lastPlayed = 0; //lets game know that white player last moved
							//playerButton.setBackground(Color.BLUE);
							
							/* if (black king in check) -> narrow legal moves accordingly (special function? call checkiflegal first and then checkifcheck?)
							/* if (black's king is in checkmate)// check for checkmate
							//  end the game
							*/
							
							playerLabel.setForeground(Color.BLUE);
							playerLabel.setBorder(new CompoundBorder(new LineBorder(Color.BLUE,5), new BevelBorder(BevelBorder.RAISED)));
							
						}else{
							model.lastPlayed = 1; //lets game know that black player last moved
							//playerButton.setBackground(Color.RED);
							
							/* if (white king in check) -> narrow legal moves accordingly (special function? call checkiflegal first and then checkifcheck?)
							/* if (white's king is in checkmate)// check for checkmate
							//  end the game
							*/
							playerLabel.setForeground(Color.RED);
							playerLabel.setBorder(new CompoundBorder(new LineBorder(Color.RED,5), new BevelBorder(BevelBorder.RAISED)));
						}
						
						playerLabel.setText(control.whoseTurn());
						
						System.out.println("Person who just moved: "+model.lastPlayed);
						System.out.println("Current location of all pieces:");
						model.printOccupied();
						//System.out.println("Current color value of all pieces:");
						//printcolorOfSquares();
						//System.out.println("The last clicked values are "+Arrays.toString(model.getLastClicked()));
						//System.out.println("The just clicked values are "+Arrays.toString(model.getJustClicked()));
						//System.out.println("The previous clicked values are "+Arrays.toString(model.getPreviousClicks()));
						
					}
				});//end adding action listener
				chessBoardSquares[ii][jj].addActionListener(listener);	
				
			}
		}
		
		/*
		//Additional action button commands (for only the button [0,0])
		chessBoardSquares[0][0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(chessBoardSquares[0][0].getModel().isEnabled() == true){
					System.out.println("The button 0 0 was pressed.");
					System.out.println(occupiedSquares[0][0]);
					
				}
				if (!chessBoardSquares[0][0].isEnabled()) {
		            System.out.println("Add Button is not pressed");
		        }
			}
		});
		*/
		
	}//End intitializeGui
	
	
	private void move(){
		BufferedImage black = null;
		BufferedImage white = null;
		try{
			black = ImageIO.read(new File("Black.png"));
			white = ImageIO.read(new File("White.png"));
		} catch (IOException e) {
			System.out.println("The background file could not be found."); 
		}
			//originally icons were combined to make background pattern streamlined, now image backgrounds are transparent, so combining is not needed
			//chessBoardSquares[justClicked[0]][justClicked[1]].setIcon(new CombineIcon(chessBoardSquares[lastClicked[0]][lastClicked[1]].getIcon(),  chessBoardSquares[justClicked[0]][justClicked[1]].getIcon()));
			chessBoardSquares[model.getJustClicked()[0]][model.getJustClicked()[1]].setIcon(chessBoardSquares[model.getLastClicked()[0]][model.getLastClicked()[1]].getIcon());
			
			
			//Keeps track of location of pieces. sets location of piece being moved
			model.setOccupiedSquares(model.getJustClicked()[0], model.getJustClicked()[1], model.getLastClicked()[2]); //occupiedSquares[justClicked[0]][justClicked[1]] = lastClicked[2]; //lastClick[2] indicates the occupied number id from that click 
			model.setOccupiedSquares(model.getLastClicked()[0], model.getLastClicked()[1], 10);
			//occupiedSquares[lastClicked[0]][lastClicked[1]] = 10; 
			
			//Keeps track of color of piece in square, square to move to gains color attribute of the one being moved
			model.setColorOfSquares(model.getJustClicked()[0], model.getJustClicked()[1], model.getColorOfSquares()[model.getLastClicked()[0]][model.getLastClicked()[1]]);
			//colorOfSquares[justClicked[0]][justClicked[1]] = colorOfSquares[lastClicked[0]][lastClicked[1]];
			model.setColorOfSquares(model.getLastClicked()[0], model.getLastClicked()[1], 2); //because piece is leaving square, it becomes empty, denoted by 2
			//colorOfSquares[lastClicked[0]][lastClicked[1]] = 2; //because piece is leaving square, it becomes empty, denoted by 2
			
			//Changes square back to default after piece has been moved (away) from it
			if(chessBoardSquares[model.getLastClicked()[0]][model.getLastClicked()[1]].getBackground() == Color.BLACK){
				chessBoardSquares[model.getLastClicked()[0]][model.getLastClicked()[1]].setIcon(new ImageIcon(black));
			}else{
				chessBoardSquares[model.getLastClicked()[0]][model.getLastClicked()[1]].setIcon(new ImageIcon(white));
			}
	//	}
		
	}//end move()
	
	/**
	 * This function is intended primarily for use of the undo button, to reinsert a piece that was captured during the move being undone.
	 */
	private void replacePiece(){
		if(model.capture){// && model.getRestorePosition()[3] != model.getColorOfSquares()[model.getRestorePosition()[0]][model.getRestorePosition()[1]]){
			model.restoreArrayPositions();
			int[] restorePosition = model.getRestorePosition();
			int pieceColor = model.getColorOfSquares()[restorePosition[0]][restorePosition[1]];
			System.out.println("Replacing piece info: "+Arrays.toString(restorePosition));
			chessBoardSquares[restorePosition[0]][restorePosition[1]].setIcon((new ImageIcon(chessPieceImages[pieceColor][restorePosition[2]])));
			model.capture=false;
		}
		
	}//end replacePiece()
	
	
	
	public final JComponent getGui()
	{
		return gui;
	}

	private final void createImages(){
		try
		{
			String chessPic = "chess.png";
			BufferedImage bi = null;
			try {
			    bi = ImageIO.read(new File(chessPic));
			} catch (IOException e) {
				System.out.println("The file: '"+chessPic+"' could not be found."); 
			}
			
			for (int ii = 0; ii < 2; ii++)
			{
				for (int jj = 0; jj < 6; jj++)
				{
					chessPieceImages[ii][jj] = bi.getSubimage(jj * 64, ii * 64, 64, 64);
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Initializes the icons of the initial chess board piece places
	 */
	private final void setupNewGame()
	{
		message.setText("Make your move!");
		clearBoard();
		
		// set up the row of black pieces
		for (int ii = 0; ii < STARTING_ROW.length; ii++){
			//this uses the CombineIcon class to blend the black image icons (used to set background square default) with the new piece images
				//note this was no longer used because Justin Yu was able to remove background from chess piece images
			//chessBoardSquares[ii][0].setIcon(new CombineIcon(new ImageIcon(chessPieceImages[BLACK][STARTING_ROW[ii]]), chessBoardSquares[ii][0].getIcon()));
			chessBoardSquares[0][ii].setIcon(new ImageIcon(chessPieceImages[BLACK][STARTING_ROW[ii]]));
			
			//chessBoardSquares[ii][1].setIcon(new CombineIcon(new ImageIcon(chessPieceImages[BLACK][PAWN]), chessBoardSquares[ii][1].getIcon()));
			chessBoardSquares[1][ii].setIcon(new ImageIcon(chessPieceImages[BLACK][PAWN]));
			
		}
		
		// set up the white pieces
		for (int ii = 0; ii < STARTING_ROW.length; ii++)
		{
			//chessBoardSquares[ii][6].setIcon(new CombineIcon(new ImageIcon(chessPieceImages[WHITE][PAWN]), chessBoardSquares[ii][6].getIcon()));
			chessBoardSquares[6][ii].setIcon(new ImageIcon(chessPieceImages[WHITE][PAWN]));
		}
		
		for (int ii = 0; ii < STARTING_ROW.length; ii++){
			//chessBoardSquares[ii][7].setIcon(new CombineIcon(new ImageIcon(chessPieceImages[WHITE][STARTING_ROW[ii]]),chessBoardSquares[ii][7].getIcon()));//new ImageIcon(chessPieceImages[WHITE][STARTING_ROW[ii]]));
			chessBoardSquares[7][ii].setIcon(new ImageIcon(chessPieceImages[WHITE][STARTING_ROW[ii]]));
		}
		
		//Document positions in 2D array
		for (int i = 0; i < STARTING_ROW.length; i++){
			model.setOccupiedSquares(0, i,STARTING_ROW[i]); //1st row of black power pieces
			model.setOccupiedSquares(1, i, PAWN); ////Second row is all black pawns
			model.setColorOfSquares(0, i, 0); //setting the color to black for 1st row
			model.setColorOfSquares(1, i, 0); //setting the color to black for 2nd row
			
			model.setOccupiedSquares(2, i, 10);
			model.setOccupiedSquares(3, i, 10);
			model.setOccupiedSquares(4, i, 10);
			model.setOccupiedSquares(5, i, 10);
			//occupiedSquares[3][i] = 10;
			
			model.setColorOfSquares(2, i, 2);
			model.setColorOfSquares(3, i, 2);
			model.setColorOfSquares(4, i, 2);
			model.setColorOfSquares(5, i, 2);
			//colorOfSquares[2][i] = 2; //setting the color to empty (no piece so color not needed)

			model.setOccupiedSquares(6, i, PAWN); //occupiedSquares[6][i] = PAWN;
			model.setOccupiedSquares(7, i, STARTING_ROW[i]);  //occupiedSquares[7][i] = STARTING_ROW[i];
			model.setColorOfSquares(6, i, 1); //setting color of pieces in squares to white
			model.setColorOfSquares(7, i, 1);
			//colorOfSquares[6][i] = 1; //setting the color to white
		}
	}
	
	/**
	 * Allows user to forfeit the game (on their turn)
	 */
	private void surrenderGame(){
		clearBoard();
	}//end surrenderGame
	
	// Exits from the game
	private void exitGame(){
		System.exit(0);
	}
	
	/**
	 * Clears images from every square in the board
	 */
	private void clearBoard(){
		BufferedImage black = null;
		BufferedImage white = null;
		try{
			black = ImageIO.read(new File("Black.png"));
			white = ImageIO.read(new File("White.png"));
		} catch (IOException e) {
			System.out.println("The file could not be found."); 
		}
		
		for (int i = 0; i < STARTING_ROW.length; i++){
			for(int j = 0; j < STARTING_ROW.length; j++){
				
				model.setOccupiedSquares(i,j,10);  //[i][j] = 10; //keeps track of which piece occupies which square, sets all to 0 as board is being cleared
				model.setColorOfSquares(i,j,2); //[i][0] = 2; //setting the color to empty space, denoted by 2
				
				if((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)){
					ImageIcon icon = new ImageIcon(black);
					chessBoardSquares[i][j].setIcon(icon);
					
				} 
				
				else{
					ImageIcon icon = new ImageIcon(white);
					chessBoardSquares[i][j].setIcon(icon);
				}
			}
		}
		
		model.setDefaults(); //sets default for arrays and variables tracking gameboard status, stored in the Model class
		
	}//End clearBoard()


	
}//End ChessGUI

