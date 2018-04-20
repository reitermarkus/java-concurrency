package ex02.task1;

import java.io.*;
import java.net.*;

public class Protocol {
  public static final int PORT = 3712;

  public static String request(Socket socket, String inputString) throws IOException {
    var outputStream = new ObjectOutputStream(socket.getOutputStream());
    var inputStream = new ObjectInputStream(socket.getInputStream());

    try {
      outputStream.writeObject(inputString);

      try {
        return (String)inputStream.readObject();
      } catch (ClassNotFoundException e) {
        return null;
      }
    } finally {
      inputStream.close();
      outputStream.close();
    }
  }

  private static String sort(String inputString) {
    return inputString.chars().sorted()
      .mapToObj(c -> Character.toString((char)c))
      .reduce("", (acc, e) -> acc + e);
  }

  public static void reply(Socket socket) throws IOException {
    var inputStream = new ObjectInputStream(socket.getInputStream());
    var outputStream = new ObjectOutputStream(socket.getOutputStream());

    try {
      String inputString = "";
      try {
        inputString = (String)inputStream.readObject();
      } catch (ClassNotFoundException e) {
        // Ignore.
      }

      System.out.println("Input String: '" + inputString + "'");

      var outputString = sort(inputString);

      System.out.println("Output String: '" + outputString + "'");

      outputStream.writeObject(outputString);

      if (inputString.equals("SHUTDOWN")) {
        socket.close();
      }
    } finally {
      inputStream.close();
      outputStream.close();
    }
  }
}
