package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260795862");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
//        MyTools.getSomething();
    	int timeLimit = 1900; 
	    long startTime = System.currentTimeMillis();
	    long endTime = startTime + timeLimit;

        // Is random the best you can do?
//        Move myMove = boardState.getRandomMove();
//        System.out.println("lala");
        ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        SaboteurTile[][] board = boardState.getHiddenBoard();
        int[][] hiddenPos = SaboteurBoardState.hiddenPos;
        //double minDistance = 500;
        //double distanceLeft = 0;
        //double distanceMid = 0;
        //double distanceRight = 0;
        //double distance = 0;
        //int position[];
        String hidden1;
        String hidden2;
        String hidden3;
        int turnPlayer = boardState.getTurnPlayer();
        //boolean isBlocked = boardState.getNbMalus(turnPlayer)>0;
        boolean isBlocked;
        if(turnPlayer == 1){        
            isBlocked= boardState.getNbMalus(turnPlayer)>0 ;
        }
        else {

            isBlocked= boardState.getNbMalus(0)>0;
        }
        //boolean [] hiddenRevealed = {!board[12][3].getIdx().equals("8"), !board[12][5].getIdx().equals("8"), !board[12][7].getIdx().equals("8")};
        //check the status of the hidden object
        //ArrayList<Integer> hiddenStatus = MyTools.gethiddenStatus(board, boardState.hiddenPos);
        SaboteurMove myMove;
        Random random=new Random();
        //int[] opt= {-1,-1};
         myMove = moves.get(random.nextInt(moves.size()) -0);
        ArrayList<Integer> hiddenStatus = MyTools.gethiddenStatus(board, SaboteurBoardState.hiddenPos);
        double[] smallestDistance=MyTools.smallestDistance1(board, hiddenStatus);
       // MyTools.smallestDistance(board, boardState.hiddenPos)[0] <= 2 || MyTools.smallestDistance(board, boardState.hiddenPos)[1] <= 2 || MyTools.smallestDistance(board, boardState.hiddenPos)[2] <= 2
