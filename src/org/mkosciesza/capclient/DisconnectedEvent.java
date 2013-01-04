package org.mkosciesza.capclient;

/**
 * This class represents event generated when client disconnects from CAP server.
 * @author Michal Kosciesza
 */
public class DisconnectedEvent extends CapEvent {

	/**
	 * Sole constructor.
	 */
	public DisconnectedEvent(Object source) {
		super(source);
	}

}
