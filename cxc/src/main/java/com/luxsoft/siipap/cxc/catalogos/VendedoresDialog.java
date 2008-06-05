package com.luxsoft.siipap.cxc.catalogos;

import java.util.List;

import javax.swing.Action;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.cxc.domain.Vendedor;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.dialog.AbstractCatalogDialog;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

public class VendedoresDialog extends AbstractCatalogDialog<Vendedor>{

	public VendedoresDialog() {
		super(new BasicEventList<Vendedor>(), "Vendedores");
		
	}

	@Override
	protected List<Vendedor> getData() {
		return ServiceLocator.getCatalogosManager().buscarVendedores();
		
	}
	
	protected List<Action> getToolbarActions(){
		final List<Action> actions=super.getToolbarActions();
		actions.remove(1); //Bajas
		actions.remove(5); //Filtros
		actions.remove(4); //Imprimir
		return actions;
	}

	@Override
	protected TableFormat<Vendedor> getTableFormat() {
		return GlazedLists.tableFormat(Vendedor.class
			, new String[]{"clave","nombre","email","activo"}
			,new String[]{"Clave","Nombre","Email","Activo"});
	}
	
	public void view(){
		final DefaultFormModel model=new DefaultFormModel(getSelected(),true);
		final VendedorForm form=new VendedorForm(model);
		form.open();
	}
	
	
	public void edit(){
		final DefaultFormModel model=new DefaultFormModel(getSelected());		
		final VendedorForm form=new VendedorForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			Vendedor c=(Vendedor)model.getBaseBean();
			ServiceLocator.getCatalogosManager().actualizarVendedor(c);
			final int index=source.indexOf(c);
			source.set(index, c);
		}
	}
	
	public Object doInsert(){
		final DefaultFormModel model=new DefaultFormModel(new Vendedor());
		final VendedorForm form=new VendedorForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			Vendedor c=(Vendedor)model.getBaseBean();
			ServiceLocator.getCatalogosManager().salvarVendedor(c);
			return c;
		}
		return null;
	}
	
	public static void main(String[] args) {
		final VendedoresDialog dialog=new VendedoresDialog();
		dialog.open();
		System.exit(0);
	}

}
