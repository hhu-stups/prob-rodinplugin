/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.domainobjects;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomSeed {

	private final BigInteger[] seed;

	public RandomSeed(final BigInteger x, final BigInteger y,
			final BigInteger z, final BigInteger b) {
		seed = new BigInteger[] { x, y, z, b };
	}

	public BigInteger getSeedX() {
		return seed[0];
	}

	public BigInteger getSeedY() {
		return seed[1];
	}

	public BigInteger getSeedZ() {
		return seed[2];
	}

	public BigInteger getSeedB() {
		return seed[3];
	}

	@Override
	public String toString() {
		return getSeedX() + ":" + getSeedY() + ":" + getSeedZ() + ":"
				+ getSeedB();
	}

	public static RandomSeed fromString(final String seed) {
		Pattern p = Pattern.compile("(\\d*):(\\d*):(\\d*):(\\d*)");
		Matcher m = p.matcher(seed);
		if (!m.matches()) {
			throw new IllegalArgumentException("Wrong format");
		}
		String[] splitSeed = seed.split(":");
		BigInteger x = new BigInteger(splitSeed[0]);
		BigInteger y = new BigInteger(splitSeed[1]);
		BigInteger z = new BigInteger(splitSeed[2]);
		BigInteger b = new BigInteger(splitSeed[3]);
		return new RandomSeed(x, y, z, b);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof RandomSeed) {
			RandomSeed seed = (RandomSeed) obj;
			if (!(seed.getSeedX().equals(this.getSeedX()))) {
				return false;
			}
			if (!(seed.getSeedY().equals(this.getSeedY()))) {
				return false;
			}
			if (!(seed.getSeedZ().equals(this.getSeedZ()))) {
				return false;
			}
			if (!(seed.getSeedB().equals(this.getSeedB()))) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int xHash = getSeedX().hashCode();
		final int yHash = getSeedY().hashCode();
		final int zHash = getSeedZ().hashCode();
		final int bHash = getSeedB().hashCode();
		return xHash + 11 * yHash + 37 * zHash + 43 * bHash;
	}
}
