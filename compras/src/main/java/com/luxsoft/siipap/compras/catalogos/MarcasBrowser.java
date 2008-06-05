package com.luxsoft.siipap.compras.catalogos;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.compras.model.MarcaModel;
import com.luxsoft.siipap.domain.Marca;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.dialog.AbstractCatalogDialog;
import com.luxsoft.siipap.swing.utils.MessageUtils;

public class MarcasBrowser extends AbstractCatalogDialog<Marca>{

	public MarcasBrowser() {
		super(new BasicEventList<Marca>(), "Catálogo de marcas");
	}

	@Override
	protected List<Marca> getData() {
		return ServiceLocator.getCatalogosManager().buscarMarcas();
	}

	@Override
	protected TableFormat<Marca> getTableFormat() {				
		return GlazedLists.tableFormat(Marca.class,new String[]{"id","nombre"}, new String[]{"Id","Nombre"});
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
	
	protected Object doInsert(){
		return crearMarca();
	}
	
	public void view(){
		mostrarMarca(getSelected());
	}
	
	protected boolean doDelete(){				
		ServiceLocator.getCatalogosManager().eliminarMarca(getSelected());
		return true;
	}		
	
	public Marca crearMarca(){
		final MarcaModel model=new MarcaModel(new Marca());		
		model.setManager(ServiceLocator.getCatalogosManager());
		final MarcaForm form=new MarcaForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			try {
				ServiceLocator.getCatalogosManager().salvarMarca((Marca)model.getBaseBean());
				return (Marca)model.getBaseBean();
			} catch (Exception e) {
				MessageUtils.showError("Salvando Marca",e);
			}
		}
		return null;
	}
	
	public void mostrarMarca(final Marca m){
		final MarcaModel model=new MarcaModel(m,true);		
		model.setManager(ServiceLocator.getCatalogosManager());
		final MarcaForm form=new MarcaForm(model);
		form.open();
	}

}
