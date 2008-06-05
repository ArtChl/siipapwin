package com.luxsoft.siipap.swing.editors;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.managers.CatalogosManager_old;
import com.luxsoft.siipap.swing.AbstractDbAccessTests;


/**
 * Prueba el correcto funcionamiento del ClienteEditor ya contectado
 * a la base de datos
 * 
 * @author Ruben Cancino
 *
 */
public class ClienteEditorIntegrationTest extends AbstractDbAccessTests{
	
	CatalogosManager_old manager;
	ClienteEditor editor;
	
	
	
	
	public void testGetAsText(){
		editor=new ClienteEditor();
		editor.setManager(manager);
		String clave="U050008";
		editor.setAsText(clave);
		Cliente expected=(Cliente)editor.getValue();
		assertNotNull(expected);
		assertEquals(expected.getClave(), clave);		
	}




	public void setManager(CatalogosManager_old manager) {
		this.manager = manager;
	}

	
	
	

}
