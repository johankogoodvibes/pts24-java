package sk.uniba.fmph.dcs.game_board;

import sk.uniba.fmph.dcs.stone_age.Effect;
import sk.uniba.fmph.dcs.stone_age.InterfaceToolUse;

import java.util.Arrays;

public final class CurrentThrow implements InterfaceToolUse {
    private int throwResult;
    private Effect throwsFor;
    private Player player;
    private boolean finished;
    private int delim;

    private static final int FOOD = 2;
    private static final int WOOD = 3;
    private static final int CLAY = 4;
    private static final int STONE = 5;
    private static final int GOLD = 6;

    public void initiate(final Player player, final Effect effect, final int dices) {
        finished = false;
        var t = Throw.hod(dices);
        int c = 0;
        for (var i : t) {
            c += i;
        }
        throwResult = c;
        throwsFor = effect;
        this.player = player;
        finished = false;

        if (throwsFor == Effect.FOOD) {
            delim = FOOD;
        } else if (throwsFor == Effect.WOOD) {
            delim = WOOD;
        } else if (throwsFor == Effect.STONE) {
            delim = STONE;
        } else if (throwsFor == Effect.CLAY) {
            delim = CLAY;
        } else {
            delim = GOLD;
        }
    }

    @Override
    public boolean useTool(final int idx) {
        if (finished) {
            return false;
        }
        var i = player.playerBoard().useTool(idx);
        if (i.isEmpty()) {
            return false;
        }
        throwResult += i.get();
        return true;
    }

    @Override
    public boolean canUseTools() {
        if (finished) {
            return false;
        }
        return player.playerBoard().hasSufficientTools(1);
    }

    @Override
    public boolean finishUsingTools() {
        if (finished) {
            return false;
        }
        finished = true;
        int count = throwResult / delim;
        Effect[] e = new Effect[count];
        Arrays.fill(e, throwsFor);
        player.playerBoard().giveEffect(e);
        return true;
    }

}
