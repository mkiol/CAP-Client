package org.mkosciesza.capclient;

/**
 * This class represents event generated when connection to CAP server is
 * established.
 * @author Michal Kosciesza
 */
public class ConnectedEvent extends CapEvent {

	/**
	 * Sole constructor.
	 */
	public ConnectedEvent(Object source) {
		super(source);
	}

}
