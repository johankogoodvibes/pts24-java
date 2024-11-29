package sk.uniba.fmph.dcs.game_board;

import org.json.JSONObject;
import sk.uniba.fmph.dcs.stone_age.CivilisationCard;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;
import sk.uniba.fmph.dcs.stone_age.InterfaceGamePhaseController;
import sk.uniba.fmph.dcs.stone_age.HasAction;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.ImmediateEffect;
import sk.uniba.fmph.dcs.stone_age.ActionResult;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;

public class CivilizationCardPlace implements InterfaceFigureLocationInternal {
    private final int requiredResources;
    private Optional<CivilisationCard> card;
    private final ArrayList<PlayerOrder> figures;
    private final CivilizationCardDeck deck;
    private final InterfaceGamePhaseController controller;
    private final RewardMenu menu;
    private final CivilizationCardPlace next;

    public CivilizationCardPlace(final int resources, final CivilizationCardDeck deck, final CivilizationCardPlace next,
                                 final RewardMenu menu, final InterfaceGamePhaseController controller) {
        card = deck.getTop();
        this.next = next;
        this.deck = deck;
        requiredResources = resources;
        figures = new ArrayList<>();
        this.controller = controller;
        this.menu = menu;
    }

    /**
     * @param player      player that places figures
     * @param figureCount number of figures
     * @return true if figures were placed
     */
    @Override
    public boolean placeFigures(final Player player, final int figureCount) {
        if (tryToPlaceFigures(player, figureCount) == HasAction.NO_ACTION_POSSIBLE) {
            return false;
        }
        figures.add(player.playerOrder());
        return true;
    }

    /**
     * can place figures?
     *
     * @param player to place figures
     * @param count  number of figures
     * @return NO_ACTION_POSSIBLE if not possible
     */
    @Override
    public HasAction tryToPlaceFigures(final Player player, final int count) {
        if (count != 1) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        if (!figures.isEmpty()) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        return HasAction.WAITING_FOR_PLAYER_ACTION;
    }

    private ActionResult realyBuyCard(final Player player, final Effect[] desiredResources) {
        var real = card.get();
        player.playerBoard().giveEndOfGameEffect(real.endOfGameEffect());
        List<Effect> fixed = new ArrayList<>();
        Effect hod = null;
        int cards = 0;
        int arbitrary = 0;
        int points = 0;
        int allTakeReward = 0;
        for (var e : real.immediateEffect()) {
            if (e == ImmediateEffect.THROW_STONE) {
                hod = Effect.STONE;
            } else if (e == ImmediateEffect.THROW_WOOD) {
                hod = Effect.WOOD;
            } else if (e == ImmediateEffect.THROW_CLAY) {
                hod = Effect.CLAY;
            } else if (e == ImmediateEffect.THROW_GOLD) {
                hod = Effect.GOLD;
            } else if (e == ImmediateEffect.POINT) {
                points++;
            } else if (e == ImmediateEffect.CARD) {
                cards++;
            } else if (e == ImmediateEffect.ARBITRARY_RESOURCE) {
                arbitrary++;
            } else if (e == ImmediateEffect.WOOD) {
                fixed.add(Effect.WOOD);
            } else if (e == ImmediateEffect.FOOD) {
                fixed.add(Effect.FOOD);
            } else if (e == ImmediateEffect.CLAY) {
                fixed.add(Effect.CLAY);
            } else if (e == ImmediateEffect.GOLD) {
                fixed.add(Effect.GOLD);
            } else if (e == ImmediateEffect.STONE) {
                fixed.add(Effect.STONE);
            } else if (e == ImmediateEffect.ALL_PLAYERS_TAKE_REWARD) {
                allTakeReward++;
            }
        }
        card = Optional.empty();
        if (!fixed.isEmpty()) {
            new GetSomethingFixed(fixed).performEffect(player, Effect.WOOD);
            return ActionResult.ACTION_DONE;
        } else if (hod != null) {
            new GetSomethingThrow(hod).performEffect(player, Effect.WOOD);
            return ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE;
        } else if (cards != 0) {
            new GetCard(deck).performEffect(player, Effect.WOOD);
            return ActionResult.ACTION_DONE;
        } else if (points != 0) {
            player.playerBoard().addPoints(points);
            return ActionResult.ACTION_DONE;
        } else if (arbitrary != 0) {
            if (desiredResources == null) {
                return ActionResult.FAILURE;
            }
            if (desiredResources.length != arbitrary) {
                return ActionResult.FAILURE;
            }
            var eval = new GetChoice(arbitrary);
            for (int i = 0; i < arbitrary; i++) {
                eval.performEffect(player, desiredResources[i]);
            }
            return ActionResult.ACTION_DONE;
        } else if (allTakeReward != 0) {
            new AllPlayersTakeReward(menu, controller);
            return ActionResult.ACTION_DONE_ALL_PLAYERS_TAKE_A_REWARD;
        }
        return ActionResult.FAILURE;
    }

