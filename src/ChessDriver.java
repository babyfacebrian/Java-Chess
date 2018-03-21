import com.chess.engine.board.Board;
import com.chess.GUI.Table;

public class ChessDriver {

    public static void main(String[] args){
        Board chess = Board.createStandardBoard();

        System.out.println(chess);

        Table.get().show();
    }


}
