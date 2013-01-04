import org.mkosciesza.capclient.*;

/**
 * Example usage of CapClient class.
 * @author Michal Kosciesza
 */
public class MyCapAgent implements CapListener {
	
	public CapClient capClient;

	/**
	 * Main function.
	 * @param args host port userID password
	 */
	public static void main(String[] args) {
		
		if(args.length==4) {
			
			new MyCapAgent(args[0], Integer.parseInt(args[1]), args[2], args[3]);
			
		} else {
			
			System.out.println("Wrong number of parameters! Enter: <host> <port> <userid> <password>");
			
		}
		
	}
	
	public MyCapAgent(String host, int port, String userid, String password) {
		
		capClient = new CapClient();
		capClient.addCallUpdateListener(this);
		capClient.addConnectedListener(this);
		capClient.addConnectionFailureListener(this);
		capClient.addDisconnectedListener(this);
		
		System.out.println("Connecting to CAP server...");
		
		capClient.connect(host, port, userid, password);
		
	}

	@Override
	public void connectedHandler(ConnectedEvent event) {
		
		System.out.println("Connection was successfully established!");
		
	}

	@Override
	public void connectionFailureHandler(ConnectionFailureEvent event) {
		
		switch(event.causeCode) {
		case 1:
			System.out.println("Cannot establish a connection, network might be blocked!");
			break;
		case 2:
			System.out.println("Cannot establish a connection, userID/password might be incorrect!");
			break;
		}
		
		System.exit(0);
		
	}

	@Override
	public void disconnectedHandler(DisconnectedEvent event) {
		
		System.out.println("Disconnected from CAP server!");
		System.exit(0);
		
	}

	@Override
	public void callUpdateHandler(CallUpdateEvent event) {
		
		System.out.println("Call update event");
		
		switch(event.personality) {
		case 1:
			System.out.println(" outgoing call");
			break;
		case 2:
			System.out.println(" incomming call");
			break;
		}
		
		switch(event.state) {
		case 0:
			System.out.println(" idle state");
			break;
		case 1:
			System.out.println(" alerting state");
			break;
		case 2:
			System.out.println(" active state");
			break;
		case 3:
			System.out.println(" held state");
			break;
		case 4:
			System.out.println(" remote held state");
			break;
		case 5:
			System.out.println(" released state");
			break;
		case 6:
			System.out.println(" detached state");
			break;
		case 7:
			System.out.println(" client alerting state");
			break;	
			
		}	
		
	}

}
