package student_player;

import java.util.ArrayList;
import java.util.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Collections;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDestroy;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMalus;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;
import student_player.PlayerBoardState;

public class MyTools {
    public static double getSomething() {
        return Math.random();
    }
    
    
    
    /****************************************************************************************************************************************************/         	
	/* K beam Search */
    public static SaboteurMove kBeam(SaboteurBoardState boardState) {
    	ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
    	Random random=new Random();
    	SaboteurMove myMove = moves.get(random.nextInt(moves.size()) -0);
    	ArrayList<Double> result = new ArrayList<Double>();
    	for(int i =0;i<moves.size();i++) {
    		result.add(0.0);
    	}
    	while(result.size()>1) {
    		result=minimax1(boardState,moves);
    		ArrayList<Double> result1 = new ArrayList<Double>();
    		for(int i =0;i<moves.size();i++) {
    			result1.add(result.get(i));
    		}
    		Collections.sort(result);
    		int size = result.size()/2;
    		double value = result.get(size-1);
    		for(int i=0;i<result1.size();i++) {
    			if(result1.get(i)>value) {
    				moves.remove(i);
    			}
    		}
    		
    	}
    	
    	myMove=moves.get(0);
    	return myMove;
    	
    }
    
    
    
    /****************************************************************************************************************************************************/         	
	/* Minimax tools*/
	// heuristic of each card
    public static ArrayList<Double> minimax1(SaboteurBoardState boardState, ArrayList<SaboteurMove> moves) {
    	
    	//PlayerBoardState studentBoard = new PlayerBoardState(boardState);
    	//ArrayList<SaboteurCard> currentHand = boardState.getCurrentPlayerCards();
    	//ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
    	
    	
    	//Random random=new Random();
    	//SaboteurMove myMove = moves.get(random.nextInt(moves.size()) -0);
    	SaboteurTile[][] board = boardState.getHiddenBoard();
    	ArrayList<Double> result = new ArrayList<Double>();
    	//int size = moves.size()/2;
    	/* Now apply the first round */
    	double minHeuristic = 100000;
    	int turnPlayer = boardState.getTurnPlayer();
    	/* Also randomly get a new card from the deck to make sure size(newHand) = 7*/
    	ArrayList<SaboteurCard> deck = SaboteurCard.getDeck();
    	Collections.shuffle(deck);

    	for(int i =0; i<moves.size();i++) {
    		SaboteurMove move = moves.get(i);
    		// Simulating the first move
    		//ArrayList<SaboteurCard> newHand = new ArrayList <SaboteurCard>();
    		PlayerBoardState studentBoard = new PlayerBoardState(boardState);
    		studentBoard.processMove(move, boardState.getCurrentPlayerCards(), boardState.getTurnPlayer());
    		ArrayList<SaboteurCard> newHand = newCard(boardState.getCurrentPlayerCards() ,move,deck.remove(0));
//    		double heuristic = simulation1(studentBoard,turnPlayer,newHand,move);
//    		result.add(heuristic);
    	
    	}
  
    	return result;
    	
    }
    
    
    
    
    
    


    /****************************************************************************************************************************************************/         	
	/* Minimax tools*/
	// heuristic of each card
    
