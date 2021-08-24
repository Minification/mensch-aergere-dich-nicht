package de.mariushubatschek.is;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Player that randomly selects a move
 */
public class RandomPlayer extends Player {

    private static final Logger LOGGER = LogManager.getLogger(RandomPlayer.class);

    public RandomPlayer(Board board, String name, int number) {
        super(board, name, number);
    }

    @Override
    public Move chooseMove(Game game, List<Move> availableMoves) {
        final int choice = game.random.nextInt(availableMoves.size());
        return availableMoves.get(choice);
    }
}
