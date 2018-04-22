package ex04.task1;

public class ANSI {
  private static final String RESET = "\u001B[0m";
  private static final String BLACK = "\u001B[30m";
  private static final String RED = "\u001B[31m";
  private static final String GREEN = "\u001B[32m";
  private static final String YELLOW = "\u001B[33m";
  private static final String BLUE = "\u001B[34m";
  private static final String PURPLE = "\u001B[35m";
  private static final String CYAN = "\u001B[36m";
  private static final String WHITE = "\u001B[37m";

  public static String green(String string) {
    return reset(GREEN + string);
  }

  public static String red(String string) {
    return reset(RED + string);
  }

  public static String blue(String string) {
    return reset(BLUE + string);
  }

  public static String purple(String string) {
    return reset(PURPLE + string);
  }

  private static String reset(String string) {
    return RESET + string + RESET;
  }
}