    public static SaboteurMove minimax(SaboteurBoardState boardState, long endTime) throws timeLimitException {
//    	PlayerBoardState copy = new PlayerBoardState(boardState);
 	
    	//PlayerBoardState studentBoard = new PlayerBoardState(boardState);
    	//ArrayList<SaboteurCard> currentHand = boardState.getCurrentPlayerCards();
    	
    	
 	
    	ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
    	
    	Random random=new Random();
    	SaboteurMove myMove = moves.get(random.nextInt(moves.size()) -0);
    	SaboteurTile[][] board = boardState.getHiddenBoard();
    	//ArrayList<Double> result = new ArrayList<Double>();
    	//int size = moves.size()/2;
    	/* Now apply the first round */
    	double minHeuristic = 100000;
    	//System.out.println("Call Minimax");
    	//System.out.println(minHeuristic);
    	
    	int turnPlayer = boardState.getTurnPlayer();
    	/* Also randomly get a new card from the deck to make sure size(newHand) = 7*/
    	ArrayList<SaboteurCard> deck = SaboteurCard.getDeck();
    	Collections.shuffle(deck);
    	int turn = 0; 
    	//double heuristic = 1000000;
    	//double min2=3;
    	int index = random.nextInt(deck.size()) - 0;
    	ArrayList<Integer> hiddenStatus = gethiddenStatus(board, boardState.hiddenPos);
    	
    	
    	for(int i =0; i<moves.size();i++) {
    		if (System.currentTimeMillis() > endTime) {
    			throw new timeLimitException(myMove,"");
    		}
//    		System.out.println(turn + ": simulations times");
    		SaboteurMove move = moves.get(i);
    		int [] movePos = move.getPosPlayed();
    		PlayerBoardState studentBoard = new PlayerBoardState(boardState);
    		SaboteurCard card = move.getCardPlayed();
    		double heuristic =1000000;
    		
    		if(card instanceof  SaboteurDestroy) {
    			heuristic=getHeuristic3(studentBoard, hiddenStatus, boardState.hiddenPos, move);
    			if(heuristic<minHeuristic) {
    				//min2=heuristic;
    				//System.out.println("Destroy: "+ movePos[0]+" "+movePos[1]);
    				//System.out.println(heuristic);
    				minHeuristic=heuristic;
    				myMove=move;
    				continue;
    				
    			}else {
    				continue;
    			}
    		}
    		
    		
    		//check if its a wonderful wonderful move :) 
    		if(move.getCardPlayed() instanceof SaboteurTile) studentBoard.board[movePos[0]][movePos[1]] = new SaboteurTile(((SaboteurTile) move.getCardPlayed()).getIdx());
    		if(studentBoard.pathToHidden()) {
    			return move; 
    		}else {
    			studentBoard.board[movePos[0]][movePos[1]] = null;
    		}
    		
    		
    		//here we check if its up in the sky 
    		if (movePos[0]<5) {
    			//System.out.println("smaller than 5");
    			continue; 
    		}
    		
    		
    		//double heuristic =1000000;
    		// Simulating the first move
    		//ArrayList<SaboteurCard> newHand = new ArrayList <SaboteurCard>();
    		
    		//System.out.println("num of leagal moves:" + 	studentBoard.getAllLegalMoves(boardState.getCurrentPlayerCards(),boardState.getTurnPlayer()).size());
    		
    		
    		/*if(move.getCardPlayed().getName().equals("Destroy")) {
    			System.out.println(":   lalalalalalalalalalalalalalalalalalal");
    		}*/
    		
    		/*if(card instanceof  SaboteurDestroy) {
    			heuristic=getHeuristic3(studentBoard,hiddenStatus,boardState.hiddenPos,move);
    			if(heuristic<minHeuristic) {
    				//min2=heuristic;
    				minHeuristic=heuristic;
    				myMove=move;
    				continue;
    				
    			}else {
    				continue;
    			}
    		}*/
    		
    		
    		
    		/*double times = 1;
            boolean[] targetPath = studentBoard.pathToTarget(move);
            if(targetPath[0]==true && targetPath[1]==false) {
            	times=15;
            }if(targetPath[0]==false && targetPath[1]==false) {
            	times=10;
            }if(targetPath[0]==true && targetPath[1]==true) {
            	times=0.1;
            }*/
    		
            // this is to simulate the processMove method 
    		SaboteurTile destroyed = null;
    		if(studentBoard.board[movePos[0]][movePos[1]] != null) {
    			destroyed = new SaboteurTile (((SaboteurTile) studentBoard.board[movePos[0]][movePos[1]]).getIdx());
    		}
    		
    		if(move.getCardPlayed() instanceof SaboteurTile) {
        		studentBoard.board[movePos[0]][movePos[1]] = new SaboteurTile(((SaboteurTile) move.getCardPlayed()).getIdx());
        		updateIntBoard(studentBoard,move,"add");
        		//studendBoard.intBoard.update
        		
    		}
    		else if (move.getCardPlayed() instanceof SaboteurDestroy){
    			//Destroy intBoard
    			studentBoard.board[movePos[0]][movePos[1]] = null;
    		}
    		
    		 /*If the move on a invlid path*/
    		double times = 1;
            boolean[] targetPath = studentBoard.pathToTarget(move);
            if(targetPath[0]==true && targetPath[1]==false) {
            	times=15;
            }if(targetPath[0]==false && targetPath[1]==false) {
            	times=10;
            }if(targetPath[0]==true && targetPath[1]==true) {
            	times=0.1;
            }
    		
    		
    		
    		
    		
    		// now we simulate 

        	double d = getHeuristic(studentBoard, hiddenStatus);
        	//double moveDistance = getHeuristic4(studentBoard,hiddenStatus, moves.get(i),d);
    		ArrayList<SaboteurCard> newHand = newCard(boardState.getCurrentPlayerCards() ,move,deck.get(index));	
    		ArrayList<SaboteurMove> newMoves = studentBoard.getAllLegalMoves(newHand, turnPlayer);
    		
    		if(move.getCardPlayed() instanceof SaboteurTile) {
    			double oppDistance = simulation1(studentBoard,turnPlayer,newMoves,move, d) ;
    			oppDistance=oppDistance + getHeuristic4(studentBoard,hiddenStatus, moves.get(i),oppDistance,targetPath);
    			//System.out.println(move.getCardPlayed().getName()+": "+ movePos[0]+" "+movePos[1]);
    			//System.out.println("Heuristic: "+ getHeuristic4(studentBoard,hiddenStatus, moves.get(i),oppDistance,targetPath));
    			//System.out.println("target Path: "+ targetPath[0]+"  "+targetPath[1]);
    			heuristic = times*oppDistance;
    			//System.out.println(move.getCardPlayed().getName()+": "+ movePos[0]+" "+movePos[1]);
    			//System.out.println("Heuristic4: "+ getHeuristic4(studentBoard,hiddenStatus, moves.get(i),oppDistance,targetPath));
    			//System.out.println("Heuristic: "+ heuristic);
    			//System.out.println("target Path: "+ targetPath[0]+"  "+targetPath[1]);
    			/*if(heuristic>times*oppDistance) {
        			heuristic = times*oppDistance;
        		}*/
    		}
    		
    		if(heuristic < minHeuristic) {
    			minHeuristic = heuristic;
    			myMove = move;
    		} 
    		
    		//System.out.println(move.getCardPlayed().getName()+":   "+heuristic);
    		//double a = getHeuristic2(studentBoard,hiddenStatus,boardState.hiddenPos,move);
//    		if(move.getCardPlayed().getName().equals("Destroy")) {
//    			System.out.println(move.getCardPlayed().getName()+":   "+ heuristic +"  "+ move.getPosPlayed()[0]+"  "+ move.getPosPlayed()[1]);
//    			System.out.println("heuristic2 value: "+ getHeuristic2(studentBoard,hiddenStatus,boardState.hiddenPos,move));
//        		System.out.println("min2: "+min2);
//    		}
    		//System.out.println(move.getCardPlayed().getName()+":   "+heuristic);
    		//System.out.println("min2: "+min2);
    		turn++;
    		
    		if(move.getCardPlayed() instanceof SaboteurTile ) {
        		studentBoard.board[movePos[0]][movePos[1]] = null;
        		updateIntBoard(studentBoard,move,"destroy");
    		}else if (move.getCardPlayed() instanceof SaboteurDestroy){
    			studentBoard.board[movePos[0]][movePos[1]] = destroyed;
    		}
    	}
    	//System.out.println("POsition: "+ myMove.getPosPlayed()[0]+" "+myMove.getPosPlayed()[1]);
    	return myMove;
    	
    } 
    
