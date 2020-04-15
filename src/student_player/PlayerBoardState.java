package student_player;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;
import boardgame.Board;
import boardgame.BoardState;
import boardgame.Move;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.UnaryOperator;

/*
 * This is a customized boardState which only includes the state of the 
 * Board that is available to the current player. 
 * 
 * The purpose of the class is to facilitate the simulations of the game 
 * in order to properly implement the StudentPlayer.chooseMove() method. 
 * 
 */


public class PlayerBoardState implements Cloneable{
	public static final int BOARD_SIZE = 14;
    public static final int originPos = 5;

    public SaboteurTile[][] board;
    public int[][] intBoard;
//    public ArrayList<SaboteurCard> Deck; //deck form which player pick
    
    public static final int EMPTY = -1;
    public static final int TUNNEL = 1;
    public static final int WALL = 0;
    
    public ArrayList<SaboteurCard> hand = new ArrayList<SaboteurCard>(); // the list of card in the current player's hand  
    
    public static final int[][] hiddenPos = {{originPos+7,originPos-2},{originPos+7,originPos},{originPos+7,originPos+2}};
    public ArrayList<Integer> player1hiddenStatus = new ArrayList<Integer>();
    public ArrayList<Integer> player2hiddenStatus = new ArrayList<Integer>();  
    public int player1nbMalus;
    public int player2nbMalus; // us -- (active when turnplayer == 0)
    public boolean[] player2hiddenRevealed = {false,false,false}; //whether hidden at pos1 is revealed, hidden at pos2 is revealed, hidden at pos3 is revealed.
    public boolean[] player1hiddenRevealed = {false,false,false}; //whether hidden at pos1 is revealed, hidden at pos2 is revealed, hidden at pos3 is revealed.
    private int turnPlayer;
    private int turnNumber;
    private boolean[] hiddenRevealed = {false,false,false}; //whether hidden at pos1 is revealed, hidden at pos2 is revealed, hidden at pos3 is revealed.
    private Random rand;

