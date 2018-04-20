package ex02.task2;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class EndPointServer {
  private static void sendRequest(final ServerSocket socket, boolean online) {
    try {
      var proxySocket = new Socket("localhost", ProxyServer.PORT);

      try {
        var outputStream = new ObjectOutputStream(proxySocket.getOutputStream());

        try {
          outputStream.flush();
          outputStream.writeInt(socket.getLocalPort());
          outputStream.writeBoolean(online);
        } finally {
          outputStream.close();
        }
      } finally {
        proxySocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void register(final ServerSocket socket) {
    sendRequest(socket, true);
  }

  public static void deRegister(final ServerSocket socket) {
    sendRequest(socket, false);
  }

  public static void main(String[] args) throws IOException {
    var socket = new ServerSocket(0);
    var exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    System.out.println("End-point server running on port " + socket.getLocalPort() + ".");

    new Thread(() -> {
      while (true) {
        try {
          var connection = socket.accept();

          System.out.println("Client connected from " + connection.getRemoteSocketAddress() + ".");

          exec.submit(() -> {
            try {
              Protocol.reply(connection);

              if (connection.isClosed()) {
                socket.close();
              }
            } catch (IOException e) {
              // Ignore.
            }
          });
        } catch (SocketException e) {
          System.out.println("Shutting down ...");
          break;
        } catch (IOException e) {
          continue;
        }
      }

      exec.shutdown();

      deRegister(socket);
    }).start();

    register(socket);
  }
}