    /**
     * @param player          player that wants to buy card
     * @param inputResources  resources I want to pay with
     * @param outputResources ignored unless i get multiple resource choice
     * @return based on action needed to be completed
     */
    @Override
    public ActionResult makeAction(final Player player, final Effect[] inputResources, final Effect[] outputResources) {
        if (inputResources.length != requiredResources) {
            return ActionResult.FAILURE;
        }
        if (!player.playerBoard().takeResources(inputResources)) {
            return ActionResult.FAILURE;
        }
        if (!skipAction(player)) {
            return ActionResult.FAILURE;
        }
        return realyBuyCard(player, outputResources);
    }

    /**
     * deny player to resolve action later.
     *
     * @param player that wants to skip
     * @return true if player can resolve action
     */
    @Override
    public boolean skipAction(final Player player) {
        if (!figures.contains(player.playerOrder())) {
            return false;
        }
        figures.clear();
        return true;
    }

    /**
     * @param player that makes automatic action
     * @return
     */
    @Override
    public HasAction tryToMakeAction(final Player player) {
        if (!skipAction(player)) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        boolean ok = true;
        for (int i = 0; i < requiredResources; i++) {
            if (player.playerBoard().takeResources(new Effect[]{Effect.WOOD})) {
                continue;
            } else if (player.playerBoard().takeResources(new Effect[]{Effect.CLAY})) {
                continue;
            } else if (player.playerBoard().takeResources(new Effect[]{Effect.STONE})) {
                continue;
            } else if (player.playerBoard().takeResources(new Effect[]{Effect.GOLD})) {
                continue;
            }
            ok = false;
        }
        if (!ok) {
            return HasAction.NO_ACTION_POSSIBLE;
        }
        var res = realyBuyCard(player, null);
        if (res == ActionResult.ACTION_DONE) {
            return HasAction.AUTOMATIC_ACTION_DONE;
        } else if (res == ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE) {
            return HasAction.WAITING_FOR_PLAYER_ACTION;
        } else if (res == ActionResult.ACTION_DONE_ALL_PLAYERS_TAKE_A_REWARD) {
            return HasAction.WAITING_FOR_PLAYER_ACTION;
        } else {
            return HasAction.AUTOMATIC_ACTION_DONE;
        }
    }

    /**
     * passes card at the end of the turn.
     *
     * @param card to pass
     * @return true if passed
     */
    public boolean passCard(final CivilisationCard card) {
        if (this.card.isPresent()) {
            return false;
        }
        this.card = Optional.of(card);
        return true;
    }

    /**
     * @return is new turn possible
     */
    @Override
    public boolean newTurn() {
        boolean pass = false;
        boolean res = true;
        if (next != null) {
            if (card.isPresent()) {
                pass = next.passCard(card.get());
            }
            res = next.newTurn();
        }
        if (pass || card.isEmpty()) {
            card = deck.getTop();
        }
        return card.isPresent() && res;
    }

    /**
     * @return curent state
     */
    @Override
    public String state() {
        Map<String, String> state = Map.of("requiredResources", String.valueOf(requiredResources), "card",
                card.toString(), "figures", figures.toString(), "deck", deck.state());
        return new JSONObject(state).toString();
    }
}
