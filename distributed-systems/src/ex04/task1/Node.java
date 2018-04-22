package ex04.task1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static ex04.task1.ANSI.*;

public class Node implements Runnable {
  private Map<String, InetSocketAddress> peers = new HashMap<>();
  private Map<UUID, String> messageBuffer = new HashMap<>();
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
    return this.table.add(name, peerAddress);
  }

  public boolean removePeer(String name) {
    return this.table.remove(name);
  }

  private void addToBuffer(Command command) {
    synchronized (this.messageBuffer) {
      if (this.messageBuffer.size() > 99) {
        messageBuffer.remove(this.messageBuffer.entrySet().toArray()[0]);
      }

      messageBuffer.putIfAbsent(command.getCmdId(), command.getMessage());
    }
  }

  public boolean broadcast(String message) {
    return broadcast(new Command(CommandType.MESSAGE, this.name, message), true);
  }

  private boolean broadcast(Command command, boolean init) {
    if (!messageBuffer.containsKey(command.getCmdId())) {
      if (!init) {
        addToBuffer(command);
        System.out.println(command.getSender() + ": Sent message: " + command.getMessage());
      }

      final var peers = this.table.getEntrySet().stream().map(Peer::new).collect(Collectors.toList());

      for (final var peer : peers) {
        try {
          peer.send((is, os) -> {
            try {
              os.writeObject(command);
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
        } catch (IOException e) {
          e.printStackTrace();
          return false;
        }
      }
    }

    return true;
  }

  private boolean handleCommand(final Command cmd, final ObjectInputStream is, final ObjectOutputStream os) throws IOException, ClassNotFoundException {
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

          log(blue("Received lookup request: " + hops + ""));

          os.writeObject(this.lookup(name, hops));
          os.writeObject(hops);
        return true;
      case MESSAGE:
        final var command = (Command) is.readObject();

        if (broadcast(command, false)) {
          return true;
        }
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

                if (this.table.merge(name, address)) {
                  log(green("Added peer '" + name + "'."));
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
          os.writeObject(this.getSocketAddress());
          os.writeObject(new Command(CommandType.GET_TABLE, this.name));

          try {
            final var peers = (Table)is.readObject();
            log(blue("Received table with " + peers.size() + " peers: " + peers));

            final var addedAndRemoved = this.table.merge(peers);
            final var added = addedAndRemoved.getKey();
            final var removed = addedAndRemoved.getValue();

            added.forEach(entry -> {
              log(green("Added peer '" + entry.getKey() + "'."));
            });

            removed.forEach(entry -> {
              log(red("Removed peer '" + entry.getKey() + "'."));
            });
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
    List<Peer> peers;

    synchronized (this.table) {
      if (this.table.contains(name)) {
        return this.table.get(name);
      } else {
        peers = this.table.getEntrySet().stream().map(Peer::new).collect(Collectors.toList());
      }
    }

    hops.add(new Peer(this.getName(), this.getSocketAddress()));

    for (final var peer: peers) {
      if (hops.contains(peer)) {
        continue;
      }

      try {
        log(blue("Looking up '" + name + "' via '" + peer.getName() + "'."));
        final var socketAddress = peer.send((is, os) -> {
          os.writeObject(this.name);
          os.writeObject(this.getSocketAddress());
          os.writeObject(new Command(CommandType.LOOKUP, this.name));
          os.writeObject(name);
          os.writeObject(hops);

          try {
            final var address = (InetSocketAddress)is.readObject();
            final var newHops = (Set<Peer>)is.readObject();
            hops.addAll(newHops);
            log(blue("Received lookup response: '" + address + "'"));
            return address;
          } catch (ClassNotFoundException e) {
            return null;
          }
        });

        if (socketAddress != null) {
          return socketAddress;
        }
      } catch (IOException e) {
        log(red("Removing unreachable peer '" + peer.getName() + "' …"));
        this.removePeer(peer.getKey());
      }
    }

    return null;
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
