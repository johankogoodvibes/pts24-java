package sk.uniba.fmph.dcs.game_board;

import org.json.JSONObject;
import sk.uniba.fmph.dcs.stone_age.InterfaceGetState;
import sk.uniba.fmph.dcs.stone_age.Location;
import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.CivilisationCard;
import sk.uniba.fmph.dcs.stone_age.InterfaceGamePhaseController;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;

public class GameBoard implements InterfaceGetState {
    private final Map<Location, InterfaceFigureLocationInternal> locations;
    private static final int BUILDING_PILES = 4;
    private static final int COST_3 = 3;
    private static final int COST_4 = 4;

    public GameBoard(final Collection<Player> players, final Building[] buildings, final CivilisationCard[] cards,
            final InterfaceGamePhaseController controller) {
        ToolMakerHutsFields fields = new ToolMakerHutsFields(players.size());
        locations = new HashMap<>();
        locations.put(Location.HUT, new PlaceOnHutAdaptor(fields));
        locations.put(Location.FIELD, new PlaceOnFieldsAdaptor(fields));
        locations.put(Location.TOOL_MAKER, new PlaceOnToolMakerAdaptor(fields));
        locations.put(Location.CLAY_MOUND, new ResourceSource(Effect.CLAY, players.size()));
        locations.put(Location.FOREST, new ResourceSource(Effect.WOOD, players.size()));
        locations.put(Location.QUARRY, new ResourceSource(Effect.STONE, players.size()));
        locations.put(Location.RIVER, new ResourceSource(Effect.GOLD, players.size()));

        ArrayList<Location> buildingTiles = new ArrayList<>();
        buildingTiles.add(Location.BUILDING_TILE1);
        buildingTiles.add(Location.BUILDING_TILE2);
        buildingTiles.add(Location.BUILDING_TILE3);
        buildingTiles.add(Location.BUILDING_TILE4);
        for (int i = 0; i < BUILDING_PILES; i++) {
            locations.put(buildingTiles.get(i), new BuildingTile(buildings[i]));
        }
        var deck = new CivilizationCardDeck(cards);
        var menu = new RewardMenu((List<Player>) players);
        var place1 = new CivilizationCardPlace(1, deck, null, menu, controller);
        var place2 = new CivilizationCardPlace(2, deck, place1, menu, controller);
        var place3 = new CivilizationCardPlace(COST_3, deck, place2, menu, controller);
        var place4 = new CivilizationCardPlace(COST_4, deck, place3, menu, controller);
        locations.put(Location.CIVILISATION_CARD1, place1);
        locations.put(Location.CIVILISATION_CARD2, place2);
        locations.put(Location.CIVILISATION_CARD3, place3);
        locations.put(Location.CIVILISATION_CARD4, place4);
    }

    /**
     * @return state combined from everything on the game board
     */
    @Override
    public String state() {
        Map<Location, String> states = new HashMap<>();

        for (var x : locations.keySet()) {
            states.put(x, locations.get(x).state());
        }

        var ret = new JSONObject(states);
        return ret.toString();
    }
}
