package src;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


/*
 *   Copyright (C) 2007-2009 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * Various ID generators.
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public class GenerateID {
	static final Random rand = new Random();

	public static void p(Object o) {
		System.out.println((o == null ? o : o.toString()));
	}
	
	/**
	 * Correct md5 implementation
	 */
	public static String md5sum(String s) {
		StringBuffer hash = new StringBuffer();
		
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			
			byte[] b = s.getBytes();
			b = md5.digest(b);
			
			for (byte bb : b) {
				String hex = Integer.toHexString((int)bb & 0xff);
				while (hex.length() < 2) hex = '0' + hex;
				hash.append(hex);
			}
			
		} catch (NoSuchAlgorithmException e) {}
		
		return hash.toString();
	}

	/**
	 * @param s is the string to calculate the hash from
	 * @return The MD5-Hash of the input string.
	 */
	public static String getMD5(String s) {
		StringBuffer out = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] b = s.getBytes();

			b = md.digest(b);

			for (int i = 0; i < b.length; i++) {
				out.append((int) b[i]);
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	/**
	 * Get the hex value of an Integer.
	 * @see toHexBi(BigInteger input) for bigger integers
	 * @param input is the Integer to calculate the hex value from
	 * @return the input in hexadecimal description
	 */
	public static String toHex(long input, boolean lowercase) {
		StringBuffer hex = new StringBuffer();
		for (byte i = 1; i < 20; i++) {
			byte c = (byte) (input % 16);
			hex.append(getHexChar(c, lowercase));
			input -= c;
			input = (long) input / 16;
		}
		hex = hex.reverse();
		while (hex.length() > 0)
			if (hex.charAt(0) == '0')
				hex.deleteCharAt(0);
			else
				break;
		return hex.toString();
	}

	/**
	 * Get the hex value of a BigInteger.
	 * @see toHex(long input)
	 * @param input is the BigInteger to calculate the hex value from
	 * @return the input in hexadecimal description
	 */
	public static String toHexBi(BigInteger input, boolean lowercase) {
		StringBuffer hex = new StringBuffer();
		for (byte i = 1; i < 20; i++) {
			byte c = Byte.parseByte(input.mod(new BigInteger("16"))
									.toString());
			hex.append(getHexChar(c, lowercase));
			input = input.subtract(new BigInteger("" + c));
			input = input.divide(new BigInteger("16"));
		}
		hex = hex.reverse();
		while (hex.length() > 0)
			if (hex.charAt(0) == '0')
				hex.deleteCharAt(0);
			else
				break;
		return hex.toString();
	}

	/**
	 * This method gives you the hex char of an integer.
	 * @param i is the input integer
	 * @return "err" if i >= 16, otherwise the char (0-9A-F)
	 */
	public static String getHexChar(byte i, boolean lowercase) {
		if (i >= 16)
			return "err";
		if (i < 10)
			return new String("" + i);
		else {
			return new String((lowercase ? "abcdef" : "ABCDEF").charAt(i - 10) + "");
		}
	}

	public static String getHexMD5id(String s, String part, boolean lowercase) {
		return "id" + getMD5Hex(s, part, lowercase);
	}

	/**
	 * @param s is the input string to generate the hash from
	 * @param part is the character which is to be used to part the sections
	 * @return An MD5 hash from the String s in hex format.
	 */
	public static String getMD5Hex(String s, String part, boolean lowercase) {
		StringBuffer hmd5 = new StringBuffer();

		s = getMD5(s);
		String[] integers = s.split("-");

		for (String integer : integers) {
			try {
				hmd5.append(toHex(Integer.parseInt(integer), lowercase) + part);
			} catch (NumberFormatException e) {
				if (integer.length() == 0)
					;
				else if (integer.length() < 10) {
					e.printStackTrace();
					System.out.println("Length < 10: >" + integer + "<");
				} else {
					BigInteger l = new BigInteger(integer);
					hmd5.append(toHexBi(l, lowercase) + part);
				}
			}
		}
		if (hmd5.length() > 0) {
			hmd5.deleteCharAt(hmd5.length() - 1);
		}

		return hmd5.toString();
	}

	/**
	 * @see Integer.parseInt("hex", 16)
	 * @see Long.parseLong("hex", 16)
	 * @param hex is the hex value
	 * @return The integer value of a hex char
	 */
	public static long hexToLong(String hex) {
		long l = 0;
		p(Long.getLong("0x14B95DA92B6CBD"));
		hex = hex.toLowerCase();
		hex = new StringBuffer(hex).reverse().toString();

		for (int i = 0; i < hex.length(); i++) {
			l += "0123456789abcdef".indexOf(hex.charAt(i)) * Math.pow(16, i);

		}

		return l;
	}
}
