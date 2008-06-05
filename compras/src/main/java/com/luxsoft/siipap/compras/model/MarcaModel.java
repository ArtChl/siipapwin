package com.luxsoft.siipap.compras.model;

import java.util.List;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.domain.Marca;
import com.luxsoft.siipap.managers.CatalogosManager;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

public class MarcaModel extends DefaultFormModel{
	
	private CatalogosManager manager;
	private List<Marca> marcas;

	public MarcaModel(final Marca m){
		super(m);
	}
	
	public MarcaModel(final Marca m,boolean readOnly){
		super(m,readOnly);
	}
	
	public MarcaModel() {
		super(new Marca());
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
				support.addError("nombre", "Esta marca ya esta registrada");
			}
		}
	}
	
	private boolean existe(){
		if(marcas==null){
			marcas=getManager().buscarMarcas();
		}
		return marcas.contains((Marca)getBaseBean());
	}

	public CatalogosManager getManager() {
		return manager;
	}
	public void setManager(CatalogosManager manager) {
		this.manager = manager;
	}
	
	

}
