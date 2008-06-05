package com.luxsoft.siipap.inventarios;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.apache.log4j.Logger;

import com.jgoodies.uif.builder.MenuBuilder;
import com.luxsoft.siipap.swing.impl.MenuFactoryImpl;

public class InvMenuFactory extends MenuFactoryImpl{
	
	@SuppressWarnings("unused")
	private Logger logger=Logger.getLogger(getClass());
	
	protected void buildCustomMenus(List<JMenu> customMenus){		
		customMenus.add(buildConsultas());
		customMenus.add(buildMantenimiento());
		customMenus.add(buildProcesos());
		
	}
	
	protected JMenuBar buildMenuBar(){
		JMenuBar bar=super.buildMenuBar();		
		return bar;
	}
	
	private JMenu buildConsultas(){		
		MenuBuilder builder=new MenuBuilder("Consultas",'n');
		builder.add(getActionManager().getAction(InvActions.ShowAnalisisDeMovimientosView.getId()));
		builder.add(getActionManager().getAction(InvActions.ShowAnalisisDeCostosView.getId()));
		builder.add(getActionManager().getAction(InvActions.ShowInventarioDeMaquilaView.getId()));
		builder.add(getActionManager().getAction(InvActions.ShowReportsView.getId()));
		return builder.getMenu();
	}	
	
	
	protected JMenu buildMantenimiento(){		
		MenuBuilder builder=new MenuBuilder("Mantenimiento",'t');	
		
		return builder.getMenu();
	}
	
	private JMenu buildProcesos(){		
		MenuBuilder builder=new MenuBuilder("Procesos",'P');
		return builder.getMenu();
	}
	
	
	
	
}
