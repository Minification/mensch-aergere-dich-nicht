package de.mariushubatschek.is;

import sim.engine.SimState;

public abstract class Game extends SimState {

    public Game(long seed) {
        super(seed);
    }

    public int rollDice() {
        return random.nextInt(6) + 1;
    }

    public abstract void playerIsDone(final Player player);

}
