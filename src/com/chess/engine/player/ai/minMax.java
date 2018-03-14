package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public class minMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;

    public minMax() {
        this.boardEvaluator = null;
    }

    @Override
    public Move execute(Board board, int depth) {
        return null;
    }
}
