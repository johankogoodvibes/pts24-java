package sk.uniba.fmph.dcs.player_board;

import java.util.Map;

public final class PlayerBoardFactory {
    private PlayerBoardFactory() {
    }

    /**
     * Creates a new instance of {@link PlayerBoard} and {@link PlayerBoardGameBoardFacade}.
     *
     * @param startingFigures The number of figures the player starts with.
     * @param initialFood     The amount of food the player starts with.
     * @return A new instance of the {@link PlayerBoard} and {@link PlayerBoardGameBoardFacade} pair.
     */
    public static Map.Entry<PlayerBoard, PlayerBoardGameBoardFacade> createPlayerBoard(final int startingFigures,
                                                                                       final int initialFood) {
        PlayerCivilisationCards pcc = new PlayerCivilisationCards();
        PlayerFigures pf = new PlayerFigures(startingFigures);
        PlayerResourcesAndFood prf = new PlayerResourcesAndFood(initialFood);
        PlayerTools pt = new PlayerTools();
        TribeFedStatus tfs = new TribeFedStatus(prf, pf);

        PlayerBoard board = new PlayerBoard(pcc, pf, prf, pt, tfs);
        return Map.entry(board, new PlayerBoardGameBoardFacade(board));
    }

    /**
     * Creates a default player board setup with standard starting figures and initial food amount.
     *
     * @return A new instance of the {@link PlayerBoard} and {@link PlayerBoardGameBoardFacade} pair
     * initialized with default values for figures and food.
     */
    public static Map.Entry<PlayerBoard, PlayerBoardGameBoardFacade> createDefaultPlayerBoard() {
        return createPlayerBoard(PlayerFigures.DEFAULT_STARTING_FIGURES,
                PlayerResourcesAndFood.DEFAULT_INITIAL_FOOD_AMOUNT);
    }
}
