package org.mkosciesza.ocilib;

import java.util.EventObject;

/**
 * This abstracts class represents CAP event.
 * 
 * @author Michal Kosciesza
 * @version 0.1
 */
public abstract class CapEvent extends EventObject {
	
	/**
	 * Sole constructor.
	 */
	public CapEvent(Object source) {
		super(source);
	}

}
