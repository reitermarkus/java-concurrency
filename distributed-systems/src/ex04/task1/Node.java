package ex04.task1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Node implements Runnable {
  private Set<InetSocketAddress> peers = new HashSet<>();

  private InetAddress address;
  private int port;
  private ServerSocket socket;

  public Node(int port) throws UnknownHostException {
    this(InetAddress.getLocalHost(), port);
  }

  public Node(InetAddress address, int port) {
    this.address = address;
    this.port = port;
  }

  public InetAddress getAddress() {
    return this.address;
  }

  public int getPort() {
    return this.port;
  }

  public boolean addPeer(InetSocketAddress peerAddress) {
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
      if (this.peers.add(peerAddress)) {
        System.out.println(this.port + ": Added peer: " + peerAddress);
        return true;
      }

      return false;
    }
  }

  public boolean removePeer(InetSocketAddress peerAddress) {
    synchronized (this.peers) {
      if (this.peers.remove(peerAddress)) {
        System.out.println(this.port + ": Removed peer: " + peerAddress);
        return true;
      }

      return false;
    }
  }

  public void run() {
    try {
      this.socket = new ServerSocket(this.port);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    var exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    updateThread().start();

    while (!Thread.interrupted()) {
      try {
        System.out.println(this.port + ": Waiting for connection …");
        var connection = this.socket.accept();

        exec.submit(() -> {
          try {
            final var is = new ObjectInputStream(connection.getInputStream());
            final var os = new ObjectOutputStream(connection.getOutputStream());

            try {
              final var address = (InetSocketAddress)is.readObject();
              final var command = (String)is.readObject();

              this.addPeer(address);

              if (command.equals("getTable")) {
                synchronized (this.peers) {
                  os.writeObject(this.peers);
                  System.out.println(this.port + ": Sent table: " + this.peers);
                }
              }

            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            } finally {
              is.close();
              os.close();
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
    }

    exec.shutdown();

    updateThread().interrupt();

    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Thread updateThread;

  private Thread updateThread() {
    if (this.updateThread == null) {
      this.updateThread = new Thread(() -> {
        System.out.println(this.port + ": Update thread starting.");

        while (!Thread.interrupted()) {
          randomPeer().ifPresent(peer -> {
            try {
              System.out.println(this.port + ": Requesting table from '" + peer + "' …");
              final var connection = new Socket(peer.getAddress(), peer.getPort());

              try {
                final var os = new ObjectOutputStream(connection.getOutputStream());
                final var is = new ObjectInputStream(connection.getInputStream());

                os.writeObject(new InetSocketAddress(this.getAddress(), this.getPort()));
                os.writeObject("getTable");

                try {
                  final var peers = (Set<InetSocketAddress>)is.readObject();
                  System.out.println(this.port + ": Received table: " + peers);
                  peers.forEach(p -> addPeer(p));
                } catch (ClassNotFoundException e) {
                  e.printStackTrace();
                } finally {
                  os.close();
                  is.close();
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            } catch (IOException e) {
              // Remove offline peer.
              this.removePeer(peer);
            }
          });

          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            break;
          }
        }

        System.out.println(this.port + ": Update thread stopped.");
      });
    }

    return this.updateThread;
  }

  private Optional<InetSocketAddress> randomPeer() {
    if (peers.isEmpty()) {
      return Optional.empty();
    }

    var i = ThreadLocalRandom.current().nextInt(peers.size());
    return Optional.of((InetSocketAddress)peers.toArray()[i]);
  }
}
