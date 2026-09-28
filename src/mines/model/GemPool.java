package mines.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A collection of gems.
 */
public class GemPool {
    /**
     * The gem pool.
     */
    private final Map<GemType, Integer> map;

    /**
     * gemType
     */
    public GemPool() {
        map = new HashMap<>();
        for (GemType type : GemType.values()) {
            map.put(type, 0);
        }
    }

    /**
     * A gem pool with a variety of GemTypes in it.
     *
     * @param quartz   the amount of quartz in this collection.
     * @param ruby     the amount of ruby in this collection.
     * @param sapphire the amount of sapphire in this collection.
     * @param emerald  the amount of emerald in this collection.
     * @param onyx     the amount of onyx in this collection.
     */
    public GemPool(int quartz, int ruby, int sapphire, int emerald, int onyx) {
        map = new HashMap<>();
        map.put(GemType.QUARTZ, quartz);
        map.put(GemType.RUBY, ruby);
        map.put(GemType.SAPPHIRE, sapphire);
        map.put(GemType.EMERALD, emerald);
        map.put(GemType.ONYX, onyx);
    }

    /**
     * A gem pool with a single GemType.
     *
     * @param gemType the type of gem in this collection.
     * @param amount  the amount of that gem in this collection.
     */
    public GemPool(GemType gemType, int amount) {
        map = new HashMap<>();
        for (GemType type : GemType.values()) {
            map.put(type, 0);
        }
        if (gemType != null) {
            map.put(gemType, amount);
        }
    }

    /**
     * Get the amount of a particular GemType in this pool.
     *
     * @param gemType the gem type to get.
     * @return the amount of the chosen gem type.
     */
    public int get(GemType gemType) {
        if (gemType == null) {
            return 0;
        }
        return map.getOrDefault(gemType, 0);
    }

