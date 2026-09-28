package mines.view;

import mines.controller.Controller;
import mines.model.GemPool;
import mines.model.GemType;
import mines.model.Player;
import mines.model.Position;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A textual interface for the Fine Minery game.
 */
public class TextView implements View {

    /**
     * This view's user input.
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * The game controller, to mediate all game state changes.
     */
    private Controller controller;

    /**
     * Builds a string into with a fixed width column.
     *
     * @param input the string to turn into a column.
     * @return the resulting string.
     */
    private String buildColumn(String input) {
        return String.format(("[%-24s]"), input);
    }

    /**
     * Returns a function that can build a string into a column.
     *
     * @return a function that can build a string into a column.
     */
    private Function<String, String> make_column() {
        return this::buildColumn;
    }

    /**
     * Get a string that displays the player's name, victory point count,
     * and whether they are the current player.
     *
     * @param player the player to display.
     * @return a string that displays the player's details.
     */
    private String playerHeader(Player player) {
        Player current = controller.getGame().getCurrentPlayer();
        String pip = player.getPlayerNumber() == current.getPlayerNumber() ? "* " : "  ";
        String name = "Player " + player.getPlayerNumber();
        String vp = player.getVictoryPoints() == 0
                ? ""
                : (" " + player.getVictoryPoints() + " " + "VP!");
        return pip + name + vp;
    }

    /**
     * Processes an integer into a String if that integer meets a specified criteria.
     *
     * @param input     a function that maps the number to a string.
     * @param num       the number to process.
     * @param predicate the condition that causes the string to be produced.
     * @return the process string if the predicate is met, or some white space otherwise.
     */
    private String stringIf(Function<Integer, String> input, int num,
                            Predicate<Integer> predicate) {
        return predicate.test(num) ? input.apply(num) : "   ";
    }

    /**
     * Generate a block in the header for a player's purchased mines.
     *
     * @param player  the player whose mines are to be displayed.
     * @param gemType the type of mine to be displayed.
     * @return the processed string.
     */
    private String mineBlock(Player player, GemType gemType) {
        return stringIf(i -> "[" + i + "]", player.getMines().get(gemType), i -> i > 0);
    }


    /**
     * Generate a block in the header for a player's collected gems.
     *
     * @param player  the player whose gems are to be displayed.
     * @param gemType the type of gem to be displayed.
     * @return the processed string.
     */
    private String gemBlock(Player player, GemType gemType) {
        return stringIf(i -> "(" + i + ")", player.getGems().get(gemType), i -> i > 0);
    }

    /**
     * Build a one of a player's header row out of blocks.
     *
     * @param player  the player whose header is being built.
     * @param builder the builder for this row.
     * @return the processed string.
     */
    private String buildCell(Player player, BiFunction<Player, GemType, String> builder) {
        return Arrays.stream(GemType.values())
                .map(t -> builder.apply(player, t))
                .collect(Collectors.joining(" "));
    }

    /**
     * Display the whole game board as a string.
     *
     * @return the string representation of the game board.
     */
    @Override
    public String toString() {
        var column = make_column();

        String playerNames = controller.getGame()
                .getPlayers()
                .stream()
                .map(this::playerHeader)
                .map(column)
                .collect(Collectors.joining(" "));

        String gemNames = Arrays.stream(GemType.values())
                .map(s -> " " + s.name().charAt(0) + " ")
                .collect(Collectors.joining(" "));

        String gemNameBlock = controller.getGame()
                .getPlayers()
                .stream()
                .map(p -> gemNames)
                .map(column)
                .collect(Collectors.joining(" "));

        String mineBlock = controller.getGame()
                .getPlayers()
                .stream()
                .map(p -> buildCell(p, this::mineBlock))
                .map(column)
                .collect(Collectors.joining(" "));

        String gemBlock = controller.getGame()
                .getPlayers()
                .stream()
                .map(p -> buildCell(p, this::gemBlock))
                .map(column)
                .collect(Collectors.joining(" "));

        String field = IntStream.range(0, 3)
                .mapToObj(y -> IntStream.range(0, 4)
                        .mapToObj(x -> controller.getGame()
                                .getMine(new Position(x, y))
                                .toString())
                        .map(column)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));

        String gameOverMessage = controller.getGame().isOver() ? "\nGAME OVER! Player "
                + controller.getGame().getCurrentPlayer().getPlayerNumber() + " Won the game!" : "";
        return playerNames + "\n" + gemNameBlock + "\n" + mineBlock + "\n" + gemBlock + "\n\n"
                + field + gameOverMessage;
    }

    /**
     * Print the game board to standard out.
     */
    private void print() {
        System.out.println(this);
    }

    /**
     * Process a purchase command on the commandline.
     *
     * @param line the arguments of the purchase command.
     */
    private void purchase(String line) {
        try {
            String[] tokens = line.split(",", 2);
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);

            if (x > 3 || x < 0 || y > 2 || y < 0) {
                throw new NumberFormatException();
            }

            if (!controller.purchase(new Position(x, y))) {
                System.out.println("You do not have enough gems to purchase this mine!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\"" + line + "\" is not a valid position");
        }
    }

    /**
     * Count the number of gems of a particular type that appear in a line.
     *
     * @param gemType the type to search for.
     * @param line    the line of text to search in.
     * @return the total count.
     */
    private int countGemsOfType(GemType gemType, String line) {
        return (int) line.chars()
                .filter(c -> Character.toUpperCase(c) == gemType.toString().charAt(0))
                .count();
    }

    /**
     * Process a collect command on the commandline.
     *
     * @param line the arguments to the collect command.
     */
    private void collect(String line) {
        Map<GemType, Integer> gems = Arrays.stream(GemType.values())
                .collect(Collectors.toMap(gemType -> gemType,
                        gemType -> countGemsOfType(gemType, line)));

        GemPool pool = new GemPool();
        for (var e : gems.entrySet()) {
            pool = pool.add(e.getKey(), e.getValue());
        }

        if (!controller.collect(pool)) {
            System.out.println("Failed to collect gems");
        }
    }

    /**
     * Display the help message.
     */
    private void help() {
        System.out.println("The Fine Minery game has the following commands:");
        System.out.println(
                "(c)ollect gems\t-\tHave the current player collect gems of specified types.");
        System.out.println("\t\t\t\t\tGems can be any three of QRSEO, or two of any one of them.");
        System.out.println(
                "(p)urchase pos\t-\tHave the current player purchase the mine at a specified "
                        + "position.");
        System.out.println("\t\t\t\t\tThe position is in the form x,y");
        System.out.println("(h)elp\t\t\t-\tDisplay this help message.");
    }

    /**
     * Process a commandline command.
     *
     * @param line the text of the command.
     */
    private void process(String line) {
        if (controller.getGame().isOver()) {
            return;
        }

        String[] tokens = line.split(" ", 2);
        switch (tokens[0]) {
            case "p", "purchase" -> purchase(tokens[1]);
            case "c", "collect" -> collect(tokens[1]);
            case "?", "h", "help" -> help();
            default -> System.out.println("Invalid command: \"" + line + "\"");
        }
    }

    /**
     * Persistently read commandline commands and process them.
     */
    private void readInput() {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            process(line);
        }
    }

    /**
     * Start a Text based UI for the game.
     *
     * @param controller the controller for the game.
     */
    @Override
    public void start(Controller controller) {
        this.controller = controller;
        controller.addObserver(this::print);
        new Thread(this::readInput).start();
        print();
    }
}
