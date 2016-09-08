import java.util.Arrays;

public class Model {
	//This will store the value of whatever occupies each square on the board
	private int[][] occupiedSquares = new int[8][8];
	
	// this will store the current square's contents color values as a number between 0-2; 0 is black, 1 is white, 2 is empty. This will help in drawing images
	private int[][] colorOfSquares = new int [8][8]; 
	
	//These integers will be used to lookup the corresponding images in chessPieceImages
			// These numbers will essentially be used as ID's for pieces throughout the game, can be checked using occupiedSquares[][]
			// Because the Queen is taking int value 0, the "empty space" icon will be assigned the int value 10 
	public static final int KING = 0, QUEEN = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
	//This will indicate which row to select images from in chessPieceImages
	public static final int BLACK = 0, WHITE = 1;
	
	
	//These will keep track of the most recent and second most recent squares clicked (index 0 and 1). Additionally it will store the current square's contents as index 2 (numerical value of 1-5 or 10)
	//Note: justClicked is the most recently clicked square, and lastClicked was the square clicked before that. In moving pieces, justClicked is moved to lastClicked
	private int [] justClicked = {0,0,10};
	private int [] lastClicked = {0,0,10};
	private int[] previousClicks = new int[6]; //this will store the last 3 click values to allow those moves to be undone
	
	private int turnCounter = 0; //keeps track of how many turns have been made in the game. Even number indicates it is player white's turn, odd means player black
	private int undoCounter = 0;
	
	boolean capture = false; //will keep track if the last legal move resulted in a capture
	int lastCaptured = 10; //stores the int value of the last piece captured. Default is 10, because 10 means empty space
	int restorePosition[] = {0,0,10,2}; //keeps track of which piece should be restored after undo. default reads as index[0][0], empty square
	int lastPlayed = 1; //keeps track of which player made the last legal move, used to track available undo moves. 1=white, 0=black
	
	private boolean check = false;
	private boolean checkMate = false;
	
	public void setCheck(){
		if(lastPlayed == 1){
			
		}else{
			
		}
		check = true;
	}
	public boolean getCheck(){
		return check;
	}
	
	public void setCheckMate(){
		checkMate = true;
	}
	public boolean getCheckMate(){
		return checkMate;
	}
	
	
	/**
	 * This method will return the 2D array holding the positions of all pieces on the board
	 * It attains the location of these values from the occupiedSquares 2D int array in this class
	 * 10 = empty space (this is indicated by 10 because the Queen is using 0 as an index and id,
	 * 5 = pawn,
	 * 2 = rook,
	 * 3 = knight,
	 * 4 = bishop,
	 * 1 = queen,
	 * 0 = king
	 * @returns the occupiedSquares[][] int array holding the values of all the square contents
	 */
	public int[][] getOccupiedSquares(){
		return occupiedSquares;
	}
	
	/**
	 * This class will return the 2D array representing the game board, and holds the values of whatever color is in each square on the board
	 * The location of values being checked is stored in the occupiedSquares.
	 * If the cell content is:
	 * 	0 = Black piece
	 *  1 = White piece
	 *  2 = Empty square
	 * @returns the coloredSquares[][] int array holding values of the square content's colors
	 */
	public int[][] getColorOfSquares(){
		return colorOfSquares;
	}
	
	//Outside accessors to the justClicked and lastClicked values, intended to check for legal moves
	public int[] getLastClicked(){
		return lastClicked;
	}
	public int[] getJustClicked(){
		return justClicked;
	}
	
	public boolean isOccupied(int rowIndex, int colIndex){
		boolean squareHasPiece = false;
		if(occupiedSquares[rowIndex][colIndex] != 10){
			squareHasPiece = true;
		}
		return squareHasPiece;
	}
	

	/**
	 * This method will set a square's contents in the 2D array holding the positions of all pieces on the board (occupiedSquares)
	 * 		It will change the location of pieces on the board inside the occupiedSquares array, using a given index [row][column], and value to set in the position
	 * The following values should be used to assign a piece to a position on the board:
	 *	10 = empty space (this is indicated by 10 because the Queen is using 0 as an index and id,
	 *	5 = pawn,
	 * 	2 = rook,
	 * 	3 = knight,
	 * 	4 = bishop,
	 * 	1 = queen,
	 * 	0 = king
	 * @param rowIndex - the id for the row in occupiedSquares to lookup for changing
	 * @param colIndex - the id for the column in occupiedSquares to lookup for changing
	 * @param occupiedValue - the actual int value of the piece (or empty square) being set in the given index [row][column] of occupiedSquares
	 */
	public void setOccupiedSquares(int rowIndex, int colIndex, int occupiedValue){
		occupiedSquares[rowIndex][colIndex] = occupiedValue;
	}
	
