package com.luxsoft.siipap.inventarios.consultas;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.inventarios.model.Totalizable;
import com.luxsoft.siipap.inventarios.model.TotalizadorDeMaquila;
import com.luxsoft.siipap.inventarios.utils.InventarioUtils;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;

/**
 *  Vista interna de las entradas de inventario
 *  
 * @author Ruben Cancino
 *
 */
public class SalidaDeBobinasView extends AbstractInternalTaskView implements Totalizable{
	
	private JXTable grid;
	private final EventList<EntradaDeMaterial> entradas;
	private final EventList<SalidaDeBobinas> salidas;
	private FilterList<SalidaDeBobinas> salidasFiltradas;
	
	public SalidaDeBobinasView(final EventList<EntradaDeMaterial> entradas,final EventList<SalidaDeBobinas> salidas){		
		setTitle("Bobinas (S)");
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
		final SalidaDeBobinaMatcher editor=new SalidaDeBobinaMatcher(entradas);
		salidasFiltradas=new FilterList<SalidaDeBobinas>(salidas
				,new ThreadedMatcherEditor<SalidaDeBobinas>(editor));
		
		final SortedList<SalidaDeBobinas> sortedList=new SortedList<SalidaDeBobinas>(salidasFiltradas,null);
		
		final EventTableModel<SalidaDeBobinas> tm=new EventTableModel<SalidaDeBobinas>(
				sortedList,InventarioUtils.getSalidaDeBobinasTF());
		final EventSelectionModel<SalidaDeBobinas> sm=new EventSelectionModel<SalidaDeBobinas>(sortedList);
		
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(sm);
		new TableComparatorChooser<SalidaDeBobinas>(grid,sortedList,true);
		ComponentUtils.decorateActions(grid);
		return new JScrollPane(grid);
	}
	
	public void pack(){
		grid.packAll();
	}
	
	public void totalizar(TotalizadorDeMaquila totalizador) {
		BigDecimal kilos=BigDecimal.ZERO;
		CantidadMonetaria pesos=CantidadMonetaria.pesos(0);
		for(SalidaDeMaterial s:salidasFiltradas){
			kilos=kilos.add(s.getKilos());
			pesos=pesos.add(s.getCostoAsMoneda());	
		}
		totalizador.setSalidaDirectasEnKilos(kilos);
		totalizador.setImporteSalidaDirectasEnKilos(pesos);
	}
	
	/**
	 * Matcher para filtrar beans {@link SalidaDeBobinas} en funcion de una lista filtrada de {@link EntradaDeMaterial}
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class SalidaDeBobinaMatcher extends AbstractMatcherEditor<SalidaDeBobinas> implements ListEventListener<EntradaDeMaterial>{
						
		private SalidaDeBobinaMatcher(final EventList<EntradaDeMaterial> entradas){			
			entradas.addListEventListener(this);
		}

		public void listChanged(ListEvent<EntradaDeMaterial> listChanges) {			
			final EventList<EntradaDeMaterial> entradas=listChanges.getSourceList();
			fireChanged(new BobinasMatcher(entradas));
		}
		
		private class BobinasMatcher implements Matcher<SalidaDeBobinas>{
			
			final Collection<EntradaDeMaterial> entradas;
			
			public BobinasMatcher(final Collection<EntradaDeMaterial> entradas){
				this.entradas=entradas;
			}

			public boolean matches(SalidaDeBobinas item) {
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