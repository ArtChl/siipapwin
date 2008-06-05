package com.luxsoft.siipap.em.importar;

/**
 * Interfaz para la importacion de información de
 * siipap a siiapw
 * 
 * @author Ruben Cancino
 *
 */
public interface Importador {
	
	/**
	 * Importa un registro de DBF a un java bean
	 * el bean retornado no es automaticamente persistido, es decir
	 * no tiene id,El tipo y cantidad de parametros es variable y definido para
	 * cada implementacion, el arreglo de beans retorndados tambien es especifico para
	 * cada implementacion
	 * 
	 * @param params
	 * @return
	 */
	public Object[] importar(Object...params);

}
