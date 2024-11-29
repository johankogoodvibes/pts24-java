package sk.uniba.fmph.dcs.game_board;

import org.json.JSONObject;
import sk.uniba.fmph.dcs.stone_age.ActionResult;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.HasAction;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;

public class ResourceSource implements InterfaceFigureLocationInternal {
    private String name;
    private final Effect resource;
    private int maxFigures;
    private int maxFigureColors;
    private List<PlayerOrder> figures;
    private CurrentThrow currentThrow;
    private static final int MAX_FIGURES_DEFAULT = 7;
    private static final int THREE = 3;
    private static final int TWO = 2;

    public ResourceSource(final Effect resource, final int playerCount) {
        this.resource = resource;
        if (resource == Effect.WOOD) {
            maxFigures = MAX_FIGURES_DEFAULT;
            name = "Forrest";
        } else if (resource == Effect.CLAY) {
            maxFigures = MAX_FIGURES_DEFAULT;
            name = "Clay mound";
        } else if (resource == Effect.GOLD) {
            maxFigures = MAX_FIGURES_DEFAULT;
            name = "River";
        } else if (resource == Effect.STONE) {
            maxFigures = MAX_FIGURES_DEFAULT;
            name = "Quarry";
        } else if (resource == Effect.FOOD) {
            maxFigures = Integer.MAX_VALUE;
            name = "Hunting grounds";
        }
        if (playerCount == TWO) {
            maxFigureColors = 1;
        } else if (playerCount == THREE) {
            maxFigureColors = TWO;
        } else {
            maxFigureColors = Integer.MAX_VALUE;
        }
        currentThrow = new CurrentThrow();
        figures = new ArrayList<>();
    }

    /**
     * adds figure to location.
     *
     * @param player
     *            player that places figures
     * @param figureCount
     *            number of figures
     *
     * @return true if action possible
     */
    @Override
    public boolean placeFigures(final Player player, final int figureCount) {
        if (tryToPlaceFigures(player, figureCount) == HasAction.NO_ACTION_POSSIBLE) {
            return false;
        }
        for (int i = 0; i < figureCount; i++) {
            figures.add(player.playerOrder());
        }
        return true;
    }

    /**
     * @param player
     *            player that places figures
     * @param count
     *            number of figures
     *
     * @return HasAction.WAITING_FOR_PLAYER_ACTION if action possible, HasAction.NO_ACTION_POSSIBLE otherwise
     */
    @Override
    public HasAction tryToPlaceFigures(final Player player, final int count) {
        if (figures.contains(player.playerOrder())) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        Set<PlayerOrder> different = new HashSet<>(figures);
        if (different.size() == maxFigureColors) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        if (figures.size() + count > maxFigures) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        return HasAction.WAITING_FOR_PLAYER_ACTION;
    }

    /**
     * @param player
     *            player to give resource to
     * @param inputResources
     *            ignored
     * @param outputResources
     *            ignored
     *
     * @return FAILURE if not possible
     */
    @Override
    public ActionResult makeAction(final Player player, final Effect[] inputResources, final Effect[] outputResources) {
        int count = 0;
        for (var i : figures) {
            if (i == player.playerOrder()) {
                count++;
            }
        }
        if (!skipAction(player)) {
            return ActionResult.FAILURE;
        }
        currentThrow.initiate(player, resource, count);
        return ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE;
    }

    /**
     * deny player from doing action later.
     *
     * @param player
     *            to skip action with
     *
     * @return true if player had action
     */
    @Override
    public boolean skipAction(final Player player) {
        if (!figures.contains(player.playerOrder())) {
            return false;
        }
        figures.removeIf(o -> o == player.playerOrder());
        return true;
    }

    /**
     * try to make action, player still needs to assign tools.
     *
     * @param player
     *            player to do it with
     *
     * @return WAITING_FOR_PLAYER_ACTION if player needs to use some tools
     */
    @Override
    public HasAction tryToMakeAction(final Player player) {
        int count = 0;
        for (var i : figures) {
            if (i == player.playerOrder()) {
                count++;
            }
        }
        if (!skipAction(player)) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        currentThrow.initiate(player, resource, count);
        if (currentThrow.canUseTools()) {
            return HasAction.WAITING_FOR_PLAYER_ACTION;
        } else {
            currentThrow.finishUsingTools();
            return HasAction.AUTOMATIC_ACTION_DONE;
        }
    }

    /**
     * clears itself.
     *
     * @return true
     */
    @Override
    public boolean newTurn() {
        figures.clear();
        return true;
    }

    /**
     * @return current state of object
     */
    @Override
    public String state() {
        Map<String, String> state = Map.of("name", name, "maxFigures", Integer.toString(maxFigures), "maxFigureColors",
                Integer.toString(maxFigureColors), "figures", figures.toString(), "resource", resource.name());
        return new JSONObject(state).toString();
    }
}
