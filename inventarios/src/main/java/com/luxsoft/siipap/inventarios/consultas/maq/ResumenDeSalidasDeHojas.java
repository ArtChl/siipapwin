package com.luxsoft.siipap.inventarios.consultas.maq;

import java.awt.BorderLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.inventarios.utils.FechaMatcherEditor;
import com.luxsoft.siipap.inventarios.utils.InventarioUtils;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;

/**
 * 
 * Consulta de todas las salidas de hojas existentes
 * 
 * @author Ruben Cancino
 *
 */
public  class ResumenDeSalidasDeHojas extends AbstractInternalTaskView{
	
	private JXTable grid;
	private EventList<SalidaDeHojas> salidas;
	private FilterList<SalidaDeHojas> salidasFiltradas;
	private EventSelectionModel sm;
	
	private JPanel filtrosPanel;
	private JPanel totalesPanel;
	private FechaMatcherEditor fechaIniEditor;
	private FechaMatcherEditor fechaFinEditor;
	private JFormattedTextField idFilter;
	private JFormattedTextField entradaFilter;
	private JFormattedTextField comFilter;
	private JFormattedTextField claveFilter;
	
	private JLabel totalKilos;
	private JLabel totalMillares;
	private JLabel totalCosto;
		
	final NumberFormat nf=NumberFormat.getIntegerInstance();
	
	
	public ResumenDeSalidasDeHojas() {
		setTitle("Salida de Hojas");
	}
	
	protected void initComponents(){
		nf.setGroupingUsed(false);
		idFilter=new JFormattedTextField();
		entradaFilter=new JFormattedTextField();
		comFilter=new JFormattedTextField();
		claveFilter=new JFormattedTextField();
		totalKilos=new JLabel("");
		totalMillares=new JLabel("");
		totalCosto=new JLabel("");
		final Font bold=totalKilos.getFont().deriveFont(Font.BOLD);
		totalCosto.setFont(bold);
		totalMillares.setFont(bold);
		totalKilos.setFont(bold);
		
		totalCosto.setHorizontalAlignment(SwingConstants.RIGHT);
		totalMillares.setHorizontalAlignment(SwingConstants.RIGHT);
		totalKilos.setHorizontalAlignment(SwingConstants.RIGHT);
		
	}

