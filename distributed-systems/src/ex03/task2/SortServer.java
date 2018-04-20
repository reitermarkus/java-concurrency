package ex03.task2;

import java.rmi.registry.*;
import java.rmi.server.*;

public class SortServer implements SortProtocol, Runnable {
  public String sort(String inputString) {
    return inputString.chars().sorted()
      .mapToObj(c -> Character.toString((char)c))
      .reduce("", (acc, e) -> acc + e);
  }

  public void run() {
    try {
      final var server = (SortProtocol) UnicastRemoteObject.exportObject(this, 0);
      final var registry = LocateRegistry.getRegistry();
      registry.bind("SortProtocol", server);
      System.out.println("SortServer ready.");
    } catch (Exception e) {
      System.err.println("SortServer Exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
