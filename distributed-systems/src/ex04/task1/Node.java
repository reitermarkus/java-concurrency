package ex04.task1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import static ex04.task1.ANSI.*;

public class Node implements Runnable {
  private Map<String, InetSocketAddress> peers = new HashMap<>();
  private Table table = new Table(this);

  private InetAddress address;
  private int port;
  private String name;
  private ServerSocket socket;

  private int networkSize;

  private void log(String string) {
    System.out.println(this.name + ": " + string);
  }

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

  private boolean handleCommand(final Command cmd, final ObjectInputStream is, final ObjectOutputStream os) throws IOException, ClassNotFoundException {
    try {
      switch (cmd.getCommandType()) {
        case GET_TABLE:
          synchronized (this.table) {
            os.writeObject(this.table);
            log(purple("Sent table: " + this.table));
          }
          return true;
        case LOOKUP:
          final var name = (String)is.readObject();
          var hops = (Set<Peer>)is.readObject();

          synchronized (this.table) {
            if (this.table.contains(name)) {
              os.writeObject(this.table.get(name));
              return true;
            }
          }

          os.writeObject(this.lookup(name, hops));
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
          log(blue("Waiting for connection …"));
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
                    log(green("Added peer '" + name + "'."));
                  }
                }

                this.handleCommand(command, is, os);
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
    log(red("Going offline …"));
  }

  private Thread updateThread = new Thread(() -> {
    log(blue("Update thread starting."));

    while (!Thread.interrupted()) {
      this.update();

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        break;
      }
    }

    log(red("Update thread stopped."));
  });

  private void update() {
    this.table.getRandom().ifPresent(peer -> {
      try {
        log(blue("Requesting table from '" + peer.getName() + "' …"));
        peer.send((is, os) -> {
          os.writeObject(this.name);
          os.writeObject(new InetSocketAddress(this.getAddress(), this.getPort()));
          os.writeObject(new Command(CommandType.GET_TABLE, this.name));

          try {
            final var peers = (Table) is.readObject();
            log(blue("Received table with " + peers.size() + " peers: " + peers));

            synchronized (this.table) {
              final var addedAndRemoved = this.table.merge(peers);
              final var added = addedAndRemoved.getKey();
              final var removed = addedAndRemoved.getValue();

              added.forEach(entry -> {
                log(green("Added peer '" + entry.getKey() + "'."));
              });

              removed.forEach(entry -> {
                log(red("Removed peer '" + entry.getKey() + "'."));
              });
            }
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }
        });
      } catch (IOException e) {
        // Remove offline peer.
        log(red("Removing unreachable peer '" + peer.getKey() + "' …"));
        this.removePeer(peer.getKey());
        this.update();
      }
    });
  }

  public InetSocketAddress lookup(String name) {
    var hops = new HashSet<Peer>();
    hops.add(new Peer(this.getName(), this.getSocketAddress()));

    return lookup(name, hops);
  }

  private InetSocketAddress lookup(String name, Set<Peer> hops) {
    if (this.table.contains(name)) {
      return this.table.get(name);
    }

    System.err.println(this.name + ": Looking up '" + name + "'.");

    hops.add(new Peer(this.getName(), this.getSocketAddress()));

    return this.table.getEntrySet().stream().map(Peer::new).filter(peer -> !hops.contains(peer)).findFirst().map(peer -> {
      try {
        return peer.send((is, os) -> {
          os.writeObject(this.name);
          os.writeObject(new InetSocketAddress(this.getAddress(), this.getPort()));
          os.writeObject(new Command(CommandType.LOOKUP, this.name));
          os.writeObject(name);
          os.writeObject(hops);

          try {
            final var address = (InetSocketAddress)is.readObject();
            return address;
          } catch (ClassNotFoundException e) {
            return null;
          }
        });
      } catch (IOException e) {
        log(red("Removing unreachable peer '" + peer.getName() + "' …"));
        this.removePeer(peer.getKey());
        return this.lookup(name, hops);
      }
    }).orElse(null);
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
