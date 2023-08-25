/**
 * 
 */
package de.prob.cli.clipatterns;

import java.util.regex.Matcher;

import de.prob.cli.CliException;
import de.prob.logging.Logger;

/**
 * This {@link CliPattern} looks for a network port number where the executable
 * listens for commands.
 * 
 * If no port number is found, {@link #notFound()} throws a {@link CliException}
 * 
 * @author plagge
 */
public class PortPattern extends CliPattern<Integer> {
	int port;

	public PortPattern() {
		super("Port: (\\d+)$");
	}

	@Override
	protected void setValue(Matcher matcher) throws IllegalArgumentException {
		port = Integer.parseInt(matcher.group(1));
		Logger.info("Server has startet and listens on port " + port);
	}

	/**
	 * Returns the port number.
	 */
	@Override
	public Integer getValue() {
		return port;
	}

	@Override
	public void notFound() throws CliException {
		throw new CliException("Could not determine port of ProB server");
	}

}
