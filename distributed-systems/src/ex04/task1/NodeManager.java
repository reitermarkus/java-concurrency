package ex04.task1;

import java.net.*;

public class NodeManager {
  public static void main(String[] args) throws UnknownHostException, InterruptedException {
    var node1 = new Node(InetAddress.getLocalHost(), 2001, "node1");
    var node2 = new Node(InetAddress.getLocalHost(), 2002, "node2");

    node2.addPeer("node1", new InetSocketAddress(InetAddress.getLocalHost(), 2001));

    var node1Thread = new Thread(node1);
    var node2Thread = new Thread(node2);

    node1Thread.start();

    Thread.sleep(2000);

    node2Thread.start();

    Thread.sleep(10000);

    System.out.println("   NM: Shutting down node 2 …");
    node2Thread.interrupt();
  }
}