    public static void updateIntBoard(PlayerBoardState studentBoard, SaboteurMove move, String type) {
    	int[][] intBoard = studentBoard.intBoard;
    	int[][] path = ((SaboteurTile)move.getCardPlayed()).getPath();
    	//int[] pos = move.getPosPlayed();
    	int x =move.getPosPlayed()[0]*3;
    	int y =move.getPosPlayed()[1]*3;
    	if(type.equals("add")) {
    		intBoard[x][y]=path[0][2];
    		intBoard[x+1][y]=path[0][1];
    		intBoard[x+2][y]=path[0][0];
    		intBoard[x][y+1]=path[1][2];
    		intBoard[x+1][y+1]=path[1][1];
    		intBoard[x+2][y+1]=path[1][0];
    		intBoard[x][y+2]=path[2][2];
    		intBoard[x+1][y+2]=path[2][1];
    		intBoard[x+2][y+2]=path[2][0];
    	}
    	if(type.equals("destroy")){
    		intBoard[x][y]=-1;
    		intBoard[x+1][y]=-1;
    		intBoard[x+2][y]=-1;
    		intBoard[x][y+1]=-1;
    		intBoard[x+1][y+1]=-1;
    		intBoard[x+2][y+1]=-1;
    		intBoard[x][y+2]=-1;
    		intBoard[x+1][y+2]=-1;
    		intBoard[x+2][y+2]=-1;
    	}

    	
    }

    public static ArrayList<SaboteurCard> newCard(ArrayList<SaboteurCard> oldHand, SaboteurMove move,SaboteurCard Card ){
    	ArrayList<SaboteurCard> newHand = new ArrayList <SaboteurCard>();
    	SaboteurCard card = move.getCardPlayed();
    	for(int i=0; i<oldHand.size();i++) {
    		if(!oldHand.get(i).getName().equals(card.getName())) {
    			newHand.add(oldHand.get(i));
    		}
    	}
    
    	newHand.add(Card);
    	
    	return newHand;
    }
    
    //simulate the opponent move
    public static double simulation1(PlayerBoardState studentBoard, int turnPlayer, ArrayList<SaboteurMove> newMoves, SaboteurMove originMove,double distance) {


    	/* Now process the second round */
    	int turnOpponent = studentBoard.getTurnPlayer();
    	
    	/* Estimate the opponent card and getAllLegalMove (opponent) */
    	ArrayList<SaboteurCard> deck = SaboteurCard.getDeck();
    	Collections.shuffle(deck);
    	ArrayList<SaboteurCard> opponentHand = new ArrayList<SaboteurCard>();
    	//int Handsize=0;
    	//int decksize=deck.size();
    	for(int i =0; i<7;i++) {
    		
    			opponentHand.add(deck.remove(0));
    			
    		}

    
    	//opponent legal move
    	ArrayList<SaboteurMove> opponentMoves = studentBoard.getAllLegalMoves(opponentHand, turnOpponent);
//    	System.out.println(opponentMoves.size()+"llalala");
    	
    	
    	double minHeuristic = 10000;
    	double maxHeuristic = 0;
    	int[][] hiddenPos=studentBoard.hiddenPos;
    	ArrayList<Integer> hiddenStatus = gethiddenStatus(studentBoard.board,hiddenPos) ;
    	
    	
    	for(int i =0; i<opponentMoves.size();i++) {
    		if (opponentMoves.get(i).getCardPlayed()instanceof SaboteurDrop ||opponentMoves.get(i).getCardPlayed()instanceof SaboteurMap ||
    				opponentMoves.get(i).getCardPlayed() instanceof SaboteurMalus || opponentMoves.get(i).getCardPlayed() instanceof SaboteurBonus|| 
    				opponentMoves.get(i).getCardPlayed() instanceof SaboteurDestroy ) {
    			continue;
    		}else {
    			int [] pos  = opponentMoves.get(i).getPosPlayed(); 
        		studentBoard.board[pos[0]][pos[1]] = new SaboteurTile(((SaboteurTile) opponentMoves.get(i).getCardPlayed()).getIdx());
        		
            	// Simulating the second move
            	minHeuristic = 10000;
            	//go through the board to check the smallest distance after the opponent move;
            	double distance1 = getHeuristic2(hiddenStatus, studentBoard.hiddenPos, opponentMoves.get(i), distance);
            	/*for(int h =0; h< 3; h++) {
            		double d = getDistance(pos, hiddenPos[h]);
            		if(d < distance1) distance1 = d;
            	}*/
            	
            	if (distance1 > distance) distance1 = distance;
            	
            	for (int j=0; j<newMoves.size();j++) {
            		if (newMoves.get(j).getCardPlayed()instanceof SaboteurDrop ||newMoves.get(j).getCardPlayed()instanceof SaboteurMap ||
            				newMoves.get(j).getCardPlayed() instanceof SaboteurMalus || newMoves.get(j).getCardPlayed() instanceof SaboteurBonus|| 
            				newMoves.get(j).getCardPlayed() instanceof SaboteurDestroy ) {
            			continue;
            		}else {
            			int [] pos2 = newMoves.get(j).getPosPlayed(); 
                		studentBoard.board[pos2[0]][pos2[1]] = new SaboteurTile(((SaboteurTile) newMoves.get(j).getCardPlayed()).getIdx());
                		//double heuristic = getHeuristic2(hiddenStatus, studentBoard.hiddenPos, originMove, distance1);
                		double heuristic = getHeuristic2(hiddenStatus, studentBoard.hiddenPos, newMoves.get(j), distance1);
                		if(heuristic < minHeuristic) {
                			minHeuristic = heuristic;
                		}
                		studentBoard.board[pos2[0]][pos2[1]] = null;
            		}
            	}

            	studentBoard.board[pos[0]][pos[1]] = null;
            	/*double min = minHeuristic + distance1;
            	if(min > maxHeuristic) {
            		maxHeuristic = min;
            	}*/
            	if(minHeuristic>maxHeuristic) {
            		maxHeuristic=minHeuristic;
            	}
    		}
    		
		}
    	
    	return maxHeuristic;
    	
    }
    
    
    
