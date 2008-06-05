package com.luxsoft.siipap.compras.model;

import java.util.List;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.domain.Linea;
import com.luxsoft.siipap.managers.CatalogosManager;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

public class LineaModel extends DefaultFormModel{
	
	private CatalogosManager manager;
	private List<Linea> lineas;

	public LineaModel(final Linea l){
		super(l);
	}
	
	public LineaModel(final Linea l,boolean readOnly){
		super(l,readOnly);
	}
	
	public LineaModel() {
		super(new Linea());
	}
	
	/**
	 * Template method para agregar validaciones
	 * 
	 * @param support
	 */
	protected void addValidation(PropertyValidationSupport support){
		if(getManager()==null){
			support.addError("nombre", "No existe CatalogosManager");
		}else{
			if(existe()){
				support.addError("nombre", "Esta línea ya esta registrada");
			}
		}
	}
	
	private boolean existe(){
		if(lineas==null){
			lineas=getManager().buscarLineas();
		}
		return lineas.contains((Linea)getBaseBean());
	}

	public CatalogosManager getManager() {
		return manager;
	}
	public void setManager(CatalogosManager manager) {
		this.manager = manager;
	}
	
	

}
