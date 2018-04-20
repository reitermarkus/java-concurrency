package ex03.task1;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

public class Server implements Protocol, Remote, Runnable {
  public Server() {}

  public String sort(String inputString) {
    return inputString.chars().sorted()
      .mapToObj(c -> Character.toString((char)c))
      .reduce("", (acc, e) -> acc + e);
  }

  public void compute(Integer inputTime) {
    try {
      Thread.sleep(inputTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      final var server = (Protocol) UnicastRemoteObject.exportObject(this, 0);
      final var registry = LocateRegistry.getRegistry();
      registry.bind("Protocol", server);
      System.out.println("Server ready.");
    } catch (Exception e) {
      System.err.println("Server Exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
