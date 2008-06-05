package com.luxsoft.siipap.swing.editors;

import org.jmock.MockObjectTestCase;


/**
 * 
 * @author Ruben Cancino
 *
 */
public class ClienteEditorTest extends MockObjectTestCase{
	
	protected ClienteEditor editor;
	
	public void setUp(){
		editor=new ClienteEditor();
	}
	
	public void setAsTextTest(){
		//Prueba la conversion de un string a cliente
		final String clave="U050008";
		final String nombre="UNION DE CREDITO";
		
		editor.setAsText(clave);
		//Cliente c=editor.set
	}
	
	public void getAsTextTest(){
		
	}

}
