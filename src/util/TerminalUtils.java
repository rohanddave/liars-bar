package util;

/**
 * Utility class for terminal operations and formatting.
 * Provides common terminal functions like clearing screen and formatted output.
 */
public class TerminalUtils {
    
    /**
     * Clears the terminal screen using ANSI escape codes.
     */
    public static void clearScreen() {
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
    
    /**
     * Prints a horizontal separator line.
     * @param length Length of the separator
     */
    public static void printSeparator(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print("=");
        }
        System.out.println();
    }
    
    /**
     * Prints a horizontal separator line with default length.
     */
    public static void printSeparator() {
        printSeparator(48);
    }
    
    /**
     * Prints text centered within a given width.
     * @param text Text to center
     * @param width Total width for centering
     */
    public static void printCentered(String text, int width) {
        int padding = Math.max(0, (width - text.length()) / 2);
        for (int i = 0; i < padding; i++) {
            System.out.print(" ");
        }
        System.out.println(text);
    }
    
    /**
     * Prints text centered with default width.
     * @param text Text to center
     */
    public static void printCentered(String text) {
        printCentered(text, 48);
    }
    
    /**
     * Prints a formatted header with title and separators.
     * @param title Header title
     */
    public static void printHeader(String title) {
        printSeparator();
        printCentered(title);
        printSeparator();
    }
    
    /**
     * Prints multiple empty lines for spacing.
     * @param lines Number of empty lines to print
     */
    public static void printSpacing(int lines) {
        for (int i = 0; i < lines; i++) {
            System.out.println();
        }
    }
    
    /**
     * Prints a single empty line.
     */
    public static void printSpacing() {
        printSpacing(1);
    }
    
    /**
     * Prints formatted text with a prefix.
     * @param prefix Prefix to add before text
     * @param text Main text content
     */
    public static void printWithPrefix(String prefix, String text) {
        System.out.println(prefix + " " + text);
    }
    
    /**
     * Prints an info message with formatting.
     * @param message Info message to display
     */
    public static void printInfo(String message) {
        printWithPrefix("ℹ️", message);
    }
    
    /**
     * Prints a success message with formatting.
     * @param message Success message to display
     */
    public static void printSuccess(String message) {
        printWithPrefix("✅", message);
    }
    
    /**
     * Prints a warning message with formatting.
     * @param message Warning message to display
     */
    public static void printWarning(String message) {
        printWithPrefix("⚠️", message);
    }
    
    /**
     * Prints an error message with formatting.
     * @param message Error message to display
     */
    public static void printError(String message) {
        printWithPrefix("❌", message);
    }
    
    /**
     * Formats a string to fit within a specified width, truncating if necessary.
     * @param text Text to format
     * @param width Maximum width
     * @return Formatted text
     */
    public static String formatToWidth(String text, int width) {
        if (text == null) {
            return "";
        }
        
        if (text.length() <= width) {
            return text;
        }
        
        return text.substring(0, width - 3) + "...";
    }
    
    /**
     * Pads a string to a specific width with spaces.
     * @param text Text to pad
     * @param width Target width
     * @return Padded text
     */
    public static String padToWidth(String text, int width) {
        if (text == null) {
            text = "";
        }
        
        if (text.length() >= width) {
            return text;
        }
        
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < width) {
            sb.append(" ");
        }
        
        return sb.toString();
    }
    
    /**
     * Creates a progress bar representation.
     * @param current Current value
     * @param max Maximum value
     * @param width Width of the progress bar
     * @return Progress bar string
     */
    public static String createProgressBar(int current, int max, int width) {
        if (max <= 0) {
            return "[" + padToWidth("", width) + "]";
        }
        
        int filled = (int) ((double) current / max * width);
        StringBuilder bar = new StringBuilder("[");
        
        for (int i = 0; i < width; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        
        bar.append("]");
        return bar.toString();
    }
}