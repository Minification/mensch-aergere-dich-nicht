package de.mariushubatschek.is;

import java.util.Comparator;
import java.util.List;

/**
 * Player that "jumps to safety" if they can
 */
public class DefensivePlayer extends Player {

    public DefensivePlayer(Board board, String name, int number) {
        super(board, name, number);
    }

    @Override
    public Move chooseMove(Game game, List<Move> availableMoves) {
        availableMoves.sort(Comparator.comparing(Move::getUrgencyScore, Comparator.reverseOrder()));
        return availableMoves.get(0);
    }

}