	/**
	 * This class will set the color of a square's contents in the 2D array representing the color of game board contents
	 * The location of the square whose color value is being set is given by the parameters for index: [row][column]; and the int color value to be assigned
	 * The following int values are used to set what color the contents of the indexed square are:
	 * 	0 = Black piece
	 *  1 = White piece
	 *  2 = Empty square
	 * @param rowIndex - the id for the row in coloredSquares to lookup for changing
	 * @param colIndex - the id for the column in coloredSquares to lookup for changing
	 * @param occupiedColor - the actual int color value of the piece (or empty square) being set at the index [row][column] of coloredSquares
	 */
	public void setColorOfSquares(int rowIndex, int colIndex, int occupiedColor){
		colorOfSquares[rowIndex][colIndex] = occupiedColor;
	}
	
	public int getTurnCounter(){
		return turnCounter;
	}
	public int getUndoCounter(){
		return undoCounter;
	}
	public void setTurnCounter(int newTurnCounter){
		turnCounter = newTurnCounter;
	}
	public void setUndoCounter(int newUndoCounter){
		undoCounter = newUndoCounter;
	}
	
	/**
	 * This method sets both the justClicked and lastClicked indexes and values of the two squares that were the most recently clicked by the user
	 * 		Whenever a new square on the game board is clicked, the lastClicked value becomes what the justClicked value is before the justClicked value
	 * 				is assigned the new index location and value of the square the user just recently clicked (the most recent click)
	 * @param newClickedSquare - is a string value passed in by a square when it is clicked, this holds the index location and occupiedPiece value number of the square that was just recently clicked
	 */
	protected void setJustClicked(String newClickedSquare){
		//The last clicked values become what the just clicked values are (because they were just 1 click ago)
		lastClicked[0] = justClicked[0];
		lastClicked[1] = justClicked[1];
		lastClicked[2] = justClicked[2]; //this third index stores the value of what is inside the square that was clicked
		
		//This string subdivides the string parameter given containing the index location of the square that was just clicked
		int iIndex = Integer.parseInt(newClickedSquare.substring(0, 1)); //row
		int jIndex =  Integer.parseInt(newClickedSquare.substring(2)); //column

		//The just clicked values become the index inputted from the string
		justClicked[0] = iIndex;
		justClicked[1] = jIndex;
		
		//Because we now know the index of the square that was just clicked, we set whatever contents are currently in that square as well
		justClicked[2] = occupiedSquares[iIndex][jIndex];
		System.out.println("The Just Clicked values are ["+justClicked[0]+" "+justClicked[1]+" "+justClicked[2]+"]");
		System.out.println("The Last Clicked values are["+lastClicked[0]+" "+lastClicked[1]+" "+lastClicked[2]+"]");
	}//end setJustClicked()
	
	/**
	 * Returns the numerical value of the piece occupying the square that was clicked, by looking up a given index in the occupiedSquares 2D array
	 * @param clickedButtonString is the string passed by the square that was just (most recently) clicked. This contains the int values of index location of the clicked square
	 * @returns the int value at the index given in occupiedSquares, this int value representing what is inside the square that was clicked
	 */
	protected int getOccupiedOnClick(String clickedButtonString) {
		int iIndex = Integer.parseInt(clickedButtonString.substring(0, 1)); //row
		int jIndex =  Integer.parseInt(clickedButtonString.substring(2)); //column
		
		//Prints to console status information about obtaining occupied index
		/*System.out.println("The index (clicked square) to check for a piece: ["+iIndex+","+jIndex+"]");
		if(occupiedSquares[iIndex][jIndex] == 10){
			System.out.println("The square you clicked is: "+pieceLookup(occupiedSquares[iIndex][jIndex]));
		}else{
			System.out.println("The piece on the square you clicked is: "+occupiedSquares[iIndex][jIndex]);
			System.out.println("The piece on the square you clicked is: "+pieceLookup(occupiedSquares[iIndex][jIndex]));
		} */
		
		return occupiedSquares[iIndex][jIndex];
	}//end getOccupiedOnClick
	
