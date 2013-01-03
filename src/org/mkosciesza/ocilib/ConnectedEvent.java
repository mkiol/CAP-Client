package org.mkosciesza.ocilib;

/**
 * This class represents event generated when connection to CAP server is
 * established.
 * 
 * @author Michal Kosciesza
 * @version 0.1
 */
public class ConnectedEvent extends CapEvent {

	/**
	 * Sole constructor.
	 */
	public ConnectedEvent(Object source) {
		super(source);
	}

}
