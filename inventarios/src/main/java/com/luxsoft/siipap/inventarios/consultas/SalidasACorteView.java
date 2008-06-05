package com.luxsoft.siipap.inventarios.consultas;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.inventarios.model.Totalizable;
import com.luxsoft.siipap.inventarios.model.TotalizadorDeMaquila;
import com.luxsoft.siipap.inventarios.utils.InventarioUtils;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;

/**
 *  Vista interna para los beans {@link SalidaACorte}
 *  
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class SalidasACorteView extends AbstractInternalTaskView implements Totalizable{
	
	
	private JXTable grid;
	
		
	private final EventList<EntradaDeMaterial> entradas;
	private final EventList<SalidaACorte> salidas;
	private EventList<SalidaACorte> salidasFiltradas;
	
	public SalidasACorteView(final EventList<EntradaDeMaterial> entradas,final EventList<SalidaACorte> salidas){		
		setTitle("Cortes");
		this.entradas=entradas;
		this.salidas=salidas;
	}

	public JComponent getControl() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(createGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
		
	@SuppressWarnings("unchecked")
	private JComponent createGridPanel(){
		
		final SortedList sortedList=new SortedList(getSalidasFiltradas(),null);
		final EventTableModel tm=new EventTableModel(sortedList,InventarioUtils.getSalidaACorteTF());
		final EventSelectionModel sm=new EventSelectionModel(sortedList);		
	
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(sm);
		ComponentUtils.decorateActions(grid);
		new TableComparatorChooser<SalidaACorte>(grid,sortedList,true);
		//return new JScrollPane(grid);
		return UIFactory.createTablePanel(grid);
	}
	
	public void pack(){
		grid.packAll();
	}
	
	public EventList<SalidaACorte> getSalidasFiltradas(){
		if(salidasFiltradas==null){
			final SalidaACorteMatcher editor=new SalidaACorteMatcher(entradas);
			salidasFiltradas=new FilterList(salidas,new ThreadedMatcherEditor(editor));		
			
		}
		return salidasFiltradas;
	}
	
	public void totalizar(TotalizadorDeMaquila totalizador) {
		BigDecimal mill=BigDecimal.ZERO;
		CantidadMonetaria pesos=CantidadMonetaria.pesos(0);
		for(SalidaACorte s:getSalidasFiltradas()){
			mill=mill.add(s.getKilos());
			pesos=pesos.add(s.getCostoAsMoneda());	
		}
		totalizador.setSalidasACorteEnKilos(mill);
		totalizador.setImporteSalidasACorteEnKilos(pesos);
		
	}
	
	/**
	 * Matcher para filtrar beans {@link SalidaACorte} en funcion de una lista filtrada de {@link EntradaDeMaterial}
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class SalidaACorteMatcher extends AbstractMatcherEditor<SalidaACorte> implements ListEventListener<EntradaDeMaterial>{
						
		private SalidaACorteMatcher(final EventList<EntradaDeMaterial> entradas){			
			entradas.addListEventListener(this);
		}

		public void listChanged(ListEvent<EntradaDeMaterial> listChanges) {			
			final EventList<EntradaDeMaterial> entradas=listChanges.getSourceList();
			fireChanged(new SCMatcher(entradas));
		}
		
		private class SCMatcher implements Matcher<SalidaACorte>{
			
			final Collection<EntradaDeMaterial> entradas;
			
			public SCMatcher(final Collection<EntradaDeMaterial> entradas){
				this.entradas=entradas;
			}

			public boolean matches(SalidaACorte item) {
				final Long id=item.getEntrada().getId();				
				Object found=CollectionUtils.find(entradas, new Predicate(){
					public boolean evaluate(Object object) {
						final EntradaDeMaterial ee=(EntradaDeMaterial)object;
						return id.equals(ee.getId());
					}					
				});
				return found!=null;
			}			
		}		
	}
	
	
	
}