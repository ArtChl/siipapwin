package com.luxsoft.siipap.compras;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.apache.log4j.Logger;

import com.jgoodies.uif.builder.MenuBuilder;
import com.luxsoft.siipap.swing.impl.MenuFactoryImpl;

public class ComprasMenuFactory extends MenuFactoryImpl{
	
	@SuppressWarnings("unused")
	private Logger logger=Logger.getLogger(getClass());

	
	
	protected void buildCustomMenus(List<JMenu> customMenus){
		customMenus.add(buildOperaciones());
		customMenus.add(buildConsultas());
		customMenus.add(buildProcesos());
		customMenus.add(buildMantenimiento());
	}
	
	protected JMenuBar buildMenuBar(){
		JMenuBar bar=super.buildMenuBar();		
		return bar;
	}
	
	private JMenu buildOperaciones(){
		MenuBuilder builder=new MenuBuilder("Operaciones",'O');
		builder.add(getActionManager().getAction(ComprasActions.ShowRequisicionesView.getId()));
		builder.add(getActionManager().getAction(ComprasActions.ShowOrdenesView.getId()));
		builder.add(getActionManager().getAction(ComprasActions.ShowOrdenesDeMaquilaView.getId()));
		builder.add(getActionManager().getAction(ComprasActions.ShowRecepcionesView.getId()));
		builder.add(getActionManager().getAction(ComprasActions.ShowDevolucionesView.getId()));
				
		return builder.getMenu();
	}
	
	private JMenu buildConsultas(){		
		MenuBuilder builder=new MenuBuilder("Consultas",'n');
		builder.add(getActionManager().getAction(ComprasActions.ConsultasView.getId()));
		builder.add(getActionManager().getAction(ComprasActions.AltI.getId()));
		return builder.getMenu();
	}	
	
	
	protected JMenu buildMantenimiento(){		
		MenuBuilder builder=new MenuBuilder("Mantenimiento",'t');
		{
			MenuBuilder bb=new MenuBuilder("Catálogos",'C');
			bb.add(getActionManager().getAction(ComprasActions.CatalogoDeProveedores.getId()));
			bb.add(getActionManager().getAction(ComprasActions.CatalogoDePrecios.getId()));
			bb.add(getActionManager().getAction(ComprasActions.CatalogoDeLineas.getId()));
			bb.add(getActionManager().getAction(ComprasActions.CatalogoDeClases.getId()));
			bb.add(getActionManager().getAction(ComprasActions.CatalogoDeMarcas.getId()));
			bb.add(getActionManager().getAction(ComprasActions.CatalogoDeProductos.getId()));
			builder.add(bb.getMenu());
		}		
		return builder.getMenu();
	}
	
	private JMenu buildProcesos(){		
		MenuBuilder builder=new MenuBuilder("Procesos",'n');
		builder.add(getActionManager().getAction(ComprasActions.DepuracionDeOrdenes.getId()));		
		return builder.getMenu();
	}	
	
	
}
