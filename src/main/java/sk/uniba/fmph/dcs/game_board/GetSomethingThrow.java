package sk.uniba.fmph.dcs.game_board;

import sk.uniba.fmph.dcs.stone_age.Effect;

public class GetSomethingThrow implements EvaluateCivilisationCardImmediateEffect {
    private final Effect resource;
    private final CurrentThrow currentThrow;

    /**
     * @param resource
     *            that will be thrown for
     */
    public GetSomethingThrow(final Effect resource) {
        this.resource = resource;
        currentThrow = new CurrentThrow();
    }

    /**
     * @param player
     *            that throws
     * @param choice
     *            ignored
     *
     * @return true
     */
    @Override
    public boolean performEffect(final Player player, final Effect choice) {
        currentThrow.initiate(player, choice, 2);
        return true;
    }
}
