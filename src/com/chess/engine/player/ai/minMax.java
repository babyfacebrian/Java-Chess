package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

public class minMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public minMax(final int depth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = depth;
    }

    @Override
    public String toString(){

        return "MiniMax";
    }

    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();
        System.out.println(board.currentPlayer() + "Evaluating with depth of: " + this.searchDepth);
        int numberOfMoves = board.currentPlayer().getLegalMoves().size();

        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if(moveTransition.getMoveStatus().isDone()){

                // Switch between white vs black player for min vs max move
                if(board.currentPlayer().getAlliance().isWhite()){
                    currentValue = min(moveTransition.getToBoard(), this.searchDepth-1);
                }else{
                    currentValue = max(moveTransition.getToBoard(), this.searchDepth-1);
                }

                // Minimizing move
                if(board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                    bestMove = move;

                // Maximizing move
                } else if(board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue){
                    lowestSeenValue =currentValue;
                    bestMove = move;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;

        System.out.printf("%s Selects %s : Time Taken: %d", board.currentPlayer().toString(),
                                                            bestMove.toString(), executionTime);
        return bestMove;
    }


    public int min(final Board board, final int depth){
        // game over
        if(depth == 0 || isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE;

        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = max(moveTransition.getToBoard(), depth-1);

                if(currentValue <= lowestSeenValue){
                    lowestSeenValue =  currentValue;
                }
            }
        }
        return lowestSeenValue;
    }


    public int max(final Board board, final int depth){
        // game over
        if(depth == 0 || isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE;

        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(moveTransition.getToBoard(), depth-1);

                if(currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    private static boolean isEndGameScenario(final Board board){
        return board.currentPlayer().isInCheckMate() || board.currentPlayer().isInStaleMate();

    }









}
