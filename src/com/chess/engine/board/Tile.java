/* MAIN BOARD TILE CLASS */

package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import java.util.*;

public abstract class Tile {

    protected final int tileCoordinate;
    private static final Map<Integer, EmptyTile> Empty_Tiles_Cache = createAllPossibleEmptyTiles();


    private static Map<Integer,EmptyTile> createAllPossibleEmptyTiles(){
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap();

        for(int i=0; i<BoardUtils.NUM_TILES; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return Collections.unmodifiableMap(emptyTileMap);
    }

    public static Tile createTile(final int tileCoordinate, final Piece piece){
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : Empty_Tiles_Cache.get(tileCoordinate);
    }


    private Tile(final int tileCoordinate){

        this.tileCoordinate = tileCoordinate;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoodinate() {
        return this.tileCoordinate;
    }

    // Defines an open tile
    public static final class EmptyTile extends Tile{

        private EmptyTile(final int coordinate){
            super(coordinate);
        }

        @Override
        public String toString(){
            return "-";
        }

        @Override
        public boolean isTileOccupied(){

            return false;
        }

        @Override
        public Piece getPiece(){

            return null;
        }
    }

    // Defines a occupied tile
    public static final class OccupiedTile extends Tile{
        private final Piece pieceOnTile;

        private OccupiedTile(int tileCoordinate, final Piece pieceOnTile){
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString(){
            if(getPiece().getPieceAlliance().isBlack()){
               return getPiece().toString().toLowerCase();

            }else {
                return getPiece().toString();
            }
        }

        @Override
        public boolean isTileOccupied(){

            return true;
        }

        @Override
        public Piece getPiece(){

            return this.pieceOnTile;
        }
    }
}