	protected void setDefaults(){
		//Although defaults indicate that the just and last clicked squares were square index [0,0] with empty contents (10), this is just used to initialize game 
			// or for restoring defaults before a new game begins. 
		clearPreviousSavedClicks();
		
		turnCounter=0;
		undoCounter=0;
		
		capture = false; //will keep track if the last legal move resulted in a capture
		lastCaptured = 10; //stores the int value of the last piece captured. Default is 10, because 10 means empty space
		
	}
	protected void clearPreviousSavedClicks(){
		justClicked[0] = 0; 
		justClicked[1] = 0;
		justClicked[2] = 10;
		lastClicked [0] = 0;
		lastClicked [1] = 0;
		lastClicked [2] = 10;
		previousClicks[0] = 0; 
		previousClicks[1] = 0;
		previousClicks[2] = 10;
		previousClicks[3] = 0;
		previousClicks[4] = 0;
		previousClicks[5] = 10;
		restorePosition[0] = 0;
		restorePosition[1] = 0;
		restorePosition[2] = 10;
		restorePosition[3] = 2;
	}
	
	/**
	 * Stores an array of the last two previous clicked values. This is set in view only after a legal move has been made
	 * @param justClicked is an int array holding 12 values (2 sets of lastClicked and justClicked values, each 3 digits long)
	 */
	protected void setPreviousClicks(int[] justClicked){
		//Setting previous clicks, for potential use of the undo action button
		//this function is called in the chessGUI after a legal move is made
			previousClicks[5] = justClicked[2];
			previousClicks[4] = justClicked[1];
			previousClicks[3] = justClicked[0]; //adding the newly clicked square values to beginning of previousClicks array
			previousClicks[2] = lastClicked[2];
			previousClicks[1] = lastClicked[1];
			previousClicks[0] = lastClicked[0]; //adding the newly clicked square values to beginning of previousClicks array
			
			setRestorePositions();
			
			System.out.println("Previous clicks recorded for undo: "+Arrays.toString(previousClicks));
			
		
	}//end previousClicks
	
	protected void setRestorePositions(){
		//Storing the value of the square piece is being moved to, essentially stores info about the piece about to be captured
		restorePosition[0] = justClicked[0];
		restorePosition[1] = justClicked[1]; 
		restorePosition[2] = lastCaptured; //stores what was in the square before the move being undid was made
		restorePosition[3] = 1 - colorOfSquares[justClicked[0]][justClicked[1]]; // because it was captured, color is opposite of the one being moved
	}
	
	protected int[] getPreviousClicks(){
		return previousClicks;
	}
	
	/**
	 * Sets the value of capture to true, and sets lastCaptured to the piece value of just clicked.
	 */
	protected void setCapture(){
		capture = true;
		lastCaptured = justClicked[2];
		System.out.println("Capture of "+pieceLookup(justClicked[2])+" will occur: "+capture);
		
	}
	
