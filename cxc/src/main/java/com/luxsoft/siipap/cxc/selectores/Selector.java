package com.luxsoft.siipap.cxc.selectores;

/**
 * Interfaz para hacer un Look up a una lista determinada de beans
 * 
 * 
 * @author Ruben Cancino
 *
 * @param <T>
 */
public interface Selector<T> {
	
	
	public T getSelected();
	
	public String getTitle();
	
	public void open();
	
	public boolean hasBeenCanceled();

}