    /*
     * @param oldBoardState the current playing board state
     * Creates a {@link Board} object. 
     * <p>
     * The newly created {@link Board} contains a copy of the current board. 
     * </p>
     */
    PlayerBoardState(SaboteurBoardState oldBoardState){
    	SaboteurTile [][] oldhiddenBoard = oldBoardState.getHiddenBoard();
    	int [][] oldHiddenIntBoard = oldBoardState.getHiddenIntBoard();

    	// make a deep copy of the current hidden board
    	this.board = new SaboteurTile[BOARD_SIZE][BOARD_SIZE];
    	for (int i = 0; i< BOARD_SIZE; i++) {
    		for (int j = 0; j< BOARD_SIZE; j++) {
    			if (oldhiddenBoard[i][j] != null) {
    				this.board[i][j] = new SaboteurTile(oldhiddenBoard[i][j].getIdx());
    			}else {
    				this.board[i][j] = null;
    			}
  
    			
    		}
    	}
    	
    	// make a deep copy of the current hidden int board
    	this.intBoard = new int[BOARD_SIZE*3][BOARD_SIZE*3]; 
    	for(int i = 0; i< BOARD_SIZE*3 ; i ++) {
    		for(int j = 0; j<BOARD_SIZE*3; j++) {
    			if (oldHiddenIntBoard[i][j] != EMPTY) {
    				this.intBoard[i][j] = oldHiddenIntBoard[i][j];
    			}
    			else {
        			this.intBoard[i][j] = EMPTY;
    			}
    			
    		}
    	}
    	
    	for(SaboteurCard card: oldBoardState.getCurrentPlayerCards()) {
    		if (card instanceof SaboteurTile) {
//    			System.out.println("Im at first condition");
//    			hand.add(card.copyACard(card.getName()));
    			this.hand.add(new SaboteurTile(((SaboteurTile) card).getIdx()));
    		}else if(card instanceof SaboteurMap) {
    			this.hand.add(new SaboteurMap());
    		}else if(card instanceof SaboteurMalus) {
    			this.hand.add(new SaboteurMalus());
    		}else if(card instanceof SaboteurBonus) {
    			this.hand.add(new SaboteurBonus());
    		}else if(card instanceof SaboteurDestroy) {
    			this.hand.add(new SaboteurDestroy());
    		}
    	}
    	
    	/*
    	 * initialize the hidden card status
    	 * 		-1: not revealed 
    	 * 		 0: revealed not a nugget 
    	 * 		 1: nugget
    	 */
    	//since we know nothing about the status of opponent. all hidden object to the opponent initialized as unrevealed 
    	
    	player1hiddenStatus.add(-1);
    	player1hiddenStatus.add(-1);
    	player1hiddenStatus.add(-1);
    	
    	player2hiddenStatus = MyTools.gethiddenStatus(this.board, PlayerBoardState.hiddenPos);  
    	
//    	player2hiddenStatus = MyTools.gethiddenStatus(oldBoardState.getHiddenBoard(), PlayerBoardState.hiddenPos);  
    	
    	int h = 0;
    	for(Integer i : player2hiddenStatus) {
    		player2hiddenRevealed[h] = !(i == -1);
    		h++;
    	}
    	
    	turnPlayer = oldBoardState.getTurnPlayer();
    	turnNumber = oldBoardState.getTurnNumber();
    	player2nbMalus = oldBoardState.getNbMalus(turnPlayer);
    	player1nbMalus = oldBoardState.getNbMalus(1-turnPlayer);
    }
    
//    public PlayerBoardState() {
//
//    	this.board = new SaboteurTile[BOARD_SIZE][BOARD_SIZE];
//    	this.intBoard = new int[BOARD_SIZE*3][BOARD_SIZE*3]; 
////    	public ArrayList<SaboteurCard> hand = new ArrayList<SaboteurCard>(); // the list of card in the current player's hand  
////        
////        public static final int[][] hiddenPos = {{originPos+7,originPos-2},{originPos+7,originPos},{originPos+7,originPos+2}};
////        public ArrayList<Integer> player1hiddenStatus = new ArrayList<Integer>();
////        public ArrayList<Integer> player2hiddenStatus = new ArrayList<Integer>();  
////        public int player1nbMalus;
////        public int player2nbMalus; // us -- (active when turnplayer == 0)
////        public boolean[] player2hiddenRevealed = {false,false,false}; //whether hidden at pos1 is revealed, hidden at pos2 is revealed, hidden at pos3 is revealed.
////        public boolean[] player1hiddenRevealed = {false,false,false}; //whether hidden at pos1 is revealed, hidden at pos2 is revealed, hidden at pos3 is revealed.
////        private int turnPlayer;
////        private int turnNumber;
////        private boolean[] hiddenRevealed = {false,false,false}; //whether hidden at pos1 is revealed, hidden at pos2 is revealed, hidden at pos3 is revealed.
////        private Random rand;
//	}

//    @Override
//	public PlayerBoardState clone() throws CloneNotSupportedException {
//    	PlayerBoardState copy = (PlayerBoardState) super.clone(); 
//    	
//    	SaboteurTile [][] oldhiddenBoard = this.board;
//    	int [][] oldHiddenIntBoard = this.intBoard;
//
//    	// make a deep copy of the current hidden board
//    	for (int i = 0; i< BOARD_SIZE; i++) {
//    		for (int j = 0; j< BOARD_SIZE; j++) {
//    			if (oldhiddenBoard[i][j] != null) {
//    				copy.board[i][j] = new SaboteurTile(oldhiddenBoard[i][j].getIdx());
//    			}else {
//        			copy.board[i][j] = null;
//    			}
//  
//    		}
//    	}
//    	
//    	// make a deep copy of the current hidden int board
//    	for(int i = 0; i< BOARD_SIZE*3 ; i ++) {
//    		for(int j = 0; j<BOARD_SIZE*3; j++) {
//    			if (oldHiddenIntBoard[i][j] != EMPTY) {
//    				copy.intBoard[i][j] = oldHiddenIntBoard[i][j];
//    			}else {
//    				copy.intBoard[i][j] = EMPTY;
//    			}
//    			
//    			
//    		}
//    	}
//    	
//    	for(SaboteurCard card: this.hand) {
////    		System.out.println("im in");
//    		if (card instanceof SaboteurTile) {
//    			copy.hand.add(new SaboteurTile(((SaboteurTile) card).getIdx()));
//    		}else if(card instanceof SaboteurMap) {
//    			copy.hand.add(new SaboteurMap());
//    		}else if(card instanceof SaboteurMalus) {
//    			copy.hand.add(new SaboteurMalus());
//    		}else if(card instanceof SaboteurBonus) {
//    			copy.hand.add(new SaboteurBonus());
//    		}else if(card instanceof SaboteurDestroy) {
//    			copy.hand.add(new SaboteurDestroy());
//    		}
//    	}
//
//	    	/*
//	   	 * initialize the hidden card status
//	   	 * 		-1: not revealed 
//	   	 * 		 0: revealed not a nugget 
//	   	 * 		 1: nugget
//	   	 */
//	   	//since we know nothing about the status of opponent. all hidden object to the opponent initialized as unrevealed 
//  	
//    	for(int i = 0; i<this.player1hiddenStatus.size(); i++) copy.player1hiddenStatus.add(this.player1hiddenStatus.indexOf(i)); 
//	   	for(int i = 0; i<this.player2hiddenStatus.size(); i++) copy.player2hiddenStatus.add(this.player2hiddenStatus.indexOf(i)); 
//	   	
//	   	for(int i = 0; i< this.player1hiddenRevealed.length;i++)copy.player1hiddenRevealed[i] = this.player1hiddenRevealed[i];
//	   	for(int i = 0; i< this.player2hiddenRevealed.length;i++)copy.player2hiddenRevealed[i] = this.player2hiddenRevealed[i];
//	   	
//	   	copy.turnPlayer = this.turnPlayer;
//	   	copy.turnNumber = this.turnNumber;
//	   	copy.player1nbMalus = this.player1nbMalus;
//	   	copy.player2nbMalus = this.player2nbMalus;
//	   	copy.rand = this.rand;
//	   	
//		return copy;
//    }
    
	
	PlayerBoardState(PlayerBoardState oldBoardState){

    	SaboteurTile [][] oldhiddenBoard = oldBoardState.board;
    	int [][] oldHiddenIntBoard = oldBoardState.intBoard;

    	// make a deep copy of the current hidden board
    	this.board = new SaboteurTile[BOARD_SIZE][BOARD_SIZE];
    	for (int i = 0; i< BOARD_SIZE; i++) {
    		for (int j = 0; j< BOARD_SIZE; j++) {
    			if (oldhiddenBoard[i][j] != null) {
    				this.board[i][j] = new SaboteurTile(oldhiddenBoard[i][j].getIdx());
    			}else {
    				this.board[i][j] = null;
    			}
  
    			
    		}
    	}
    	
    	// make a deep copy of the current hidden int board
    	this.intBoard = new int[BOARD_SIZE*3][BOARD_SIZE*3]; 
    	for(int i = 0; i< BOARD_SIZE*3 ; i ++) {
    		for(int j = 0; j<BOARD_SIZE*3; j++) {
    			if (oldHiddenIntBoard[i][j] != EMPTY) {
    				this.intBoard[i][j] = oldHiddenIntBoard[i][j];
    			
    			}else {
    				this.intBoard[i][j] = EMPTY;
    			}
    			
    			
    		}
    	}
    	
    	for(SaboteurCard card: oldBoardState.hand) {
    		if (card instanceof SaboteurTile) {
    			this.hand.add(new SaboteurTile(((SaboteurTile) card).getIdx()));
    		}else if(card instanceof SaboteurMap) {
    			this.hand.add(new SaboteurMap());
    		}else if(card instanceof SaboteurMalus) {
    			this.hand.add(new SaboteurMalus());
    		}else if(card instanceof SaboteurBonus) {
    			this.hand.add(new SaboteurBonus());
    		}else if(card instanceof SaboteurDestroy) {
    			this.hand.add(new SaboteurDestroy());
    		}
    	}
    	
      	
    	/*
    	 * initialize the hidden card status
    	 * 		-1: not revealed 
    	 * 		 0: revealed not a nugget 
    	 * 		 1: nugget
    	 */
    	//since we know nothing about the status of opponent. all hidden object to the opponent initialized as unrevealed 
    	for(int i = 0; i<oldBoardState.player1hiddenStatus.size(); i++) this.player1hiddenStatus.add(oldBoardState.player1hiddenStatus.indexOf(i)); 
    	for(int i = 0; i<oldBoardState.player2hiddenStatus.size(); i++) this.player2hiddenStatus.add(oldBoardState.player2hiddenStatus.indexOf(i)); 

    	
    	int h = 0;
    	for(Integer i : player2hiddenStatus) {
    		player2hiddenRevealed[h] = !(i == -1);
    		h++;
    	}
    	
    	turnPlayer = oldBoardState.getTurnPlayer();
    	turnNumber = oldBoardState.getTurnNumber();
    	player2nbMalus = oldBoardState.getNbMalus(turnPlayer);

    }
    
    
    public int getTurnPlayer() {
        if(turnNumber==0){ //at first, the board is playing
            return Board.BOARD;
        }
        return turnPlayer;
    }
    
