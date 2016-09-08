import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Controller {
	//ChessGUI chessGame = new ChessGUI();
	static Controller controller = new Controller();
	static Model model = new Model();
	static int lastClicked[] = model.getLastClicked();
	static int justClicked[] = model.getJustClicked();
	static int[][] occupied = model.getOccupiedSquares();
    static int[][] pieceColor = model.getColorOfSquares();
    
    //boolean whiteTurn;
	
	/**
	 * Determines if it is white player's turn or not (in chess, white player is conventional player 1, to begin first)
	 * 		The turn counter, which is incremented by 1 every time a legal move is made (see button actions in chessgui), and when the number of turns made is even,
	 * 			(i.e. turns 0,2,4,etc) then it is player 1, or white player's turn. When it is odd, it is black player's turn.
	 * 		This method uses the turn counter to determine if it is player ones turn, or not
	 * 	"White begins, black wins!"
	 * @returns a boolean value as to whether it is (true) or whether it is not (false) player white's turn
	 */
	public boolean playerTurn(){
		boolean whiteTurn;
		if( (model.getTurnCounter() % 2) == 0){
			//System.out.println("The turn counter is: "+model.getTurnCounter());
			whiteTurn = true;
			//System.out.println("Is it white's turn: "+whiteTurn);
		}else{
			whiteTurn = false;
			//System.out.println("The turn counter (number of legal moves made so far) is: "+model.getTurnCounter());
			//System.out.println("Is it white's turn: "+whiteTurn);
		}
		return whiteTurn;
	}
	
	public String whoseTurn(){
		String player = "";
		if (playerTurn()){
			player = "Player 1";
		}else{
			player = "Player 2";
		}
		return player;
	}
	
	/**
	 * This function will use the int values held in justClicked and lastClicked to determine if a move can be made withing the legal bounds of a chess game.
	 * 	It will use, in addition to the justClicked and lastClicked values (to determine where a piece is attempting to be moved)
	 *  This method was originally designed as as the logic controller for the game, by Justin McVicker
	 * @returns a boolean value: true means move is legal; false means move is not legal
	 */
	public boolean checkIfLegal() {
		int move[] = {lastClicked[0], lastClicked[1], justClicked[0], justClicked[1]}; // The lastClicked values are what is going to be moved into the justClicked values
		boolean isLegal = false;
		//Note: cannot change model.capture to false here, because checkIfLegal is constantly checked on every click. We set this value to false
		
		boolean whitesTurn = playerTurn(); //if true, it is white player's turn
		
		//Checks to see if the piece that is trying to be moved (the last clicked value) is the current player's turn
		if(pieceColor[move[0]][move[1]] == 1 && whitesTurn == false || pieceColor[move[0]][move[1]] == 0 && whitesTurn == true ){
			System.out.println("The color of the piece located in the square you Last (previously) Clicked is: "+model.colorLookup(pieceColor[move[0]][move[1]]));
			System.out.println("Move is NOT LEGAL because you clicked a "+model.colorLookup(pieceColor[move[0]][move[1]])+" piece.");
			return isLegal;
		} 
		
		//holds the value of what is in the square we are attempting to move from
		int pieceRankNum = lastClicked[2]; //the value at index 2 for lastClicked and justClicked stores what the contents in the square were/are
		System.out.println("The value of the square you last clicked was: "+model.pieceLookup(pieceRankNum)+" ("+pieceRankNum+")");
        
		
		//checks if the square we are attempting to move from is empty. exits the function without further checking
		if(pieceRankNum == 10){ //the getLastClicked[2] index holds the piece value of the square that we are attempting to move from. If this is 10, then we are attempting to move from an empty square
			return isLegal;
		}
		
		// for now just check if move is out of bounds of the board
		for (int i = 0; i < 4; i++) {
			if (!(0 <= move[i] || move[i] < 7)){
				System.out.printf("%d%s%n", move[i], " is not a space on the board");
	            return isLegal;
	        }
	    }
	        
	     
		// Checks if attempted move is trying to capture a piece of the same color
        if(model.getJustClicked()[2] != 10){ //if the value in the square the piece is trying to move to is empty
        	if(pieceColor[move[2]][move[3]] != model.lastPlayed){ //person who last played is opponent, and piece attempting to be captured must be opponent's
        		System.out.println("Attempting to capture your own piece. Not legal.");
        		return isLegal;
        	}else if(pieceColor[move[2]][move[3]] == model.lastPlayed){
        		model.setCapture(); //because it is attempting to capture a piece that is of a different color, it is ok to set capture to true here.
        	}
         }
		
	        
			// check is space is occupied (non-capture move only)
			switch (pieceRankNum) {
				case 5: 
					//pawn
					// move to space if not occupied, so long as it's a legal pawn non-capture move
					// (for now only supporting one space at a time, white only)
					if (occupied[move[2]][move[3]] == 10) {
						//System.out.println("pawn");
						if (pieceColor[move[0]][move[1]] == 1) {
							if ((move[1] == move[3]) && (move[0] == (move[2] + 1))) {
								isLegal = true;
							}
							else if ((move[0] == 6) && (move[1] == move[3]) && (move[0] == (move[2] + 2))) { // legal to move up 2 spots on first move
								isLegal = true;
							}
						}
						else if (pieceColor[move[0]][move[1]] == 0) {
							if ((move[1] == move[3]) && (move[0] == (move[2] - 1))) {
								isLegal = true;
							}
							else if ((move[0] == 1) && (move[1] == move[3]) && (move[0] == (move[2] - 2))) { // legal to move up 2 spots on first move
								isLegal = true;
							}
						}
						else {
							System.out.printf("%s%n", "That spot is occupied");
							isLegal = false;
						}
					}
					// check if it's a legal capture move for a pawn, otherwise space is occupied. This happens when the occupied position in the square moving to is not 10
					else if (pieceColor[move[0]][move[1]] == 1) {
						if ((move[2] == move[0] - 1) && ((move[3] == move[1] - 1) || (move[3] == move[1] + 1))) {
							isLegal = true;
							//model.setCapture(); //setting capture in each case is not needed. If function above sets capture whenever piece being moved to is a possible capture
						}
					}
					else if (pieceColor[move[0]][move[1]] == 0) {
						if ((move[2] == move[0] + 1) && ((move[3] == move[1] - 1) || (move[3] == move[1] + 1))) {
							isLegal = true;
							//model.setCapture();
						}
					}
					else {
						System.out.printf("%d%s%d%s\n", move[2], "  ", move[3], " Illegal move");
						isLegal = false;
					}
					break;
				case 3:
					// knight
					// remember: a piece going up is equivalent to counting down, since TOP left corner is (0,0)
					if (move[2] == (move[0] - 1)) { // up 1
						if ((move[3] == (move[1] - 2)) || (move[3] == (move[1] + 2))) { // 2 spaces to the left or right
							isLegal = true;
						}
					}
					else if (move[2] == (move[0] - 2)) { // up 2
						if ((move[3] == (move[1] - 1)) || (move[3] == (move[1] + 1))) { // 1 space to the left or right
							isLegal = true;
						}
					}
					else if (move[2] == (move[0] + 1)) { // down 1
						if ((move[3] == (move[1] - 2)) || (move[3] == (move[1] + 2))) { // 2 spaces to the left or right
							isLegal = true;
						}
					}
					else if (move[2] == (move[0] + 2)) { // down 2
						if ((move[3] == (move[1] - 1)) || (move[3] == (move[1] + 1))) { // 1 space to the left or right
							isLegal = true;
						}
					}
					else {
						System.out.printf("%d%s%d%s\n", move[2], "  ", move[3], " Illegal move");
						isLegal = false;
					}
					break;
					
				case 4: 
					//bishop
					// if legal move, check if piece is blocking path
					if ((Math.abs(move[0] - move[2])) == (Math.abs(move[1] - move[3]))) { //ensures movement is diagonal from original position
						isLegal = true;
						if (move[0] < move[2]) { //moving down the board (increasing by row numbers)
							if (move[3] < move[1]) {  //moving left across the board (decreasing column numbers) and down
								for (int i = 1; i < move[2] - move[0]; i++) { //have to move a certain amount of rows down
									if (occupied[move[0]+i][move[1]-i] != 10) {
										isLegal = false;
									}
								}	
							}
							else { //increasing columns, moving right across the board (and down) ie move[3]>move[1]
								for (int i = 1; i < move[2] - move[0]; i++) {
									if (occupied[move[0]+i][move[1]+i] != 10) {
										isLegal = false;
									}
								}
							}
						}
						else { //moving up the board, ie move[0]>move[2]  (decreasing rows)
							if (move[3] < move[1]) { //moving left across the board (decreasing columns)
								for (int i = 1; i < move[0] - move[2]; i++) {
									if (occupied[move[0]-i][move[1]-i] != 10) {
										isLegal = false;
									}
								}	
							}
							else { //moving right across the board, incerasing columns (ie move[3]>move[1]
								for (int i = 1; i < move[0] - move[2]; i++) {
									if (occupied[move[0]-i][move[1]+i] != 10) {
										isLegal = false;
									}
								}
							}
						}
					}
					else
						isLegal = false;
					break;
					
				case 2:
					//rook
					if ((move[0] == move[2] && move[1] != move[3]) || (move[0] != move[2] && move[1] == move[3])) {
						isLegal = true;
						if (move[0] < move[2]) {
							for (int i = 1; i < move[2] - move[0]; i++) {
								if (occupied[move[0]+i][move[1]] != 10) {
									isLegal = false;
								}
							}
						}
						else if (move[0] > move[2]) {
							for (int i = 1; i < move[0] - move[2]; i++) {
								if (occupied[move[2]+i][move[1]] != 10) {
									isLegal = false;
								}
							}
						}
						else {
							for (int i = 0; i < move[0] - move[2]; i++) {
								if (occupied[move[2]+i][move[3]] != 10) {
									isLegal = false;
								}
							} 
						}
					}
					else
						isLegal = false;
					break;
				
				case 1:
					//queen
					if ((Math.abs(move[0] - move[2])) == (Math.abs(move[1] - move[3]))) {
						isLegal = true;
						if (move[0] < move[2]) {
							if (move[3] < move[1]) {
								for (int i = 1; i < move[2] - move[0]; i++) {
									if (occupied[move[0]+i][move[1]-i] != 10) {
										isLegal = false;
									}
								}	
							}
							else {
								for (int i = 1; i < move[2] - move[0]; i++) {
									if (occupied[move[0]+i][move[1]+i] != 10) {
										isLegal = false;
									}
								}
							}
						}
						else {
							if (move[3] < move[1]) {
								for (int i = 1; i < move[0] - move[2]; i++) {
									if (occupied[move[0]-i][move[1]-i] != 10) {
										isLegal = false;
									}
								}	
							}
							else {
								for (int i = 1; i < move[0] - move[2]; i++) {
									if (occupied[move[0]-i][move[1]+i] != 10) {
										isLegal = false;
									}
								}
							}
						}
					}
					else if (((move[1] == move[3]) && (move[0] != move[2]))) { //straight vertical line
						isLegal = true;
						if (move[0] < move[2]) { //down
								for (int i = 1; i < move[2] - move[0]; i++) {
									if (occupied[move[0]+i][move[1]] != 10) {
										isLegal = false;
									}
								}	
							}
							else { //up
								for (int i = 1; i < move[0] - move[2]; i++) {
									if (occupied[move[0]-i][move[1]] != 10) {
										isLegal = false;
									}
								}
							}
						}
					else if ((move[0] == move[2]) && (move[1] != move[3])){ //straight horizontal line
						isLegal = true;
						if (move[1] < move[3]) { //right
							for (int i = 1; i < move[3] - move[1]; i++) {
								if (occupied[move[0]][move[1]+i] != 10) {
									isLegal = false;
								}
							}	
						}
						else { //left
							for (int i = 1; i < move[1] - move[3]; i++) {
								if (occupied[move[0]][move[1]-i] != 10) {
									isLegal = false;
								}
							}
						}
					}
					else
						isLegal = false;
					break;
				case 0:
					//king
					if ((move[2] == (move[0] - 1)) || (move[2] == move[0] + 1)) { 
						if ((move[3] == move[1] - 1) || (move[3] == move[1]) || (move[3] == move[1] + 1)) {
							isLegal = true;
						}
					}
					else if (move[2] == move[0]) {
						if ((move[3] == move[1] - 1) || (move[3] == move[1] + 1)) {
							isLegal = true;
						}
					}
					
					
					else
						isLegal = false;
					break;
				default:
					isLegal = false;
					break;
			}
		System.out.println("Will move be legally allowed: "+isLegal);
		return isLegal;
	} //end checkIfLegal()
	
		
	public static void main(String[] args) {
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				ChessGUI chessGame = new ChessGUI(controller, model);
				

				JFrame f = new JFrame("Chess Game by Beta Group");
				f.add(chessGame.getGui());
				// Ensures JVM closes after frame(s) closed and
				// all non-daemon threads are finished
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				// See http://stackoverflow.com/a/7143398/418556 for demo.
				f.setLocationByPlatform(true);

				// ensures the frame is the minimum size it needs to be
				// in order display the components within it
				f.pack();
				// ensures the minimum size is enforced.
				f.setMinimumSize(f.getSize());
				f.setVisible(true);
			}
		};
		
		// Swing GUIs should be created and updated on the EDT
		// http://docs.oracle.com/javase/tutorial/uiswing/concurrency
		SwingUtilities.invokeLater(r);
	}
}
