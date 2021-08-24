package de.mariushubatschek.is;

public class Move {

    private double[] weights = new double[3];

    private final boolean beating;

    private final boolean starting;

    private final Figurine figurine;

    private final int startPosition;

    private final int endPosition;

    private final Figurine figurineToBeat;

    private final int urgencyScore;

    public Move(final Figurine figurine, final int startPosition, final int endPosition, final boolean beating, final boolean starting, final Figurine figurineToBeat, final int urgencyScore) {
        this.figurine = figurine;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.beating = beating;
        this.starting = starting;
        this.figurineToBeat = figurineToBeat;
        this.urgencyScore = urgencyScore;
    }

    public boolean isBeating() {
        return beating;
    }

    public boolean isStarting() {
        return starting;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public Figurine getFigurine() {
        return figurine;
    }

    public Figurine getFigurineToBeat() {
        return figurineToBeat;
    }

    public int getUrgencyScore() {
        return urgencyScore;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    @Override
    public String toString() {
        return "Move{" +
                "beating=" + beating +
                ", starting=" + starting +
                ", figurine=" + figurine +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", figurineToBeat=" + figurineToBeat +
                ", urgencyScore=" + urgencyScore +
                '}';
    }

    public double getScore() {
        int beatingValue = beating ? 1 : 0;
        int startingValue = starting ? 1 : 0;
        return weights[0] * beatingValue + weights[1] * startingValue + weights[2] * urgencyScore;
    }

}
