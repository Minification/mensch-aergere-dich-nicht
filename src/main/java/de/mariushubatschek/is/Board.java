package de.mariushubatschek.is;

import java.util.List;

public interface Board {

    void reset();

    void addPlayer(final Player player);

    AnalysisData analyzeBoard(final Player player, final int dice);

    void doMove(final Move move);

}