    /*
     * @param optMove:
     * 		- give the opt move to the three hidden object separately. initialized with {null, null, null}
     * @return optMoves  
     * 			
     */
    
    public static double getHeuristic(PlayerBoardState board, ArrayList<Integer> hiddenStatus) {
    	
    	double minDistance = 500000;
        double distance = 0;
        int [] position;
        int numOfTile = -1;
        boolean map = false;
        int simulated_revealed= -1;
        int [][] hiddenPos = board.hiddenPos;
        
        // find the position of the nugget and the revealed card if possible 
        int nuggetPos = -1 ; 
        if (hiddenStatus.contains(1)) nuggetPos = hiddenStatus.indexOf(1); 
        else if (Collections.frequency(hiddenStatus, 0)==2) nuggetPos = hiddenStatus.indexOf(-1);
 
        // find the revealed card if possible 
        int crossPos = -1; 
        if (hiddenStatus.contains(0)) crossPos = hiddenStatus.indexOf(0);
        if (hiddenStatus.contains(10)) simulated_revealed = Collections.frequency(hiddenStatus, 0);

        
        
        if (nuggetPos != -1) { // there is a nugget 
        	
        	int [] target = hiddenPos[nuggetPos];
        	for (int i = 0 ; i < board.board.length;i++) {
        		for (int j =0; j<board.board.length;j++) {
    				int[] a = {i,j};
        			if (Arrays.equals(a,hiddenPos[0]) ||Arrays.equals(a,hiddenPos[1]) ||Arrays.equals(a,hiddenPos[2])) continue;
        			if(board.board[i][j]!=null) {
        				distance = getDistance(a,target);
        				
        				if(distance < minDistance)  minDistance = distance; 
        			}
        		}
        	}
        }
        else { //no nugget revealed 
        	ArrayList<int []> target = new ArrayList<int []>();
        	for(int h=0; h<3; h++) {
        		if (h != crossPos) target.add(hiddenPos[h]);
        	}
        	for (int i = 0 ; i < board.board.length;i++) {
        		for (int j =0; j<board.board.length;j++) {
    				int[] a = {i,j};
        			if (Arrays.equals(a,hiddenPos[0]) ||Arrays.equals(a,hiddenPos[1]) ||Arrays.equals(a,hiddenPos[2])) continue;
        			if(board.board[i][j]!=null) {
        				for (int [] t: target) {
        					distance = getDistance(a,t);
            				if(distance < minDistance)  minDistance = distance; 
        				}
        				
        			}
        		}
        	}
        }
        return minDistance;
    	
    }
    
    public static double getHeuristic2( ArrayList<Integer> hiddenStatus, int [][] hiddenPos, SaboteurMove move, double distance1) {
		
        double minDistance = 500000;


        // find the position of the nugget and the revealed card if possible 
        int nuggetPos = -1 ;
        if (hiddenStatus.contains(1)) nuggetPos = hiddenStatus.indexOf(1);
        else if (Collections.frequency(hiddenStatus, 0)==2) nuggetPos = hiddenStatus.indexOf(-1);

        // find the revealed card if possible 
        int crossPos = -1;
        if (hiddenStatus.contains(0)) crossPos = hiddenStatus.indexOf(0);
//        if (hiddenStatus.contains(10)) simulated_revealed = Collections.frequency(hiddenStatus, 0);

        int [] targetPos = move.getPosPlayed();

        
        if (nuggetPos != -1) { // there is a nugget 

            int [] target = hiddenPos[nuggetPos];
            double distance = getDistance(targetPos,target);
            if(distance < minDistance)  minDistance = distance;
        }
        else { //no nugget revealed 
            for(int h=0; h<3; h++) {
                if (h != crossPos) {
                	double distance = getDistance(targetPos,hiddenPos[h]);
                	if(distance < minDistance) minDistance = distance;
                }
            }
        }
        
        if(distance1 < minDistance) minDistance = distance1;

        return minDistance;
    }  
    
