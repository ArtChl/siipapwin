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
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;

/**
 * Selector de devoluciones para notas de credito
 * 
 * Este selector no carga datos, es oblicacion de quien lo usa 
 * cargar los datos en el grid mediante el acceso a la lista
 * devoluciones, que es Trhead safe
 * 
 * @author Ruben Cancino
 *
 */
public class NCSelectorDeDevoluciones extends SXAbstractDialog{
	
	private EventList<DevolucionDet> devoluciones;
	private EventSelectionModel<DevolucionDet> selectionModel;
	private JTextField inputField;

	public NCSelectorDeDevoluciones() {
		super("Selector de Devoluciones");
		devoluciones=GlazedLists.threadSafeList(new BasicEventList<DevolucionDet>());
	}
	
	

	public EventList<DevolucionDet> getDevoluciones() {
		return devoluciones;
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
		final SortedList<DevolucionDet> sortedList=new SortedList<DevolucionDet>(devoluciones,null);
		final TextFilterator<DevolucionDet> filterator=GlazedLists.textFilterator(new String[]{"numero"});
		final TextComponentMatcherEditor<DevolucionDet> editor=new TextComponentMatcherEditor<DevolucionDet>(inputField,filterator);		
		final FilterList<DevolucionDet> filterList=new FilterList<DevolucionDet>(devoluciones,editor);
		
		
		final TableFormat<DevolucionDet> tf=GlazedLists.tableFormat(DevolucionDet.class,getPartidasProps(), getPartidasLabels());
		
		final EventTableModel<DevolucionDet> tm=new EventTableModel<DevolucionDet>(filterList,tf);
		selectionModel=new EventSelectionModel<DevolucionDet>(filterList);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		new TableComparatorChooser<DevolucionDet>(grid,sortedList,true);
		grid.packAll();
		
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	protected String[] getPartidasProps(){
		return new String[]{"numero","articulo.clave","articulo.descripcion1","cantidad","ventaDet.precioFacturado","importe","devolucion.cliente"};
	}
	protected String[] getPartidasLabels(){
		return new String[]{"Número","Articulo","Descripción","Cantidad","Precio","Devolución","Cliente"};
	}
	
	
	
	@Override
	public void doClose() {
		this.devoluciones.clear();
		super.doClose();
	}



	public Devolucion getSelected(){
		if(!selectionModel.getSelected().isEmpty()){
			return selectionModel.getSelected().get(0).getDevolucion();
		}
		return null;
	}
	
	private class SelectAction extends AbstractAction{
		public void actionPerformed(ActionEvent e) {
			doAccept();
		}		
	}

}
