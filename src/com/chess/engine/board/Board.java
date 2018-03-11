/* Main Chessboard Class */

package com.chess.engine.board;


import com.chess.engine.Alliance;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.WhitePlayer;
import com.chess.engine.pieces.*;
import com.chess.engine.player.Player;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final Pawn EnPassantPawn;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;


    private Board(final Builder builder){

        // Create the board and get all white and black pieces
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.EnPassantPawn = builder.EnPassantPawn;

        // Collect all legal moves for white and black pieces
        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        //Each Player has both white and black legal moves
        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);

        // Set the current player to the next player to move
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString(){
        final StringBuilder builder = new StringBuilder();

        for(int i = 0; i< BoardUtils.NUM_TILES; i++){
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));

            if((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0){
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public Player whitePlayer() {
        return this.whitePlayer;
    }

    public Player blackPlayer(){
        return this.blackPlayer;
    }

    public Player currentPlayer(){
        return this.currentPlayer;
    }

    public Pawn getEnPassantPawn() {
        return this.EnPassantPawn;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final Piece piece : pieces){
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }

        // return a collection of moves from the collection of pieces
        return Collections.unmodifiableList(legalMoves);
    }

    public static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();

        for(final Tile tile : gameBoard){
            if(tile.isTileOccupied()){
                final Piece piece = tile.getPiece();

                if(piece.getPieceAlliance() == alliance){
                    activePieces.add(piece);
                }
            }
        }

        // return a collection of pieces from occupied tiles on the board
        return ImmutableList.copyOf(activePieces);
    }

    public Tile getTile(final int tileCoordinate){

        return gameBoard.get(tileCoordinate);
    }

    private static List<Tile> createGameBoard(final Builder builder){
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];

        for(int i=0; i<BoardUtils.NUM_TILES; i++){
            tiles[i] = Tile.createTile(i,builder.BoardConfig.get(i));
        }

        // Return list of 64 tiles (0-63)
        return Arrays.asList(tiles);
    }

    public static Board createStandardBoard(){
        final Builder builder = new Builder();

        //Black pieces
        builder.setPiece(new Pawn(8,Alliance.BLACK));
        builder.setPiece(new Pawn(9,Alliance.BLACK));
        builder.setPiece(new Pawn(10,Alliance.BLACK));
        builder.setPiece(new Pawn(11,Alliance.BLACK));
        builder.setPiece(new Pawn(12,Alliance.BLACK));
        builder.setPiece(new Pawn(13,Alliance.BLACK));
        builder.setPiece(new Pawn(14,Alliance.BLACK));
        builder.setPiece(new Pawn(15,Alliance.BLACK));

        builder.setPiece(new Rook(0,Alliance.BLACK));
        builder.setPiece(new Rook(7,Alliance.BLACK));

        builder.setPiece(new Knight(1,Alliance.BLACK));
        builder.setPiece(new Knight(6,Alliance.BLACK));

        builder.setPiece(new Bishop(2,Alliance.BLACK));
        builder.setPiece(new Bishop(5,Alliance.BLACK));

        builder.setPiece(new Queen(3,Alliance.BLACK));
        builder.setPiece(new King(4,Alliance.BLACK));

        // While pieces
        builder.setPiece(new Pawn(48,Alliance.WHITE));
        builder.setPiece(new Pawn(49,Alliance.WHITE));
        builder.setPiece(new Pawn(50,Alliance.WHITE));
        builder.setPiece(new Pawn(51,Alliance.WHITE));
        builder.setPiece(new Pawn(52,Alliance.WHITE));
        builder.setPiece(new Pawn(53,Alliance.WHITE));
        builder.setPiece(new Pawn(54,Alliance.WHITE));
        builder.setPiece(new Pawn(55,Alliance.WHITE));

        builder.setPiece(new Rook(56,Alliance.WHITE));
        builder.setPiece(new Rook(63,Alliance.WHITE));

        builder.setPiece(new Knight(57,Alliance.WHITE));
        builder.setPiece(new Knight(62,Alliance.WHITE));

        builder.setPiece(new Bishop(58,Alliance.WHITE));
        builder.setPiece(new Bishop(61,Alliance.WHITE));

        builder.setPiece(new Queen(59,Alliance.WHITE));
        builder.setPiece(new King(60,Alliance.WHITE));

        // Set White as first to move
        builder.setMoveMaker(Alliance.WHITE);

        // Builds the starting board with pieces in staring locations
        return builder.build();

    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    public static class Builder {
        //Used to generate a new board after each move

        Map<Integer, Piece> BoardConfig;
        Alliance nextMoveMaker;
        Pawn EnPassantPawn;

        public Builder(){

            this.BoardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece piece){
            this.BoardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn diagonal) {
            this.EnPassantPawn = diagonal;
        }
    }


}