    // for destroy
    public static double getHeuristic3(PlayerBoardState playerBoard, ArrayList<Integer> hiddenStatus, int [][] hiddenPos, SaboteurMove move) {
    	double moveDistance = 300;
    	int[] pos =move.getPosPlayed();
    	String [] unreasonableMove = {"13","1","2","2_flip","3","3_flip","4","4_flip","11","11_flip","12","12_flip","14","14_flip","15"};
    	double minDistance = getDistance(hiddenPos[0],pos);
    	if(minDistance<getDistance(hiddenPos[1],pos)) {
    		minDistance = getDistance(hiddenPos[0],pos);
    	}
    	if(minDistance<getDistance(hiddenPos[2],pos)) {
    		minDistance = getDistance(hiddenPos[2],pos);
    	}
    	SaboteurTile [][] board = playerBoard.board;
    	for(int i =0;i<unreasonableMove.length;i++) {
            if(board[pos[0]][pos[1]] != null && board[pos[0]][pos[1]].getIdx().equals(unreasonableMove[i])) {
                moveDistance=0;
                //if(minDistance>100) {minDistance=minDistance*0.01;}
                minDistance=minDistance*0.01;
                break;
            }
        }
    	
    	
    	minDistance=minDistance+moveDistance;
    	return minDistance;
    	
    }    
    
    
    //for move distance
    public static double getHeuristic4(PlayerBoardState playerBoard, ArrayList<Integer> hiddenStatus, SaboteurMove move, double minDistance, boolean[] cardPath) {
        /* Depends on getcardapth value and the moveDistance to the gold
    	If the move does not have path, then moveDistance will be increased by *1.3
    	If the move have path ,then moveDistance will be decreased to 0.5*
    	*/
    	//minDistance=minDistance+moveDistance;
        String [] unreasonableMove = {"13","1","2","2_flip","3","3_flip","4","4_flip","11","11_flip","12","12_flip","14","14_flip","15"};
        String	[] unreasonableMove2 = {"5_flip","7","9_flip","10"};
        String[] unreasonableMove3= {"5","9","7_flip"};
        double moveDistance = 500000;
        String index = ((SaboteurTile)move.getCardPlayed()).getIdx();
        
        int [][] hiddenPos = playerBoard.hiddenPos;
        moveDistance= getHeuristic2(hiddenStatus,hiddenPos,move,moveDistance);
        
        /*if(move.getCardPlayed() instanceof SaboteurTile) {
        	
            moveDistance = getDistance(hiddenPos[0],move.getPosPlayed());
            if(moveDistance>getDistance(hiddenPos[1],move.getPosPlayed())) {
                moveDistance=getDistance(hiddenPos[1],move.getPosPlayed());
            }
            if(moveDistance>getDistance(hiddenPos[2],move.getPosPlayed())) {
                moveDistance=getDistance(hiddenPos[2],move.getPosPlayed());
            }*/
            //String index = ((SaboteurTile)move.getCardPlayed()).getIdx();

            /* If it is a unreasonableMove,then it's heuristic*10 or * 3.5 depends on minDistance */
            for(int i =0;i<unreasonableMove.length;i++) {
                if(index.equals(unreasonableMove[i])) {
                	if(minDistance<=4) {
                		moveDistance=moveDistance*20;
                		if(cardPath[1]==true) {
                			moveDistance=moveDistance*200;
                		}
                		break;
                	}else {
                		moveDistance=moveDistance*15;
                		if(cardPath[1]==true) {
                			moveDistance=moveDistance*150;
                		}
                		break;
                	}
                }
            }
            /* If one step far from win */
            if(minDistance==1 && playerBoard.pathToHidden()) {  		
            			return 0;
            
            /* If it is a unreasonableMove2,then it's heuristic*1.5 */
            }
            /* If lower than 12, then towards up tiles and horizontal tiles are optimal*/
            /* if */
            if(minDistance<=2&&move.getPosPlayed()[0]>=12) {
            	for(int i=0;i<unreasonableMove3.length;i++) {
            		if(index.equals(unreasonableMove3[i])) {
        				moveDistance=moveDistance*1.5;
        			}
            	}
            }
            else if(minDistance<=2) {
            		for(int i =0;i<unreasonableMove2.length;i++) {
            			if(index.equals(unreasonableMove2[i])) {
            				moveDistance=moveDistance*1.5;
            			}
            		}
            }else if(minDistance<=5) {
        		for(int i =0;i<unreasonableMove2.length;i++) {
        			if(index.equals(unreasonableMove2[i])) {
        				moveDistance=moveDistance*1.3;
        			}
        		}
        }

        
       
        
        return moveDistance;
    }
   
	
	/****************************************************************************************************************************************************/        	
    
    
    
 
    /* write a method to get the evaluated distance of 2 coordinate */  
    public static double getDistance(int [] a , int[] b) {
    	//return (a[0]-b[0])*(a[0]-b[0])+(a[1]-b[1])*(a[1]-b[1]) ;
    	return Math.abs((a[0]-b[0]))*Math.abs((a[0]-b[0]))+Math.abs((a[1]-b[1]))*Math.abs((a[1]-b[1]));
    }
    
