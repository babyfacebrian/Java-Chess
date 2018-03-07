/* MAIN CLASS FOR A ROOK PIECE */

package com.chess.engine.pieces;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.Alliance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Rook extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-8,-1, 1, 8};

    public Rook(int piecePosition, Alliance pieceAlliance) {

        super(PieceType.ROOK, piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATES){
            int candidateDestinationCoordinate = this.piecePosition;

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){

                if(isFirstColumnExclusion(candidateDestinationCoordinate,currentCandidateOffset) ||
                        isEighthColumnExclusion(candidateDestinationCoordinate, currentCandidateOffset)){
                    break;
                }
                candidateDestinationCoordinate += currentCandidateOffset;

                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                    if(!candidateDestinationTile.isTileOccupied()){
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));

                    }else{
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                        if(this.pieceAlliance != pieceAlliance){
                            legalMoves.add(new Move.AttackMove(board, this,candidateDestinationCoordinate,pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Rook move_Piece(Move move) {
        return new Rook(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int canidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (canidateOffset == -1);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int canidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (canidateOffset == 1);
    }

    @Override
    public String toString(){
        return PieceType.ROOK.toString();
    }


}









