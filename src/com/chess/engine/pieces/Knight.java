/* MAIN CLASS FOR A KNIGHT PIECE */

package com.chess.engine.pieces;


import com.chess.engine.Alliance;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class Knight extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17,-15,-10,-6,6,10,15,17};

    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES){
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){

                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isEighthColumnExclusion(this.piecePosition,currentCandidateOffset)){
                    continue;
                }
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                if(!candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));

                }else{
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if(this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new Move.AttackMove(board, this,candidateDestinationCoordinate,pieceAtDestination));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Knight move_Piece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int canidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (canidateOffset == -17 || canidateOffset == -10 ||
                canidateOffset == 6 || canidateOffset == 15);
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int canidateOffset){
        return BoardUtils.SECOND_COLUMN[currentPosition] && (canidateOffset == -10 || canidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int canidateOffset){
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (canidateOffset == -6 || canidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int canidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (canidateOffset == -15 || canidateOffset == -6
                || canidateOffset == 10 || canidateOffset == 17);
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

}