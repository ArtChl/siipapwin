package com.luxsoft.siipap.cxc.swing.task;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.util.ComponentUtils;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.domain.Abogado;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

public class TraspasoAJuridicoDialog extends SXAbstractDialog{
	
	private final TraspasoAJuridicoModel model;
	
	private JComboBox abogado;
	private JComponent fecha;
	private JTextField cliente;
	private JTextArea comentario;
	private JTable grid;
	private EventSelectionModel selectionModel;
	
	public TraspasoAJuridicoDialog(final List<Venta> ventas) {
		super("Traspaso a Jurídico");
		model=new TraspasoAJuridicoModel(ventas);
	}
	
	@SuppressWarnings("unchecked")
	private void initComponents(){
		abogado=CXCBindings.createAbogadoBinding(model.getModel("abogado"));
		fecha=BasicComponentFactory.createFormattedTextField(model.getComponentModel("fecha"), DateFormat.getDateInstance());
		cliente=BasicComponentFactory.createTextField(model.getComponentModel("cliente"));
		cliente.setFocusable(false);
		comentario=BasicComponentFactory.createTextArea(model.getComponentModel("comentario"));
		EventList source=model.getVentas();
		final String[] props={"sucursal","tipo","numero","numeroFiscal","fecha","vencimiento","total","saldo","atraso"};
		final String[] labels={"Sucursal","Tipo","Numero","No Fiscal","Fecha","Vencimiento","Total","Saldo","Atraso"};
		final TableFormat tf=GlazedLists.tableFormat(props,labels);		
		EventTableModel tm=new EventTableModel(source,tf);
		selectionModel =new EventSelectionModel(source);
		grid=new JTable(tm);
		grid.setSelectionModel(selectionModel);
		grid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		Action delete=CommandUtils.createDeleteAction(this,"delete");
		ComponentUtils.addAction(grid,delete,KeyStroke.getKeyStroke("DELETE"));		
	}
	
	@Override
	protected JComponent buildContent() {
		initComponents();
		JPanel panel=new JPanel(new BorderLayout());
		
		PanelBuilder builder=new PanelBuilder(new FormLayout("250dlu","p,6dlu,p"));
		CellConstraints cc=new CellConstraints();
		builder.add(buildEditorPanel(),cc.xy(1,1));
		builder.add(buildGridPanel(),cc.xy(1,3));
		
		panel.add(builder.getPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	
	private JPanel buildEditorPanel(){
		FormLayout layout=new FormLayout("l:p,3dlu,f:60dlu,3dlu,l:50dlu,3dlu,f:p:g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);		
		builder.setDefaultDialogBorder();
		//builder.append("Cliente",cliente,5);
		builder.append("Fecha",fecha);
		builder.append("Abogado", abogado,true);
		builder.append("Comentario",new JScrollPane(comentario),5);		
		return builder.getPanel();
	}
	
	private JComponent buildGridPanel(){
		JComponent c=UIFactory.createTablePanel(grid);		
		return c;
	}
	
	
	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Traspaso a Jurídico","Control jurídico de ventas con problema de pago",getIconFromResource("siipapwin/"));
	}
	
	public void delete(){
		if(selectionModel.isSelectionEmpty()) return;
		selectionModel.getSelected().clear();
	}

	public class TraspasoAJuridicoModel extends PresentationModel{
		
		private Abogado abogado;
		private String cliente;
		private Date fecha=new Date();
		private String comentario;
		private final EventList<Venta> ventas;		
		
		public TraspasoAJuridicoModel(final List<Venta> list) {
			super(null);
			setBean(this);
			ventas=GlazedLists.eventList(list);
			getComponentModel("cliente").setEditable(false);
			getComponentModel("fecha").setEditable(false);
		}

		public EventList<Venta> getVentas(){
			return ventas;
		}
		
		public Abogado getAbogado() {
			return abogado;
		}
		public void setAbogado(Abogado abogado) {
			this.abogado = abogado;
		}
		public Date getFecha() {
			return fecha;
		}
		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public String getCliente() {
			return cliente;
		}

		public void setCliente(String cliente) {
			Object old=this.cliente;
			this.cliente = cliente;
			firePropertyChange("cliente", old, cliente);
		}

		public String getComentario() {
			return comentario;
		}

		public void setComentario(String comentario) {
			this.comentario = comentario;
		}
		
		
		
	}

	public static void main(String[] args) {
		TraspasoAJuridicoDialog dialog=new TraspasoAJuridicoDialog(DatosDePrueba.ventasDePrueba());
		dialog.open();
	}
	

}
