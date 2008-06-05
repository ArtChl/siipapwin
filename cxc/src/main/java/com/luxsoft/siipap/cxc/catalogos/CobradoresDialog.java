package com.luxsoft.siipap.cxc.catalogos;

import java.util.List;

import javax.swing.Action;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.cxc.domain.Cobrador;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.dialog.AbstractCatalogDialog;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

public class CobradoresDialog extends AbstractCatalogDialog<Cobrador>{

	public CobradoresDialog() {
		super(new BasicEventList<Cobrador>(), "Cobradores");
		
	}

	@Override
	protected List<Cobrador> getData() {
		return ServiceLocator.getCatalogosManager().buscarCobradores();
	}
	
	protected List<Action> getToolbarActions(){
		final List<Action> actions=super.getToolbarActions();
		actions.remove(1); //Bajas
		actions.remove(5); //Filtros
		actions.remove(4); //Imprimir
		return actions;
	}

	@Override
	protected TableFormat<Cobrador> getTableFormat() {
		return GlazedLists.tableFormat(Cobrador.class
			, new String[]{"clave","nombre","comision","activo"}
			,new String[]{"Clave","Nombre","Comisión","Activo"});
	}
	
	public void view(){
		final DefaultFormModel model=new DefaultFormModel(getSelected(),true);
		final CobradoresForm form=new CobradoresForm(model);
		form.open();
	}
	
	
	public void edit(){
		final DefaultFormModel model=new DefaultFormModel(getSelected());
		final CobradoresForm form=new CobradoresForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			Cobrador c=(Cobrador)model.getBaseBean();
			ServiceLocator.getCatalogosManager().actualizarCobrador(c);
			final int index=source.indexOf(c);
			source.set(index, c);
		}
	}
	
	public Object doInsert(){
		final DefaultFormModel model=new DefaultFormModel(new Cobrador());
		final CobradoresForm form=new CobradoresForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			Cobrador c=(Cobrador)model.getBaseBean();
			ServiceLocator.getCatalogosManager().salvarCobrador(c);
			return c;
		}
		return null;
	}
	
	public static void main(String[] args) {
		final CobradoresDialog dialog=new CobradoresDialog();
		dialog.open();
		System.exit(0);
	}

}
