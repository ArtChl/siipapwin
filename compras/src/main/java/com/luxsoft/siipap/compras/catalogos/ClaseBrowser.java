package com.luxsoft.siipap.compras.catalogos;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.domain.Clase;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.dialog.AbstractCatalogDialog;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.utils.MessageUtils;

public class ClaseBrowser extends AbstractCatalogDialog<Clase>{

	public ClaseBrowser() {
		super("Catálogo de clases");		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<Clase> getData() {
		return ServiceLocator.getUniversalDao().getAll(Clase.class);
	}

	@Override
	protected TableFormat<Clase> getTableFormat() {
		return GlazedLists.tableFormat(Clase.class
				,new String[]{"id","nombre"}
				,new String[]{"Id","Nombre"}
		);
	}
	
	@Override
	protected JComponent buildContent() {				
		JComponent c=super.buildContent();
		c.setPreferredSize(new Dimension(500,300));
		grid.packAll();
		getToolbarActions().get(2).setEnabled(false);
		getToolbarActions().get(5).setEnabled(false);
		getToolbarActions().get(6).setEnabled(false);
		return c;
	}
	
	public void view(){
		mostrar(getSelected());
	}
	
	protected Object doInsert(){
		return crear();
	}
	
	public void mostrar(final Clase c){
		final DefaultFormModel model=new DefaultFormModel(c,true);
		final ClaseForm form=new ClaseForm(model);
		form.open();
	}
	
	public Clase crear(){
		final DefaultFormModel model=new DefaultFormModel(new Clase());
		final ClaseForm form=new ClaseForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			try {
				ServiceLocator.getUniversalDao().save(model.getBaseBean());
				return (Clase)model.getBaseBean();
			} catch (Exception e) {
				MessageUtils.showError("Salvando Marca",e);
			}
		}
		return null;
	}
	
	protected boolean doDelete(){				
		ServiceLocator.getUniversalDao().remove(Clase.class,getSelected().getId());
		return true;
	}
	
}
