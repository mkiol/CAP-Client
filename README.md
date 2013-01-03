CAP Client
==========

CAP Client is a Java-API implementation of BroadWorks Client Application Protocols (abbr. CAP).
The CAP are protocols that expose an external Call Control and Call Monitoring interface to BroadWorks server. 

BroadWorks is a SIP-based Telephony Application Server developed by BroadSoft company.

What does it do?
----------------

CAP Client provides classes that enables, using Java event-driven model, listening for 
specifics telephony call events.

How to use?
-----------

Create CapClient instance and start listening of Call Update events:

    CapClient client = new CapClient();
    client.addCallUpdateListener(this);
    
Connect to CAP server:

    client.connect(host, port, userid, password);
    
Handle Call Update events:

    public void callUpdateHandler(CallUpdateEvent event) {
      ...
    }
    

