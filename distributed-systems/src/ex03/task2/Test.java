package ex03.task2;

import java.rmi.registry.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static ex03.task2.AccessLevel.*;

public class Test {
  public static void main(String[] args) throws Exception {
    // Start RMI registry on default port 1099.
    final var registry = LocateRegistry.createRegistry(1099);

    // Start server thread.
    final var proxyServerThread = new Thread(new ProxyServer());
    final var sortServerThread = new Thread(new SortServer());
    final var computeServerThread = new Thread(new ComputeServer());

    sortServerThread.start();
    computeServerThread.start();

    // Wait for end-point servers to start.
    Thread.sleep(100);

    proxyServerThread.start();

    // Wait for proxy server to start.
    Thread.sleep(100);

    System.out.println("Remote Objects:");
    for (final var remoteObject: registry.list()) {
      System.out.println("  "  + remoteObject);
    }

    final var exec = Executors.newFixedThreadPool(4);

    final var start = new Date().getTime();

    // Run three clients in parallel.
    final var sortedStrings = exec.invokeAll(List.of(
        new Client(1, List.of(COMPUTE)),
        new Client(2, List.of(SORT)),
        new Client(3, List.of(COMPUTE, SORT)),
        new Client(4, List.of())
      )).parallelStream().map(f -> {
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
    proxyServerThread.join();
    sortServerThread.join();
    System.exit(0);
  }
}
