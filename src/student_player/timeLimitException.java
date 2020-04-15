package student_player;

import Saboteur.SaboteurMove;

public class timeLimitException extends Exception{
	final SaboteurMove myMove;
	public timeLimitException(SaboteurMove myMove,String message) {
		
        super(message);
        this.myMove = myMove;
    }
	
//	public SaboteurMove getMove() {
//		return this.myMove;
//	}
}

