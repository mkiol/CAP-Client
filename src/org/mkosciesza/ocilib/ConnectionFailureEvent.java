package org.mkosciesza.ocilib;

/**
 * This class represents event generated when connection to CAP server is
 * failed to established.
 * 
 * @author Michal Kosciesza
 * @version 0.1
 */
public class ConnectionFailureEvent extends CapEvent {
	
	/**
	 * The cause code of connection failure. <p>
	 * 0 - Normal<p>
	 * 1 - Unable to establish TCP connection<p>
	 * 2 - CAP registration failed<p>
	 */
	public int causeCode = 0;
	
	/**
	 * Short description of the cause of connection failure.
	 */
	public String causeText = "";
	
	/**
	 * Sole constructor.
	 */
	public ConnectionFailureEvent(Object source) {
		super(source);
	}

}
