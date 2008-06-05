package com.luxsoft.siipap.inventarios.consultas;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Date;

import org.springframework.beans.BeanWrapperImpl;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.InventarioMaq;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;

/**
 * PresentationModel que mantiene el estado y algo del comportamiento
 * requerido para {@link AnalisisDeEntrada}
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeEntradaModel extends PresentationModel{
	
	
	private final PresentationModel inventarioModel;
	private final BeanWrapperImpl wrapper;
	private EventList<SalidaACorte> cortes;
	private EventList<EntradaDeHojas> entradasDeHojas;
	private EventList<SalidaDeHojas> salidaDeHojas;
	
	public AnalisisDeEntradaModel(){
		this(new InventarioMaq());
	}
	
	public AnalisisDeEntradaModel(final InventarioMaq inventario){
		super(inventario.getEntrada());		
		this.inventarioModel=new PresentationModel(inventario);
		this.wrapper=new BeanWrapperImpl(inventario);
	}
	
	protected InventarioMaq getInventario(){
		return (InventarioMaq)inventarioModel.getBean();
	}
	protected PresentationModel getInventarioModel(){
		return inventarioModel;
	}
	
	public EventList<SalidaACorte> getCortes(){
		if(cortes==null){
			cortes=GlazedLists.threadSafeList(GlazedLists.eventList(getInventario().getCortes()));
			final SalidaACorteEditor editor=new SalidaACorteEditor();
			getInventario().addPropertyChangeListener("fecha",editor);
			cortes=new FilterList<SalidaACorte>(cortes,editor);
		}
		return cortes;
	}
	
	public EventList<EntradaDeHojas> getEntradaDeHojas(){
		if(entradasDeHojas==null){
			entradasDeHojas=GlazedLists.threadSafeList(GlazedLists.eventList(getInventario().getEntradasDeHojas()));
			final EntradaDeHojasEditor editor=new EntradaDeHojasEditor();
			getInventario().addPropertyChangeListener("fecha",editor);
			entradasDeHojas=new FilterList<EntradaDeHojas>(entradasDeHojas,editor);
		}
		return entradasDeHojas;
	}
	public EventList<SalidaDeHojas> getSalidasDeHojas(){
		if(salidaDeHojas==null){
			salidaDeHojas=GlazedLists.threadSafeList(GlazedLists.eventList(getInventario().getSalidaDeHojas()));
			final SalidaDeHojasEditor editor=new SalidaDeHojasEditor();
			getInventario().addPropertyChangeListener("fecha",editor);
			salidaDeHojas=new FilterList<SalidaDeHojas>(salidaDeHojas,editor);
		}
		return salidaDeHojas;
	}
	
	protected EntradaDeMaterial getEntrada(){
		return (EntradaDeMaterial)getBean();
	}
	
	private NumberFormat nf1=NumberFormat.getNumberInstance();
	
	
	public String getLabel(final String property){
		try {
			final Object val=wrapper.getPropertyValue(property);
			//if("precioPorKilo".equals(property) || "precioPorM2".equals(property)){
			if(val instanceof Number){
				nf1.setMaximumFractionDigits(4);
				Number bval=(Number)val;
				return nf1.format(bval.doubleValue());
			}
			return val.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	
	
	private class SalidaACorteEditor extends AbstractMatcherEditor<SalidaACorte> implements PropertyChangeListener{		

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals("fecha")){
				final Date fecha=(Date)evt.getNewValue();
				fireChanged(new SalidaACorteMatcher(fecha));
			}			
		}
		
		private class SalidaACorteMatcher implements Matcher<SalidaACorte>{			
			private final Date fecha;			
			public SalidaACorteMatcher(final Date fecha){
				this.fecha=fecha;
			}			
			public boolean matches(SalidaACorte s) {				
					final Date forden=s.getOrden().getFecha();
					if(forden.compareTo(fecha)<=0){
						final Date fentrada=s.getEntradaReceptora().getFecha();
						System.out.println("Atenidad sel: "+fentrada);
						System.out.println("Fecha ord: "+fecha);
						return fentrada.compareTo(fecha)<=0;
							
					}
					return false;				
			}			
		}
		
	}
	
	private class EntradaDeHojasEditor extends AbstractMatcherEditor<EntradaDeHojas> implements PropertyChangeListener{		

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals("fecha")){
				final Date fecha=(Date)evt.getNewValue();
				fireChanged(new EntradaDeHojasMatcher(fecha));
			}			
		}
		
		private class EntradaDeHojasMatcher implements Matcher<EntradaDeHojas>{
			
			private final Date fecha;	
			
			public EntradaDeHojasMatcher(final Date fecha){
				this.fecha=fecha;
			}			
			public boolean matches(EntradaDeHojas e) {
				final Date fe=e.getFecha();
				return fe.compareTo(fecha)<=0;					
					
								
			}			
		}
		
	}
	
	private class SalidaDeHojasEditor extends AbstractMatcherEditor<SalidaDeHojas> implements PropertyChangeListener{		

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals("fecha")){
				final Date fecha=(Date)evt.getNewValue();
				fireChanged(new SalidaDeHojasMatcher(fecha));
			}			
		}
		
		private class SalidaDeHojasMatcher implements Matcher<SalidaDeHojas>{
			
			private final Date fecha;	
			
			public SalidaDeHojasMatcher(final Date fecha){
				this.fecha=fecha;
			}			
			public boolean matches(SalidaDeHojas e) {
				final Date fe=e.getFecha();
				return fe.compareTo(fecha)<=0;					
					
								
			}			
		}
		
	}

}
