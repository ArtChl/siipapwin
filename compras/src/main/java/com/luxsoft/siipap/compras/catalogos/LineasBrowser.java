package com.luxsoft.siipap.compras.catalogos;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.compras.model.LineaModel;
import com.luxsoft.siipap.domain.Linea;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.dialog.AbstractCatalogDialog;
import com.luxsoft.siipap.swing.utils.MessageUtils;

public class LineasBrowser extends AbstractCatalogDialog<Linea>{
	
	public LineasBrowser() {
		super("Catálogo de Líneas");
	}
	
	@Override
	protected TableFormat<Linea> getTableFormat() {				
		return GlazedLists.tableFormat(Linea.class,new String[]{"id","nombre","descripcion"}, new String[]{"Id","Nombre","Descripción"});
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
	
	@Override
	protected List<Linea> getData() {
		return ServiceLocator.getCatalogosManager().buscarLineas();
	}
	
	protected Object doInsert(){
		return crearLinea();
	}
	
	public void view(){
		mostrarLinea(getSelected());
	}
	
	protected boolean doDelete(){				
		ServiceLocator.getCatalogosManager().eliminarLinea(getSelected());
		return true;
	}
	
	public Linea crearLinea(){
		final LineaModel model=new LineaModel();
		model.setManager(ServiceLocator.getCatalogosManager());
		final LineaForm form=new LineaForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			try {
				ServiceLocator.getCatalogosManager().salvarLinea((Linea)model.getBaseBean());
				return (Linea)model.getBaseBean();
			} catch (Exception e) {
				MessageUtils.showError("Salvando línea",e);
			}
		}
		return null;
	}
	
	public void mostrarLinea(final Linea l){
		final LineaModel model=new LineaModel(l,true);
		model.setManager(ServiceLocator.getCatalogosManager());
		final LineaForm form=new LineaForm(model);
		form.open();
	}

}
