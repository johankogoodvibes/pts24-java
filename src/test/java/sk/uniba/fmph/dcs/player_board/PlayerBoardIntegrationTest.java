package sk.uniba.fmph.dcs.player_board;

import org.junit.Test;
import sk.uniba.fmph.dcs.stone_age.Effect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class PlayerBoardIntegrationTest {
    @Test
    public void simulateActions() {

        class PlayerBoardFactory {
            public Map.Entry<PlayerBoard, PlayerBoardGameBoardFacade> createPlayerBoard() {
                PlayerBoard board = new PlayerBoard();
                return Map.entry(board, new PlayerBoardGameBoardFacade(board));
            }
        }

        PlayerBoardFactory factory = new PlayerBoardFactory();
        Map.Entry<PlayerBoard, PlayerBoardGameBoardFacade> tmp = factory.createPlayerBoard();

        PlayerBoard pb = tmp.getKey();
        PlayerBoardGameBoardFacade pbf = tmp.getValue();

        Effect[] resources = new Effect[]{Effect.FOOD, Effect.WOOD, Effect.CLAY, Effect.STONE, Effect.GOLD};
        pbf.giveEffect(resources);
        assert pb.getPlayerResourcesAndFood().hasResources(resources);

        assert !pbf.isTribeFed();
        assert pbf.hasFigures(5);

        assert !pbf.feedTribeIfEnoughFood();
        assert !pbf.feedTribe(List.of());
        assert pbf.doNotFeedThisTurn();
        assert pbf.feedTribe(List.of());
        assert !pbf.takeResources(new Effect[]{Effect.FOOD});

        int points = pb.addPoints(0);
        assert points == -10;

        pbf.newTurn();
        pbf.giveEffect(new Effect[]{Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.STONE, Effect.GOLD});
        assert !pbf.hasFigures(6);
        assert !pbf.isTribeFed();
        assert !pbf.feedTribe(Arrays.asList(Effect.FOOD, Effect.FOOD, Effect.CLAY, Effect.STONE, Effect.GOLD));
        assert !pbf.isTribeFed();

        assert pbf.feedTribe(Arrays.asList(Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.STONE, Effect.GOLD));
        assert pb.getPlayerResourcesAndFood().hasResources(new Effect[]{Effect.WOOD, Effect.CLAY, Effect.STONE, Effect.GOLD});
        assert !pb.getPlayerResourcesAndFood().hasResources(new Effect[]{Effect.FOOD});
        assert pbf.isTribeFed();

        pbf.giveFigure();
        assert pbf.hasFigures(5);   // nie 6?

        pbf.newTurn();
        assert pbf.hasFigures(6);
        assert !pbf.hasFigures(7);
        assert !pbf.takeFigures(7);
        assert pbf.hasFigures(6);
        assert pbf.takeFigures(6);

        pbf.giveEffect(new Effect[]{Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.FOOD});
        assert !pbf.feedTribeIfEnoughFood();
        assert !pbf.isTribeFed();
        pbf.giveEffect(new Effect[]{Effect.FOOD});
        assert pbf.feedTribeIfEnoughFood();
        assert pb.getPlayerResourcesAndFood().hasResources(new Effect[]{Effect.WOOD, Effect.CLAY, Effect.STONE, Effect.GOLD});
        assert !pb.getPlayerResourcesAndFood().hasResources(new Effect[]{Effect.FOOD});

        points = pb.addPoints(0);
        assert points == -10;
        pb.addEndOfGamePoints();
        points = pb.addPoints(0);
//        assert points == (-10 + 4);
    }
}
