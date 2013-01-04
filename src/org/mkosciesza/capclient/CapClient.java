package org.mkosciesza.capclient;

import java.io.*;
import java.net.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import java.security.*;
import java.util.*;

/**
 * The CapClient class provides an java event-driven API for Client Application
 * Protocol (CAP).
 * @author Michal Kosciesza
 */
public class CapClient {

	private Socket _socket;
	private PrintWriter _out;
	private BufferedReader _in;
	private Thread _reader;

	private XPathExpression exprCommandType;
	private XPathExpression exprNonce;
	private XPathExpression exprState;
	private XPathExpression exprPersonality;
	private XPathExpression exprReleaseCause;
	private XPathExpression exprRemoteNumber;
	private XPathExpression exprFailureCause;
	private XPathExpression exprRemoteName;
	private XPathExpression exprCallType;

	private String _appid;
	private String _host;
	private int _port;
	private String _userid;
	private String _password;

	private List<CapListener> _listenersConnected = new ArrayList<CapListener>();
	private List<CapListener> _listenersDisconnected = new ArrayList<CapListener>();
	private List<CapListener> _listenersCall = new ArrayList<CapListener>();
	private List<CapListener> _listenersFailure = new ArrayList<CapListener>();

	/**
	 * Constructs CapClient with specific application name.
	 * @param appid
	 *            CAP application name
	 */
	public CapClient(String appid) {

		_appid = appid;

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		try {

			exprCommandType = xpath
					.compile("/BroadsoftDocument/command/@commandType");
			exprNonce = xpath
					.compile("/BroadsoftDocument/command/commandData/nonce");
			exprState = xpath
					.compile("/BroadsoftDocument/command/commandData/user/call/state");
			exprPersonality = xpath
					.compile("/BroadsoftDocument/command/commandData/user/call/personality");
			exprReleaseCause = xpath
					.compile("/BroadsoftDocument/command/commandData/user/call/releaseCause");
			exprRemoteNumber = xpath
					.compile("/BroadsoftDocument/command/commandData/user/call/remoteNumber");
			exprFailureCause = xpath
					.compile("/BroadsoftDocument/command/commandData/user/failure/@failureCause");
			exprRemoteName = xpath
					.compile("/BroadsoftDocument/command/commandData/user/call/remoteName");
			exprCallType = xpath
					.compile("/BroadsoftDocument/command/commandData/user/call/callType");

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Constructs CapClient with default application name.
	 */
	public CapClient() {
		this("CapClient");
	}

	/**
	 * Connects to CAP server and performs registration.
	 * @param host
	 *            address of BW web server
	 * @param port
	 *            port of CAP server
	 * @param userid
	 *            BW user ID
	 * @param password
	 *            BW user password
	 */
	public void connect(String host, int port, String userid, String password) {

		if (isConnected()) {
			// System.out.println("Already connected!");
			return;
		}

		_host = host;
		_port = port;
		_userid = userid;
		_password = password;

		_reader = new Thread(new OciReceiver(this));
		_reader.start();

	}

	/**
	 * Disconnects from CAP server.
	 */
	public void disconnect() {

		if (_socket != null && _socket.isConnected()) {
			sendCAPunregisterRequest();
			try {
				_socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		DisconnectedEvent event = new DisconnectedEvent(this);
		_fireOciEvent(event);

	}

	/**
	 * Indicates connection state.
	 * @return <code>true</code> if connection is established
	 */
	public boolean isConnected() {

		if (_socket != null)
			return _socket.isConnected();
		else
			return false;

	}

	private void sendCAPregisterAuthentication() {

		// System.out.println("sendCAPregisterAuthentication");
		String request = new String(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<BroadsoftDocument protocol=\"CAP\" version=\"16.0\">"
						+ "<command commandType=\"registerAuthentication\">"
						+ "<commandData>" + "<user userType=\"CallClient\">"
						+ "<id>" + _userid + "</id>" + "<applicationId>"
						+ _appid + "</applicationId>" + "</user>"
						+ "</commandData>" + "</command>"
						+ "</BroadsoftDocument>");

		_out.println(request);

	}

	private void sendCAPregisterRequest(String pass) {

		// System.out.println("sendCAPregisterRequest");
		String request = new String(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<BroadsoftDocument protocol=\"CAP\" version=\"16.0\">"
						+ "<command commandType=\"registerRequest\">"
						+ "<commandData>" + "<user userType=\"CallClient\">"
						+ "<id>" + _userid + "</id>" + "<securePassword>"
						+ pass + "</securePassword>" + "<applicationId>"
						+ _appid + "</applicationId>" + "</user>"
						+ "</commandData>" + "</command>"
						+ "</BroadsoftDocument>");

		_out.println(request);

	}

	private void sendCAPunregisterRequest() {

		// System.out.println("sendCAPunregisterRequest");
		String request = new String(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<BroadsoftDocument protocol=\"CAP\" version=\"16.0\">"
						+ "<command commandType=\"unRegister\">"
						+ "<commandData>"
						+ "<user userLogoutReason=\"ClientLogout\" userType=\"CallClient\" "
						+ "id=\"" + _userid + "\">" + "<applicationId>"
						+ _appid + "</applicationId>" + "</user>"
						+ "</commandData>" + "</command>"
						+ "</BroadsoftDocument>");

		//System.out.println(request);
		_out.println(request);

	}

	private void sendCAPacknowledgement() {

		// System.out.println("sendCAPacknowledgement");
		String request = new String(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<BroadsoftDocument protocol=\"CAP\" version=\"16.0\">"
						+ "<command commandType=\"acknowledgement\">"
						+ "<commandData>"
						+ "<user userType=\"CallClient\" id=\"" + _userid
						+ "\">" + "<message messageName=\"registerResponse\"/>"
						+ "<applicationId>" + _appid + "</applicationId>"
						+ "</user>" + "</commandData>" + "</command>"
						+ "</BroadsoftDocument>");
		_out.println(request);

	}

	private void receive(String bwdoc) {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new ByteArrayInputStream(bwdoc
					.getBytes()));
			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();
			String protocol = root.getAttributeNode("protocol").getValue();
			// String version = root.getAttributeNode("version").getValue();
			// System.out.println("Protocol:" + protocol);
			// System.out.println("Version:" + version);

			if (protocol == null) {
				return;
			} else if (!protocol.equals("CAP")) {
				System.out.println("Only CAP is supported!");
				return;
			}

			String commandType = (String) exprCommandType.evaluate(doc,
					XPathConstants.STRING);
			//System.out.println("commandType: " + commandType);

			switch (commandType) {
			case "responseAuthentication":
				responseAuthenticationHandler(doc);
				break;
			case "registerResponse":
				registerResponseHandler(doc);
				break;
			case "callUpdate":
				callUpdateHandler(doc);
				break;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void callUpdateHandler(Document doc) {

		CallUpdateEvent event = new CallUpdateEvent(this);

		try {
			event.state = Integer.parseInt(((String) exprState.evaluate(doc,
					XPathConstants.STRING)));
			event.personality = Integer.parseInt(((String) exprPersonality
					.evaluate(doc, XPathConstants.STRING)));
			event.releaseCause = Integer.parseInt(((String) exprReleaseCause
					.evaluate(doc, XPathConstants.STRING)));
			event.callType = Integer.parseInt(((String) exprCallType.evaluate(
					doc, XPathConstants.STRING)));
			event.remoteNumber = (String) exprRemoteNumber.evaluate(doc,
					XPathConstants.STRING);
			event.remoteName = (String) exprRemoteName.evaluate(doc,
					XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		_fireOciEvent(event);

	}

	private void registerResponseHandler(Document doc)
			throws XPathExpressionException {

		String failureCause = (String) exprFailureCause.evaluate(doc,
				XPathConstants.STRING);

		if (failureCause.equals("")) {
			sendCAPacknowledgement();
			ConnectedEvent event = new ConnectedEvent(this);
			_fireOciEvent(event);
		} else {
			/*
			 * System.out.println("CAP registration failed! failureCause=" +
			 * failureCause);
			 */
			ConnectionFailureEvent event = new ConnectionFailureEvent(this);
			event.causeCode = 2;
			event.causeText = failureCause;
			_fireOciEvent(event);
			disconnect();
		}

	}

	private void responseAuthenticationHandler(Document doc)
			throws XPathExpressionException {

		String nonce = (String) exprNonce.evaluate(doc, XPathConstants.STRING);
		//System.out.println("nonce: " + nonce);

		try {
			String s1 = sha1(_password);
			String s2 = nonce + ":" + s1;
			String md5h = md5(s2);
			sendCAPregisterRequest(md5h);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void finalize() {

		disconnect();

	}

	// Listeners

	/**
	 * Adds a CapListener for connected event.
	 * @param l
	 *            the listener to be added
	 */
	public synchronized void addConnectedListener(CapListener l) {

		_listenersConnected.add(l);

	}

	/**
	 * Removes an CapListener for connected event.
	 * @param l
	 *            the listener to be removed
	 */
	public synchronized void removeConnectedListener(CapListener l) {

		_listenersConnected.remove(l);

	}

	/**
	 * Adds a CapListener for disconnected event.
	 * @param l
	 *            the listener to be added
	 */
	public synchronized void addDisconnectedListener(CapListener l) {

		_listenersDisconnected.add(l);

	}

	/**
	 * Removes an CapListener for disconnected event.
	 * @param l
	 *            the listener to be removed
	 */
	public synchronized void removeDisconnectedListener(CapListener l) {

		_listenersDisconnected.remove(l);

	}

	/**
	 * Adds a CapListener for call update event. 
	 * @param l
	 *            the listener to be added
	 */
	public synchronized void addCallUpdateListener(CapListener l) {

		_listenersCall.add(l);

	}

	/**
	 * Removes an CapListener for call update event.
	 * @param l
	 *            the listener to be removed
	 */
	public synchronized void removeCallUpdateListener(CapListener l) {

		_listenersCall.remove(l);

	}

	/**
	 * Adds a CapListener for connection failure event. 
	 * @param l
	 *            the listener to be added
	 */
	public synchronized void addConnectionFailureListener(CapListener l) {

		_listenersFailure.add(l);

	}

	/**
	 * Removes an CapListener for connected failure event.
	 * @param l
	 *            the listener to be removed
	 */
	public synchronized void removeConnectionFailureListener(CapListener l) {

		_listenersFailure.remove(l);

	}

	private synchronized void _fireOciEvent(CapEvent event) {

		Iterator<CapListener> listeners = null;

		if (event.getClass() == ConnectedEvent.class) {
			listeners = _listenersConnected.iterator();
			while (listeners != null && listeners.hasNext()) {
				((CapListener) listeners.next())
						.connectedHandler((ConnectedEvent) event);
			}
		} else if (event.getClass() == DisconnectedEvent.class) {
			listeners = _listenersDisconnected.iterator();
			while (listeners != null && listeners.hasNext()) {
				((CapListener) listeners.next())
						.disconnectedHandler((DisconnectedEvent) event);
			}
		} else if (event.getClass() == CallUpdateEvent.class) {
			listeners = _listenersCall.iterator();
			while (listeners != null && listeners.hasNext()) {
				((CapListener) listeners.next())
						.callUpdateHandler((CallUpdateEvent) event);
			}
		} else if (event.getClass() == ConnectionFailureEvent.class) {
			listeners = _listenersCall.iterator();
			while (listeners != null && listeners.hasNext()) {
				((CapListener) listeners.next())
						.connectionFailureHandler((ConnectionFailureEvent) event);
			}
		}

	}

	// --

	private String convToHex(byte[] data) {

		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();

	}

	private String sha1(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("UTF-8"), 0, text.length());
		sha1hash = md.digest();
		return convToHex(sha1hash);

	}

	private String md5(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(text.getBytes("UTF-8"), 0, text.length());
		byte[] md5hash = md.digest();
		return convToHex(md5hash);

	}

	private class OciReceiver implements Runnable {

		private String lines = "";
		private CapClient _oci;

		public OciReceiver(CapClient oci) {
			_oci = oci;
		}

		@Override
		public void run() {
			if (connect()) {
				while (true) {
					try {
						lines += _in.readLine();
						// System.out.println(lines);
						getBwDoc();
					} catch (IOException e) {
						System.out.println("Error: Read failed!");
						disconnect();
					}
				}
			} else {
				disconnect();
			}
		}

		private boolean connect() {
			try {
				//System.out.println("Connecting to " + _host + "...");
				_socket = new Socket(_host, _port);
				_out = new PrintWriter(_socket.getOutputStream(), true);
				_in = new BufferedReader(new InputStreamReader(
						_socket.getInputStream()));
			} catch (UnknownHostException e) {
				// System.out.println("Unknown host: " + _host);
				ConnectionFailureEvent event = new ConnectionFailureEvent(_oci);
				event.causeCode = 1;
				event.causeText = e.toString();
				_fireOciEvent(event);
				return false;
			} catch (IOException e) {
				// System.out.println("No I/O");
				ConnectionFailureEvent event = new ConnectionFailureEvent(_oci);
				event.causeCode = 1;
				event.causeText = e.toString();
				_fireOciEvent(event);
				return false;
			}
			// System.out.println("Connected!");

			sendCAPregisterAuthentication();

			return true;
		}

		private Boolean getBwDoc() {
			String ss = "<BroadsoftDocument";
			String es = "</BroadsoftDocument>";

			int si = lines.indexOf(ss);
			if (si != -1) {
				int ei = lines.indexOf(es);
				if (ei != -1) {
					_oci.receive(lines.substring(si, ei + es.length()));
					lines = "";
					return true;
				}
			}
			return false;
		}

	}

}
