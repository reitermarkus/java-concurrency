package ex03.task2;

import java.rmi.registry.*;
import java.rmi.server.*;

public class ComputeServer implements ComputeProtocol, Runnable {
  public void compute(Integer inputTime) {
    try {
      Thread.sleep(inputTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      final var server = (ComputeProtocol) UnicastRemoteObject.exportObject(this, 0);
      final var registry = LocateRegistry.getRegistry();
      registry.bind("ComputeProtocol", server);
      System.out.println("ComputeServer ready.");
    } catch (Exception e) {
      System.err.println("ComputeServer Exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
