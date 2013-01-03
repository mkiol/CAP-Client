package org.mkosciesza.ocilib;

public interface CapListener {
	
	/**
	 * Is invoked when connection and CAP registration is successfully finished.
	 */
	public void connectedHandler(ConnectedEvent event);
	
	/**
	 * Is invoked when connection to BW host or CAP registration is failed.
	 */
	public void connectionFailureHandler(ConnectionFailureEvent event);
	
	/**
	 * Is invoked when connection has been disconnected.
	 */
	public void disconnectedHandler(DisconnectedEvent event);
	
	/**
	 * Is invoked on Call Update event.
	 */
	public void callUpdateHandler(CallUpdateEvent event);
	
}
