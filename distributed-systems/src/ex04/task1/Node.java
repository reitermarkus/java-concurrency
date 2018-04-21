package ex04.task1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Node implements Runnable {
  private volatile boolean running = false;

  private Map<String, InetSocketAddress> peers = new HashMap<>();

  private InetAddress address;
  private int port;
  private String name;
  private ServerSocket socket;

  private int networkSize;

  public Node(int port, int networkSize) throws UnknownHostException {
    this(UUID.randomUUID().toString(), port, networkSize);
  }

  public Node(String name, int port, int networkSize) throws UnknownHostException {
    this(name, InetAddress.getLocalHost(), port, networkSize);
  }

  public Node(String name, InetAddress address, int port, int networkSize) {
    this.name = name;
    this.address = address;
    this.port = port;
    this.networkSize = networkSize;
  }

  public String getName() {
    return this.name;
  }

  public InetSocketAddress getSocketAddress() {
    return new InetSocketAddress(this.address, this.port);
  }

  public InetAddress getAddress() {
    return this.address;
  }

  public int getPort() {
    return this.port;
  }

  public boolean addPeer(String name, InetSocketAddress peerAddress) {
    final var address = peerAddress.getAddress();
    final var port = peerAddress.getPort();

    final var isLoopback = address.isLoopbackAddress();
    var isLocalhost = false;
    try {
      isLocalhost = address.equals(InetAddress.getLocalHost());
    } catch (UnknownHostException e) {}

    if ((isLocalhost || isLoopback || address.equals(this.getAddress())) && port == this.getPort()) {
      // Do not add this node's own address.
      return false;
    }

    synchronized (this.peers) {
      if (!this.peers.containsKey(name)) {
        this.peers.put(name, peerAddress);
        return true;
      }

      return false;
    }
  }

  public boolean removePeer(String name) {
    synchronized (this.peers) {
      if (this.peers.containsKey(name)) {
        this.peers.remove(name);
        return true;
      }

      return false;
    }
  }

  private boolean parseCommand(final String cmd, final ObjectOutputStream os) throws IOException {
    try {
      switch (Command.valueOf(cmd)) {
        case GET_TABLE:
          synchronized (this.peers) {
            os.writeObject(this.peers);
            System.out.println(this.name + ": Sent table: " + this.peers);
          }
          return true;
      }
    } catch (IllegalArgumentException e) {
      System.err.println("Illegal command " + "\"" + cmd + "\"" + " was sent!");
      return false;
    }

    return false;
  }

  public void run() {
    this.running = true;

    try {
      this.socket = new ServerSocket(this.port);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    var exec = Executors.newWorkStealingPool();

    updateThread.start();

    while (this.running) {
      try {
        System.out.println(this.name + ": Waiting for connection …");
        var connection = this.socket.accept();

        exec.submit(() -> {
          try (
            final var is = new ObjectInputStream(connection.getInputStream());
            final var os = new ObjectOutputStream(connection.getOutputStream());
          ) {
            try {
              final var name = (String)is.readObject();
              final var address = (InetSocketAddress)is.readObject();
              final var command = (String)is.readObject();

              if (this.addPeer(name, address)) {
                System.out.println(this.name + ": Added peer '" + name + "'.");
              }

              this.parseCommand(command, os);

            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
      } catch (SocketException e) {
        System.out.println("Shutting down ...");
        break;
      } catch (IOException e) {
        e.printStackTrace();
      }

      Thread.yield();
    }

    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    exec.shutdown();
    System.out.println(this.name + ": Going offline …");
  }

  private Thread updateThread = new Thread(() -> {
    System.out.println(this.name + ": Update thread starting.");

    while (this.running) {
      final var optionalPeer = this.randomPeer();

      if (optionalPeer.isPresent()) {
        final var peer = optionalPeer.get();

        try {
          System.out.println(this.name + ": Requesting table from '" + peer.getKey() + "' …");
          final var connection = new Socket(peer.getValue().getAddress(), peer.getValue().getPort());

          try (
            final var os = new ObjectOutputStream(connection.getOutputStream());
            final var is = new ObjectInputStream(connection.getInputStream());
          ) {
            os.writeObject(this.name);
            os.writeObject(new InetSocketAddress(this.getAddress(), this.getPort()));
            os.writeObject("GET_TABLE");

            try {
              final var peers = (Map<String, InetSocketAddress>)is.readObject();
              System.out.println(this.name + ": Received table with " + peers.size() + " peers: " + peers);

              synchronized (this.peers) {
                peers.forEach((name, address) -> {
                  if (this.addPeer(name, address)) {
                    System.out.println(this.name + ": Added peer '" + name + "'.");
                  }
                });
              }
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            }
          }
        } catch (IOException e) {
          // Remove offline peer.
          System.out.println(this.name + ": Removing unreachable peer '" + peer.getKey() + "' …");
          this.removePeer(peer.getKey());
          continue;
        }
      }

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        continue;
      }
    }

    System.out.println(this.name + ": Update thread stopped.");
  });

  private Optional<Map.Entry<String, InetSocketAddress>> randomPeer() {
    if (peers.isEmpty()) {
      return Optional.empty();
    }

    var i = ThreadLocalRandom.current().nextInt(peers.size());
    return Optional.of((Map.Entry<String, InetSocketAddress>)peers.entrySet().toArray()[i]);
  }

  public void shutdown() {
    this.running = false;
  }

  @Override
  public String toString() {
    return "Node(" + this.getName() + ", " + this.getAddress() + ", " + this.getPort() + ")";
  }
}
