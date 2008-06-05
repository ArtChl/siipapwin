package com.luxsoft.siipap.inventarios.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.SwingWorker;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.UniqueList;
import ca.odell.glazedlists.FunctionList.Function;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.MaterialHojeado;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;
import com.luxsoft.siipap.maquila.manager.MaquilaManager;

public class AnalisisDeInventarioDeMaquilaModelImpl extends PresentationModel implements AnalisisDeInventarioDeMaquilaModel{
	
	private MaquilaManager maquilaManager;	
	private Date fechaDeCorte;
	
	private EventList<EntradaDeMaterial> entradas;
	private EventList<SalidaACorte> salidas;
	private EventList<SalidaACorte> salidasACorte;
	private EventList<SalidaACorte> salidasEnProceso;
	private EventList<SalidaDeBobinas> salidaDeBobinas;
	private EventList<SalidaDeMaterial> salidasDeMaterial;
	private EventList<SalidaDeMaterial> traslados;
	
	/** Material Cortado **/	
	private EventList<SalidaDeHojas> salidaDeHojas;
	private EventList<EntradaDeHojas> entradaDeHojas;
	private EventList<SalidaDeHojas> salidasDeHojasSource;
	
	public AnalisisDeInventarioDeMaquilaModelImpl(){
		super(null);
		setBean(this);
	}

	public EventList<EntradaDeMaterial> getEntradas() {
		if(entradas==null){
			
			final EventList<EntradaDeMaterial> source=GlazedLists.threadSafeList(new BasicEventList<EntradaDeMaterial>());			
			final MenorAFilter editor1=new MenorAFilter();
			addBeanPropertyChangeListener("fechaDeCorte", editor1);
			final EventList<MatcherEditor<EntradaDeMaterial>> editores=new BasicEventList<MatcherEditor<EntradaDeMaterial>>();			
			editores.add(editor1);
			final CompositeMatcherEditor<EntradaDeMaterial> compositeEditor=new CompositeMatcherEditor<EntradaDeMaterial>(editores);
			compositeEditor.setMode(CompositeMatcherEditor.AND);			
			entradas=new FilterList<EntradaDeMaterial>(source,new ThreadedMatcherEditor<EntradaDeMaterial>(compositeEditor));
			
		}		
		return entradas;
	}
	
	public List<EntradaDeMaterial> buscarEntradas() {
		return getMaquilaManager().buscarEntradasDeMaterial();
	}
	
	public List<SalidaACorte> buscarSalidasACorte() {
		return getMaquilaManager().buscarSalidasACorte();
	}
	
	public EventList<SalidaACorte> getSalidas(){
		if(salidas==null){
			salidas=GlazedLists.threadSafeList(new BasicEventList<SalidaACorte>());
		}
		return salidas;
	} 
	
	public EventList<SalidaACorte> getSalidasACorte() {
		if(salidasACorte==null){
			//final EventList<SalidaACorte> source=GlazedLists.threadSafeList(new BasicEventList<SalidaACorte>());
			final EventList<SalidaACorte> source=getSalidas();
			final SalidaACorteEditor editor=new SalidaACorteEditor();
			addBeanPropertyChangeListener("fechaDeCorte", editor);
			salidasACorte=new FilterList<SalidaACorte>(source,editor);
		}
		return salidasACorte;
	}
	
	
	
	/**
	 * Regresa un List con las salidas que estan en proceso
	 * @return
	 */
	public EventList<SalidaACorte> getSalidasEnProceso(){
		if(salidasEnProceso==null){
			//final EventList<SalidaACorte> source=GlazedLists.threadSafeList(new BasicEventList<SalidaACorte>());
			final EventList<SalidaACorte> source=getSalidas();
			final EnProcesoDeCorteEditor editor=new EnProcesoDeCorteEditor();
			addBeanPropertyChangeListener("fechaDeCorte", editor);
			salidasEnProceso=new FilterList<SalidaACorte>(source,editor);
		}
		return salidasEnProceso;
	}
	
