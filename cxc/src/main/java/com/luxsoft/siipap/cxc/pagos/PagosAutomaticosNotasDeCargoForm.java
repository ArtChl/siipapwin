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
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;


/**
 * Forma para presentar una lista de  pagos automaticos, Se pueden eliminar pagos
 * de la lista presentada. 
 * 
 * @author Ruben Cancino
 *
 */
public class PagosAutomaticosNotasDeCargoForm extends SXAbstractDialog{
	
	private JXTable grid;
	private EventList<NotaDeCredito> cargos;
	private EventSelectionModel<NotaDeCredito> selectionModel;

	public PagosAutomaticosNotasDeCargoForm(final Collection<NotaDeCredito> cargos) {
		super("Generación de pagos automáticos");
		this.cargos=GlazedLists.eventList(cargos);
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
		final String[] props={"clave","cliente.nombre","numero","tipo","fecha","totalAsMoneda2","saldoDelCargo"};
		final String[] cols={"Cliente","Nombre","Factura","Tipo","fecha","Total","saldo"};
		final TableFormat<NotaDeCredito> tf=GlazedLists.tableFormat(NotaDeCredito.class,props, cols);		
		final SortedList<NotaDeCredito> sortedList=new SortedList<NotaDeCredito>(cargos,null);
		final EventTableModel<NotaDeCredito> tm=new EventTableModel<NotaDeCredito>(sortedList,tf);
		selectionModel=new EventSelectionModel<NotaDeCredito>(sortedList); 
		grid.setModel(tm);	
		grid.setSelectionModel(selectionModel);
		grid.packAll();
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addDeleteAction(grid,CommandUtils.createDeleteAction(this, "quitarCargo"));
		final JScrollPane sc=new JScrollPane(grid);		
		return sc;
	}
	
	public EventList<NotaDeCredito> getCargos(){
		return cargos;
	}
	
	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel(
				"Pagos automaticos para cargos con saldos menores a 5 pesos",
				"Lista de cargos que califican para pago automatico",
				getIconFromResource("images/siipapwin/cxc64.gif")
				);
		
	}
	
	public void quitarCargo(){
		if(!selectionModel.getSelected().isEmpty()){
			cargos.removeAll(selectionModel.getSelected());
		}
	}
	

}
