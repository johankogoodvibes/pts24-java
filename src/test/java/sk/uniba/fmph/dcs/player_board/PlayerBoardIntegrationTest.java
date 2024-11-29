package sk.uniba.fmph.dcs.player_board;

import org.junit.Test;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.EndOfGameEffect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlayerBoardIntegrationTest {
    @Test
    public void simulateActions() {
        Map.Entry<PlayerBoard, PlayerBoardGameBoardFacade> tmp = PlayerBoardFactory.createPlayerBoard(5, 0);
        PlayerBoard pb = tmp.getKey();
        PlayerBoardGameBoardFacade pbf = tmp.getValue();

        Effect[] resources = new Effect[] { Effect.FOOD, Effect.WOOD, Effect.CLAY, Effect.STONE, Effect.GOLD };
        pbf.giveEffect(resources);
        assert pb.getPlayerResourcesAndFood().hasResources(resources);

        assert !pbf.isTribeFed();
        assert pbf.hasFigures(5);

        assert !pbf.feedTribeIfEnoughFood();
        assert !pbf.feedTribe(List.of());
        assert pbf.doNotFeedThisTurn();
        assert pbf.feedTribe(List.of());
        assert !pbf.takeResources(new Effect[] { Effect.FOOD });

        int points = pb.addPoints(0);
        assert points == -10;

        pbf.newTurn();
        pbf.giveEffect(new Effect[] { Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.STONE, Effect.GOLD });
        assert !pbf.hasFigures(6);
        assert !pbf.isTribeFed();
        assert !pbf.feedTribe(Arrays.asList(Effect.FOOD, Effect.FOOD, Effect.CLAY, Effect.STONE, Effect.GOLD));
        assert !pbf.isTribeFed();

        assert pbf.feedTribe(Arrays.asList(Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.STONE, Effect.GOLD));
        assert pb.getPlayerResourcesAndFood()
                .hasResources(new Effect[] { Effect.WOOD, Effect.CLAY, Effect.STONE, Effect.GOLD });
        assert !pb.getPlayerResourcesAndFood().hasResources(new Effect[] { Effect.FOOD });
        assert pbf.isTribeFed();

        pbf.giveFigure();
        assert pbf.hasFigures(5); // nie 6?

        pbf.newTurn();
        assert pbf.hasFigures(6);
        assert !pbf.hasFigures(7);
        assert !pbf.takeFigures(7);
        assert pbf.hasFigures(6);
        assert pbf.takeFigures(6);

        pbf.giveEffect(new Effect[] { Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.FOOD });
        assert !pbf.feedTribeIfEnoughFood();
        assert !pbf.isTribeFed();
        pbf.giveEffect(new Effect[] { Effect.FOOD });
        assert pbf.feedTribeIfEnoughFood();
        assert pb.getPlayerResourcesAndFood()
                .hasResources(new Effect[] { Effect.WOOD, Effect.CLAY, Effect.STONE, Effect.GOLD });
        assert !pb.getPlayerResourcesAndFood().hasResources(new Effect[] { Effect.FOOD });

        points = pb.addPoints(0);
        assert points == -10;
        pb.addEndOfGamePoints();
        points = pb.addPoints(0);
        assert points == (-10 + 4);
        pb.addEndOfGamePoints();
        points = pb.addPoints(0);
        assert points == (-10 + 4);
    }

    @Test
    public void defaultConfigurationTest() {
        Map.Entry<PlayerBoard, PlayerBoardGameBoardFacade> tmp = PlayerBoardFactory.createDefaultPlayerBoard();
        PlayerBoard pb = tmp.getKey();
        PlayerBoardGameBoardFacade pbf = tmp.getValue();

        Effect[] twelveFoods = new Effect[12];
        Arrays.fill(twelveFoods, Effect.FOOD);
        assert pb.getPlayerResourcesAndFood().hasResources(twelveFoods);
        assert pb.getPlayerFigures().hasFigures(5);
        assert !pb.getPlayerFigures().hasFigures(6);

        assert !pbf.isTribeFed();
        assert pbf.takeFigures(5);
        assert !pbf.hasFigures(1);

        pbf.giveEffect(new Effect[] { Effect.WOOD });
        List<Effect> feed = Arrays.asList(Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.FOOD, Effect.WOOD);
        assert !pbf.feedTribe(feed);
        assert !pbf.isTribeFed();
        assert pbf.feedTribeIfEnoughFood();
        assert pbf.isTribeFed();

        assert pbf.hasSufficientTools(0);
        pb.getPlayerTools().addSingleUseTool(4);
        int points = pb.addPoints(0);
        assert points == 0;

        assert !pbf.hasSufficientTools(5);
        pb.getPlayerTools().addTool();
        assert pbf.hasSufficientTools(5);

        Optional<Integer> optInt = pbf.useTool(3);
        assert optInt.isPresent() && optInt.get() == 4;
        optInt = pbf.useTool(0);
        assert optInt.isPresent() && optInt.get() == 1;

        pb.getPlayerTools().addTool();
        assert !pbf.hasSufficientTools(2);
        optInt = pbf.useTool(0);
        assert optInt.isEmpty();
        assert pbf.hasSufficientTools(1);

        pbf.giveEndOfGameEffect(new EndOfGameEffect[] { EndOfGameEffect.WRITING, EndOfGameEffect.ART });
        pbf.giveEndOfGameEffect(new EndOfGameEffect[] { EndOfGameEffect.TOOL_MAKER });
        pbf.giveEndOfGameEffect(new EndOfGameEffect[] { EndOfGameEffect.SHAMAN, EndOfGameEffect.SHAMAN });

        pb.addEndOfGamePoints();
        points = pb.addPoints(0);
        assert points == 3 + 4 + 10;
    }
}