    public int getTurnNumber() { return turnNumber; }
    

    // Given a tile's path, and a position to put this path, verify that it respects the rule of positioning;
    public boolean verifyLegit(int[][] path,int[] pos){
        if (!(0 <= pos[0] && pos[0] < BOARD_SIZE && 0 <= pos[1] && pos[1] < BOARD_SIZE)) {
            return false;
        }
        
        if(board[pos[0]][pos[1]] != null) return false;

        //the following integer are used to make sure that at least one path exists between the possible new tile to be added and existing tiles.
        // There are 2 cases:  a tile can't be placed near an hidden objective and a tile can't be connected only by a wall to another tile.
        int requiredEmptyAround=4;
        int numberOfEmptyAround=0;

        ArrayList<SaboteurTile> objHiddenList=new ArrayList<>();
        for(int i=0;i<3;i++) {
            if (!hiddenRevealed[i]){
                objHiddenList.add(this.board[hiddenPos[i][0]][hiddenPos[i][1]]);
            }
        }
        //verify left side:
        if(pos[1]>0) {
            SaboteurTile neighborCard = this.board[pos[0]][pos[1] - 1];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                if (path[0][0] != neighborPath[2][0] || path[0][1] != neighborPath[2][1] || path[0][2] != neighborPath[2][2] ) return false;
                else if(path[0][0] == 0 && path[0][1]== 0 && path[0][2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify right side
        if(pos[1]<BOARD_SIZE-1) {
            SaboteurTile neighborCard = this.board[pos[0]][pos[1] + 1];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                if (path[2][0] != neighborPath[0][0] || path[2][1] != neighborPath[0][1] || path[2][2] != neighborPath[0][2]) return false;
                else if(path[2][0] == 0 && path[2][1]== 0 && path[2][2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify upper side
        if(pos[0]>0) {
            SaboteurTile neighborCard = this.board[pos[0]-1][pos[1]];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                int[] p={path[0][2],path[1][2],path[2][2]};
                int[] np={neighborPath[0][0],neighborPath[1][0],neighborPath[2][0]};
                if (p[0] != np[0] || p[1] != np[1] || p[2] != np[2]) return false;
                else if(p[0] == 0 && p[1]== 0 && p[2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify bottom side:
        if(pos[0]<BOARD_SIZE-1) {
            SaboteurTile neighborCard = this.board[pos[0]+1][pos[1]];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                int[] p={path[0][0],path[1][0],path[2][0]};
                int[] np={neighborPath[0][2],neighborPath[1][2],neighborPath[2][2]};
                if (p[0] != np[0] || p[1] != np[1] || p[2] != np[2]) return false;
                else if(p[0] == 0 && p[1]== 0 && p[2] ==0 ) numberOfEmptyAround +=1; //we are touching by a wall
            }
        }
        else numberOfEmptyAround+=1;

        if(numberOfEmptyAround==requiredEmptyAround)  return false;

        return true;
    }
    
    
    public ArrayList<int[]> possiblePositions(SaboteurTile card) {
        // Given a card, returns all the possiblePositions at which the card could be positioned in an ArrayList of int[];
        // Note that the card will not be flipped in this test, a test for the flipped card should be made by giving to the function the flipped card.
        ArrayList<int[]> possiblePos = new ArrayList<int[]>();
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}}; //to make the test faster, we simply verify around all already placed tiles.
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j] != null) {
                    for (int m = 0; m < 4; m++) {
                        if (0 <= i+moves[m][0] && i+moves[m][0] < BOARD_SIZE && 0 <= j+moves[m][1] && j+moves[m][1] < BOARD_SIZE) {
                            if (this.verifyLegit(card.getPath(), new int[]{i + moves[m][0], j + moves[m][1]} )){
                                possiblePos.add(new int[]{i + moves[m][0], j +moves[m][1]});
                            }
                        }
                    }
                }
            }
        }
        return possiblePos;
    }
    
    
    
    /*
     * @param hand the list of card in the player's hand 
     * @param turnPlayer specify the player number. 
     * @return legalMoves a list of possible legal {@link SaboteurMove} that is available for player. 
     */
    public ArrayList<SaboteurMove> getAllLegalMoves(ArrayList<SaboteurCard> hand, int turnPlayer){
    	ArrayList<SaboteurMove> legalMoves = new ArrayList<>();
        boolean isBlocked;
        if(turnPlayer == 1){
            isBlocked= player1nbMalus > 0;
        }
        else {
            isBlocked= player2nbMalus > 0;
        }
    	
        for(SaboteurCard card : hand){
            if( card instanceof SaboteurTile && !isBlocked) {
                ArrayList<int[]> allowedPositions = possiblePositions((SaboteurTile)card);
                for(int[] pos:allowedPositions){
                    legalMoves.add(new SaboteurMove(card,pos[0],pos[1],turnPlayer));
                }
                //if the card can be flipped, we also had legal moves where the card is flipped;
                if(SaboteurTile.canBeFlipped(((SaboteurTile)card).getIdx())){
                    SaboteurTile flippedCard = ((SaboteurTile)card).getFlipped();
                    ArrayList<int[]> allowedPositionsflipped = possiblePositions(flippedCard);
                    for(int[] pos:allowedPositionsflipped){
                        legalMoves.add(new SaboteurMove(flippedCard,pos[0],pos[1],turnPlayer));
                    }
                }
            }
            else if(card instanceof SaboteurBonus){
                if(turnPlayer ==1){
                    if(player1nbMalus > 0) legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
                }
                else if(player2nbMalus>0) legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
            }
            else if(card instanceof SaboteurMalus){
                legalMoves.add(new SaboteurMove(card,0,0,turnPlayer));
            }
            else if(card instanceof SaboteurMap){
                for(int i =0;i<3;i++){ //for each hidden card that has not be revealed, we can still take a look at it.
                    if(! this.hiddenRevealed[i]) legalMoves.add(new SaboteurMove(card,hiddenPos[i][0],hiddenPos[i][1],turnPlayer));
                }
            }
            else if(card instanceof SaboteurDestroy){
                for (int i = 0; i < BOARD_SIZE; i++) {
                    for (int j = 0; j < BOARD_SIZE; j++) { //we can't destroy an empty tile, the starting, or final tiles.
                        if(this.board[i][j] != null && (i!=originPos || j!= originPos) && (i != hiddenPos[0][0] || j!=hiddenPos[0][1] )
                           && (i != hiddenPos[1][0] || j!=hiddenPos[1][1] ) && (i != hiddenPos[2][0] || j!=hiddenPos[2][1] ) ){
                            legalMoves.add(new SaboteurMove(card,i,j,turnPlayer));
                        }
                    }
                }
            }
        }
        // we can also drop any of the card in our hand
        for(int i=0;i<hand.size();i++) {
            legalMoves.add(new SaboteurMove(new SaboteurDrop(), i, 0, turnPlayer));
        }
            
    	return legalMoves;
    	
    }
    
    
    public boolean isLegal(ArrayList<SaboteurCard> hand, SaboteurMove m) {
        // For a move to be legal, the player must have the card in its hand
        // and then the game rules apply.
        // Note that we do not test the flipped version. To test it: use the flipped card in the SaboteurMove object.

        SaboteurCard testCard = m.getCardPlayed();
        int[] pos = m.getPosPlayed();
        int currentPlayer = m.getPlayerID();
        if (currentPlayer != turnPlayer) {
//        	System.out.println(currentPlayer + " , " + turnPlayer);
        	return false;
        }

        
        boolean isBlocked;
        if(turnPlayer == 1){
//        	System.out.println("im blocked");
            isBlocked= player1nbMalus > 0;
        }
        else {
//        	System.out.println("im blocked");
            isBlocked= player2nbMalus > 0;
        }
        if(testCard instanceof SaboteurDrop){
            if(hand.size()>=pos[0]){
                return true;
            }
        }
        boolean legal = false;
        for(SaboteurCard card : hand){
//        	System.out.println("im here");
            if (card instanceof SaboteurTile && testCard instanceof SaboteurTile && !isBlocked) {
                if(((SaboteurTile) card).getIdx().equals(((SaboteurTile) testCard).getIdx())){
//                	System.out.println(verifyLegit(((SaboteurTile) card).getPath(),pos));
                    return verifyLegit(((SaboteurTile) card).getPath(),pos);
                }
                else if(((SaboteurTile) card).getFlipped().getIdx().equals(((SaboteurTile) testCard).getIdx())){
                	// System.out.println(verifyLegit(((SaboteurTile) card).getPath(),pos));
                    return verifyLegit(((SaboteurTile) card).getFlipped().getPath(),pos);
                }
            }
            else if (card instanceof SaboteurBonus && testCard instanceof SaboteurBonus) {
                if (turnPlayer == 1) {
                    if (player1nbMalus > 0) return true;
                } else if (player2nbMalus > 0) return true;
                return false;
            }
            else if (card instanceof SaboteurMalus && testCard instanceof SaboteurMalus ) {
                return true;
            }
            else if (card instanceof SaboteurMap && testCard instanceof SaboteurMap) {
                int ph = 0;
                for(int j=0;j<3;j++) {
                    if (pos[0] == hiddenPos[j][0] && pos[1] == hiddenPos[j][1]) ph=j;
                }
                if (!this.hiddenRevealed[ph])
                    return true;
            }
            else if (card instanceof SaboteurDestroy && testCard instanceof SaboteurDestroy) {
                int i = pos[0];
                int j = pos[1];
                if (this.board[i][j] != null && (i != originPos || j != originPos) && (i != hiddenPos[0][0] || j != hiddenPos[0][1])
                        && (i != hiddenPos[1][0] || j != hiddenPos[1][1]) && (i != hiddenPos[2][0] || j != hiddenPos[2][1])) {
                    return true;
                }
            }
        }
        return legal;
    }
    
    
    /*
     * @param m take as input a {@link SaboteurMove} and update the playerBoard accordingly. 
     */
    public void processMove(SaboteurMove m, ArrayList<SaboteurCard> hand, int turnPlayer) throws IllegalArgumentException {

        if(m.getFromBoard()){
            turnNumber++;
            return;
        }

        // Verify that a move is legal (if not throw an IllegalArgumentException)
        // And then execute the move.
        // Concerning the map observation, the player then has to check by himself the result of its observation.
        //Note: this method is ran in a BoardState ran by the server as well as in a BoardState ran by the player.
        
        if (!isLegal(hand, m)) {
            throw new IllegalArgumentException("Invalid move. Move: " + m.toPrettyString());
        }

        SaboteurCard testCard = m.getCardPlayed();
        int[] pos = m.getPosPlayed();

        if(testCard instanceof SaboteurTile){
            this.board[pos[0]][pos[1]] = new SaboteurTile(((SaboteurTile) testCard).getIdx());
            //Remove from the player card the card that was used.
            for(SaboteurCard card : hand) {
                if (card instanceof SaboteurTile) {
                    if (((SaboteurTile) card).getIdx().equals(((SaboteurTile) testCard).getIdx())) {
                        hand.remove(card);
                        break; //leave the loop....
                    }
                    else if(((SaboteurTile) card).getFlipped().getIdx().equals(((SaboteurTile) testCard).getIdx())) {
                        hand.remove(card);
                        break; //leave the loop....
                    }
                }
            }
//            
//            if(turnPlayer==1){
//                //Remove from the player card the card that was used.
//                for(SaboteurCard card : hand) {
//                    if (card instanceof SaboteurTile) {
//                        if (((SaboteurTile) card).getIdx().equals(((SaboteurTile) testCard).getIdx())) {
//                            hand.remove(card);
//                            break; //leave the loop....
//                        }
//                        else if(((SaboteurTile) card).getFlipped().getIdx().equals(((SaboteurTile) testCard).getIdx())) {
//                            hand.remove(card);
//                            break; //leave the loop....
//                        }
//                    }
//                }
//            }
//            else {
//                for (SaboteurCard card : hand) {
//                    if (card instanceof SaboteurTile) {
//                        if (((SaboteurTile) card).getIdx().equals(((SaboteurTile) testCard).getIdx())) {
//                            hand.remove(card);
//                            break; //leave the loop....
//                        }
//                        else if(((SaboteurTile) card).getFlipped().getIdx().equals(((SaboteurTile) testCard).getIdx())) {
//                            hand.remove(card);
//                            break; //leave the loop....
//                        }
//                    }
//                }
//            }
        }
        else if(testCard instanceof SaboteurBonus){
            if(turnPlayer==1){
                player1nbMalus --;
                for(SaboteurCard card : hand) {
                    if (card instanceof SaboteurBonus) {
                        hand.remove(card);
                        break; //leave the loop....
                    }
                }
            }
            else{
                player2nbMalus --;
                for(SaboteurCard card : this.hand) {
                    if (card instanceof SaboteurBonus) {
                        hand.remove(card);
                        break; //leave the loop....
                    }
                }
            }
        }
        else if(testCard instanceof SaboteurMalus){
            if(turnPlayer==1){
                player2nbMalus ++;
                for(SaboteurCard card : hand) {
                    if (card instanceof SaboteurMalus) {
                        hand.remove(card);
                        break; //leave the loop....
                    }
                }
            }
            else{
                player1nbMalus ++;
                for(SaboteurCard card : hand) {
                    if (card instanceof SaboteurMalus) {
                        hand.remove(card);
                        break; //leave the loop....
                    }
                }
            }
        }
        else if(testCard instanceof SaboteurMap){
            if(turnPlayer==1){
                for(SaboteurCard card : hand) {
                    if (card instanceof SaboteurMap) {
                        hand.remove(card);
                        int ph = 0;
                        for(int j=0;j<3;j++) {
                            if (pos[0] == hiddenPos[j][0] && pos[1] == hiddenPos[j][1]) ph=j;
                        }
                        this.player1hiddenRevealed[ph] = true;
                        this.player1hiddenStatus.set(ph,this.player2hiddenStatus.indexOf(ph));
                        break; //leave the loop....
                    }
                }
            }
            else{
                for(SaboteurCard card : hand) {
                    if (card instanceof SaboteurMap) {
                        hand.remove(card);
                        int ph = 0;
                        for(int j=0;j<3;j++) {
                            if (pos[0] == hiddenPos[j][0] && pos[1] == hiddenPos[j][1]) ph=j;
                        }
                        
                        this.player2hiddenRevealed[ph] = true;
                        this.player2hiddenStatus.set(ph, 10);
                        break; //leave the loop....
                    }
                }
            }
        }
        else if (testCard instanceof SaboteurDestroy) {
            int i = pos[0];
            int j = pos[1];
            for(SaboteurCard card : hand) {
                if (card instanceof SaboteurDestroy) {
                    hand.remove(card);
                    this.board[i][j] = null;
                    break; //leave the loop....
                }
            }
//            if(turnPlayer==1){
//                for(SaboteurCard card : this.player1Cards) {
//                    if (card instanceof SaboteurDestroy) {
//                        this.player1Cards.remove(card);
//                        this.board[i][j] = null;
//                        break; //leave the loop....
//                    }
//                }
//            }
//            else{
//                for(SaboteurCard card : this.player2Cards) {
//                    if (card instanceof SaboteurDestroy) {
//                        this.player2Cards.remove(card);
//                        this.board[i][j] = null;
//                        break; //leave the loop....
//                    }
//                }
//            }
        }
        else if(testCard instanceof SaboteurDrop){
        	hand.remove(pos[0]);
//            if(turnPlayer==1) this.player1Cards.remove(pos[0]);
//            else this.player2Cards.remove(pos[0]);
        }
        turnPlayer = 1 - turnPlayer; // Swap player
        turnNumber++;
    }

    
    public SaboteurMove getRandomMove(ArrayList<SaboteurCard>hand, int turnPlayer) {
        ArrayList<SaboteurMove> moves = getAllLegalMoves(hand,turnPlayer);
        return moves.get(rand.nextInt(moves.size()));
    }

    public int getNbMalus(int playerNb){
        if(playerNb==1) return this.player1nbMalus;
        return this.player2nbMalus;
    }
    
    private boolean containsIntArray(ArrayList<int[]> a,int[] o){ //the .equals used in Arraylist.contains is not working between arrays..
        if (o == null) {
            for (int i = 0; i < a.size(); i++) {
                if (a.get(i) == null)
                    return true;
            }
        } else {
            for (int i = 0; i < a.size(); i++) {
                if (Arrays.equals(o, a.get(i)))
                    return true;
            }
        }
        return false;
    }
    
    public Boolean cardPath(ArrayList<int[]> originTargets,int[] targetPos,Boolean usingCard){
        // the search algorithm, usingCard indicate weither we search a path of cards (true) or a path of ones (aka tunnel)(false).
    	
        ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
        
        ArrayList<int[]> visited = new ArrayList<int[]>(); //will store the visited tile with an Hash table where the key is the position the board.
        
        visited.add(targetPos);
        if(usingCard) addUnvisitedNeighborToQueue(targetPos,queue,visited,BOARD_SIZE,usingCard);
        else addUnvisitedNeighborToQueue(targetPos,queue,visited,BOARD_SIZE*3,usingCard);
        while(queue.size()>0){
            int[] visitingPos = queue.remove(0);
            if(containsIntArray(originTargets,visitingPos)){
                return true;
            }
            visited.add(visitingPos);
            if(usingCard) addUnvisitedNeighborToQueue(visitingPos,queue,visited,BOARD_SIZE,usingCard);
            else addUnvisitedNeighborToQueue(visitingPos,queue,visited,BOARD_SIZE*3,usingCard);
            // System.out.println(queue.size());
        }
        return false;
    }
    
    private void addUnvisitedNeighborToQueue(int[] pos,ArrayList<int[]> queue, ArrayList<int[]> visited,int maxSize,boolean usingCard){
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}};
        int i = pos[0];
        int j = pos[1];
        for (int m = 0; m < 4; m++) {
            if (0 <= i+moves[m][0] && i+moves[m][0] < maxSize && 0 <= j+moves[m][1] && j+moves[m][1] < maxSize) { //if the hypothetical neighbor is still inside the board
                int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
                if(!containsIntArray(visited,neighborPos)){
                    if(usingCard && this.board[neighborPos[0]][neighborPos[1]]!=null) queue.add(neighborPos);
                    else if(!usingCard && this.intBoard[neighborPos[0]][neighborPos[1]]==1) queue.add(neighborPos);
                }
            }
        }
    }
    

