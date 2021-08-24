package de.mariushubatschek.is;

public class MoveFactory {

    private final double[] weights;

    public MoveFactory(double[] weights) {
        this.weights = weights;
    }

    public Move create(final Figurine figurine, final int startPosition, final int endPosition, final boolean beating, final boolean starting, final Figurine figurineToBeat, final int urgencyScore) {
        Move move = new Move(figurine, startPosition, endPosition, beating, starting, figurineToBeat, urgencyScore);
        move.setWeights(weights);
        return move;
    }

}