	/**
	 * This function allows the most recent legal move made to be undone. When a player undoes a move, it is still their turn.
	 * 	Note: undoLastMove relies on the boolean value capture to decide if there is a piece that was captured that should be restored to the game board after
	 * 			a player clicks undo. If checkIfLegal does not notify when a capture was made, the piece will not be restored.
	 * @returns a boolean value that decides whether a move can be undone (true), or if there are no moves to be undone at this time (false).
	 */
	public boolean undoLastMove(){
		boolean isUndoOk = false;
		System.out.println("Previous clicks recorded for undo: "+Arrays.toString(previousClicks));
		if (undoCounter == 0 && turnCounter >=1 && capture==false){
			int num = 3;
			for(int i=0; i<3; i++){
				justClicked[i] = previousClicks[i];  //justClicked values become values at index 0-2 of previousClicks
				lastClicked[i] = previousClicks[num]; //lastClicked values become values at index 3-5 of previousClicks 
				num++;
			}
			
			int swapValue = justClicked[2]; //because index 2 in justClicked and lastClicked stores what piece is there, and we are about to reverse this,
			justClicked[2] = lastClicked[2]; //swapping these values here will allow the gameboard to correctly update the occupiedSquares[][] array
			lastClicked[2] = swapValue;
			undoCounter++;
			isUndoOk = true;
			turnCounter--; //Because player is undoing move, they should still be allowed to have their turn
		}
		else if (undoCounter == 0 && turnCounter >=1 && capture==true){
			System.out.println("Attempting to undo a move that included a capture");
			//setRestorePositions();
			int num = 3;
			for(int i=0; i<3; i++){
				justClicked[i] = previousClicks[i];  //justClicked values become values at index 0-2 of previousClicks
				lastClicked[i] = previousClicks[num]; //lastClicked values become values at index 3-5 of previousClicks 
				num++;
			}
			int swapValue = justClicked[2]; //because index 2 in justClicked and lastClicked stores what piece is there, and we are about to reverse this,
			justClicked[2] = lastClicked[2]; //swapping these values here will allow the gameboard to correctly update the occupiedSquares[][] array
			lastClicked[2] = swapValue;
			
			undoCounter++;
			isUndoOk = true;
			turnCounter--; //Because player is undoing move, they should still be allowed to have their turn
			
		}
		else{
			clearPreviousSavedClicks();
			System.out.println("Cannot undo move, sorry!");
		}
		System.out.println("New JustClicked Values: "+Arrays.toString(justClicked));
		System.out.println("New LastClicked Values: "+Arrays.toString(lastClicked));
		return isUndoOk;
	}//End undoLastMove
	
	public void restoreArrayPositions(){
		occupiedSquares[restorePosition[0]][restorePosition[1]] = restorePosition[2];
		colorOfSquares[restorePosition[0]][restorePosition[1]] = restorePosition[3];// because it was captured, color is opposite
	}
	public int[] getRestorePosition(){
		return restorePosition;
	}
	
	/**
	 * This will print the numerical values of all squares on the board. (Shows locations of all pieces) 
	 * It attains the location of these values from the occupiedSquares 2D int array in this class
	 * * = empty space (this is indicated by 10 in occupuedSquares, but represented as * here,
	 * 5 = pawn,
	 * 2 = rook,
	 * 3 = knight,
	 * 4 = bishop,
	 * 1 = queen,
	 * 0 = king
	 */
	protected void printOccupied() {
	    for(int i=0; i<8; i++) {
	       for(int j=0; j<8; j++)
	    	   if(occupiedSquares[i][j]==10){
	    		   System.out.print("* ");
	    	   }else{
	    		   System.out.print(occupiedSquares[i][j] + " ");
	    	   }
	       System.out.println();
	    }
	    System.out.println();
	}//end printOccupied
	
	
	protected int getColorOnClick(String clickedButtonString) {
		int iIndex = Integer.parseInt(clickedButtonString.substring(0, 1)); //row
		int jIndex =  Integer.parseInt(clickedButtonString.substring(2)); //column
		
		return colorOfSquares[iIndex][jIndex];
	}//end getColorOnClick
	
	public String pieceLookup(int pieceNumber){
		String pieceString = null;
		String qu = "Queen";
		String ki = "King";
		String bi = "Bishop";
		String kn = "Knight";
		String ro = "Rook";
		String pa = "Pawn";
		String em = "Empty";
		//QUEEN = 0, KING = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
		if(pieceNumber == 0){
			pieceString = ki;
		}else if(pieceNumber == 1){
			pieceString = qu;
		}else if(pieceNumber == 2){ 
			pieceString = ro;
		}else if(pieceNumber == 3){ 
			pieceString = kn;
		}else if(pieceNumber == 4){ 
			pieceString = bi;
		}else if(pieceNumber == 5){
			pieceString = pa;
		}else if(pieceNumber == 10){ 
			pieceString = em;
		}else{
			pieceString = "unknown";
		}
		return pieceString;
	}//end pieceLookup
	
	public String colorLookup(int pieceColorNumber){
		String pieceString = null;
		String bl = "Black";
		String wh = "White";
		String em = "Empty";
		//Black = 0; White = 1; Empty = 2
		if(pieceColorNumber == 0){
			pieceString = bl;
		}else if(pieceColorNumber == 1){
			pieceString = wh;
		}else if(pieceColorNumber == 2){ 
			pieceString = em;
		}else{
			pieceString = "unknown";
		}
		return pieceString;
	}
	
	

}