	/**
	 * Regresa la lista filtrada de salidas de bobinas
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public EventList<SalidaDeBobinas> getSalidaDeBobinas() {
		if(salidaDeBobinas==null){
			final EventList<SalidaDeBobinas> source=GlazedLists.threadSafeList(new BasicEventList<SalidaDeBobinas>());			
			final SalidaDeBobinasEditor editor1=new SalidaDeBobinasEditor();
			addBeanPropertyChangeListener("fechaDeCorte", editor1);
			final EventList<MatcherEditor<SalidaDeBobinas>> editores=new BasicEventList<MatcherEditor<SalidaDeBobinas>>();			
			editores.add(editor1);
			final CompositeMatcherEditor<SalidaDeBobinas> compositeEditor=new CompositeMatcherEditor<SalidaDeBobinas>(editores);
			compositeEditor.setMode(CompositeMatcherEditor.AND);			
			salidaDeBobinas=new FilterList<SalidaDeBobinas>(source,new ThreadedMatcherEditor<SalidaDeBobinas>(compositeEditor));
		}
		return salidaDeBobinas;
	}
	
	public List<SalidaDeBobinas> buscarSalidaDeBobinas() {
		return getMaquilaManager().buscarSalidasDeBobina();
	}
	
	/**
	 * Busca todas las salidas de material(Traslados y Salidas de bobinas directas)
	 * 
	 * @return
	 */
	public List<SalidaDeMaterial> buscarSalidasDeMaterial(){
		return getMaquilaManager().buscarSalidasDeMaterial();
	}
	

	@SuppressWarnings("unchecked")
	public EventList<SalidaDeMaterial> getTraslados() {
		if(traslados==null){
			final EventList<SalidaDeMaterial> source=getSalidasDeMaterial();
			final Matcher<SalidaDeMaterial> matcher=new Matcher<SalidaDeMaterial>(){
				public boolean matches(SalidaDeMaterial item) {					
					return !(item instanceof SalidaDeBobinas);
				}
				
			};
			final MatcherEditor<SalidaDeMaterial> editor=GlazedLists.fixedMatcherEditor(matcher);
			
			final SalidaDeBobinasEditor editor2=new SalidaDeBobinasEditor();
			addBeanPropertyChangeListener("fechaDeCorte", editor2);
			
			final EventList<MatcherEditor<SalidaDeMaterial>> editors=new BasicEventList<MatcherEditor<SalidaDeMaterial>>();
			editors.add(editor);
			editors.add(editor2);
			
			final CompositeMatcherEditor<SalidaDeMaterial> compositeEditor=new CompositeMatcherEditor<SalidaDeMaterial>(editors);
			compositeEditor.setMode(CompositeMatcherEditor.AND);
			
			traslados=new FilterList<SalidaDeMaterial>(source,compositeEditor);
		}
		return traslados;
	}
	
	
	
	public EventList<SalidaDeMaterial> getSalidasDeMaterial() {
		if(salidasDeMaterial==null){
			salidasDeMaterial=GlazedLists.threadSafeList(new BasicEventList<SalidaDeMaterial>());
		}
		return salidasDeMaterial;
	}
	
	/// ***********************************Procedimientos para material cortado************************************************ ///
	

	@SuppressWarnings("unchecked")
	public EventList<SalidaDeHojas> getSalidaDeHojas() {
		if(salidaDeHojas==null){
			final EventList<SalidaDeHojas> source=getSalidasDeHojasSource();
			final MaterialHojeadoMatcherEditor editor=new MaterialHojeadoMatcherEditor();
			addBeanPropertyChangeListener("fechaDeCorte", editor);
			salidaDeHojas=new FilterList<SalidaDeHojas>(source,new ThreadedMatcherEditor(editor));
		}
		return salidaDeHojas;
	}
	
