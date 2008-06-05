package com.luxsoft.siipap.cxc.swing.cobranza;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.BrowserUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * Este es un dialogo con los resultados de la generacion
 * de un pago en grupo, mostrando un listado de los pagos
 * generados y una lista con las notas de credito generadas
 * De ser aceptada estos datos son persistidos a la base de datos
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class PagoEnGrupoResult extends SXAbstractDialog{
	
	private EventList<Pago> pagos;
	private EventList<NotasDeCreditoDet> notas;

	
	public PagoEnGrupoResult(List<Pago> pagos,List<NotasDeCreditoDet> notas,Dialog owner) {
		super(owner,"Aplicación de pagos automáticos");
		this.pagos=GlazedLists.eventList(pagos);
		this.notas=GlazedLists.eventList(notas);
	}
	

	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	
	
	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Pagos y Descuentos","Lista de Pagos y Descuentos por aplicar");
	}


	private JComponent buildMainPanel(){
		FormLayout layout=new FormLayout("max(p;350dlu):g","p,4dlu,p,4dlu,p,4dlu,p");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.setDefaultDialogBorder();
		builder.addSeparator("Pagos", cc.xy(1, 1));
		builder.add(buildPagosGrid(),cc.xy(1, 3));
		builder.addSeparator("Notas (X Desc 1)", cc.xy(1, 5));
		builder.add(buildNotasGrid(),cc.xy(1, 7));
		return builder.getPanel();
	}
	
	@SuppressWarnings("unchecked")
	private JComponent buildPagosGrid(){
		final JXTable table=BrowserUtils.buildBrowserGrid();
		final String[] props={"clave","venta.numero","formaDePago","referencia","importe"};
		final String[] cols={"Cliente","Venta","F.P","Referencia","Importe"};
		final TableFormat tf=GlazedLists.tableFormat(props, cols);
		EventTableModel tm=new EventTableModel(this.pagos,tf);
		table.setModel(tm);
		JComponent c=UIFactory.createTablePanel(table);
		table.packAll();
		return c;
	}
	
	@SuppressWarnings("unchecked")
	private JComponent buildNotasGrid(){
		final JXTable table=ComponentUtils.getStandardTable();
		final String[] props={"clave","factura.numero","tipo","numero","importe"};
		final String[] cols={"Cliente","Factura","Tipo","Nota","Importe"};
		final TableFormat tf=GlazedLists.tableFormat(NotasDeCreditoDet.class,props, cols);
		EventTableModel tm=new EventTableModel(this.notas,tf);
		table.setModel(tm);
		JComponent c=UIFactory.createTablePanel(table);
		table.packAll();
		return c;
	}
	
	

}
