package de.mariushubatschek.is;

import java.util.*;

public class TournamentRunner {

    /**
     * Runs a kind of 1 vs 3 tournament: 1 player type vs the other types in any constellation
     */
    private void run() {
        GameTournament gameTournament = new GameTournament(System.currentTimeMillis());
        Map<Integer, Set<ArrayHolder>> setupMap = generateSetups();
        for (Map.Entry<Integer, Set<ArrayHolder>> entries : setupMap.entrySet()) {
            Set<ArrayHolder> setupList = entries.getValue();
            int playerType = entries.getKey();
            for (ArrayHolder setup : setupList) {
                double winPercentage = gameTournament.runTournament(setup.array, playerType);
                System.out.println(indexToName(playerType) + " wins against (" + indexToName(setup.array[0]) + ", " + indexToName(setup.array[1]) + ", " + indexToName(setup.array[2]) + ") about " + winPercentage + "% of the time.");
            }
        }
    }

    /**
     * Generate a list of tuples in a certain shape resembling the cartesian product of:
     * In each tuple, the first item is unique
     * @return
     */
    private Map<Integer, Set<ArrayHolder>> generateSetups() {

        Map<Integer, Set<ArrayHolder>> map = new HashMap<>();

        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            list.add(i);
            map.put(i, new HashSet<>());
        }

        for (int i = 1; i < 5; i++) {
            //Idea. Use a modified version of the cartesian product:
            //Potential items are in a list. In each iteration,
            // 1. remove one item (call it i), yielding list',
            // 2. build { (i, k, j, i) | (k, j, i) in list' x list' x list' },
            // 3. add i back into list.
            list.remove(Integer.valueOf(i));

            for (int k = 0; k < list.size(); k++) {
                for (int j = 0; j < list.size(); j++) {
                    for (int l = 0; l < list.size(); l++) {
                        int[] setup = new int[3];
                        Set<ArrayHolder> setupList = map.get(i);
                        setup[0] = i;
                        setup[0] = list.get(k);
                        setup[1] = list.get(j);
                        setup[2] = list.get(l);
                        Arrays.sort(setup);
                        ArrayHolder arrayHolder = new ArrayHolder();
                        arrayHolder.array = setup;
                        setupList.add(arrayHolder);
                    }
                }
            }

            list.add(i);
        }

        return map;
    }

    private String indexToName(final int index) {
        switch (index) {
            case 1:
                return "RandomPlayer";
            case 2:
                return "DefensivePlayer";
            case 3:
                return "AggressivePlayer";
            case 4:
                return "ScoreBasedPlayer";
            default:
                throw new IllegalArgumentException("Out of range: " + index + ". Allowed (1-4)");
        }
    }

    public static void main(String[] args) {
        new TournamentRunner().run();
    }

    private class ArrayHolder {

        public int[] array;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArrayHolder that = (ArrayHolder) o;
            return Arrays.equals(array, that.array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }

}