	/**
	 * Cuando este metodo es llamado por primer vez crea un{@link EventList} para todas los beans {@link EntradaDeHojas}.
	 * <p> En esta implementacion la lista se crea a partir de la lista de {@link SalidaDeHojas} fuente utilizando
	 * varios objetos de GlazedList. 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public EventList<EntradaDeHojas> getEntradasDeHojas() {
		if(entradaDeHojas==null){
			/**
			final EventList<SalidaDeHojas> source=GlazedLists.threadSafeList(new BasicEventList<SalidaDeHojas>());
			final Function<SalidaDeHojas, EntradaDeHojas> function=new Function<SalidaDeHojas, EntradaDeHojas>(){
				public EntradaDeHojas evaluate(SalidaDeHojas sourceValue) {					
					return sourceValue.getOrigen();
				}				
			};
			final FunctionList<SalidaDeHojas, EntradaDeHojas> transformedList=new FunctionList<SalidaDeHojas, EntradaDeHojas>(source,function);
			final Comparator<EntradaDeHojas> comparator=GlazedLists.beanPropertyComparator(EntradaDeHojas.class, "id");
			final UniqueList<EntradaDeHojas> uniqueList=new UniqueList<EntradaDeHojas>(transformedList,comparator);
			**/
			final EventList<EntradaDeHojas> source=GlazedLists.threadSafeList(new BasicEventList<EntradaDeHojas>());
			final MaterialHojeadoMatcherEditor editor=new MaterialHojeadoMatcherEditor();
			addBeanPropertyChangeListener("fechaDeCorte", editor);
			entradaDeHojas=new FilterList<EntradaDeHojas>(source,new ThreadedMatcherEditor(editor));
			
		}
		return entradaDeHojas;	
	}
	
	/**
	 * Esta lista origen de todas las salidas de hojas existentes.
	 * 
	 * <p> Esta lista sirve como base para diversas listas filtradas como la
	 * que regresa el metodo getSalidasDeHojas();
	 * 
	 * @return
	 */
	public EventList<SalidaDeHojas> getSalidasDeHojasSource(){
		if(salidasDeHojasSource==null){
			salidasDeHojasSource=GlazedLists.threadSafeList(new BasicEventList<SalidaDeHojas>());
		}
		return salidasDeHojasSource;
	}
	
	/**
	 * TODO Este metodo se puede implementar con {@link SwingWorker} y teniendo acceso a componentes graficos
	 * para actualizar informacion del proceso
	 * 
	 */
	public void cargarInvnentarioDeHojas() {
		//getMaterialCortadoSource().clear();
		getSalidaDeHojas().clear();
		getSalidaDeHojas().addAll(getMaquilaManager().buscarSalidaDeHojas());
		getEntradasDeHojas().clear();
		getEntradasDeHojas().addAll(getMaquilaManager().buscarEntradasDeHojas());
		
	}
	
	/// ************************************************Fin de procedimientos para material cortado*************************************** //
	

	/**** Spring dependency injected collaborators ***/

	public MaquilaManager getMaquilaManager() {
		return maquilaManager;
	}
	public void setMaquilaManager(MaquilaManager maquilaManager) {
		this.maquilaManager = maquilaManager;
	}

	public Date getFechaDeCorte() {
		return fechaDeCorte;
	}

	public void setFechaDeCorte(Date fechaDeCorte) {
		Object old=this.fechaDeCorte;
		this.fechaDeCorte = fechaDeCorte;
		firePropertyChange("fechaDeCorte", old, fechaDeCorte);
	}
	
	
	/**
	 * {@link MatcherEditor} para filtrar una lista de beans tipo {@link SalidaACorte} en funcion de una fecha de corte
	 * para la cual esta registrada como {@link PropertyChangeListener}
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class SalidaACorteEditor extends AbstractMatcherEditor<SalidaACorte> implements PropertyChangeListener{		

		public void propertyChange(PropertyChangeEvent evt) {			
			final Date fecha=(Date)evt.getNewValue();			
			fireChanged(new SalidaACorteMatcher(fecha));
						
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
						return fentrada.compareTo(fecha)<=0;
							
					}
					return false;				
			}			
		}
	}
	
	/**
	 * Permite filtrar una lista de beans tipo {@link SalidaACorte} en la que los elementos se evaluan en funcion de una 
	 * fecha de corte y permite elementos posteriores a la misma, es decir que estan en proceso. 
	 * <p>Implementa {@link PropertyChangeListener} para  detectar los cambios de fecha y disparar un nuevo {@link Matcher}
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class EnProcesoDeCorteEditor extends AbstractMatcherEditor<SalidaACorte> implements PropertyChangeListener{		

		public void propertyChange(PropertyChangeEvent evt) {			
			final Date fecha=(Date)evt.getNewValue();			
			fireChanged(new EnProcesoDeCorteMatcher(fecha));
						
		}		
		private class EnProcesoDeCorteMatcher implements Matcher<SalidaACorte>{			
			private final Date fecha;			
			public EnProcesoDeCorteMatcher(final Date fecha){
				this.fecha=fecha;
			}			
			public boolean matches(SalidaACorte s) {				
					final Date forden=s.getOrden().getFecha();
					if(forden.compareTo(fecha)<=0){
						final Date fentrada=s.getEntradaReceptora().getFecha();
						return fentrada.compareTo(fecha)>0;
							
					}
					return false;				
			}			
		}
	}
	
	
	/**
	 * Matcher editor para filtrar las entradas para una fecha mayor a la escuchada
	 * 
	 * @author Ruben Cancino
	 *
	 */
	public class MenorAFilter extends AbstractMatcherEditor<EntradaDeMaterial> implements PropertyChangeListener{

		public void propertyChange(PropertyChangeEvent evt) {
			Object val=evt.getNewValue();
			if(val==null) 
				fireMatchAll();				
			else{				
				fireChanged(new MayorMatcher((Date)evt.getNewValue()));
			}
		}
		
		private class MayorMatcher implements Matcher<EntradaDeMaterial>{
			
			private final Date fecha;
			
			private MayorMatcher(final Date date){
				fecha=date;
			}
			public boolean matches(EntradaDeMaterial item) {
				return item.getFecha().compareTo(fecha)<=0;
			}
			
		}		
	}
	
	
	/**
	 * Editor para filtrar {@link SalidaDeBobinas} en funcion de una fecha
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class SalidaDeBobinasEditor extends AbstractMatcherEditor implements PropertyChangeListener{		

		@SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent evt) {
			final Date fecha=(Date)evt.getNewValue();
			fireChanged(new SalidaDeBobinaMatcher(fecha));
						
		}
		
		private class SalidaDeBobinaMatcher implements Matcher{
			
			private final Date fechaCorte;
			
			public SalidaDeBobinaMatcher(final Date fecha){
				this.fechaCorte=fecha;
			}			
			public boolean matches(Object obj) {
				final SalidaDeMaterial s=(SalidaDeMaterial)obj;
				final Date fechaSalida=s.getFecha();
				return fechaSalida.compareTo(fechaCorte)<=0;					
			}			
		}		
	}
	
	/**
	 * {@link MatcherEditor} para filtrar {@link MaterialHojeado} en funcion de una fecha 
	 * 
 	 * @author Ruben Cancino
	 *
	 */
	private class MaterialHojeadoMatcherEditor extends AbstractMatcherEditor<MaterialHojeado> implements PropertyChangeListener{		

		public void propertyChange(PropertyChangeEvent evt) {
			final Date fecha=(Date)evt.getNewValue();
			fireChanged(new SalidaDeHojasMatcher(fecha));
						
		}
		
		private class SalidaDeHojasMatcher implements Matcher<MaterialHojeado>{
			
			private final Date fecha;	
			
			public SalidaDeHojasMatcher(final Date fecha){
				this.fecha=fecha;
			}			
			public boolean matches(MaterialHojeado e) {				
				final Date fe=e.getFecha();
				return fe.compareTo(fecha)<=0;					
					
								
			}			
		}
		
	}
	

}
