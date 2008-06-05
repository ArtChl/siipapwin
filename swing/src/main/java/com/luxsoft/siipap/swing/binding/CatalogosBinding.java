package com.luxsoft.siipap.swing.binding;

import javax.swing.JComboBox;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.dao.ClaseDao;
import com.luxsoft.siipap.dao.LineaDao;
import com.luxsoft.siipap.dao.MarcaDao;
import com.luxsoft.siipap.swing.Application;

@SuppressWarnings("unchecked")
public class CatalogosBinding {
	
	
	public static JComboBox createLineasBinding(final ValueModel vm){
		EventList list=new BasicEventList();
		if(Application.isLoaded()){
			LineaDao dao=(LineaDao)Application.instance().getApplicationContext().getBean("lineaDao");
			list.addAll(dao.buscarTodas());			
		}
		return Binder.createBindingBox(vm, list);
	}
	
	public static JComboBox createMarcasBinding(final ValueModel vm){
		EventList list=new BasicEventList();
		if(Application.isLoaded()){
			MarcaDao dao=(MarcaDao)Application.instance().getApplicationContext().getBean("marcaDao");
			list.addAll(dao.buscarTodas());			
		}
		return Binder.createBindingBox(vm, list);
	}
	
	public static JComboBox createClasesBinding(final ValueModel vm){
		EventList list=new BasicEventList();
		if(Application.isLoaded()){
			ClaseDao dao=(ClaseDao)Application.instance().getApplicationContext().getBean("claseDao");
			list.addAll(dao.buscarTodas());			
		}
		return Binder.createBindingBox(vm, list);
	}
	
}
