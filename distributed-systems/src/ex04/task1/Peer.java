package ex04.task1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;

public class Peer implements Map.Entry<String, InetSocketAddress>, Serializable {
  @FunctionalInterface
  interface Function<T, U, R> {
    public R apply(T t, U u) throws IOException;
  }

  @FunctionalInterface
  interface BiConsumer<T, U> {
    public void accept(T t, U u) throws IOException;
  }

  private static final long serialVersionUID = 1L;

  private String name;
  private InetSocketAddress address;

  Peer(String name, InetSocketAddress address) {
    this.name = name;
    this.address = address;
  }

  Peer(Map.Entry<String, InetSocketAddress> entry) {
    this(entry.getKey(), entry.getValue());
  }

  public Socket connection() throws IOException {
    return new Socket(this.address.getAddress(), this.address.getPort());
  }

  public void send(BiConsumer<? super ObjectInputStream, ? super ObjectOutputStream> f) throws IOException {
    this.send((is, os) -> {
      f.accept(is, os);
      return null;
    });
  }

  public <R> R send(Function<? super ObjectInputStream, ? super ObjectOutputStream, R> f) throws IOException {
    final var connection = this.connection();

    try (
      final var os = new ObjectOutputStream(connection.getOutputStream());
      final var is = new ObjectInputStream(connection.getInputStream());
    ) {
      return f.apply(is, os);
    }
  }

  public String getName() {
    return this.name;
  }

  public InetSocketAddress getAddress() {
    return this.address;
  }

  public String getKey() {
    return this.name;
  }

  public InetSocketAddress getValue() {
    return this.address;
  }

  public InetSocketAddress setValue(InetSocketAddress address) {
    return this.address = address;
  }

  public String toString() {
    return this.name;
  }
}
