package com.chess.GUI;


import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveStatus;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;



public class Table {

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 800);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(600, 550);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(20, 20);

    private static String defaultPieceImagesPath = "art/alpha_pieces/";
    private Color lightTileColor = Color.decode("#FFFACD");
    private Color darkTileColor = Color.decode("#593E1A");
    private Color highlightColor = Color.decode("#6CF2FC");

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private BoardDirection boardDirection;
    private boolean highlightLegalMoves;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;


    public Table() {
        this.gameFrame = new JFrame("Brian's Chess");
        this.gameFrame.setLayout(new BorderLayout());

        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);


        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.boardPanel = new BoardPanel();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;

        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PNG file");

        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open PGN file");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit Game");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoard = new JMenuItem("Flip Board");

        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoard);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highligh Legal Moves", false);

        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();

            }
        });
        preferencesMenu.add(legalMoveHighlighterCheckbox);
        return preferencesMenu;
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);

            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board chessBoard) {
            removeAll();

            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(chessBoard);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    private static class MoveLog {
        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move){
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index){
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());

            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);


            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){

                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;

                    } else if(isLeftMouseButton(e)){

                        if(sourceTile == null){
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();

                            if(humanMovedPiece == null){
                                sourceTile = null;
                            }

                        }else{
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard,
                                                                          sourceTile.getTileCoodinate(),
                                                                          destinationTile.getTileCoodinate());

                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.drawBoard(chessBoard);

                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        private void highlightLegals(final Board board){
            if(highlightLegalMoves){

                for(final Move move : pieceLegalMoves(board)){

                    if(move.getDestinationCoordinate() == this.tileId){

                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/alpha_pieces/green_dot.png")))));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTilePieceIcon(final Board board){
            this.removeAll();

            if(board.getTile(this.tileId).isTileOccupied()){

                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath +
                            board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1) +
                            board.getTile(this.tileId).getPiece().toString() + ".png"));

                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.FORTH_RANK[this.tileId] ||
                    BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);

            } else if (BoardUtils.SEVENTH_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }
    }

}



