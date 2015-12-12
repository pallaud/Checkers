package Chat;

/**
 * SocketName encapsulates information about a socket connection so that it can
 * be easily displayed and used as a primary key in a hash map.
 *
 * YOU DO NOT NEED TO EDIT THIS CLASS.
 *
 * $Id: SocketName.java
 *
 * @author Jessica, Nanxi, Prachi
 * @version May 25, 2015
 * @author Period: 2
 * @author Assignment: CHECKERS
 *
 * @author Sources: Simple Chat
 */
public class SocketName implements Comparable {
	/** Host or IP address to connect to */
	private String host;

	/** Port to connect to on remote host */
	private int port;

	/** Name for this connection (optional) */
	private String name;

	/**
	 * Constructor. Checks arguments for sane values.
	 * 
	 * @param h
	 *            The hostname or IP address to connect to
	 * @param p
	 *            An integer port number, from 1025-65535
	 * @param n
	 *            An optional name to assign to this socket connection
	 * 
	 * @throws IllegalArgumentException
	 *             If the values are not acceptable (empty, out of range,
	 *             contain spaces, etc)
	 */
	public SocketName(String h, int p, String n) {
		initialize(h, p, n);
	}

	/**
	 * Constructor accepting all-String values. Converts strings to other
	 * primitives as necessary. Checks arguments for sane values.
	 * 
	 * @param h
	 *            The hostname or IP address to connect to
	 * @param p
	 *            An integer port number, from 1025-65535
	 * @param n
	 *            An optional name to assign to this socket connection
	 * 
	 * @throws IllegalArgumentException
	 *             If the values are not acceptable (empty, out of range,
	 *             contain spaces, etc)
	 */
	public SocketName(String h, String p, String n) {
		if (p == null) {
			throw new IllegalArgumentException("Port may not be null");
		}

		int iPort = -1;

		try {
			iPort = Integer.parseInt(p);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Port must be an integer");
		}

		initialize(h, iPort, n);
	}

	/**
	 * Checks the socket values for sanity, throwing an exception for values
	 * that are unacceptable. Performs final instance variable assignment once
	 * variables are checked.
	 * 
	 * @param h
	 *            The hostname or IP address to connect to
	 * @param p
	 *            An integer port number, from 1025-65535
	 * @param n
	 *            An optional name to assign to this socket connection
	 * 
	 * @throws IllegalArgumentException
	 *             If the values are not acceptable (empty, out of range,
	 *             contain spaces, etc)
	 */
	private void initialize(String h, int p, String n) {
		if (h == null) {
			throw new IllegalArgumentException("Hostname cannot be null");
		}
		if ("".equals(h)) {
			throw new IllegalArgumentException("Hostname cannot be empty");
		}
		if (h.indexOf(" ") > -1) {
			throw new IllegalArgumentException(
					"Hostname may not contain spaces");
		}
		if (h.indexOf("_") > -1) {
			throw new IllegalArgumentException(
					"Hostname may not contain underscores");
		}

		host = h;

		if (p < 0) {
			throw new IllegalArgumentException("Port may not be negative");
		}
		if (p < 1024) {
			throw new IllegalArgumentException(
					"Port may not be privileged (less than 1024)");
		}
		if (p > 65535) {
			throw new IllegalArgumentException(
					"Port may not be greater than 65535");
		}

		port = p;

		// just make sure name isn't null or empty
		if (n == null || "".equals(n)) {
			name = "Unknown";
		} else {
			name = n;
		}
	}

	/**
	 * Tests this object for equality with another object.
	 * 
	 * @param o
	 *            The other object to test for equality
	 * 
	 * @return boolean True if the objects contain the same values, false
	 *         otherwise
	 */
	public boolean equals(Object o) {
		return this.compareTo(o) == 0;
	}

	/**
	 * Compares this object to another object lexigraphically.
	 * 
	 * @param o
	 *            The other object to compare to
	 * 
	 * @return int Returns < 0 if this is less than o, > 0 if this is greater
	 *         than o, and 0 if they're equal
	 */
	public int compareTo(Object o) {
		if (o instanceof SocketName) {
			SocketName that = (SocketName) o;
			int temp;

			temp = this.host.compareTo(that.host);

			if (temp == 0) {
				temp = this.port - that.port;
			}

			return temp;
		} else {
			return (hashCode() - o.hashCode());
		}
	}

	/**
	 * Returns a string representation of this object.
	 * 
	 * @return String A one-line encapsulation of this SocketName
	 */
	public String toString() {
		return name + "@" + host + ":" + port;
	}

	/**
	 * Returns the host portion of this SocketName
	 * 
	 * @return String The host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns the port portion of this SocketName
	 * 
	 * @return int The port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the name portion of this SocketName
	 * 
	 * @return String The name
	 */
	public String getName() {
		return name;
	}
}
