package settings;

public class SyntacticSettings {
    private static final String BOLD = "\033[1m";
    private static final String ITALIC = "\033[3m";
    private static final String UNDERLINE = "\033[4m";
    private static final String CROSSED_OUT = "\033[9m";
    private static final String FRAMED = "\033[51m";

    private static final String COLOR_BLACK = "\033[30m";
    private static final String COLOR_RED = "\033[31m";
    private static final String COLOR_GREEN = "\033[32m";
    private static final String COLOR_YELLOW = "\033[33m";
    private static final String COLOR_BLUE = "\033[34m";
    private static final String COLOR_MAGENTA = "\033[35m";
    private static final String COLOR_CYAN = "\033[36m";
    private static final String COLOR_WHITE = "\033[37m";
    private static final String COLOR_RESET = "\033[0m";


    private static final String COLOR_BRIGHT_BLACK = "\033[90m";
    private static final String COLOR_BRIGHT_RED = "\033[91m";
    private static final String COLOR_BRIGHT_GREEN = "\033[92m";
    private static final String COLOR_BRIGHT_YELLOW = "\033[93m";
    private static final String COLOR_BRIGHT_BLUE = "\033[94m";
    private static final String COLOR_BRIGHT_MAGENTA = "\033[95m";
    private static final String COLOR_BRIGHT_CYAN = "\033[96m";
    private static final String COLOR_BRIGHT_WHITE = "\033[97m";

    private static boolean useColorsInConsole = false;

    public static void useColorsInConsole(boolean yesNo) {
        useColorsInConsole = yesNo;
    }

    public static String bold() {
        return useColorsInConsole ? BOLD : "";
    }

    public static String italic() {
        return useColorsInConsole ? ITALIC : "";
    }


    public static String underline() {
        return useColorsInConsole ? UNDERLINE : "";
    }

    public static String crossed_out() {
        return useColorsInConsole ? CROSSED_OUT : "";
    }

    public static String framed() {
        return useColorsInConsole ? FRAMED : "";
    }

    public static String black() {
        return useColorsInConsole ? COLOR_BLACK : "";
    }

    public static String red() {
        return useColorsInConsole ? COLOR_RED : "";
    }

    public static String green() {
        return useColorsInConsole ? COLOR_GREEN : "";
    }

    public static String yellow() {
        return useColorsInConsole ? COLOR_YELLOW : "";
    }

    public static String blue() {
        return useColorsInConsole ? COLOR_BLUE : "";
    }

    public static String magenta() {
        return useColorsInConsole ? COLOR_MAGENTA : "";
    }

    public static String cyan() {
        return useColorsInConsole ? COLOR_CYAN : "";
    }

    public static String white() {
        return useColorsInConsole ? COLOR_WHITE : "";
    }

    public static String brightBlack() {
        return useColorsInConsole ? COLOR_BRIGHT_BLACK : "";
    }

    public static String brightRed() {
        return useColorsInConsole ? COLOR_BRIGHT_RED : "";
    }

    public static String brightGreen() {
        return useColorsInConsole ? COLOR_BRIGHT_GREEN : "";
    }

    public static String brightYellow() {
        return useColorsInConsole ? COLOR_BRIGHT_YELLOW : "";
    }

    public static String brightBlue() {
        return useColorsInConsole ? COLOR_BRIGHT_BLUE : "";
    }

    public static String brightMagenta() {
        return useColorsInConsole ? COLOR_BRIGHT_MAGENTA : "";
    }

    public static String brightCyan() {
        return useColorsInConsole ? COLOR_BRIGHT_CYAN : "";
    }

    public static String brightWhite() {
        return useColorsInConsole ? COLOR_BRIGHT_WHITE : "";
    }

    public static String reset() {
        return useColorsInConsole ? COLOR_RESET : "";
    }
}