    /* A method to get the current distance to all three object */
   /* public static double[] smallestDistance(SaboteurTile[][] board, int [][] hiddenPos) {
    	double[] threeDistance = {1000,1000,1000};
//    	int [] object1 = hiddenPos[0];
//    	int [] object2 = hiddenPos[1];
//    	int [] object3 = hiddenPos[2];
    	for (int i = 0 ; i < board.length;i++) {
    		for (int j =0; j<board.length;j++) {
    			int[] a = {i,j};
    			// if (Arrays.equals(a, hiddenPos[0]) ||Arrays.equals(a, hiddenPos[1]) ||Arrays.equals(a, hiddenPos[2])) continue; 
    			if (Arrays.equals(a,hiddenPos[0]) ||Arrays.equals(a,hiddenPos[1]) ||Arrays.equals(a,hiddenPos[2])) continue;
    			else if(board[i][j]!=null) {
        			if(getDistance(a,hiddenPos[0]) < threeDistance[0]) { threeDistance[0] = getDistance(a,hiddenPos[0]); }
        			if(getDistance(a,hiddenPos[1]) < threeDistance[1]) { threeDistance[1] = getDistance(a,hiddenPos[1]); }
        			if(getDistance(a,hiddenPos[2]) < threeDistance[2]) { threeDistance[2] = getDistance(a,hiddenPos[2]); }
        				
        			
    			}

    		}
    	}
    	return threeDistance;
    }*/
    
 public static double[] smallestDistance1(SaboteurTile[][] board, ArrayList<Integer> hiddenStatus) {
    	
    	double minDistance = 500000;
        double distance = 0;
        //int [] position;
        //int numOfTile = -1;
        //boolean map = false;
        int simulated_revealed= -1;
        int [][] hiddenPos = {{12,3},{12,5},{12,7}};
        double[] result = {1000,1000,1000};
        
        // find the position of the nugget and the revealed card if possible 
        int nuggetPos = -1 ; 
        if (hiddenStatus.contains(1)) nuggetPos = hiddenStatus.indexOf(1); 
        else if (Collections.frequency(hiddenStatus, 0)==2) nuggetPos = hiddenStatus.indexOf(-1);
 
        // find the revealed card if possible 
        int crossPos = -1; 
        if (hiddenStatus.contains(0)) crossPos = hiddenStatus.indexOf(0);
        if (hiddenStatus.contains(10)) simulated_revealed = Collections.frequency(hiddenStatus, 0);

        
        
        if (nuggetPos != -1) { // there is a nugget 
        	
        	int [] target = hiddenPos[nuggetPos];
        	for (int i = 10 ; i < board.length;i++) {
        		for (int j =0; j<board.length;j++) {
    				int[] a = {i,j};
        			if (Arrays.equals(a,hiddenPos[0]) ||Arrays.equals(a,hiddenPos[1]) ||Arrays.equals(a,hiddenPos[2])) continue;
        			if(board[i][j]!=null) {
        				distance = getDistance(a,target);
        				
        				if(distance < minDistance)  
        					{
        					minDistance = distance; 
        					result[0]=minDistance;
        					result[1]=i;
        					result[2]=j;
        					}
        			}
        		}
        	}
        }
        else { //no nugget revealed 
        	ArrayList<int []> target = new ArrayList<int []>();
        	for(int h=0; h<3; h++) {
        		if (h != crossPos) target.add(hiddenPos[h]);
        	}
        	for (int i = 10 ; i < board.length;i++) {
        		for (int j =0; j<board.length;j++) {
    				int[] a = {i,j};
        			if (Arrays.equals(a,hiddenPos[0]) ||Arrays.equals(a,hiddenPos[1]) ||Arrays.equals(a,hiddenPos[2])) continue;
        			if(board[i][j]!=null) {
        				for (int [] t: target) {
        					distance = getDistance(a,t);
            				if(distance < minDistance) {
            					
            					minDistance = distance; 
            					result[0]=minDistance;
            					result[1]=i;
            					result[2]=j;
            				}
        				}
        				
        			}
        		}
        	}
        }
        return result;
    	
    }
    
    
    

