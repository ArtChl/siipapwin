/*
 *     file: MutableObject.java
 *  package: oreilly.hcj.datamodeling
 *
 * This software is granted under the terms of the Common Public License,
 * CPL, which may be found at the following URL:
 * http://www-124.ibm.com/developerworks/oss/CPLv1.0.htm
 *
 * Copyright(c) 2003-2005 by the authors indicated in the @author tags.
 * All Rights are Reserved by the various authors.
 *
########## DO NOT EDIT ABOVE THIS LINE ########## */

package com.luxsoft.utils.domain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;




//import com.luxsoft.luxor.utils.PropertyChangeSupport;

/**  
 * Base class for data model objects that are mutable.
 * 
 * <p>
 * All data model objects that are not constant descend from this class. It provides
 * services for handling these objects such as firing property change events.
 * </p>
 *
 * @author <a href="mailto:worderisor@yahoo.com">Robert Simmons jr.</a>
 * @version $Revision: 1.1 $
 */
public abstract class MutableObject implements Serializable {
	


	/** Utility field used by bound properties. */
	//protected final transient PropertyChangeSupport propertyChangeSupport =		new PropertyChangeSupport(this);
	protected final transient PropertyChangeSupport propertyChangeSupport=new PropertyChangeSupport(this);

	/** 
	 * Creates a new instance of MutableObject
	 */
	protected MutableObject() {
	}

	/** 
	 * Adds a PropertyChangeListener to the listener list.
	 *
	 * @param listener The listener to add.
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/** 
	 * Adds a PropertyChangeListener to the listener list for a specific property.
	 *
	 * @param property The property to listen to.
	 * @param listener The listener to add.
	 */
	public void addPropertyChangeListener(final String property,
	                                      final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(property, listener);
	}

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public abstract boolean equals(Object obj);

	/** 
	 * @see java.lang.Object#hashCode()
	 */
	public abstract int hashCode();

	/** 
	 * Removes a PropertyChangeListener to the listener list.
	 *
	 * @param listener The listener to add.
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/** 
	 * Removes a PropertyChangeListener to the listener list for a specific property.
	 *
	 * @param property The property to listen to.
	 * @param listener The listener to add.
	 */
	public void removePropertyChangeListener(final String property,
	                                         final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(property, listener);
	}
	
	

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }
    /**
    public  String  prityPrint(){
    	return ToStringBuilder
		.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
    }
    **/
}


