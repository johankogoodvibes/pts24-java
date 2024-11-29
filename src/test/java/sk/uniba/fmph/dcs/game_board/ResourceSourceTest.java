package sk.uniba.fmph.dcs.game_board;

import org.junit.Test;
import sk.uniba.fmph.dcs.player_board.*;
import sk.uniba.fmph.dcs.stone_age.ActionResult;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.HasAction;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;

import java.util.ArrayList;
import java.util.List;

public class ResourceSourceTest {
    @Test
    public void test_calculation() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            PlayerResourcesAndFood prf = new PlayerResourcesAndFood(0);
            PlayerFigures pf = new PlayerFigures(5);
            TribeFedStatus tfs = new TribeFedStatus(prf, pf);
            PlayerBoard pb = new PlayerBoard(new PlayerCivilisationCards(), pf, prf, new PlayerTools(), tfs);
            PlayerBoardGameBoardFacade PBGBF = new PlayerBoardGameBoardFacade(pb);

            pb.getPlayerResourcesAndFood()
                    .giveResources(new Effect[] { Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.FOOD });
            pb.getPlayerResourcesAndFood().giveResources(new Effect[] { Effect.CLAY, Effect.STONE, Effect.GOLD });
            if (i != 3) {
                pb.getPlayerTools().addTool();
                pb.getPlayerTools().addTool();
                pb.getPlayerTools().addTool();
            }
            Player p = new Player(new PlayerOrder(i + 1, 4), PBGBF);
            players.add(p);
        }
        var wood4 = new ResourceSource(Effect.WOOD, 4);
        assert wood4.placeFigures(players.get(0), 2);
        assert wood4.placeFigures(players.get(1), 2);
        assert wood4.placeFigures(players.get(2), 2);
        assert !wood4.placeFigures(players.get(3), 5);
        assert !wood4.placeFigures(players.get(3), 7);
        assert !wood4.placeFigures(players.get(0), 1);

        assert wood4.placeFigures(players.get(3), 1);

        assert wood4.makeAction(players.get(0), new Effect[0],
                new Effect[0]) == ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE;
        assert wood4.makeAction(players.get(0), new Effect[0], new Effect[0]) == ActionResult.FAILURE;
        assert wood4.makeAction(players.get(1), new Effect[0],
                new Effect[0]) == ActionResult.ACTION_DONE_WAIT_FOR_TOOL_USE;
        assert wood4.tryToMakeAction(players.get(0)) == HasAction.NO_ACTION_POSSIBLE;
        assert wood4.tryToMakeAction(players.get(3)) == HasAction.AUTOMATIC_ACTION_DONE;
        assert wood4.tryToMakeAction(players.get(2)) == HasAction.WAITING_FOR_PLAYER_ACTION;

        var wood3 = new ResourceSource(Effect.WOOD, 3);
        assert wood3.placeFigures(players.get(0), 4);
        assert wood3.placeFigures(players.get(1), 2);
        assert !wood3.placeFigures(players.get(2), 4);
        assert !wood3.placeFigures(players.get(2), 1);
        assert !wood3.placeFigures(players.get(3), 2);

        var wood2 = new ResourceSource(Effect.WOOD, 2);
        assert !wood2.placeFigures(players.get(0), 11);
        assert wood2.placeFigures(players.get(0), 5);
        assert !wood2.placeFigures(players.get(1), 2);

    }
}
