package ex03.task2;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.concurrent.*;

public class Client implements Callable<String> {
  private Integer id;
  private List<AccessLevel> accessLevels;

  public Client(Integer id, List<AccessLevel> accessLevels) {
    this.id = id;
    this.accessLevels = accessLevels;
  }

  public String call() {
    try {
      final var registry = LocateRegistry.getRegistry();
      final var server = (Protocol) registry.lookup("Protocol");


      final var inputString = "CBA";

      try {
        server.compute(this.accessLevels, inputString.length() * 1000);
      } catch (RemoteException e) {
        printException(e.getCause());
      }

      return server.sort(this.accessLevels, "CBA");
    } catch (RemoteException e) {
      printException(e.getCause());
    } catch (NotBoundException e) {
      printException(e.getCause());
    }

    return null;
  }

  private void printException(Throwable e) {
    System.err.println("Client " + this.id + " Exception: " + e);
    // e.printStackTrace();
  }
}
