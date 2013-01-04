package org.mkosciesza.capclient;

import java.util.EventObject;

/**
 * This abstracts class represents CAP event.
 * @author Michal Kosciesza
 */
public abstract class CapEvent extends EventObject {
	
	/**
	 * Sole constructor.
	 */
	public CapEvent(Object source) {
		super(source);
	}

}
