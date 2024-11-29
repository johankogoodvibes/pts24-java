package sk.uniba.fmph.dcs.game_board;

import org.json.JSONObject;
import sk.uniba.fmph.dcs.stone_age.CivilisationCard;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public final class CivilizationCardDeck {
    private final List<CivilisationCard> cards;

    public CivilizationCardDeck(final CivilisationCard[] cards) {
        this.cards = new ArrayList<>(List.of(cards));
    }

    public Optional<CivilisationCard> getTop() {
        if (cards.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(cards.removeFirst());
    }

    public String state() {
        Map<String, String> state = Map.of("cards", cards.toString());
        return new JSONObject(state).toString();
    }
}
