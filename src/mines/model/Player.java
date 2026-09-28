package mines.model;

/**
 * Represents a Player in a game of Fine Minery.
 */
public class Player {
    /**
     * player's number
     */
    private final int playerNumber;
    /**
     * mines in the player
     */
    private GemPool mines;
    /**
     * gems in the hand of player
     */
    private GemPool gems;
    /**
     * victory points
     */
    private int victoryPoints;

    /**
     * Initialise a new player with no gems, mines, or victory points.
     *
     * @param playerNumber the player's number in play order (0-3).
     */
    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
        this.victoryPoints = 0;
        this.gems = new GemPool();
        this.mines = new GemPool();
    }

    /**
     * Attempt to have this player purchase a mine. Costs are paid in the following way: Each
     * mine that the player already owns reduces the cost of buying the new mine by 1 of the
     * associated gem type. After this discount is applied the remaining cost is removed from
     * the player's current gems. If the player cannot afford the mine no gems are removed.
     * Otherwise, add this mine to the player's mine collection, and add the mine's victory
     * points to this player.
     *
     * @param mine the mine the player will purchase.
     * @return false if the mine could not be purchased, true otherwise.
     */
    public boolean purchase(Mine mine) {
        if (mine == null) {
            return false;
        }
        GemPool originalCost = mine.cost();
        GemPool discount = new GemPool();
        for (GemType type : GemType.values()) {
            int mineCount = this.mines.get(type);
            int costAmount = originalCost.get(type);
            discount = discount.add(type, Math.min(mineCount, costAmount));
        }

        // Calculate the discounted cost
        // original cost minus discount, allowing negative numbers but considering them as
        // 0 in the future
        GemPool discountedCost = originalCost.subtract(discount);
        // Verify if the player can pay the discounted cost
        // all gemstone quantities ≥ discounted cost
        boolean canAfford = true;
        for (GemType type : GemType.values()) {
            int required = Math.max(discountedCost.get(type), 0);
            if (this.gems.get(type) < required) {
                canAfford = false;
                break;
            }
        }

        if (canAfford) {
            // Deducting gems (only deducting non-negative costs)
            GemPool costToRemove = new GemPool();
            for (GemType type : GemType.values()) {
                costToRemove = costToRemove.add(type, Math.max(discountedCost.get(type), 0));
            }
            this.gems = this.gems.remove(costToRemove);

            // Add mines to player assets
            GemType productionType = mine.gemType();
            this.mines = this.mines.add(productionType, 1);

            this.victoryPoints += mine.victoryPoints();
            return true;
        }
        return false;
    }

    /**
     * Attempt to add a pool of gems to the player's current gem collection. A player may add
     * 3 gems each of separate types, or 2 gems of the same type. If the collection is of any
     * other form this method will fail and return false.
     *
     * @param toCollect the new gems this player will collect.
     * @return whether the gems were successfully collected.
     */
    public boolean addGems(GemPool toCollect) {
        if (toCollect == null || toCollect.isEmpty()) {
            return false;
        }
        int totalGems = 0;
        int nonZeroTypes = 0;
        int maxSingleTypeAmount = 0;
        for (GemType type : GemType.values()) {
            int amount = toCollect.get(type);
            if (amount > 0) {
                totalGems += amount;
                nonZeroTypes++;
                maxSingleTypeAmount = Math.max(maxSingleTypeAmount, amount);
            } else if (amount < 0) {
                return false;
            }
        }
        boolean isValidCollection =
                (totalGems == 2 && nonZeroTypes == 1 && maxSingleTypeAmount == 2)
                        || (totalGems == 3 && nonZeroTypes == 3 && maxSingleTypeAmount == 1);

        if (isValidCollection) {
            this.gems = this.gems.add(toCollect);
            return true;
        }
        return false;
    }

    /**
     * Get this player's number (in play order).
     *
     * @return player's number
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Get this player's currently collected gems.
     *
     * @return this player's gems
     */
    public GemPool getGems() {
        return gems;
    }

    /**
     * Get this player's currently owned mine
     *
     * @return this player's currently owned mine
     */
    public GemPool getMines() {
        return mines;
    }

    /**
     * Get the number of victory points this player has collected.
     *
     * @return the number of victory points this player has collected.
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Return a string to represent this player. It should be in the form:
     * "QUARTZ [M] (G), RUBY [M] (G), SAPPHIRE [M] (G), EMERALD [M] (G), ONYX [M] (G)"
     * where M is the number of mines this player has of the associated type. and G is the number
     * of Gems this player has of that type.
     *
     * @return a string to represent this player.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendPlayerResource(sb, GemType.QUARTZ);
        sb.append(", ");
        appendPlayerResource(sb, GemType.RUBY);
        sb.append(", ");
        appendPlayerResource(sb, GemType.SAPPHIRE);
        sb.append(", ");
        appendPlayerResource(sb, GemType.EMERALD);
        sb.append(", ");
        appendPlayerResource(sb, GemType.ONYX);
        return sb.toString();
    }

    /**
     * helper function for toString method
     *
     * @param sb   string builder
     * @param type gem type
     */
    private void appendPlayerResource(StringBuilder sb, GemType type) {
        int mineCount = this.mines.get(type);
        int gemCount = this.gems.get(type);
        sb.append(type.name()).append(" [").append(mineCount).append("] (")
                .append(gemCount).append(")");
    }

}