    public static ArrayList<Integer> gethiddenStatus(SaboteurTile[][] hiddenBoard, int[][] hiddenPos ) {
    	
    	ArrayList<Integer> hiddenStatus = new ArrayList<Integer>();
    	for(int i = 0; i<3; i++) hiddenStatus.add(-1);
    	int a =0;
    	
    	for(int h=0; h<3; h++) {
//    		System.out.println("hidden Pos name: " + hiddenBoard[hiddenPos[h][0]][hiddenPos[h][1]].getName());
    		if (hiddenBoard[hiddenPos[h][0]][hiddenPos[h][1]].getIdx().equals("hidden1") || hiddenBoard[hiddenPos[h][0]][hiddenPos[h][1]].getName().equals("hidden2")) {
//    			System.out.println("im revealed");
    			a=a+1;
    			hiddenStatus.set(h,0) ;
    		}else if (hiddenBoard[hiddenPos[h][0]][hiddenPos[h][1]].getIdx().equals("nugget")) {
//    			System.out.println("im nugget");
    			hiddenStatus.set(h,1);
    		}else if(hiddenBoard[hiddenPos[h][0]][hiddenPos[h][1]].getIdx().equals("8")){
//    			System.out.println("its a cross here");
    		}
    	}
    	if(a==2) {
    		for(int i=0;i<3;i++) {
    			if(hiddenStatus.get(i)== -1){
    				hiddenStatus.set(i,1);
    			}
    			
    			
    		}
    	}

    	return hiddenStatus;
    }
 
    

    
    public static SaboteurMove useMap(SaboteurBoardState boardState,ArrayList<SaboteurMove> moves) {
    	SaboteurMove map = null;
    	int[][] hiddenPos = {{12,3},{12,5},{12,7}};
    	SaboteurTile [][] board = boardState.getHiddenBoard();
    	ArrayList<Integer> hiddenStatus = MyTools.gethiddenStatus(board, SaboteurBoardState.hiddenPos);
//		for(Integer i : hiddenStatus) System.out.println("hiddenStatus: " + i);
		
		
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
        		if(moveCardName.equals("Map") || moveCardName.equals("Bonus")) return null;
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
        return map;
    }

    
    /* The Optimal move when we get blocked */
    public static SaboteurMove specialOptMove(ArrayList<SaboteurMove> moves, ArrayList <String> optOrder, SaboteurBoardState boardState) {
    	Random random=new Random();
    	SaboteurMove myMove = moves.get(random.nextInt(moves.size()) -0);
    	int[] opt = new int [optOrder.size()];
    	for(int i =0; i< optOrder.size();i++) {
    		opt[i]=-1;
    	}
    	double minDistance = 10000;
    	int[][] hiddenPos = {{12,3}, {12,5},{12,7}};
    	for (int i=0; i<moves.size();i++) {
    		String cardName = moves.get(i).getCardPlayed().getName();
    		if(cardName.equals(optOrder.get(0))) {
    			opt[0]=i;
    			if(cardName.equals("Destroy")) {
    				
    				myMove = useDestroy(boardState, moves.get(i));
    				if(myMove != null) return myMove;
    				
    				int[] destroyPosition = moves.get(i).getPosPlayed();
    				double distance = getDistance(hiddenPos[0] , destroyPosition);
    				
    				if(distance < getDistance(hiddenPos[1] , destroyPosition) ) {
    					distance =  getDistance(hiddenPos[1] , destroyPosition);
    				}
    				if(distance < getDistance(hiddenPos[2] , destroyPosition) ) {
    					distance =  getDistance(hiddenPos[2] , destroyPosition);
    				}
    				if(distance < minDistance) {
    					myMove = moves.get(i);
    					minDistance = distance;
    				}				
    			}else if(cardName.equals("Map")) {
    				myMove = useMap(boardState,moves);
    				if (myMove!=null) {
    					return myMove;
    				}
    			}
    			else {
    				myMove = moves.get(i);
    				break;
    			}
    		
    		}else if (cardName.equals(optOrder.get(1)) && opt[0]==-1) {
    			opt[1]=i;
    			if(cardName.equals("Destroy")) {
    				myMove = useDestroy(boardState, moves.get(i));
    				if(myMove != null) return myMove;
    				int[] destroyPosition = moves.get(i).getPosPlayed();
    				double distance = getDistance(hiddenPos[0] , destroyPosition);
    				
    				if(distance < getDistance(hiddenPos[1] , destroyPosition) ) {
    					distance =  getDistance(hiddenPos[1] , destroyPosition);
    				}
    				if(distance < getDistance(hiddenPos[2] , destroyPosition) ) {
    					distance =  getDistance(hiddenPos[2] , destroyPosition);
    				}
    				if(distance < minDistance) {
    					myMove = moves.get(i);
    					minDistance = distance;
    				}				
    			}else if(cardName.equals("Map")) {
    				myMove = useMap(boardState,moves);
    				if (myMove!=null) {
    					return myMove;
    				}
    			}
    			else {
    				myMove = moves.get(i);
    				break;
    			}
    		}else if (cardName.equals(optOrder.get(2)) && opt[0]==-1 && opt[1]==-1) {
    			opt[2]=i;
    			if(cardName.equals("Destroy")) {
    				myMove = useDestroy(boardState, moves.get(i));
    				if(myMove != null) return myMove;
    				
    				int[] destroyPosition = moves.get(i).getPosPlayed();
    				double distance = getDistance(hiddenPos[0] , destroyPosition);
    				
    				if(distance < getDistance(hiddenPos[1] , destroyPosition) ) {
    					distance =  getDistance(hiddenPos[1] , destroyPosition);
    				}
    				if(distance < getDistance(hiddenPos[2] , destroyPosition) ) {
    					distance =  getDistance(hiddenPos[2] , destroyPosition);
    				}
    				if(distance < minDistance) {
    					myMove = moves.get(i);
    					minDistance = distance;
    				}				
    			}else if(cardName.equals("Map")) {
    				myMove = useMap(boardState,moves);
    				if (myMove!=null) {
    					return myMove;
    				}
    			}
    			else {
    				myMove = moves.get(i);
    				break;
    			}
    		}else if (cardName.equals(optOrder.get(3)) && opt[0]==-1 && opt[1]==-1 && opt[2]==-1) {
    			opt[3]=i;
    			if(cardName.equals("Destroy")) {
    				myMove = useDestroy(boardState, moves.get(i));
    				if(myMove != null) return myMove;
    				int[] destroyPosition = moves.get(i).getPosPlayed();
    				double distance = getDistance(hiddenPos[0] , destroyPosition);
    				
    				if(distance < getDistance(hiddenPos[1] , destroyPosition) ) {
    					distance =  getDistance(hiddenPos[1] , destroyPosition);
    				}
    				if(distance < getDistance(hiddenPos[2] , destroyPosition) ) {
    					distance =  getDistance(hiddenPos[2] , destroyPosition);
    				}
    				if(distance < minDistance) {
    					myMove = moves.get(i);
    					minDistance = distance;
    				}				
    			}else if(cardName.equals("Map")) {
    				myMove = useMap(boardState,moves);
    				if (myMove!=null) {
    					return myMove;
    				}
    			}
    			else {
    				myMove = moves.get(i);
    				break;
    			}
    		}
    		
    	}
    	   	
    	return myMove;
    }
 
