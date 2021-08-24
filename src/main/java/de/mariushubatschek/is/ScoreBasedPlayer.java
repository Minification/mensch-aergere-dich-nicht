package de.mariushubatschek.is;

import java.util.Comparator;
import java.util.List;

/**
 * Player who evaluates each move and chooses the best move
 */
public class ScoreBasedPlayer extends Player {

    public ScoreBasedPlayer(Board board, String name, int number) {
        super(board, name, number);
    }

    @Override
    public Move chooseMove(Game game, List<Move> availableMoves) {
        availableMoves.sort(Comparator.comparing(Move::getScore, Comparator.reverseOrder()));
        return availableMoves.get(0);
    }

}
