package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(), depth);

    }

    private int scorePlayer(final Board board, final Player player, final int depth) {

        return pieceValue(player) + mobility(player) +
                                    check(player) +
                                    checkMate(player,depth) +
                                    castled(player);


        /* TODO: add in various board states */
    }

    private static int castled(Player player) {
        if(player.isCastled()){
            return CASTLE_BONUS;
        }else{
            return 0;
        }
    }

    private static int check(final Player player) {
        if(player.getOpponent().isInCheck()){
            return CHECK_BONUS;
        }else{
            return 0;
        }
    }

    private static int checkMate(Player player, int depth){
        if(player.getOpponent().isInCheckMate()){
            return CHECKMATE_BONUS * depthFactor(depth);
        }else{
            return 0;
        }
    }

    private static int depthFactor(int depth) {
        if(depth == 0){
            return 1;
        }else{
            return DEPTH_BONUS * depth;
        }
    }

    private static int mobility(final Player player) {
        //int scaleingFactor = 0;

        return player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;

        for (final Piece piece : player.getActivePieces()){
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }


}
