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
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.inventarios.model.Totalizable;
import com.luxsoft.siipap.inventarios.model.TotalizadorDeMaquila;
import com.luxsoft.siipap.inventarios.utils.InventarioUtils;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;

/**
 *  Vista interna para los beans {@link SalidaDeHojas} 
 *  
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class EntradaDeHojasView extends AbstractInternalTaskView implements Totalizable{
	
	
	private JXTable grid;
	
		
	private final EventList<EntradaDeMaterial> entradas;
	private final EventList<EntradaDeHojas> entradaDeHojas;
	private FilterList<EntradaDeHojas> entradaDeHojasFiltradas;
	
	
	public EntradaDeHojasView(final EventList<EntradaDeMaterial> entradas
			,final EventList<EntradaDeHojas> entradaDeHojas){		
		setTitle("Entrada de Hojas");
		this.entradas=entradas;
		this.entradaDeHojas=entradaDeHojas;
	}

	public JComponent getControl() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(createGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
		
	@SuppressWarnings("unchecked")
	private JComponent createGridPanel(){
		
		final SHEditor editor=new SHEditor(entradas);
		entradaDeHojasFiltradas=new FilterList(entradaDeHojas,new ThreadedMatcherEditor(editor));
		final SortedList sortedList=new SortedList(entradaDeHojasFiltradas,null);
		final EventTableModel tm=new EventTableModel(sortedList,InventarioUtils.getEntradaDeHojasTF());
		final EventSelectionModel sm=new EventSelectionModel(sortedList);		
	
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(sm);
		ComponentUtils.decorateActions(grid);
		new TableComparatorChooser<EntradaDeHojas>(grid,sortedList,true);		
		return UIFactory.createTablePanel(grid);
	}
	
	public void pack(){
		grid.packAll();
	}
	
	
	
	public FilterList<EntradaDeHojas> getEntradaDEHojasFiltradas() {
		return entradaDeHojasFiltradas;
	}
	
	public void totalizar(TotalizadorDeMaquila totalizador) {
		BigDecimal mill=BigDecimal.ZERO;
		CantidadMonetaria pesos=CantidadMonetaria.pesos(0);
		for(EntradaDeHojas e:entradaDeHojasFiltradas){
			mill=mill.add(e.getCantidad());
			pesos=pesos.add(e.getCostoCalculado());	
		}
		totalizador.setTotalEntradaDeMillares(mill);
		totalizador.setImporteEntradaEnMillares(pesos);
		
		
	}
	
	
	
	/**
	 * {@link MatcherEditor} para filtrar una lista de {@link SalidaDeHojas} en funcion de una de {@link EntradaDeMaterial}
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class SHEditor extends AbstractMatcherEditor<EntradaDeHojas> implements ListEventListener<EntradaDeMaterial>{
						
		private SHEditor(final EventList<EntradaDeMaterial> entradas){			
			entradas.addListEventListener(this);
		}

		public void listChanged(ListEvent<EntradaDeMaterial> listChanges) {			
			final EventList<EntradaDeMaterial> entradas=listChanges.getSourceList();
			fireChanged(new SCMatcher(entradas));
		}
		
		private class SCMatcher implements Matcher<EntradaDeHojas>{
			
			final Collection<EntradaDeMaterial> entradas;
			
			public SCMatcher(final Collection<EntradaDeMaterial> entradas){
				this.entradas=entradas;
			}

			public boolean matches(EntradaDeHojas item) {
				final Long id=item.getOrigen().getEntrada().getId();				
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