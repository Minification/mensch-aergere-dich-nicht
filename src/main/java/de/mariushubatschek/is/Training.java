package de.mariushubatschek.is;

import sim.engine.IterativeRepeat;
import sim.engine.Stoppable;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

public class Training extends Game {

    private double[] gameWeights;

    private Player winner;

    private Player playerToWatch;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public Training(long seed) {
        super(seed);
    }

    public Player getWinner() {
        return winner;
    }

    @Override
    public void playerIsDone(Player player) {
        //System.out.println("Player " + player + " is done");
        playerStoppableMap.get(player).stop();
        if (winner == null) {
            winner = player;
            //System.out.println("The winner is " + player + ".");
        }
    }

    private Map<Player, Stoppable> playerStoppableMap = new HashMap<>();

    private double calculateScore(double[] weights) {
        Training[] trainings = new Training[100];
        Thread[] threads = new Thread[trainings.length];
        CountDownLatch countDownLatch = new CountDownLatch(threads.length);
        for (int i = 0; i < trainings.length; i++) {
            trainings[i] = new Training(System.currentTimeMillis() + i);
            trainings[i].gameWeights = weights;
            trainings[i].start();
            int finalI = i;
            executorService.submit(() -> {
                while (true) {
                    if (!trainings[finalI].schedule.step(trainings[finalI])) {
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

        /*while (true) {
            boolean shouldStop = true;
            for (Training training : trainings) {
                if (training.schedule.step(training)) {
                    shouldStop = false;
                }
            }
            if (shouldStop) {
                break;
            }
        }*/
        int score = 0;
        for (Training training : trainings) {
            training.finish();
            Player winner = training.getWinner();
            if (winner.equals(training.playerToWatch)) {
                score++;
            }
        }
        //System.out.println("The player won " + score + " out of 100 times.");
        return score / 100.0;
    }

    private double[] scaleVector(double[] vector, double scalar) {
        double[] resultVector = vector.clone();
        for (int i = 0; i < vector.length; i++) {
            resultVector[i] *= scalar;
        }
        return resultVector;
    }

    private double [] addVectors(double[] vector1, double[] vector2) {
        double[] resultVector = vector1.clone();
        for (int i = 0; i < vector1.length; i++) {
            resultVector[i] += vector2[i];
        }
        return resultVector;
    }

    private double[] randomVector(final int dimension, final SecureRandom secureRandom) {
        double[] vector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            vector[i] = secureRandom.nextGaussian();
        }
        return vector;
    }

    private double[] optimize() {
        SecureRandom secureRandom = new SecureRandom();
        int maxIterations = 5000;
        int populationSize = 100;
        double tau = 1./3;
        double[] x = scaleVector(randomVector(3, secureRandom), 5);
        double d = Math.sqrt(x.length);
        double sigma = (Math.random() + Math.nextUp(0)) * 20;
        for(int i = 0; i < maxIterations; i++) {
            System.out.println("Starting iteration " + (i+1) + " out of " + maxIterations);
            List<Individuum> individuumList = new ArrayList<>();
            for (int k = 0; k < populationSize; k++) {
                double psi = tau * secureRandom.nextGaussian();
                double[] z_k = randomVector(x.length, secureRandom);
                double[] x_k = addVectors(x, scaleVector(z_k, Math.exp(psi)));
                double sigma_k = sigma * Math.pow(Math.exp(psi), 1/d);
                Individuum individuum = new Individuum();
                individuum.x = x_k;
                individuum.sigma = sigma_k;
                individuum.score = calculateScore(x_k);

                individuumList.add(individuum);
            }
            Individuum bestIndividuum = individuumList.stream().max(Comparator.comparingDouble(individuum -> individuum.score)).get();
            x = bestIndividuum.x;
            sigma = bestIndividuum.sigma;
            System.out.println("Stopping iteration " + (i+1) + ". Winner has score " + bestIndividuum.score);
        }
        executorService.shutdown();
        return x;
    }

    @Override
    public void start() {
        super.start();
        MoveFactory moveFactory = new MoveFactory(gameWeights);
        LocalPlayerBoard localPlayerBoard = new LocalPlayerBoard(moveFactory);
        localPlayerBoard.setShouldPrint(false);
        List<Integer> chooseList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            chooseList.add(i);
        }

        Player player = new ScoreBasedPlayer(localPlayerBoard, "ScoreBasedPlayer (watched)", 1);
        playerToWatch = player;
        Collections.shuffle(chooseList);
        List<Player> thePlayers = new ArrayList<>();
        thePlayers.add(player);

        for (int i = 0; i < 3; i++) {
            int chosen = chooseList.remove(0);
            Player newPlayer;
            switch (chosen) {
                case 0:
                    newPlayer = new ScoreBasedPlayer(localPlayerBoard, "ScoreBasedPlayer " + (i+2), i + 2);
                    break;
                case 1:
                    newPlayer = new RandomPlayer(localPlayerBoard, "RandomPlayer", i + 2);
                    break;
                case 2:
                    newPlayer = new AggressivePlayer(localPlayerBoard, "AggressivePlayer", i + 2);
                    break;
                case 3:
                    newPlayer = new DefensivePlayer(localPlayerBoard, "DefensivePlayer", i + 2);
                    break;
                default:
                    throw new RuntimeException("Out of Bounds");
            }
            thePlayers.add(newPlayer);
        }

        for (int i = 1; i < 5; i++) {
            Player player1 = thePlayers.get(i-1);
            IterativeRepeat iterativeRepeat = schedule.scheduleRepeating(player1, i, 1);
            playerStoppableMap.put(player1, iterativeRepeat);
        }
    }

    public static void main(String[] args) {
        Training training = new Training(System.currentTimeMillis());
        double[] optimizedWeights = training.optimize();
        System.out.println("The optimized weights are: " + Arrays.toString(optimizedWeights));
    }

    private static class Individuum {
        public double score;
        public double[] x;
        public double sigma;
    }

}
