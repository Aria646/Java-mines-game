package mines.model;

/**
 * A position on the game board.
 * @param x the horizontal position.
 * @param y the vertical position.
 */
public record Position(int x, int y) {
    /**
     * Creates an instance of a Position record class.
     *
     * @param x the value for the x record component
     * @param y the value for the y record component
     */
    public Position {

    }

    /**
     * Returns the value of the x record component.
     *
     * @return the value of the x record component
     */
    @Override
    public int x() {
        return x;
    }

    /**
     * Returns the value of the y record component.
     *
     * @return the value of the y record component
     */
    @Override
    public int y() {
        return y;
    }

    /**
     * Returns a string representation of this Position in the form: "(x, y)"
     *
     * @return a string representation of this Position
     */
    @Override
    public String toString() {
        return String.format("(%d, %d)", this.x, this.y);
    }

    /**
     * Indicates whether some other object is "equal to" this one. The objects are equal if the
     * other object is of the same class and if all the record components are equal. All
     * components in this record class are compared with '=='.
     *
     * @param o the reference object with which to compare.
     * @return true if this object is the same as the o argument; false otherwise.
     */
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        Position position = (Position) o;
        return position.x() == x && position.y() == y;
    }

    /**
     * Returns a hash code value for this object.
     *
     * @return a hash code value for this object
     */
    public final int hashCode() {
        return java.util.Objects.hash(x, y);
    }
}
