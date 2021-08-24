package de.mariushubatschek.is;

import sim.engine.Stoppable;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GameTournament extends Game {

    private class PlayerData {
        public Stoppable stoppable;
        public int playerNumber;
    }

    private Map<Integer, PlayerData> playerStoppableMap = new HashMap<>();

    private int[] setup;

    private double[] weights;

    private int winner = -1;

    private int playerToWatch;

    public void setSetup(int[] setup) {
        this.setup = setup;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public GameTournament(long seed) {
        super(seed);
    }

    @Override
    public void start() {
        super.start();
        LocalPlayerBoard board = new LocalPlayerBoard(new MoveFactory(weights));
        board.setShouldPrint(false);

        List<Player> players = new ArrayList<>();
        Player thePlayerToWatch = getPlayerByType(board, playerToWatch, 1);
        players.add(thePlayerToWatch);

        for (int i = 0; i < setup.length; i++) {
            int playerType = setup[i];
            Player player = getPlayerByType(board, playerType, i + 2);
            players.add(player);
        }

        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerData playerData = new PlayerData();
            playerData.playerNumber = i+1;
            playerData.stoppable = schedule.scheduleRepeating(player, i+1, 1);
            playerStoppableMap.put(player.getNumber(), playerData);
        }
    }

    private Player getPlayerByType(final Board board, final int typeNumber, final int playerNumber) {
        Player playerToSchedule;
        switch (typeNumber) {
            case 1:
                playerToSchedule = new RandomPlayer(board, "Player " + playerNumber, playerNumber);
                break;
            case 2:
                playerToSchedule = new DefensivePlayer(board, "Player " + playerNumber, playerNumber);
                break;
            case 3:
                playerToSchedule = new AggressivePlayer(board, "Player " + playerNumber, playerNumber);
                break;
            case 4:
                playerToSchedule = new ScoreBasedPlayer(board, "Player " + playerNumber, playerNumber);
                break;
            default:
                throw new IllegalArgumentException("Unknown player type index: " + typeNumber);
        }
        return playerToSchedule;
    }

    public double runTournament(final int[] setup, final int playerToWatch) {
        int numberOfGames = 1000;

        //Weights determined by Training
        double[] weights = new double[] { 6.260195229659329d, 51.27404092469855d, -20.920479371913178d };

        ExecutorService executorService = Executors.newCachedThreadPool();
        GameTournament[] gameTournaments = new GameTournament[numberOfGames];
        CountDownLatch countDownLatch = new CountDownLatch(gameTournaments.length);
        for (int i = 0; i < gameTournaments.length; i++) {
            gameTournaments[i] = new GameTournament(System.currentTimeMillis() + i);
            gameTournaments[i].setWeights(weights);
            gameTournaments[i].playerToWatch = playerToWatch;

            /*List<Integer> clonedSetup = Arrays.stream(setup).boxed().collect(Collectors.toList());
            Collections.shuffle(clonedSetup);
            int[] clonedSetupArray = new int[clonedSetup.size()];
            for (int k = 0; k < clonedSetupArray.length; k++) {
                clonedSetupArray[i] = clonedSetup.get(i);
            }*/

            gameTournaments[i].setSetup(setup);
            int finalI = i;
            executorService.submit(() -> {
                gameTournaments[finalI].start();
                while (true) {
                    if (!gameTournaments[finalI].schedule.step(gameTournaments[finalI])) {
                        break;
                    }
                }
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int winCount = 0;

        for (GameTournament gameTournament : gameTournaments) {
            if (gameTournament.winner == 1) {// playerNumber 1 is the player we should watch
                winCount++;
            }
        }

        executorService.shutdown();

        return winCount / (double) numberOfGames;

    }

    @Override
    public void playerIsDone(Player player) {
        PlayerData playerData = playerStoppableMap.get(player.getNumber());
        playerData.stoppable.stop();
        if (winner == -1) {
            winner = player.getNumber();
        }
    }

}
