package de.mariushubatschek.is;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.engine.SimState;
import sim.engine.Steppable;

import java.util.List;
import java.util.Objects;

public abstract class Player implements Steppable {

    private static final Logger LOGGER = LogManager.getLogger(Player.class);

    private final Board board;

    private final String name;

    private final int number;

    public Player(final Board board, final String name, final int number) {
        this.board = board;
        this.name = name;
        this.number = number;
        board.addPlayer(this);
    }

    @Override
    public void step(SimState simState) {
        Game game = (Game) simState;

        LOGGER.trace("Stepping Player");
        LOGGER.trace("It is Player " + number + "'s turn.");

        Move move = null;

        do {
            final int eyes = game.rollDice();
            LOGGER.trace("Player " + number + " rolled dice eyes: " + eyes);

            AnalysisData analysisData = board.analyzeBoard(this, eyes);
            if (analysisData.isDone) {
                game.playerIsDone(this);
                break;
            }

            final List<Move> availableMoves = analysisData.availableMoves;

            LOGGER.trace("Player " + number + " has " + availableMoves.size() + " available moves");

            if (availableMoves.isEmpty()) {
                return;
            }

            LOGGER.trace("The available moves are: ");
            LOGGER.trace(availableMoves);

            move = chooseMove(game, availableMoves);

            board.doMove(move);

        } while (move.isStarting());

        LOGGER.trace("Done stepping Player");
    }

    public abstract Move chooseMove(final Game game, final List<Move> availableMoves);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Player{" +
                "board=" + board +
                ", name='" + name + '\'' +
                ", number=" + number +
                '}';
    }

    public int getNumber() {
        return number;
    }
}