    public boolean [] pathToTarget(int[] targetPos ){
        /* This function look if a path is linking the starting point to the states among objectives.
            :return: if there exists one: true
                     if not: false
                     In Addition it changes each reached states hidden variable to true:  self.hidden[foundState] <- true
            Implementation details:
            For each hidden objectives:
                We verify there is a path of cards between the start and the hidden objectives.
                    If there is one, we do the same but with the 0-1s matrix!

            To verify a path, we use a simple search algorithm where we propagate a front of visited neighbor.
               TODO To speed up: The neighbor are added ranked on their distance to the origin... (simply use a PriorityQueue with a Comparator)
        */
        boolean cardPath = false;
        boolean intPath = false;
       
        ArrayList<int[]> originTargets = new ArrayList<>();
        originTargets.add(new int[]{originPos,originPos}); //the starting points
        
        //get the target position
        
        
        if (cardPath(originTargets, targetPos, true)) { //checks that there is a cardPath
        	cardPath = true;
        	
            //next: checks that there is a path of ones.
            ArrayList<int[]> originTargets2 = new ArrayList<>();
            //the starting points
            originTargets2.add(new int[]{originPos*3+1, originPos*3+1});
            originTargets2.add(new int[]{originPos*3+1, originPos*3+2});
            originTargets2.add(new int[]{originPos*3+1, originPos*3});
            originTargets2.add(new int[]{originPos*3, originPos*3+1});
            originTargets2.add(new int[]{originPos*3+2, originPos*3+1});
            
            //get the target position in 0-1 coordinate
            int[] targetPos2 = {targetPos[0]*3+1, targetPos[1]*3+1};
            if (cardPath(originTargets2, targetPos2, false)) {
                intPath =true;
            }
        }
        
        boolean [] found = new boolean [] {cardPath, intPath};
        return found;
    }
    public boolean [] pathToTarget(SaboteurMove target ){
        /* This function look if a path is linking the starting point to the states among objectives.
            :return: if there exists one: true
                     if not: false
                     In Addition it changes each reached states hidden variable to true:  self.hidden[foundState] <- true
            Implementation details:
            For each hidden objectives:
                We verify there is a path of cards between the start and the hidden objectives.
                    If there is one, we do the same but with the 0-1s matrix!

            To verify a path, we use a simple search algorithm where we propagate a front of visited neighbor.
               TODO To speed up: The neighbor are added ranked on their distance to the origin... (simply use a PriorityQueue with a Comparator)
        */
        boolean cardPath = false;
        boolean intPath = false;
       
        ArrayList<int[]> originTargets = new ArrayList<>();
        originTargets.add(new int[]{originPos,originPos}); //the starting points
        
        //get the target position
        int [] targetPos = target.getPosPlayed();
       /* SaboteurTile a = this.board[targetPos[0]][targetPos[1]];
        if(a==null) {
        	System.out.println(false);
        }else {
        	System.out.println(true);
        }*/
        
        if (cardPath(originTargets, targetPos, true)) { //checks that there is a cardPath
        	cardPath = true;
        	
            //next: checks that there is a path of ones.
            ArrayList<int[]> originTargets2 = new ArrayList<>();
            //the starting points
            originTargets2.add(new int[]{originPos*3+1, originPos*3+1});
            originTargets2.add(new int[]{originPos*3+1, originPos*3+2});
            originTargets2.add(new int[]{originPos*3+1, originPos*3});
            originTargets2.add(new int[]{originPos*3, originPos*3+1});
            originTargets2.add(new int[]{originPos*3+2, originPos*3+1});
            
            //get the target position in 0-1 coordinate
            int[] targetPos2 = {targetPos[0]*3+1, targetPos[1]*3+1};
            if (cardPath(originTargets2, targetPos2, false)) {
                intPath =true;
            }
        }
        boolean [] found = new boolean [] {cardPath, intPath};
        return found;
    }
    

