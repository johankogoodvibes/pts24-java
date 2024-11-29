package sk.uniba.fmph.dcs.game_board;

import org.junit.Test;
import sk.uniba.fmph.dcs.player_board.*;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.PlayerOrder;

public class CurrentThrowTest {
    @Test
    public void test_calculation() {
        var t = new CurrentThrow();

        PlayerResourcesAndFood prf = new PlayerResourcesAndFood(0);
        PlayerFigures pf = new PlayerFigures(5);
        TribeFedStatus tfs = new TribeFedStatus(prf, pf);
        PlayerBoard pb = new PlayerBoard(new PlayerCivilisationCards(), pf, prf, new PlayerTools(), tfs);
        PlayerBoardGameBoardFacade PBGBF = new PlayerBoardGameBoardFacade(pb);

        pb.getPlayerTools().addTool();
        pb.getPlayerTools().addTool();
        pb.getPlayerTools().addTool();

        Player p = new Player(new PlayerOrder(1, 4), PBGBF);

        t.initiate(p, Effect.WOOD, 2);

        assert t.useTool(0);
        assert t.useTool(1);
        assert t.useTool(2);

        assert !t.useTool(0);
        assert !t.canUseTools();
        pb.getPlayerTools().addTool();
        assert t.finishUsingTools();
        assert !t.useTool(3);
        assert !t.canUseTools();
        assert !t.finishUsingTools();
        assert pb.getPlayerResourcesAndFood().hasResources(new Effect[] { Effect.WOOD, Effect.WOOD });
        pb.newTurn();
        t.initiate(p, Effect.STONE, 3);
        assert t.useTool(0);
        assert t.useTool(1);
        assert t.useTool(2);

        assert t.finishUsingTools();
        assert !t.useTool(3);
        assert pb.getPlayerResourcesAndFood().hasResources(new Effect[] { Effect.WOOD, Effect.WOOD, Effect.STONE });
    }
}
