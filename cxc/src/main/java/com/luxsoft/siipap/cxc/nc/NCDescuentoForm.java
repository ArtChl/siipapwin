package com.luxsoft.siipap.cxc.nc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para la generacion de notas de credito para descuento
 *  
 * 
 * @author Ruben Cancino
 *
 */
public class NCDescuentoForm extends SXAbstractDialog{
	
	private final NCDescuentoFormModel model;
	private JCheckBox condonar;
	private JXTable grid;
	
	public NCDescuentoForm(final NCDescuentoFormModel model){
		super("Notas de descuento");
		this.model=model;
	}

	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	private JComponent buildMainPanel(){
		final FormLayout layout=new FormLayout(
				"450dlu"
				,"p,3dlu,p,3dlu,p");
		CellConstraints cc=new CellConstraints();
		PanelBuilder builder=new PanelBuilder(layout);
		builder.add(buildFormPanel(),cc.xy(1, 1));
		builder.add(buildGridPanel(),cc.xy(1, 3));
		//builder.add(createValidationView(),cc.xy(1,3))
		return builder.getPanel();
	}
	
	private JComponent buildFormPanel(){
		FormLayout layout=new FormLayout("l:p,2dlu,75dlu","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Fecha",Binder.createDateComponent(model.getModel("fecha")));
		condonar=BasicComponentFactory.createCheckBox(model.getModel("condonar"), "");
		builder.append("Condonar cargo",condonar);		
		return builder.getPanel();
	}	
	

	@Override
	protected JComponent buildHeader() {		
		return new HeaderPanel("Descuentos a facturas"
				,"Lista de facturas que califican para un descuento");
	}
	
	private JComponent buildGridPanel(){
		final String[] props={"clave","factura.numero","factura.fecha","factura.tipo"
				,"factura.total","factura.pagos","factura.devolucionesCred","factura.saldo"
				,"factura.descuentoPactado","factura.cargo","importe"};
		final String[] cols={"Cliente","Factura","Fecha","T","Total","Pagos","Devoluciones","Saldo","Desc","Cargo","Importe"};
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class, props, cols);
		final SortedList<NotasDeCreditoDet> sortedList=new SortedList<NotasDeCreditoDet>(model.getPartidas(),null);
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(sortedList,tf);
		final EventSelectionModel<NotasDeCreditoDet> selectionModel=new EventSelectionModel<NotasDeCreditoDet>(sortedList);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		final Action deleteAction=new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				if(!selectionModel.getSelected().isEmpty()){
					selectionModel.getSelected().remove(0);
					if(sortedList.isEmpty())
						getOKAction().setEnabled(false);
				}
			}
		};
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.packAll();
		grid.setSelectionModel(selectionModel);
		ComponentUtils.addDeleteAction(grid, deleteAction);
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	
	
	@Override
	protected void onWindowOpened() {
		getOKAction().setEnabled(!model.getPartidas().isEmpty());
	}

	public static void main(String[] args) {
		SWExtUIManager.setup();
		List<Venta> ventas=DatosDePrueba.ventasDePrueba();
		for(Venta v:ventas){
			v.setPagos(10);
		}
		NCDescuentoFormModel model=new NCDescuentoFormModel(ventas.get(0).getCliente(),ventas);
		NCDescuentoForm form=new NCDescuentoForm(model);
		form.open();
		
	}

}
