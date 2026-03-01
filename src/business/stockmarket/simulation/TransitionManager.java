package business.stockmarket.simulation;

import entities.Stock;
import shared.logging.Logger;

import java.util.Random;

public class TransitionManager
{
    private static final Random RANDOM = new Random();

    private static final Stock.State[] STATES = {
            Stock.State.STEADY,
            Stock.State.GROWING,
            Stock.State.DECLINING,
            Stock.State.HIGH_FLUCTUATING,
            Stock.State.RAPID_GROWTH,
            Stock.State.RAPID_DECLINE,
            Stock.State.RAPID_CRASH
    };

    private static final int[][] TRANSITIONS = {
            {50, 18, 18, 10, 2, 2, 0}, // STEADY
            {20, 50, 5, 12, 10, 2, 1}, // GROWING
            {20, 5, 50, 12, 10, 2, 1}, // DECLINING
            {30, 15, 15, 25, 7, 7, 1}, // HIGH_FLUCTUATING
            {30, 35, 0, 25, 8, 1, 1},  // RAPID_GROWTH
            {25, 3, 35, 25, 0, 10, 2}, // RAPID_DECLINE
            {35, 5, 30, 20, 0, 5, 5}   // RAPID_CRASH
    };

    private TransitionManager(){}

    public static Stock.State nextState(Stock.State fromState) {
        if (fromState == Stock.State.BANKRUPT) return Stock.State.RESET;
        if (fromState == Stock.State.RESET) return Stock.State.STEADY;

        int row = indexOf(fromState);
        double roll = RANDOM.nextDouble() * 100;
        int cumulative = 0;

        for (int col = 0; col < STATES.length; col++)
        {
            cumulative += TRANSITIONS[row][col];

            if (roll < cumulative){
                return STATES[col];
            }
        }
        return fromState;
    }

    private static int indexOf(Stock.State state) {
        for (int i = 0; i < STATES.length; i++)
        {
            if (STATES[i] == state){
                return i;
            }
        }
        Logger.getInstance().error("Invalid transition state: " + state);
        throw new IllegalArgumentException("Invalid transition state: " + state);
    }
}
