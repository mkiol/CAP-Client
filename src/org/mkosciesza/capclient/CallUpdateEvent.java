package org.mkosciesza.capclient;

/**
 * This class represents CAP Call Update event.
 * @author Michal Kosciesza
 */
public class CallUpdateEvent extends CapEvent {

	/**
	 * The personality of the call. The personality indicates whether this user
	 * originated this call or this call was placed to the user.
	 * <p>
	 * Possible values:
	 * <p>
	 * 0 - BroadWorks Originator
	 * <p>
	 * 1 - Originator
	 * <p>
	 * 2 - Terminator
	 * <p>
	 */
	public int personality;

	/**
	 * The state of the call.
	 * <p>
	 * Possible values:
	 * <p>
	 * 0 - idle
	 * <p>
	 * 1 - alerting
	 * <p>
	 * 2 - active
	 * <p>
	 * 3 - held
	 * <p>
	 * 4 - remote held
	 * <p>
	 * 5 - released
	 * <p>
	 * 6 - detached
	 * <p>
	 * 7 - client alerting
	 * <p>
	 */
	public int state;

	/**
	 * The release cause of the call. This tag has meaning when the call is
	 * released. Until then, it has a default value of “0” that can be ignored.
	 */
	public int releaseCause;

	/**
	 * The remote phone number in the call.
	 */
	public String remoteNumber;

	/**
	 * The remote name (if available) in the call.
	 */
	public String remoteName;

	/**
	 * The call type for this call leg.
	 * <p>
	 * 0 - unknown
	 * <p>
	 * 1 - intra-group
	 * <p>
	 * 2 - enterprise
	 * <p>
	 * 3 - network
	 * <p>
	 * 4 - network URL
	 * <p>
	 * 5 - emergency
	 * <p>
	 * 6 - repair
	 * <p>
	 * 7 - error
	 * <p>
	 * 8 - city-wide centrex
	 * <p>
	 * 9 - private dial plan
	 * <p>
	 */
	public int callType;

	/**
	 * Sole constructor.
	 */
	public CallUpdateEvent(Object source) {
		super(source);
	}

}
