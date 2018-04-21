package ex04.task1;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class NodeManager {
  private static int CURRENT_PORT = 2000;

  private static List<Node> createCluster(String clusterName, int size) {
    final List<Node> nodes = IntStream.rangeClosed(1, size).mapToObj(i -> {
      try {
        return new Node(clusterName + "-node" + i, CURRENT_PORT++, size);
      } catch (UnknownHostException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList());

    // “Chain” all nodes in the cluster together.
    for (int i = 1; i < size; i++) {
      final var previousNode = nodes.get(i - 1);
      nodes.get(i).addPeer(previousNode.getName(), previousNode.getSocketAddress());
    }

    return nodes;
  }

  public static void main(String[] args) throws UnknownHostException, InterruptedException {
    int n = 5;

    final var cluster1 = createCluster("cluster1", n);
    final var cluster2 = createCluster("cluster2", n);
    final var cluster3 = createCluster("cluster3", n);

    final var cluster1Node1 = cluster1.get(0);
    final var cluster2Node1 = cluster2.get(0);
    final var cluster3Node1 = cluster3.get(0);

    cluster2Node1.addPeer(cluster1Node1.getName(), cluster1Node1.getSocketAddress());
    cluster3Node1.addPeer(cluster1Node1.getName(), cluster1Node1.getSocketAddress());

    var allNodes = List.of(cluster1, cluster2, cluster3).stream().flatMap(nodes -> nodes.stream()).collect(Collectors.toList());

    allNodes.stream().forEach(node -> new Thread(node).start());

    // Wait for full network propagation.
    Thread.sleep(3 * 3 * n * 1000 + 10000);

    // Shut down all nodes in random order, with random delay in-between.
    Collections.shuffle(allNodes);

    allNodes.stream().forEach(node -> {
      try {
        Thread.sleep(ThreadLocalRandom.current().nextInt(10,20) * 1000);
        System.err.println("Shutting down node '" + node.getName() + "'.");
        node.shutdown();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    System.err.println("All nodes are now offline.");
  }
}
