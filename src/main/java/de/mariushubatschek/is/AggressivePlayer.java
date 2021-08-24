package de.mariushubatschek.is;

import java.util.Comparator;
import java.util.List;

/**
 * Player that chooses to beat other if they can
 */
public class AggressivePlayer extends Player {

    public AggressivePlayer(Board board, String name, int number) {
        super(board, name, number);
    }

    @Override
    public Move chooseMove(Game game, List<Move> availableMoves) {
        availableMoves.sort(Comparator.comparing(Move::isBeating, Comparator.reverseOrder()));
        return availableMoves.get(0);
    }

}
