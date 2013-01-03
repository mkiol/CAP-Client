package org.mkosciesza.ocilib;

/**
 * This class represents event generated when client disconnects from CAP server.
 * 
 * @author Michal Kosciesza
 * @version 0.1
 */
public class DisconnectedEvent extends CapEvent {

	/**
	 * Sole constructor.
	 */
	public DisconnectedEvent(Object source) {
		super(source);
	}

}
