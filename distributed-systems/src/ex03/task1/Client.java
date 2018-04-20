package ex03.task1;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.concurrent.*;

public class Client implements Callable<String> {
  public Client() {}

  public String call() {
    try {
      final var registry = LocateRegistry.getRegistry();
      final var server = (Protocol) registry.lookup("Protocol");

      final var inputString = "CBA";

      server.compute(inputString.length() * 1000);

      return server.sort("CBA");
    } catch (Exception e) {
      System.out.println("Client Exception: " + e);
      e.printStackTrace();
    }

    return null;
  }
}