    /**
     * Returns whether there is no gems in this pool.
     *
     * @return whether there is no gems in this pool.
     */
    public boolean isEmpty() {
        for (GemType type : GemType.values()) {
            if (map.getOrDefault(type, 0) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether there is a negative amount of gems for ANY type of gem in this pool.
     *
     * @return whether there is a negative amount of gems for ANY type of gem in this pool.
     */
    public boolean hasNegative() {
        for (GemType gemType : GemType.values()) {
            if (map.get(gemType) < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a new gem pool that has all the contents of this gem pool, with some amount of a
     * chosen gem type added.
     *
     * @param gemType the type of gem to add to the new pool.
     * @param amount  the amount of that gem to add.
     * @return the new gem pool.
     */
    public GemPool add(GemType gemType, int amount) {
        GemPool gemPool = new GemPool();
        for (GemType gemType1 : map.keySet()) {
            gemPool.map.put(gemType1, map.get(gemType1));
        }
        gemPool.map.put(gemType, map.getOrDefault(gemType, 0) + amount);
        return gemPool;
    }

    /**
     * Merge two gem pools into another. The produced pool has the sum of each pool's gems.
     *
     * @param other the other gem pool to merge with this one.
     * @return a gem pool with all the contents of this and other.
     */
    public GemPool add(GemPool other) {
        if (other == null) {
            return new GemPool(this.get(GemType.QUARTZ),
                    this.get(GemType.RUBY),
                    this.get(GemType.SAPPHIRE),
                    this.get(GemType.EMERALD),
                    this.get(GemType.ONYX));
        }
        GemPool newPool = new GemPool();
        for (GemType type : GemType.values()) {
            newPool.map.put(type, this.get(type) + other.get(type));
        }
        return newPool;
    }

    /**
     * Create a new gem pool with the contents of this pool with the specified amount of a
     * particular type subtracted from it.
     *
     * @param gemType the gem type to subtract from.
     * @param amount  the amount of that gem type to subtract.
     * @return the new gem pool.
     */
    public GemPool subtract(GemType gemType, int amount) {
        return this.add(gemType, -amount);
    }

    /**
     * Create a new gem pool with the contents of this pool, but with the contents of another pool
     * subtracted from it.
     *
     * @param other the gem pool to remove.
     * @return the new gem pool.
     */
    public GemPool subtract(GemPool other) {
        if (other == null) {
            return new GemPool(this.get(GemType.QUARTZ),
                    this.get(GemType.RUBY),
                    this.get(GemType.SAPPHIRE),
                    this.get(GemType.EMERALD),
                    this.get(GemType.ONYX));
        }
        GemPool newPool = new GemPool();
        for (GemType type : GemType.values()) {
            newPool.map.put(type, this.get(type) - other.get(type));
        }
        return newPool;
    }

    /**
     * Create a new gem pool with the positive contents of this pool, with some removed. If any of
     * the following values are negative numbers they are treated as a 0: The amount of gems to
     * remove, the amount of gems already in this pool, or the result of removing the required
     * number of gems.
     *
     * @param gemType the gem type to remove.
     * @param amount  the amount of that gem type to remove.
     * @return the new gem pool.
     */
    public GemPool remove(GemType gemType, int amount) {
        int removeAmount = Math.max(amount, 0);
        GemPool newPool = new GemPool();
        for (GemType type : GemType.values()) {
            newPool.map.put(type, this.get(type));
        }
        if (gemType != null) {
            int currentAmount = Math.max(this.get(gemType), 0);
            int newAmount = Math.max(currentAmount - removeAmount, 0);
            newPool.map.put(gemType, newAmount);
        }
        return newPool;
    }

    /**
     * Create a new gem pool with the contents of this pool, but with the contents of another pool
     * removed from it. If any of the following values are negative numbers they are treated as
     * a 0: An amount of a gem type in other, An amount of a gem type in this pool, or the result
     * of removing the required number of gems.
     *
     * @param other the gem pool to remove.
     * @return the new gem pool.
     */
    public GemPool remove(GemPool other) {
        if (other == null) {
            GemPool newPool = new GemPool();
            for (GemType type : GemType.values()) {
                newPool.map.put(type, Math.max(this.get(type), 0));
            }
            return newPool;
        }
        GemPool newPool = new GemPool();
        for (GemType type : GemType.values()) {
            int thisAmount = Math.max(this.get(type), 0);
            int otherAmount = Math.max(other.get(type), 0);
            int newAmount = Math.max(thisAmount - otherAmount, 0);
            newPool.map.put(type, newAmount);
        }
        return newPool;
    }

    /**
     * Returns whether two GemPools are identical. Two GemPools are identical iff they
     * contain the same amount of each type of gem.
     *
     * @param obj the other GemPool to test equality with.
     * @return whether the two GemPools are identical.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GemPool other = (GemPool) obj;
        for (GemType type : GemType.values()) {
            if (this.get(type) != other.get(type)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string representation of this GemPool in the form: "VQ WR XS YE ZO" where
     * V, W, X, Y, Z are replaced with the number of QUARTZ, RUBY, SAPPHIRE, EMERALD, and
     * ONYX respectively. Additionally, any value which is less than or equal to 0 will not
     * appear in the final string. for example: a pool containing 5 QUARTZ, -3 RUBY, and
     * 2 SAPPHIRE will result in: "5Q 2S"
     *
     * @return a string representation of this GemPool.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendGem(sb, GemType.QUARTZ);
        appendGem(sb, GemType.RUBY);
        appendGem(sb, GemType.SAPPHIRE);
        appendGem(sb, GemType.EMERALD);
        appendGem(sb, GemType.ONYX);
        return sb.toString().trim();
    }

    /**
     * a helper function for toString() method
     *
     * @param sb      a string builder
     * @param gemType the gem type to be a string.
     */
    private void appendGem(StringBuilder sb, GemType gemType) {
        int amount = get(gemType);
        if (amount > 0) {
            sb.append(amount).append(gemType.toString().charAt(0)).append(" ");
        }
    }

    /**
     * Returns a hash code value for this object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                get(GemType.QUARTZ),
                get(GemType.RUBY),
                get(GemType.SAPPHIRE),
                get(GemType.EMERALD),
                get(GemType.ONYX)
        );
    }
}
