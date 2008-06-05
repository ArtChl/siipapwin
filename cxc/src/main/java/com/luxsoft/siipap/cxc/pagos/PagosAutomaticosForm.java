package com.luxsoft.siipap.cxc.pagos;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para presentar una lista de  pagos automaticos, Se pueden eliminar pagos
 * de la lista presentada. 
 * 
 * @author Ruben Cancino
 *
 */
public class PagosAutomaticosForm extends SXAbstractDialog{
	
	private JXTable grid;
	private EventList<Venta> ventas;
	private EventSelectionModel<Venta> selectionModel;

	public PagosAutomaticosForm(final Collection<Venta> ventas) {
		super("Generación de pagos automáticos");
		this.ventas=GlazedLists.eventList(ventas);
	}

	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		
		return panel;
	}
	
	private JComponent buildMainPanel(){
		grid=ComponentUtils.getStandardTable();
		final String[] props={"clave","nombre","numero","tipo","fecha","vencimiento","total","saldo"};
		final String[] cols={"Cliente","Nombre","Factura","Tipo","fecha","vencimiento","total","saldo"};
		final TableFormat<Venta> tf=GlazedLists.tableFormat(Venta.class,props, cols);		
		final SortedList<Venta> sortedList=new SortedList<Venta>(ventas,null);
		final EventTableModel<Venta> tm=new EventTableModel<Venta>(sortedList,tf);
		selectionModel=new EventSelectionModel<Venta>(sortedList); 
		grid.setModel(tm);	
		grid.setSelectionModel(selectionModel);
		grid.packAll();
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addDeleteAction(grid,CommandUtils.createDeleteAction(this, "quitarVentas"));
		final JScrollPane sc=new JScrollPane(grid);		
		return sc;
	}
	
	public EventList<Venta> getVentas(){
		return ventas;
	}
	
	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel(
				"Pagos automaticos para ventas con saldos menores a 100 pesos",
				"Lista de ventas que califican para pago automatico",
				getIconFromResource("images/siipapwin/cxc64.gif")
				);
		
	}
	
	public void quitarVentas(){
		if(!selectionModel.getSelected().isEmpty()){
			ventas.removeAll(selectionModel.getSelected());
		}
	}
	

}
