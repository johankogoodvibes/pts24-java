package sk.uniba.fmph.dcs.game_board;

import org.junit.Test;
import sk.uniba.fmph.dcs.player_board.*;
import sk.uniba.fmph.dcs.stone_age.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class CivilisationCardPlaceTest {
    @Test
    public void test_calculation() {

        CivilisationCard[] cards = new CivilisationCard[] {
                new CivilisationCard(new ImmediateEffect[] { ImmediateEffect.CARD },
                        new EndOfGameEffect[] { EndOfGameEffect.SHAMAN }),
                new CivilisationCard(new ImmediateEffect[] { ImmediateEffect.THROW_WOOD },
                        new EndOfGameEffect[] { EndOfGameEffect.TOOL_MAKER }),
                new CivilisationCard(new ImmediateEffect[] { ImmediateEffect.ALL_PLAYERS_TAKE_REWARD },
                        new EndOfGameEffect[] { EndOfGameEffect.MEDICINE }),
                new CivilisationCard(new ImmediateEffect[] { ImmediateEffect.ALL_PLAYERS_TAKE_REWARD },
                        new EndOfGameEffect[] { EndOfGameEffect.MEDICINE }),
                new CivilisationCard(
                        new ImmediateEffect[] { ImmediateEffect.WOOD, ImmediateEffect.WOOD, ImmediateEffect.WOOD },
                        new EndOfGameEffect[] { EndOfGameEffect.MUSIC }),
                new CivilisationCard(
                        new ImmediateEffect[] { ImmediateEffect.ARBITRARY_RESOURCE,
                                ImmediateEffect.ARBITRARY_RESOURCE, },
                        new EndOfGameEffect[] { EndOfGameEffect.TRANSPORT }),
                new CivilisationCard(
                        new ImmediateEffect[] { ImmediateEffect.ARBITRARY_RESOURCE,
                                ImmediateEffect.ARBITRARY_RESOURCE, },
                        new EndOfGameEffect[] { EndOfGameEffect.TRANSPORT }),
                new CivilisationCard(new ImmediateEffect[] { ImmediateEffect.POINT, ImmediateEffect.POINT },
                        new EndOfGameEffect[] { EndOfGameEffect.BUILDER }), };

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            PlayerResourcesAndFood prf = new PlayerResourcesAndFood(0);
            PlayerFigures pf = new PlayerFigures(10);
            TribeFedStatus tfs = new TribeFedStatus(prf, pf);
            PlayerBoard pb = new PlayerBoard(new PlayerCivilisationCards(), pf, prf, new PlayerTools(), tfs);
            PlayerBoardGameBoardFacade PBGBF = new PlayerBoardGameBoardFacade(pb);

            Player p = new Player(new PlayerOrder(i + 1, 4), PBGBF);
            p.playerBoard().giveEffect(new Effect[] { Effect.WOOD, Effect.WOOD, Effect.WOOD, Effect.WOOD, Effect.WOOD,
                    Effect.WOOD, Effect.WOOD, Effect.WOOD, Effect.WOOD });
            players.add(p);
        }

        CivilizationCardDeck deck = new CivilizationCardDeck(cards);
        RewardMenu menu = mock(RewardMenu.class);
        InterfaceGamePhaseController controller = mock(InterfaceGamePhaseController.class);
        CivilizationCardPlace place1 = new CivilizationCardPlace(1, deck, null, menu, controller);
        CivilizationCardPlace place2 = new CivilizationCardPlace(2, deck, place1, menu, controller);

        assert !place2.placeFigures(players.getFirst(), 2);
        assert place2.placeFigures(players.getFirst(), 1);
        assert !place2.placeFigures(players.getFirst(), 1);
        assert !place2.placeFigures(players.get(1), 1);

        assert place1.placeFigures(players.get(0), 1);
        assert place1.makeAction(players.get(0), new Effect[] { Effect.WOOD },
                new Effect[] {}) == ActionResult.ACTION_DONE; // I will draw + 1 card

        assert !place2.skipAction(players.get(1));
        assert place2.skipAction(players.get(0));

        assert place2.newTurn();
        assert place1.newTurn();

        assert place1.placeFigures(players.getFirst(), 1);
        assert place2.placeFigures(players.getFirst(), 1);

        assert place1.makeAction(players.getFirst(), new Effect[] { Effect.WOOD },
                new Effect[] {}) == ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE; // I will draw throw_wood
        assert place2.makeAction(players.getFirst(), new Effect[] { Effect.WOOD },
                new Effect[] {}) == ActionResult.FAILURE;
        assert place2.makeAction(players.getFirst(), new Effect[] { Effect.WOOD, Effect.WOOD },
                new Effect[] {}) == ActionResult.ACTION_DONE_ALL_PLAYERS_TAKE_A_REWARD; // I will draw all take reward

        assert place2.newTurn();

        assert place1.placeFigures(players.getFirst(), 1);
        assert place2.placeFigures(players.getFirst(), 1);

        assert place1.makeAction(players.getFirst(), new Effect[] { Effect.WOOD },
                new Effect[] {}) == ActionResult.ACTION_DONE; // I will draw take fixed
        assert place2.makeAction(players.getFirst(), new Effect[] { Effect.WOOD, Effect.WOOD },
                new Effect[] {}) == ActionResult.FAILURE; // I will draw arbitrary resource

        assert place2.newTurn();

        assert place1.placeFigures(players.getFirst(), 1);
        assert place2.placeFigures(players.getFirst(), 1);

        assert place1.makeAction(players.getFirst(), new Effect[] { Effect.WOOD },
                new Effect[] { Effect.STONE, Effect.STONE }) == ActionResult.ACTION_DONE; // I will draw arbitrary
                                                                                          // resource
        assert place2.makeAction(players.getFirst(), new Effect[] { Effect.WOOD, Effect.WOOD },
                new Effect[] {}) == ActionResult.ACTION_DONE; // I will draw take point

        assert !place2.newTurn();
    }
}
