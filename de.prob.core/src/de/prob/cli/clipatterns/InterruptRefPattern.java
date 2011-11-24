/**
 * 
 */
package de.prob.cli.clipatterns;

import java.util.regex.Matcher;

import de.prob.cli.CliException;
import de.prob.logging.Logger;

/**
 * Extracts the reference for user interrupt calls from the process' startup
 * information. The reference must be later passed to the send_interrupt command
 * when an user interrupt should be signalled.
 * 
 * @author plagge
 */
public class InterruptRefPattern extends CliPattern<Long> {

	private Long reference;

	public InterruptRefPattern() {
		super("user interrupt reference id: *(\\d+) *$");
	}

	@Override
	protected void setValue(final Matcher matcher) {
		reference = Long.parseLong(matcher.group(1));
		Logger.info("Server can receive user interrupts via reference "
				+ reference);
	}

	@Override
	public Long getValue() {
		return reference;
	}

	@Override
	public void notFound() throws CliException {
		Logger.notifyUser("Could not determine user interrupt reference of ProB server. "
				+ "You might not be able to interrupt a running calculation.");
	}

}
