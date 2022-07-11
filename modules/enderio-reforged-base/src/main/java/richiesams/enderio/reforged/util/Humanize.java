package richiesams.enderio.reforged.util;

public class Humanize {
    private static final char[] magnitude = new char[]{'k', 'M', 'G', 'T'};

    public static String number(long input) {
        if (input < 1000) {
            return "%d".formatted(input);
        }

        int index = -1;
        while (input > 1000 && index < magnitude.length - 1) {
            ++index;
            input /= 1000;
        }

        return "%d%c".formatted(input, magnitude[index]);
    }
}
