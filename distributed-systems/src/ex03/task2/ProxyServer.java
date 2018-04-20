package ex03.task2;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

import static ex03.task2.AccessLevel.*;

public class ProxyServer implements Protocol, Runnable {
  public void compute(List<AccessLevel> accessLevels, Integer inputTime) throws RemoteException {
    try {
      final var registry = LocateRegistry.getRegistry();
      final var server = (ComputeProtocol) registry.lookup("ComputeProtocol");


      if (!accessLevels.contains(COMPUTE)) {
        throw new AccessException("Access denied. Client does not have " + COMPUTE + " access.");
      }

      server.compute(inputTime);
    } catch (NotBoundException e) {
      throw new RemoteException(null, e);
    }
  }

  public String sort(List<AccessLevel> accessLevels, String inputString) throws RemoteException {
    try {
      final var registry = LocateRegistry.getRegistry();
      final var server = (SortProtocol) registry.lookup("SortProtocol");

      if (!accessLevels.contains(SORT)) {
        throw new AccessException("Access denied. Client does not have " + SORT + " access.");
      }

      return server.sort(inputString);
    } catch (NotBoundException e) {
      throw new RemoteException(null, e);
    }
  }

  public void run() {
    try {
      final var server = (Protocol) UnicastRemoteObject.exportObject(this, 0);
      final var registry = LocateRegistry.getRegistry();
      registry.bind("Protocol", server);
      System.out.println("ProxyServer ready.");
    } catch (Exception e) {
      System.err.println("ProxyServer Exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
