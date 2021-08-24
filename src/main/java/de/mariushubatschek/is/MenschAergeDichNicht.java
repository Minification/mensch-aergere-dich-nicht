package de.mariushubatschek.is;

import sim.engine.SimState;

public class MenschAergeDichNicht extends Game {

    public MenschAergeDichNicht(long seed) {
        super(seed);
    }

    @Override
    public void playerIsDone(Player player) {

    }

    @Override
    public void start() {
        super.start();
        MoveFactory moveFactory = new MoveFactory(new double[] {0,0,0});
        LocalPlayerBoard board = new LocalPlayerBoard(moveFactory);
        Player player1 = new RandomPlayer(board, "Player1", 0);
        Player player2 = new RandomPlayer(board, "Player2", 1);
        Player player3 = new RandomPlayer(board, "Player3", 2);
        Player player4 = new RandomPlayer(board, "Player4", 3);
        schedule.scheduleRepeating(player1, 1, 1);
        schedule.scheduleRepeating(player2, 2, 1);
        schedule.scheduleRepeating(player3, 3, 1);
        schedule.scheduleRepeating(player4, 4, 1);
    }

    public static void main(String[] args) {
        SimState state = new MenschAergeDichNicht(System.currentTimeMillis());
        state.start();
        do
            if (!state.schedule.step(state)) break;
        while(state.schedule.getSteps() < 5000000);
        state.finish();
        System.exit(0);

        //doLoop(MenschAergeDichNicht.class, args);
        //System.exit(0);
    }

}
