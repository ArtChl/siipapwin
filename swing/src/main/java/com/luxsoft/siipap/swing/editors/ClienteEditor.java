package com.luxsoft.siipap.swing.editors;

import java.beans.PropertyEditorSupport;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.managers.CatalogosManager_old;


public class ClienteEditor extends PropertyEditorSupport{
	
	
	private CatalogosManager_old manager;
	
	

	@Override
	public String getAsText() {		
		if(getValue()!=null){
			Cliente c=(Cliente)getValue();
			return c.getClave();
		}
		return "";
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Cliente c=getManager().buscarClientePorClave(text);
		if(c!=null){
			setValue(c);
		}
	}
	
	

	public CatalogosManager_old getManager() {
		return manager;
	}

	public void setManager(CatalogosManager_old manager) {
		this.manager = manager;
	}
	
	
	
	
	

}
