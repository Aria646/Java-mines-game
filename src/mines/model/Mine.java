package mines.model;

import java.util.Objects;
import java.util.Random;

/**
 * Represents a purchasable mine in the game.
 *
 * @param gemType       The type of gem this mine produces.
 * @param victoryPoints The number of victory points purchasing this mine awards.
 * @param cost          The cost in gems of this mine.
 */
public record Mine(GemType gemType, int victoryPoints, GemPool cost) {
    /**
     * Generate a new mine from a source of randomness. The mine has a randomly selected GemType.
     * The mine has a random number of victory points between 0 and 3 (inclusive). The mine has
     * a randomly selected total cost between X + 2,and 2X + 2 (inclusive) where X is equal to
     * the number of Victory Points selected. The final cost may be distributed any way
     * amongst the GemTypes.
     * <p>
     * This method is deterministic and will produce the same Mine if provided an identically
     * seeded pseudorandom source in the same state. This method can produce Mines of each type,
     * and of each valid Victory Point amount,and of each valid total cost if given a sufficient
     * source of randomness. No particular distribution of the costsamongst gem types is
     * guaranteed.
     *
     * @param random the source of randomness to use when generating the Mine.
     * @return a new random Mine.
     */
    public static Mine generate(Random random) {
        GemType gemType = GemType.values()[random.nextInt(GemType.values().length)];
        int vp = random.nextInt(4); // 0-3
        int minCost = vp + 2;
        int maxCost = 2 * vp + 2;
        int totalCost = minCost + random.nextInt(maxCost - minCost + 1);

        int[] costs = new int[GemType.values().length];
        for (int i = 0; i < totalCost; i++) {
            int idx = random.nextInt(costs.length);
            costs[idx]++;
        }
        GemPool cost = new GemPool(
                costs[0], costs[1], costs[2], costs[3], costs[4]
        );
        return new Mine(gemType, vp, cost);
    }

    /**
     * Returns a string that represent this mine in the form: "V|TYPE COST", where V is it's
     * number of victory points awarded, TYPE is the GemType this Mine produces, COST is the
     * string representation of the cost of this mine.
     * <p>
     * The string will always be 24 characters wide, with however many " " characters are
     * necessary to reach that length added between the TYPE and the COST.
     *
     * @return a string that represents this mine.
     */
    @Override
    public String toString() {
        String base = victoryPoints + "|" + gemType + cost;
        if (base.length() < 24) {
            int spaces = 24 - base.length();
            int insertPos = (victoryPoints + "|" + gemType).length();
            return base.substring(0, insertPos) + " ".repeat(spaces) + base.substring(insertPos);
        }
        return base;
    }

    /**
     * Returns a hash code value for this object. The value is derived from the hash code of
     * each of the record components.
     *
     * @return a hash code value for this object
     */
    @Override
    public final int hashCode() {
        return Objects.hash(gemType, victoryPoints, cost);
    }

    /**
     * Indicates whether some other object is "equal to" this one. The objects are equal if the
     * other object is of the same class and if all the record components are equal. Reference
     * components are compared withObjects::equals(Object,Object); primitive components are
     * compared with '=='.
     *
     * @param o the reference object with which to compare.
     * @return true if this object is the same as the o argument; false otherwise.
     */
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mine mine = (Mine) o;
        return victoryPoints == mine.victoryPoints && gemType == mine.gemType
                && cost.equals(mine.cost);
    }

    /**
     * Returns the value of the gemType record component.
     *
     * @return the value of the gemType record component.
     */
    public GemType gemType() {
        return gemType;
    }

    /**
     * Returns the value of the victoryPoints record component.
     *
     * @return the value of the victoryPoints record component.
     */
    public int victoryPoints() {
        return victoryPoints;
    }

    /**
     * Returns the value of the cost record component.
     *
     * @return the value of the cost record component.
     */
    public GemPool cost() {
        return cost;
    }
}
