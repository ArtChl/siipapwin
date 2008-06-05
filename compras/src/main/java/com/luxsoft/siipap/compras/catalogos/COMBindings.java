package com.luxsoft.siipap.compras.catalogos;

import javax.swing.JComboBox;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.domain.Clase;
import com.luxsoft.siipap.domain.Linea;
import com.luxsoft.siipap.domain.Marca;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;

public class COMBindings extends Binder{
	
	
	@SuppressWarnings("unchecked")
	public static JComboBox createLineasBinding(final ValueModel model){
		final EventList<Linea> lineas=GlazedLists.eventList(ServiceLocator.getUniversalDao().getAll(Linea.class));
		final SelectionInList list=new SelectionInList(lineas,model);
		return BasicComponentFactory.createComboBox(list);
		/**
		return createBindingBox(model, lineas);/*, GlazedLists.textFilterator(new String[]{}), new Format(){			
			public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
				final Linea l=(Linea)obj;
				if(l!=null)
					toAppendTo.append(l.getNombre());
				return toAppendTo;
			}			
			public Object parseObject(String source, ParsePosition pos) {				
				return null;
			}			
		});*/
		
	}
	
	@SuppressWarnings("unchecked")
	public static JComboBox createClasesBinding(final ValueModel model){
		final EventList<Clase> clases=GlazedLists.eventList(ServiceLocator.getUniversalDao().getAll(Clase.class));
		final SelectionInList list=new SelectionInList(clases,model);
		return BasicComponentFactory.createComboBox(list);
		/**
		return createBindingBox(model, clases, GlazedLists.textFilterator(new String[]{}), new Format(){			
			public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
				final Clase l=(Clase)obj;
				if(l!=null)
					toAppendTo.append(l.getNombre());
				return toAppendTo;
			}			
			public Object parseObject(String source, ParsePosition pos) {				
				return null;
			}			
		});**/
	}
	
	@SuppressWarnings("unchecked")
	public static JComboBox createMarcaBinding(final ValueModel model){
		final EventList<Marca> marcas=GlazedLists.eventList(ServiceLocator.getUniversalDao().getAll(Marca.class));
		final SelectionInList list=new SelectionInList(marcas,model);
		return BasicComponentFactory.createComboBox(list);
		/**
		return createBindingBox(model, marcas, GlazedLists.textFilterator(new String[]{}), new Format(){			
			public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
				final Marca l=(Marca)obj;
				if(l!=null)
					toAppendTo.append(l.getNombre());
				return toAppendTo;
			}			
			public Object parseObject(String source, ParsePosition pos) {				
				return null;
			}			
		});
		**/
	}
	
	public static JComboBox createClasificacionBox(final ValueModel model){
		SelectionInList list=new SelectionInList(new String[]{"L","E"},model);
		return BasicComponentFactory.createComboBox(list,new TransformerRenderer(){

			@Override
			protected Object transform(Object val) {
				String s=val.toString();
				if("L".equals(s))
					return "DE LINEA";
				else if("E".equals(s))
					return "ESPECIAL";
				else return "";
			}
			
		});
		
	}
	
	public static JComboBox createAcabadoBox(final ValueModel model){
		SelectionInList list=new SelectionInList(new String[]{"BRILLANTE","MATE","ALTO BRILLO","NA"},model);
		return BasicComponentFactory.createComboBox(list);		
	}
	
	public static JComboBox createColorBox(final ValueModel model){
		SelectionInList list=new SelectionInList(new String[]{
				"BLANCO"
				,"NEGRO"
				,"ROJO"}
		,model);
		return BasicComponentFactory.createComboBox(list);		
	}

}
