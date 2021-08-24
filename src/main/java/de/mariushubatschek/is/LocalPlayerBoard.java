package de.mariushubatschek.is;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class LocalPlayerBoard implements Board {

    private static final Logger LOGGER = LogManager.getLogger(LocalPlayerBoard.class);

    private static final int PLAYER_OFFSET = 10;

    private static final int PLAYER_COUNT = 4;

    private static final int BOARD_SIZE = 40;

    private static final int BOARD_SIZE_FULL = BOARD_SIZE + PLAYER_COUNT;

    private Figurine[][] board = new Figurine[PLAYER_COUNT][BOARD_SIZE + PLAYER_COUNT];

    private final List<Player> players = new ArrayList<>();

    private final Map<Player, List<Figurine>> playerFigurinesMap = new HashMap<>();

    private final Map<Integer, Coordinate> coordinateMap = new HashMap<>();

    private final Map<Integer, Color> playerColorMap = new HashMap<>();

    private final List<Move> moveHistory = new ArrayList<>();

    private boolean shouldPrint = false;

    private MoveFactory moveFactory;

    public LocalPlayerBoard(final MoveFactory moveFactory) {
        this.moveFactory = moveFactory;
        reset();
    }

    public void setShouldPrint(boolean shouldPrint) {
        this.shouldPrint = shouldPrint;
    }

    private void setupCoordinateMap() {
        coordinateMap.clear();
        // right start point up to middle right
        coordinateMap.put(0, new Coordinate(10, 6));
        coordinateMap.put(1, new Coordinate(9, 6));
        coordinateMap.put(2, new Coordinate(8, 6));
        coordinateMap.put(3, new Coordinate(7, 6));
        coordinateMap.put(4, new Coordinate(6, 6));
        // right middle down to bottom righ
        coordinateMap.put(5, new Coordinate(6, 7));
        coordinateMap.put(6, new Coordinate(6, 8));
        coordinateMap.put(7, new Coordinate(6, 9));
        coordinateMap.put(8, new Coordinate(6, 10));
        // bottom middle
        coordinateMap.put(9, new Coordinate(5, 10));
        // bottom start
        coordinateMap.put(10, new Coordinate(4, 10));
        //left up to middle
        coordinateMap.put(11, new Coordinate(4, 9));
        coordinateMap.put(12, new Coordinate(4, 8));
        coordinateMap.put(13, new Coordinate(4, 7));
        coordinateMap.put(14, new Coordinate(4, 6));
        //left middle to left
        coordinateMap.put(15, new Coordinate(3, 6));
        coordinateMap.put(16, new Coordinate(2, 6));
        coordinateMap.put(17, new Coordinate(1, 6));
        coordinateMap.put(18, new Coordinate(0, 6));
        //left
        coordinateMap.put(19, new Coordinate(0, 5));
        //left start
        coordinateMap.put(20, new Coordinate(0, 4));
        //left to left middle
        coordinateMap.put(21, new Coordinate(1, 4));
        coordinateMap.put(22, new Coordinate(2, 4));
        coordinateMap.put(23, new Coordinate(3, 4));
        coordinateMap.put(24, new Coordinate(4, 4));
        //left to left middle
        coordinateMap.put(25, new Coordinate(4, 3));
        coordinateMap.put(26, new Coordinate(4, 2));
        coordinateMap.put(27, new Coordinate(4, 1));
        coordinateMap.put(28, new Coordinate(4, 0));
        //top
        coordinateMap.put(29, new Coordinate(5, 0));
        //top start
        coordinateMap.put(30, new Coordinate(6, 0));
        //top right to middle right
        coordinateMap.put(31, new Coordinate(6, 1));
        coordinateMap.put(32, new Coordinate(6, 2));
        coordinateMap.put(33, new Coordinate(6, 3));
        coordinateMap.put(34, new Coordinate(6, 4));
        //middle right to right
        coordinateMap.put(35, new Coordinate(7, 4));
        coordinateMap.put(36, new Coordinate(8, 4));
        coordinateMap.put(37, new Coordinate(9, 4));
        coordinateMap.put(38, new Coordinate(10, 4));
        //right
        coordinateMap.put(39, new Coordinate(10, 5));
        //right house
        coordinateMap.put(40, new Coordinate(9, 5));
        coordinateMap.put(41, new Coordinate(8, 5));
        coordinateMap.put(42, new Coordinate(7, 5));
        coordinateMap.put(43, new Coordinate(6, 5));
        //bottom house
        coordinateMap.put(44, new Coordinate(5, 9));
        coordinateMap.put(45, new Coordinate(5, 8));
        coordinateMap.put(46, new Coordinate(5, 7));
        coordinateMap.put(47, new Coordinate(5, 6));
        //left house
        coordinateMap.put(48, new Coordinate(1, 5));
        coordinateMap.put(49, new Coordinate(2, 5));
        coordinateMap.put(50, new Coordinate(3, 5));
        coordinateMap.put(51, new Coordinate(4, 5));
        //top house
        coordinateMap.put(52, new Coordinate(5, 1));
        coordinateMap.put(53, new Coordinate(5, 2));
        coordinateMap.put(54, new Coordinate(5, 3));
        coordinateMap.put(55, new Coordinate(5, 4));
    }

    private void setupColorMap() {
        playerColorMap.clear();
        playerColorMap.put(0, Color.RED);
        playerColorMap.put(1, Color.BLUE);
        playerColorMap.put(2, Color.YELLOW);
        playerColorMap.put(3, Color.GREEN);
    }

    @Override
    public void reset() {
        board = new Figurine[PLAYER_COUNT][BOARD_SIZE + PLAYER_COUNT];
        players.clear();
        playerFigurinesMap.clear();
        moveHistory.clear();
        setupCoordinateMap();
        setupColorMap();
    }

    private List<Figurine> getFigurines(final Player player) {
        return playerFigurinesMap.get(player);
    }

    private boolean isDone(final Player player) {
        for (final Figurine figurine : getFigurines(player)) {
            if (!isFigurineInHome(figurine)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addPlayer(final Player player) {
        players.add(player);

        //Each player has 4 figurines
        List<Figurine> figurines = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            figurines.add(new Figurine(player, i));
        }
        playerFigurinesMap.put(player, figurines);
    }

    @Override
    public AnalysisData analyzeBoard(final Player player, final int dice) {
        List<Move> availableMoves = new ArrayList<>();

        //Player has nothing to move if they have already won
        if (isDone(player)) {
            LOGGER.debug("Player " + player + " is done");
            AnalysisData analysisData = new AnalysisData();
            analysisData.isDone = true;
            analysisData.availableMoves = availableMoves;
            return analysisData;
        }

        for (final Figurine figurine : getFigurines(player)) {

            //If the player has a 6 and a figurine is available to be put on the field
            if (dice == 6 && isFigurineAvailable(figurine)) {

                //LOGGER.debug("6 was rolled and figurine is available.");

                //If the starting position is empty
                final int startPosition = 0;
                //final int endPosition = startPosition + dice;
                if (!isOccupied(player, startPosition)) {
                    LOGGER.debug("The start position is not occupied");
                    Move move = moveFactory.create(figurine, startPosition, startPosition, false, true, null, 0);
                    availableMoves.add(move);
                    //We *must* start if we can
                    continue;
                }
            } else {
                //LOGGER.debug("Dice " + dice + " figurine available: " + isFigurineAvailable(figurine));
            }

            if (isFigurineOnField(figurine)) {
                LOGGER.debug("The figurine is on the board");
                final int figurinePosition = getFigurinePosition(figurine);
                final int figurineEndPosition = figurinePosition + dice;

                if (figurineEndPosition >= BOARD_SIZE_FULL) {
                    LOGGER.debug("end position " + figurineEndPosition + " out of bounds");
                    continue;
                }

                //Jumping over pieces is not allowed
                //TODO: Check for home
                /*if (isObstructed(player, figurinePosition, figurineEndPosition)) {
                    LOGGER.debug("path from " + figurinePosition + " to end position " + figurineEndPosition + " exclusive is obstructed");
                    continue;
                }*/

                final int urgencyScore = urgencyScore(player, figurineEndPosition);

                if (isOccupied(player, figurineEndPosition)) {
                    LOGGER.debug("end position " + figurineEndPosition + " is occupied");
                    Player otherPlayer = getPlayerAt(player, figurineEndPosition);
                    //We can't beat ourselves
                    if (player.equals(otherPlayer)) {
                        LOGGER.debug("we can't beat ourselves at " + figurinePosition);
                        continue;
                    }
                    //There is another player to beat
                    LOGGER.debug("there is a beatable player at " + figurineEndPosition);
                    Figurine figurineToBeat = get(otherPlayer, translatePlayerPositions(player, otherPlayer, figurineEndPosition));
                    Move move = moveFactory.create(figurine, figurinePosition, figurineEndPosition, true, false, figurineToBeat, urgencyScore);
                    availableMoves.add(move);
                }

                if (!isOccupied(player, figurineEndPosition)) {
                    LOGGER.debug("end position " + figurineEndPosition + " is free, i.e. move allowed");
                    Move move = moveFactory.create(figurine, figurinePosition, figurineEndPosition, false, false, null, urgencyScore);
                    availableMoves.add(move);
                }

            }

        }
        AnalysisData analysisData = new AnalysisData();
        analysisData.isDone = false;
        analysisData.availableMoves = availableMoves;
        return analysisData;
    }

    private boolean isFigurineAvailable(final Figurine figurine) {
        return getFigurinePosition(figurine) < 0;
    }

    private boolean isFigurineOnField(final Figurine figurine) {
        return getFigurinePosition(figurine) > -1;
    }

    private boolean isFigurineInHome(final Figurine figurine) {
        return getFigurinePosition(figurine) > getPlayerHomeBorder(figurine.getPlayer());
    }

    private int getPlayerHomeBorder(final Player player) {
        return BOARD_SIZE - 1;
    }

    private boolean canBeat(final Player player, final int figurineEndPosition) {
        return !get(player, figurineEndPosition).getPlayer().equals(player);
    }

    /**
     * Calculate how many figurines could potentially beat us if we stayed put. This is done via the following:
     * Check if there is a different player on the board behind the current player, within 6 steps from the end position.
     * If yes, increase score by 1.
     *
     * @param player
     * @param endPosition
     * @return
     */
    private int urgencyScore(final Player player, final int endPosition) {
        int urgencyScore = 0;
        for (int intermediateLocalPosition = endPosition - 6; intermediateLocalPosition < endPosition; intermediateLocalPosition++) {
            if (isOccupied(player, intermediateLocalPosition)) {
                final Player otherPlayer = getPlayerAt(player, intermediateLocalPosition);
                //We can't beat ourselves
                if (player.equals(otherPlayer)) {
                    continue;
                }

                int otherFigurinePosition = translatePlayerPositions(player, otherPlayer, intermediateLocalPosition);
                int otherFigurineEndPosition = translatePlayerPositions(player, otherPlayer, endPosition);
                //The other figurine would step into its home trying to beat us
                if (otherFigurineEndPosition - otherFigurinePosition < 0) {
                    continue;
                }

                urgencyScore++;
            }
        }
        return urgencyScore;
    }

    /**
     * Check if there is a figurine on the board between the startPosition and the endPosition, both exclusive
     * @param player the player to check for
     * @param startPosition current figurine position
     * @param endPosition end figurine position
     * @return the obvious
     */
    private boolean isObstructed(final Player player, final int startPosition, final int endPosition) {
        for (int intermediateLocalPosition = startPosition + 1; intermediateLocalPosition < endPosition; intermediateLocalPosition++) {
            if (isOccupied(player, intermediateLocalPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the figurine object in the player's local array
     *
     * @param figurine The figurine
     * @return Index of the figurine object in the player's local array
     */
    private int getFigurinePosition(final Figurine figurine) {
        int playerIndex = getPlayerIndex(figurine.getPlayer());
        for (int i = 0; i < board[playerIndex].length; i++) {
            if (figurine.equals(board[playerIndex][i])) {
                return i;
            }
        }
        return -1;
    }

    private boolean isOccupied(final Player player, final int localPosition) {
        if (localPosition > getPlayerHomeBorder(player)) {
            return isOccupiedInHome(player, localPosition);
        }
        return getPlayerAt(player, localPosition) != null;
    }

    private boolean isOccupiedInHome(final Player player, final int localPosition) {
        return get(player, localPosition) != null;
    }

    /**
     * Returns the player at the position, or null if there is none
     * @param player
     * @param localPosition
     * @return
     */
    private Player getPlayerAt(final Player player, final int localPosition) {
        //We are looking in our home, so only check ourselves
        if (localPosition > getPlayerHomeBorder(player)) {
            if (get(player, localPosition) != null) {
                return player;
            }
            return null;
        }
        //We are one the board, so check other players
        for (final Player otherPlayer : players) {
            if (get(otherPlayer, translatePlayerPositions(player, otherPlayer, localPosition)) != null) {
                return otherPlayer;
            }
        }
        return null;
    }

    private int getPlayerIndex(final Player player) {
        return players.indexOf(player);
    }

    private Figurine get(final Player player, final int localPosition) {
        return board[getPlayerIndex(player)][localPosition];
    }

    private void set(final Player player, final int localPosition, final Figurine figurine) {
        board[getPlayerIndex(player)][localPosition] = figurine;
    }

    private int startPositionFor(final Player player) {
        return players.indexOf(player) * PLAYER_OFFSET;
    }

    private int toGlobalPosition(final Player player, final int localPosition) {
        final int playerStartPosition = startPositionFor(player);
        return Math.floorMod(localPosition + playerStartPosition, BOARD_SIZE);
    }

    private int globalToPlayerPosition(final int globalPosition, final Player player) {
        final int playerStartPosition = startPositionFor(player);
        return Math.floorMod(globalPosition - playerStartPosition, BOARD_SIZE);
    }

    private int translatePlayerPositions(final Player fromPlayer, final Player toPlayer, int fromPosition) {
        return globalToPlayerPosition(toGlobalPosition(fromPlayer, fromPosition), toPlayer);
    }

    @Override
    public void doMove(final Move move) {

        final Player player = move.getFigurine().getPlayer();

        if (move.isBeating()) {
            Figurine figurineToBeat = move.getFigurineToBeat();
            figurineToBeat.beat();
            set(figurineToBeat.getPlayer(), translatePlayerPositions(player, figurineToBeat.getPlayer(), move.getEndPosition()), null);
            LOGGER.debug("Beating at " + globalToPlayerPosition(move.getEndPosition(), player));
        }

        set(player, move.getStartPosition(), null);
        set(player, move.getEndPosition(), move.getFigurine());

        LOGGER.debug("Moving from " + globalToPlayerPosition(move.getStartPosition(), player) + " to " + globalToPlayerPosition(move.getEndPosition(), player));

        if (shouldPrint) {
            snapshot();
        }
        consistencyCheck();
        moveHistory.add(move);
    }

    private void consistencyCheck() {
        for (final Player player : players) {
            int figurineCount = 0;
            for (int i = 0; i < BOARD_SIZE_FULL; i++) {
                if (get(player, i) != null) {
                    figurineCount++;
                }
            }
            if (figurineCount > PLAYER_COUNT) {
                throw new IllegalStateException("More than 4 figures for a player encountered per player");
            }
        }
    }

    private void snapshot() {
        BufferedImage image = new BufferedImage(11*32, 11*32, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,0,11*32+1,11*32+1);
        for (final Player player : players) {
            Color color = playerColorMap.get(getPlayerIndex(player));
            graphics.setColor(color);
            for (final Figurine figurine : getFigurines(player)) {
                final int localPosition = getFigurinePosition(figurine);
                if (!isFigurineOnField(figurine)) {
                    continue;
                }
                if (isFigurineInHome(figurine)) {
                    final Coordinate coordinate = coordinateMap.get(localPosition + PLAYER_COUNT * getPlayerIndex(player));
                    graphics.fillOval(coordinate.getX() * 32, coordinate.getY() * 32, 1*32+1, 1*32+1);
                } else {
                    final int globalPosition = toGlobalPosition(player, localPosition);
                    final Coordinate coordinate = coordinateMap.get(globalPosition);
                    graphics.fillOval(coordinate.getX() * 32, coordinate.getY() * 32, 1*32+1, 1*32+1);
                }
            }
        }
        for (Map.Entry<Integer, Coordinate> entry : coordinateMap.entrySet()) {
            graphics.setColor(Color.BLACK);
            if (entry.getKey() >= BOARD_SIZE) {
                graphics.setColor(Color.LIGHT_GRAY);
            }
            Coordinate coordinate = entry.getValue();
            graphics.drawOval(coordinate.getX() * 32, coordinate.getY() * 32, 1*32+1, 1*32+1);
        }
        try {
            File file = new File(System.getProperty("user.dir")+File.separator + "images" + File.separator + "game_" + moveHistory.size() + ".png");
            file.mkdirs();
            file.createNewFile();
            LOGGER.info(file.getAbsolutePath());
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