//        System.out.println("Current Smallest Distance1 : " + MyTools.smallestDistance(board, boardState.hiddenPos)[0]);
//        System.out.println("Current Smallest Distance2 : " + MyTools.smallestDistance(board, boardState.hiddenPos)[1]);
//        System.out.println("Current Smallest Distance3 : " + MyTools.smallestDistance(board, boardState.hiddenPos)[2]);
		if(isBlocked) {
            if(smallestDistance[0]<=2) {
                ArrayList <String> optOrder = new ArrayList<String>();
                optOrder.add("Destroy");
                optOrder.add("Malus");
                optOrder.add("Bonus");
                optOrder.add("Map");
                myMove = MyTools.specialOptMove(moves, optOrder,boardState);
                return myMove;
                
            }else {
                ArrayList <String> optOrder = new ArrayList<String>();
                optOrder.add("Bonus");
                optOrder.add("Malus");
                optOrder.add("Map");
                optOrder.add("Destroy");
                myMove = MyTools.specialOptMove(moves, optOrder,boardState);
                return myMove;
            }
        /* Not blocked but opponent will win in one step */	
        }else if(smallestDistance[0]<= 2) {
        	 for(int i=0;i<moves.size();i++) {
             	SaboteurMove move=moves.get(i);
             	int[] pos = move.getPosPlayed();
             	String cardName = move.getCardPlayed().getName();
         			if(cardName.equals("Malus")) {

         				return move;
         				
         			}
             }
//        	 return MyTools.minimax(boardState, endTime);
 	    	try {
	    		return MyTools.minimax(boardState,endTime);
	    	}catch (timeLimitException e){
	    		return e.myMove;
	    	}
	        
        }
		
		
		
		
		else if(smallestDistance[0]<= 4 && smallestDistance[0] >=2) {
            /*ArrayList <String> optOrder = new ArrayList<String>();
            optOrder.add("Destroy");
            optOrder.add("Malus");
            optOrder.add("empty");
            optOrder.add("empty");
            myMove = MyTools.specialOptMove(moves, optOrder);  */
            
            
            
            //System.out.println("Within 2");
             
        	//double minDistance = 3;
			int x = (int)smallestDistance[1];
			int y = (int)smallestDistance[2];
			int a =-1;
            for(int i=0;i<moves.size();i++) {
            	SaboteurMove move=moves.get(i);
            	int[] pos = move.getPosPlayed();
            	String cardName = move.getCardPlayed().getName();
        			if(cardName.equals("Malus")) {

        				return move;
        				
        			}else if (cardName.equals("Destroy") && pos[0]==x && pos[1]==y) {  
        				a=1;
        				myMove=move;
        			}
            }
            if(a==1 ) {
            	return myMove;
            }else {
//            	return MyTools.minimax(boardState,endTime);
    	    	try {
    	    		return MyTools.minimax(boardState,endTime);
    	    	}catch (timeLimitException e){
    	    		return e.myMove;
    	    	}
    	        
            }
           // return MyTools.minimax(boardState);
        
        } 
		else {
        	
        	/****************************************************************************************************************************************************/         	
        	/* If there's a map card then apply it first */
			
			
			// find the position of the nugget and the revealed card if possible 
			//ArrayList<Integer> hiddenStatus = MyTools.gethiddenStatus(board, SaboteurBoardState.hiddenPos);
//			for(Integer i : hiddenStatus) System.out.println("hiddenStatus: " + i);
			
			
	        int nuggetPos = -1 ; 
	        if (hiddenStatus.contains(1)) nuggetPos = hiddenStatus.indexOf(1); 
	        else if (Collections.frequency(hiddenStatus, 0)==2) nuggetPos = hiddenStatus.indexOf(-1);
	 
	        // find the revealed card if possible 
	        int crossPos = -1; 
	        if (hiddenStatus.contains(0)) crossPos = hiddenStatus.indexOf(0);
	        
	        if(Collections.frequency(hiddenStatus, -1)==3) {
	        	// if no hidden objective has been revealed, place a map at a random hidden object
	        	for(SaboteurMove move: moves) {
            		if(move.getCardPlayed().getName().equals("Map")) return move; 
	        	}
	        }else if (nuggetPos != -1) {
	        	// a nugget has been revealed 
	        	for(int i=0;i<moves.size();i++) {
            		String moveCardName = moves.get(i).getCardPlayed().getName();
            		if(moveCardName.equals("Map") || moveCardName.equals("Bonus")) moves.remove(i);
            		
            	}
	        }else {
	        	// no nugget found but some hidden obj has been revealed 
	        	ArrayList<int []> target = new ArrayList<int []>();
	        	for(int h=0; h<3; h++) {
	        		if (h != crossPos) target.add(hiddenPos[h]);
	        	}
	        	for(int i=0;i<moves.size();i++) {
	        		String moveCardName = moves.get(i).getCardPlayed().getName();
            		if(moveCardName.equals("Map")) {
            			int [] pos = moves.get(i).getPosPlayed();
            			for (int [] t: target) {
                			if(Arrays.equals(pos, t)) return moves.get(i);
            			}
            		}
	        	}
            		
            			 
            		
	        }
	        
	        // check for destroy 
//	        for(int i=0;i<moves.size();i++) {
//        		String moveCardName = moves.get(i).getCardPlayed().getName();
//        		if(moveCardName.equals("Destroy")) {
//			        myMove = MyTools.useDestroy(boardState, moves.get(i));
//					if(myMove != null) return myMove;
//        		}
//	        }
			
	        //Now take out all the bonus card
	        for (int i = 0; i<moves.size();i++) {
	        	if (moves.get(i).getCardPlayed().getName().equals("Bonus") || moves.get(i).getCardPlayed().getName().equals("Map") || moves.get(i).getCardPlayed().getName().equals("Drop")|| moves.get(i).getCardPlayed().getName().equals("Malus"))moves.remove(i);
	        }
	        
			
			
			// now decide which tile to play 
            // return MyTools.minimax(boardState);
           
	    	
	    	try {
	    		return MyTools.minimax(boardState,endTime);
	    	}catch (timeLimitException e){
	    		return e.myMove;
	    	}
	        
			
			
//        	int key =1;
//        	
//        	hidden1 = board[12][3].getName();
//        	hidden2 = board[12][5].getName();
//        	hidden3 = board[12][7].getName();
//        
//        	if(hidden1.equals("nugget") || hidden2.equals("nugget") || hidden3.equals("nugget")) {
//        		key = -1;
//        	}else if(hidden1.equals("hidden1") || hidden2.equals("hidden1") || hidden3.equals("hidden1")){
//        		key = -1;
//        		
//        	}else if(hidden1.equals("hidden2") || hidden2.equals("hidden2") || hidden3.equals("hidden2")){
//        		key = -1;
//        		
//        	}else {
//        		int notRevealed = 0;
//        		if(hidden1.equals("8")) {notRevealed++;}
//        		if(hidden2.equals("8")) {notRevealed++;}
//        		if(hidden3.equals("8")) {notRevealed++;}
//        		if(notRevealed<=1) {
//        			key=-1;
//        		}
//        		
//        	}
//        	/* If key = -1, then Remove the map card from Legal Moves */
//        	if(key==1) {
//        		for(int i=0;i<moves.size();i++) {
//            		String moveCardName = moves.get(i).getCardPlayed().getName();
//            		if(moveCardName.equals("Map")) {
//            			key=i;
//            			//break;
//            		}else if(moveCardName.equals("Bonus")) {
//            			moves.remove(i);
//            		}
//            	}
//        	}else if (key==-1) {
//        		for(int i=0;i<moves.size();i++) {
//            		String moveCardName = moves.get(i).getCardPlayed().getName();
//            		if(moveCardName.equals("Map")) {
//            			moves.remove(i);
//            		}else if(moveCardName.equals("Bonus")) {
//            			moves.remove(i);
//            		}
//            	}
//        	}
        	
        	
        	
        	
        	/****************************************************************************************************************************************************/  
        	/* If there's no map card */
	        
//        	/* Minimax */
//        	if(key!=-1) {
//        		myMove = moves.get(key);
//        	}else {
////        		myMove=MyTools.kBeam(boardState);
//        		myMove=MyTools.minimax(boardState);
//        	}
        	//myMove = MyTools.getOptMoves(moves, hiddenStatus, boardState.hiddenPos);
        	
//        	
//        	/* if there's a map card, apply it first */	
//            ArrayList <String> optOrder = new ArrayList<String>();
//            optOrder.add("Map");
//            optOrder.add("empty");
//            optOrder.add("empty");
//            optOrder.add("empty");
//            myMove = MyTools.specialOptMove(moves, optOrder);
//            
//        /* Then case of not block  & no map card & opponent will not win in 1 step*/
//            for(int i=0; i<moves.size();i++) {
//                /* if this is a tile card */
//                if(moves.get(i).getCardPlayed() instanceof  Saboteur.cardClasses.SaboteurTile) {
//                    //double heuristic = MyTools.getHeuristics(moves.get(i).getCardPlayed());
//                    position =((SaboteurMove) moves.get(i)).getPosPlayed();
//                    distanceLeft = (position[0]-12)*(position[0]-12) + (position[1]-3)*(position[1]-3);
//                    distanceMid = (position[0]-12)*(position[0]-12) + (position[1]-5)*(position[1]-5);
//                    distanceRight = (position[0]-12)*(position[0]-12) + (position[1]-7)*(position[1]-7);
//                    // take the min of all these 3 diatance
//                    distance = Math.min(distanceLeft, distanceMid);
//                    distance = Math.min(distance, distanceRight);
//                    if (distance < minDistance) {
//                        minDistance=distance;
//                        myMove = moves.get(i);
//                    }
//                    
//                    
//                }

            }
            
            
        

       
        /*int position[]=((SaboteurMove) myMove).getPosPlayed();
        System.out.println("X is: "+ position[0]);
        System.out.println("Y is: "+ position[1]);*/
        

        // Return your move to be processed by the server.
//        return myMove;
    }
}