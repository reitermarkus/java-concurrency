package ex04.task1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Node implements Runnable {
  private Map<String, InetSocketAddress> peers = new HashMap<>();
  private Table table = new Table(this);

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

  public int getNetworkSize() {
    return this.networkSize;
  }

  public boolean addPeer(String name, InetSocketAddress peerAddress) {
    synchronized (this.table) {
      return this.table.add(name, peerAddress);
    }
  }

  public boolean removePeer(String name) {
    synchronized (this.table) {
      return this.table.remove(name);
    }
  }

  private boolean parseCommand(final Command cmd, final ObjectOutputStream os) throws IOException {
    try {
      switch (cmd.getCommandType()) {
        case GET_TABLE:
          synchronized (this.table) {
            os.writeObject(this.table);
            System.out.println(this.name + ": Sent table: " + this.table);
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
    var exec = Executors.newWorkStealingPool();

    updateThread.start();

    try (final var socket = this.socket = new ServerSocket(this.port)) {
      while (!Thread.interrupted()) {
        try {
          System.out.println(this.name + ": Waiting for connection …");
          var connection = socket.accept();

          exec.submit(() -> {
            try (
              final var is = new ObjectInputStream(connection.getInputStream());
              final var os = new ObjectOutputStream(connection.getOutputStream());
            ) {
              try {
                final var name = (String)is.readObject();
                final var address = (InetSocketAddress)is.readObject();
                final var command = (Command)is.readObject();

                synchronized (this.table) {
                  if (this.table.merge(name, address)) {
                    System.out.println(this.name + ": Added peer '" + name + "'.");
                  }
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
          break;
        } catch (IOException e) {
          e.printStackTrace();
        }

        Thread.yield();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      updateThread.interrupt();
      updateThread.join();
    } catch (InterruptedException e) {}

    exec.shutdown();
    System.out.println(this.name + ": Going offline …");
  }

  private Thread updateThread = new Thread(() -> {
    System.out.println(this.name + ": Update thread starting.");

    while (!Thread.interrupted()) {
      this.update();

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        break;
      }
    }

    System.out.println(this.name + ": Update thread stopped.");
  });

  private void update() {
    this.table.getRandom().ifPresent(peer -> {
      try {
        System.out.println(this.name + ": Requesting table from '" + peer.getKey() + "' …");
        final var connection = new Socket(peer.getValue().getAddress(), peer.getValue().getPort());

        try (
          final var os = new ObjectOutputStream(connection.getOutputStream());
          final var is = new ObjectInputStream(connection.getInputStream());
        ) {
          os.writeObject(this.name);
          os.writeObject(new InetSocketAddress(this.getAddress(), this.getPort()));
          os.writeObject(new Command(CommandType.GET_TABLE, this.name));

          try {
            final var peers = (Table)is.readObject();
            System.out.println(this.name + ": Received table with " + peers.size() + " peers: " + peers);

            synchronized (this.table) {
              final var addedAndRemoved = this.table.merge(peers);
              final var added = addedAndRemoved.getKey();
              final var removed = addedAndRemoved.getValue();

              added.forEach(entry -> {
                System.out.println(this.name + ": Added peer '" + entry.getKey() + "'.");
              });

              removed.forEach(entry -> {
                System.out.println(this.name + ": Removed peer '" + entry.getKey() + "'.");
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
        this.update();
      }
    });
  }

  public void shutdown() {
    if (this.socket != null) {
      try {
        this.socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public String toString() {
    return "Node(" + this.getName() + ", " + this.getAddress() + ", " + this.getPort() + ")";
  }
}
