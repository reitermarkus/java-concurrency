package ex5.task2.part2;

import java.math.BigInteger;

/**
 * Created by Michael Kaltschmid on 10.12.2017.
 */
public final class ImmutableCache {
  private final BigInteger[] lastFactors;
  private final BigInteger lastNumber;

  public ImmutableCache(BigInteger[] lastFactors, BigInteger lastNumber) {
    this.lastFactors = lastFactors;
    this.lastNumber = lastNumber;
  }

  public BigInteger[] getLastFactors() {
    return lastFactors;
  }

  public BigInteger getLastNumber() {
    return lastNumber;
  }
}
