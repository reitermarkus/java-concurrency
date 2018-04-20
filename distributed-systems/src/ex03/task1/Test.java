package ex03.task1;

import java.rmi.registry.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Test {
  public static void main(String[] args) throws Exception {
    // Start RMI registry on default port 1099.
    final var registry = LocateRegistry.createRegistry(1099);

    // Start server thread.
    final var serverThread = new Thread(new Server());
    serverThread.start();

    // Wait for server to start.
    Thread.sleep(100);

    System.out.println("Remote Objects:");
    for (final var remoteObject: registry.list()) {
      System.out.println("  "  + remoteObject);
    }

    final var exec = Executors.newFixedThreadPool(3);

    final var start = new Date().getTime();

    // Run three clients in parallel.
    final var sortedStrings = exec.invokeAll(List.of(new Client(), new Client(), new Client()))
      .parallelStream().map(f -> {
        try {
          return f.get();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }).collect(Collectors.toList());

    System.out.println("Client Responses:");
    sortedStrings.forEach(s -> System.out.println("  '" + s + "'"));

    final var stop = new Date().getTime();

    System.out.println("Took " + (stop - start) + " ms.");

    exec.shutdown();
    serverThread.join();
    System.exit(0);
  }
}