    public static SaboteurMove useDestroy(SaboteurBoardState boardState, SaboteurMove destroy) {
    	
    	SaboteurMove myDestroy = null; 
    	
    	int [] pos = destroy.getPosPlayed(); 
    	SaboteurTile [][] board = boardState.getHiddenBoard();
    	String [] unreasonableMove = {"1","2","2_flip","3","3_flip","4","4_flip","11","11_flip","12","12_flip","13","14","14_flip","15"};
    	
    	for (int i = 0 ; i < board.length;i++) {
    		for (int j =0; j<board.length;j++) {
    			SaboteurTile temp = board[i][j];
    			int [] p;
    			p = new int [] {i,j};
				
				if (temp == null) {
					continue;
				}else {
					boolean cond2 = Arrays.equals(pos, p);
	    			boolean cond3 = false; 
	    			for (String s: unreasonableMove) {
	    				if (s.equals(temp.getIdx())) cond3 = true;
	    			}
	    			
	    			if(cond2 && cond3) {
	    				return destroy;
	    			}
				}
    			
    			
    		}
    	}
    	
    	return myDestroy;
    }
    

    //Define a method 
    /*
     * @param hiddenStatus: 
     * 			- represent the status of the three hidden objects initialized with {-1,-1,-1}
     * 			- -1 -- the hidden object is unrevealed 
     * 			- 0  -- the hidden object is revealed but it is not a nugget (it's a cross tile)
     * 			- 1  -- the hidden object is revealed and it is a nugget 	
     * @return hiddenStatus 	
     */
   
    /*
     * @param optMove:
     * 		- give the opt move to the three hidden object separately. initialized with {null, null, null}
     * @return optMoves 
     * 			
     */
    
   /* public static SaboteurMove getOptMoves(ArrayList<SaboteurMove> legalMoves, ArrayList<Integer> hiddenStatus, int [][] hiddenPos) {
        
    	SaboteurMove optMove = null;
    	
    	double minDistance = 500;
        double distance = 0;
        int [] position;
        int numOfTile = -1;
        boolean map = false;
        
        // find the position of the nugget and the revealed card if possible 
        int nuggetPos = -1 ; 
        if (hiddenStatus.contains(1)) nuggetPos = hiddenStatus.indexOf(1); 
        else if (Collections.frequency(hiddenStatus, 0)==2) nuggetPos = hiddenStatus.indexOf(-1);
 
        // find the revealed card if possible 
        int crossPos = -1; 
        if (hiddenStatus.contains(0)) crossPos = hiddenStatus.indexOf(0);

        if (nuggetPos != -1) { // there is a nugget 
        	
        	int [] target = hiddenPos[nuggetPos];
        	for(int i = 0; i<legalMoves.size(); i++) {
        		if(legalMoves.get(i).getCardPlayed() instanceof Saboteur.cardClasses.SaboteurTile) {
        			numOfTile ++; 
        			position =((SaboteurMove) legalMoves.get(i)).getPosPlayed();
        			int height = Math.abs(target[0] - position[0]);
        			int width = Math.abs(target[1] - position[1]);
        			distance = Math.sqrt(height * height + width * width);
        			if (distance < minDistance) {
        				minDistance = distance;
        				optMove = legalMoves.get(i);
        			}
        		}
        	}
        }else { //no nugget revealed 
        	ArrayList<int []> target = new ArrayList<int []>();
        	for(int h=0; h<3; h++) {
        		if (h != crossPos) target.add(hiddenPos[h]);
        	}
        	
        	for(int i = 0; i<legalMoves.size(); i++) {
        		// if there is a map card 
        		if(legalMoves.get(i).getCardPlayed() instanceof Saboteur.cardClasses.SaboteurMap) {
        			optMove = legalMoves.get(i);
        			map = true;
        		}else if (legalMoves.get(i).getCardPlayed() instanceof Saboteur.cardClasses.SaboteurTile) {
        			numOfTile ++; 
        			position =((SaboteurMove) legalMoves.get(i)).getPosPlayed();
        			for (int [] t: target) {
        				int height = Math.abs(t[0] - position[0]);
            			int width = Math.abs(t[1] - position[1]);
            			distance = Math.sqrt(height * height + width * width);
            			if(distance < minDistance) {
            				minDistance = distance;
            				optMove = legalMoves.get(i);
            			}
        			}
        		}
        	
        	}
        	
        }
        
        if(!map || (numOfTile != -1)) {// no map card nor tile card in hand 
        	for(int i = 0; i<legalMoves.size(); i++) {
            	if (legalMoves.get(i).getCardPlayed() instanceof Saboteur.cardClasses.SaboteurMalus) {
            		optMove = legalMoves.get(i);
            	}else if(legalMoves.get(i).getCardPlayed() instanceof Saboteur.cardClasses.SaboteurDestroy) {
            		optMove = legalMoves.get(i);
            	}
        	}

        }
        
        
        return optMove;
      
    	
    } */
    
} 
    
   