    public boolean pathToHidden(){
        /* This function look if a path is linking the starting point to the states among objectives.
            :return: if there exists one: true
                     if not: false
                     In Addition it changes each reached states hidden variable to true:  self.hidden[foundState] <- true
            Implementation details:
            For each hidden objectives:
                We verify there is a path of cards between the start and the hidden objectives.
                    If there is one, we do the same but with the 0-1s matrix!

            To verify a path, we use a simple search algorithm where we propagate a front of visited neighbor.
               TODO To speed up: The neighbor are added ranked on their distance to the origin... (simply use a PriorityQueue with a Comparator)
        */
        boolean atLeastOnefound = false;
        ArrayList<Integer> targetIndex = new ArrayList<Integer>();
        targetIndex.add(0);
        targetIndex.add(1);
        targetIndex.add(2);
        
        for(Integer currentTargetIdx: targetIndex) {
        	ArrayList<int[]> originTargets = new ArrayList<>();
            originTargets.add(new int[]{originPos,originPos}); //the starting points
            //get the target position
            int[] targetPos = {0,0};
            
            if(!this.hiddenRevealed[currentTargetIdx]) {  //verify that the current target has not been already discovered. Even if there is a destruction event, the target keeps being revealed!

                if (cardPath(originTargets, targetPos, true)) { //checks that there is a cardPath
                    //next: checks that there is a path of ones.
                    ArrayList<int[]> originTargets2 = new ArrayList<>();
                    //the starting points
                    originTargets2.add(new int[]{originPos*3+1, originPos*3+1});
                    originTargets2.add(new int[]{originPos*3+1, originPos*3+2});
                    originTargets2.add(new int[]{originPos*3+1, originPos*3});
                    originTargets2.add(new int[]{originPos*3, originPos*3+1});
                    originTargets2.add(new int[]{originPos*3+2, originPos*3+1});
                    //get the target position in 0-1 coordinate
                    int[] targetPos2 = {targetPos[0]*3+1, targetPos[1]*3+1};
                    if (cardPath(originTargets2, targetPos2, false)) {

                        this.hiddenRevealed[currentTargetIdx] = true;
                        this.player1hiddenRevealed[currentTargetIdx] = true;
                        this.player2hiddenRevealed[currentTargetIdx] = true;
                        atLeastOnefound =true;
                    }
                //    else{
                //        System.out.println("0-1 path was not found");
                //    }
                }
            }
            else{
                atLeastOnefound = true;
            }
        }
        return atLeastOnefound;
    }
    
    
}
