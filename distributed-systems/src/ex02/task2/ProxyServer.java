package ex02.task2;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ProxyServer {
  public static final int PORT = 3128;

  Set<Integer> proxies = new HashSet<>();

  public synchronized boolean registerServer(int port) {
    return this.proxies.add(port);
  }

  public synchronized boolean deregisterServer(int port) {
    return this.proxies.remove(port);
  }

  public synchronized Integer getServer() {
    if (proxies.isEmpty()) {
      return null;
    }

    var index = new Random().nextInt(proxies.size());
    return (Integer) proxies.toArray()[index];
  }

  public static void main(String[] args) throws IOException {
    var proxyServer = new ProxyServer();

    var proxyServerSocket = new ServerSocket(PORT);

    var exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    new Thread(() -> {
      while (true) {
        try {
          var connection = proxyServerSocket.accept();

          var inputStream = new ObjectInputStream(connection.getInputStream());

          try {
            var serverPort = inputStream.readInt();
            var serverOnline = inputStream.readBoolean();

            if (serverOnline) {
              System.out.println("Registering server: " + serverPort);
              proxyServer.registerServer(serverPort);
            } else {
              System.out.println("De-registering server: " + serverPort);
              proxyServer.deregisterServer(serverPort);
            }

          } finally {
            inputStream.close();
          }
        } catch (SocketException e) {
          System.out.println("Shutting down proxy server ...");
          break;
        } catch (IOException e) {
          continue;
        }
      }
    }).start();

    var socket = new ServerSocket(Protocol.PORT);

    while (true) {
      try {
        var clientConnection = socket.accept();

        System.out.println("Client connected from " + clientConnection.getRemoteSocketAddress() + ".");

        exec.submit(() -> {
          try {
            Integer serverPort = null;
            Socket serverConnection = null;

            while (serverConnection == null) {
              serverPort = proxyServer.getServer();

              try {
                if (serverPort == null) {
                  System.out.println("No end-point server available, closing client connection.");
                  clientConnection.close();
                  return;
                }

                serverConnection = new Socket("localhost", serverPort);
              } catch (SocketException e) {
                proxyServer.deregisterServer(serverPort);
              }
            }

            var clientInput = new ObjectInputStream(clientConnection.getInputStream());
            var clientOutput = new ObjectOutputStream(clientConnection.getOutputStream());

            var serverOutput = new ObjectOutputStream(serverConnection.getOutputStream());
            var serverInput = new ObjectInputStream(serverConnection.getInputStream());

            try {
              serverOutput.writeObject(clientInput.readObject());
              var outputString = (String)serverInput.readObject();
              clientOutput.writeObject(outputString + " (from server " + serverPort + ")");
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            } finally {
              clientInput.close();
              clientOutput.close();
              serverOutput.close();
              serverInput.close();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
      } catch (SocketException e) {
        System.out.println("Shutting down ...");
        break;
      } catch (IOException e) {
        continue;
      }
    }

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
