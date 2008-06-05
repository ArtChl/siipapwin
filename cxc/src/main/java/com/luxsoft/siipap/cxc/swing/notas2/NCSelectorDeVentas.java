package com.luxsoft.siipap.cxc.swing.notas2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.BrowserUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Selector de ventas para notas de credito
 * 
 * Este selector no carga datos, es oblicacion de quien lo usa 
 * cargar los datos en el grid mediante el acceso a la lista
 * ventas, que es Trhead safe
 * 
 * @author Ruben Cancino
 *
 */
public class NCSelectorDeVentas extends SXAbstractDialog{
	
	private EventList<Venta> ventas;
	private EventSelectionModel<Venta> selectionModel;
	private JTextField inputField;

	public NCSelectorDeVentas() {
		super("Selector de Ventas");
		ventas=GlazedLists.threadSafeList(new BasicEventList<Venta>());
	}
	
	

	public EventList<Venta> getVentas() {
		return ventas;
	}



	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout(5,5));
		panel.add(buildFilterPanel(),BorderLayout.NORTH);
		panel.add(buildGridPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent buildFilterPanel(){
		inputField=new JTextField();
		return BrowserUtils.buildTextFilterPanel(inputField);
	}
	private JComponent buildGridPanel(){
		JXTable grid=ComponentUtils.getStandardTable();	
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addEnterAction(grid, new SelectAction());
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {				
				if(e.getClickCount()==2)
					doAccept();
			}			
		});
		final SortedList<Venta> sortedList=new SortedList<Venta>(ventas,null);
		final TextFilterator<Venta> filterator=GlazedLists.textFilterator(new String[]{"numero"});
		final TextComponentMatcherEditor<Venta> editor=new TextComponentMatcherEditor<Venta>(inputField,filterator);		
		final FilterList<Venta> filterList=new FilterList<Venta>(ventas,editor);
		
		final String[] props={"clave","numero","tipo","serie","total","saldo"};
		final String[] cols={"Cliente","Numero","Tipo","Serie","Total","Saldo"};
		final TableFormat<Venta> tf=GlazedLists.tableFormat(props, cols);
		
		final EventTableModel<Venta> tm=new EventTableModel<Venta>(filterList,tf);
		selectionModel=new EventSelectionModel<Venta>(filterList);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		new TableComparatorChooser<Venta>(grid,sortedList,true);
		grid.packAll();
		
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	
	
	public Venta getSelected(){
		if(!selectionModel.getSelected().isEmpty()){
			return selectionModel.getSelected().get(0);
		}
		return null;
	}
	
	private class SelectAction extends AbstractAction{
		public void actionPerformed(ActionEvent e) {
			doAccept();
		}		
	}

}