	public JComponent getControl() {
		initComponents();
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(createGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	@SuppressWarnings("unchecked")
	private JComponent createGridPanel(){
		
		
		
		final EventList<MatcherEditor<SalidaDeHojas>> editors=new BasicEventList<MatcherEditor<SalidaDeHojas>>();
		
		//		Filtros encadenados
		fechaIniEditor=new FechaIni();
		fechaFinEditor=new FechaFin();
		editors.add(fechaIniEditor);
		editors.add(fechaFinEditor);
		final TextFilterator<SalidaDeHojas> idFilterator=GlazedLists.textFilterator(new String[]{"id"});
		final MatcherEditor<SalidaDeHojas> idEditor=new TextComponentMatcherEditor<SalidaDeHojas>(idFilter,idFilterator);
		editors.add(idEditor);
		
		final TextFilterator<SalidaDeHojas> entradaFilterator=GlazedLists.textFilterator(new String[]{"origen.origen.entrada.entradaDeMaquilador"});
		final MatcherEditor<SalidaDeHojas> entradaEditor=new TextComponentMatcherEditor<SalidaDeHojas>(entradaFilter,entradaFilterator);
		editors.add(entradaEditor);
		
		final TextFilterator<SalidaDeHojas> comFilterator=GlazedLists.textFilterator(new String[]{"destino.COM"});
		final MatcherEditor<SalidaDeHojas> comEditor=new TextComponentMatcherEditor<SalidaDeHojas>(comFilter,comFilterator);
		editors.add(comEditor);
		
		final TextFilterator<SalidaDeHojas> claveFilterator=GlazedLists.textFilterator(new String[]{"clave"});
		final MatcherEditor<SalidaDeHojas> claveEditor=new TextComponentMatcherEditor<SalidaDeHojas>(claveFilter,claveFilterator);
		editors.add(claveEditor);
		
		final CompositeMatcherEditor<SalidaDeHojas> matcher=new CompositeMatcherEditor<SalidaDeHojas>(editors);
		matcher.setMode(CompositeMatcherEditor.AND);
		
		salidasFiltradas=new FilterList<SalidaDeHojas>(salidas,new ThreadedMatcherEditor<SalidaDeHojas>(matcher));
		salidasFiltradas.addListEventListener(new TotalesHandler());
		
		final SortedList sortedList=new SortedList(salidasFiltradas,null);
		final EventTableModel tm=new EventTableModel(sortedList,InventarioUtils.getSalidaDeHojasTF());
		sm=new EventSelectionModel(sortedList);		
	
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(sm);
		ComponentUtils.decorateActions(grid);
		new TableComparatorChooser<SalidaDeHojas>(grid,sortedList,true);		
		return UIFactory.createTablePanel(grid);
	}
	
	public void pack(){
		grid.packAll();
	}
	
	public void load(){
		
	}
	
	protected JPanel getFiltrosPanel(){
		if(filtrosPanel==null){
			final DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:p,2dlu,f:max(p;70dlu):g",""));
			builder.append("Fecha Inicial",fechaIniEditor.getPikcer(),true);
			builder.append("Fecha Final ",fechaFinEditor.getPikcer(),true);
			builder.append("Id",idFilter,true);
			builder.append("Entrada",entradaFilter,true);			
			builder.append("COM",comFilter,true);
			builder.append("Clave",claveFilter,true);
			builder.getPanel().setOpaque(false);
			filtrosPanel=builder.getPanel();
		}
		return filtrosPanel;
	}
	
	protected JPanel getTotalesPanel(){
		if(totalesPanel==null){
			final DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:p,2dlu,f:p:g",""));
			builder.appendSeparator("Totales");
			builder.append("Kilos",totalKilos,true);
			builder.append("Millares ",totalMillares,true);
			builder.append("Costo",totalCosto,true);			
			builder.getPanel().setOpaque(false);
			totalesPanel=builder.getPanel();
			
		}
		return totalesPanel;
	}

	public void setSalidas(EventList<SalidaDeHojas> salidas) {
		this.salidas = salidas;
	}

	@Override
	public void installDetallesPanel(JXTaskPane detalle) {
		detalle.add(getTotalesPanel());
	}

	@Override
	public void installFiltrosPanel(JXTaskPane filtros) {
		filtros.add(getFiltrosPanel());
	}
	
	private void actualizarTotales(){
		BigDecimal kilos=BigDecimal.ZERO;
		BigDecimal mil=BigDecimal.ZERO;
		CantidadMonetaria costo=CantidadMonetaria.pesos(0);
		for(SalidaDeHojas s:salidasFiltradas ){
			kilos=kilos.add(s.getKilos());
			mil=mil.add(s.getCantidad());
			costo=costo.add(s.getCosto());
		}
		totalKilos.setText(kilos.toString());
		totalMillares.setText(nf.format(mil));
		totalCosto.setText(costo.toString());
	}
	
	 private class TotalesHandler implements ListEventListener<SalidaDeHojas>{

		public void listChanged(ListEvent<SalidaDeHojas> listChanges) {
			while(listChanges.hasNext()){
				listChanges.next();
			}
			actualizarTotales();
			
			
		}
		 
	 }
	 
	 private class FechaIni extends FechaMatcherEditor{

		@Override
		protected void evaluarFecha(Date fecha) {
			fireChanged(new MayorMatcher());
			
		}
		private class MayorMatcher implements Matcher<SalidaDeHojas>{
			public boolean matches(SalidaDeHojas item) {				
				return picker.getDate().compareTo(item.getFecha())<=0;
			}
		}
		 
	 }
	 
	 private class FechaFin extends FechaMatcherEditor{

			@Override
			protected void evaluarFecha(Date fecha) {
				fireChanged(new MayorMatcher());
				
			}
			private class MayorMatcher implements Matcher<SalidaDeHojas>{
				public boolean matches(SalidaDeHojas item) {				
					return picker.getDate().compareTo(item.getFecha())>=0;
				}
			}
			 
		 }

}
