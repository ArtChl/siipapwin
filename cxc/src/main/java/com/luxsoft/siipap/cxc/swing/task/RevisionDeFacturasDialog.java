package com.luxsoft.siipap.cxc.swing.task;

import java.awt.BorderLayout;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.model.Cobradores;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para modificar datos de venta:
 *  -Cobrador
 *  -Dia pato
 *  -Dia revision
 *  -Fecha revision
 *   
 * @author Ruben Cancino
 *
 */
public class RevisionDeFacturasDialog extends SXAbstractDialog{
	
	private final RevisionDeFacturasModel model;
	
	private JComponent diaRevision;
	private JComponent diaPago;
	private JComboBox cobrador;
	private JComponent fecha;
	private JCheckBox fvencimiento;
	private JTextField plazo;
	private JTextField cliente;
	private JTextArea comentario;
	private JXTable grid;
	private EventSelectionModel selectionModel;
	
	public RevisionDeFacturasDialog(final List<Venta> ventas) {
		super("Revisión de Facturas");
		model=new RevisionDeFacturasModel(ventas);
	}
	
	@SuppressWarnings("unchecked")
	private void initComponents(){
		final SelectionInList sl=new SelectionInList(Cobradores.getCobradores(),model.getModel("cobrador"));
		cobrador=BasicComponentFactory.createComboBox(sl);
		fecha=Binder.createDateComponent(model.getModel("fecha"));
		cliente=BasicComponentFactory.createTextField(model.getComponentModel("cliente"));
		cliente.setFocusable(false);
		comentario=BasicComponentFactory.createTextArea(model.getComponentModel("comentario"));
		diaRevision=Binder.createDiaDeLaSemanaBinding(model.getModel("diaRevision"));
		diaPago=Binder.createDiaDeLaSemanaBinding(model.getModel("diaPago"));
		
		fvencimiento=BasicComponentFactory.createCheckBox(model.getComponentModel("revision"), "F.Revisión");
		plazo=BasicComponentFactory.createIntegerField(model.getComponentModel("plazo"));
		
		EventList source=model.getVentas();
		final String[] props={"cobrador","sucursal","tipo","numero","numeroFiscal","fecha","vencimiento","total","saldo","atraso"};
		final String[] labels={"Cobrador","Sucursal","Tipo","Numero","No Fiscal","Fecha","Vencimiento","Total","Saldo","Atraso"};
		final TableFormat tf=GlazedLists.tableFormat(Venta.class,props,labels);		
		EventTableModel tm=new EventTableModel(source,tf);
		selectionModel =new EventSelectionModel(source);
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.packAll();
		grid.setSelectionModel(selectionModel);
		grid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		Action delete=CommandUtils.createDeleteAction(this,"delete");
		ComponentUtils.addAction(grid,delete,KeyStroke.getKeyStroke("DELETE"));		
	}
	
	@Override
	protected JComponent buildContent() {
		initComponents();
		JPanel panel=new JPanel(new BorderLayout());
		
		PanelBuilder builder=new PanelBuilder(new FormLayout("p","p,6dlu,p"));
		CellConstraints cc=new CellConstraints();
		builder.add(buildEditorPanel(),cc.xy(1,1));
		builder.add(buildGridPanel(),cc.xy(1,3));
		
		panel.add(builder.getPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	
	private JPanel buildEditorPanel(){		
		FormLayout layout=new FormLayout(
				"l:50dlu,3dlu,f:60dlu,3dlu, " +
				"l:50dlu,3dlu,f:p:g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);		
		builder.setDefaultDialogBorder();
		//builder.append("Cliente",cliente,5);
		builder.append("Dia Revisión",diaRevision);
		builder.append("Dia Cobro",diaPago,true);
		builder.append("Fecha Revisión",fecha);		
		builder.append("Cobrador", cobrador,true);
		builder.append("Vencimiento",fvencimiento);
		builder.append("Plazo",plazo);
		//builder.append("Comentario",new JScrollPane(comentario),5);		
		return builder.getPanel();
	}
	
	private JComponent buildGridPanel(){
		JComponent c=UIFactory.createTablePanel(grid);		
		return c;
	}
	
	
	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Facturas a revisión","Control de facturas entregadas a revisión",getIconFromResource("siipapwin/cxc64.gif"));
	}
	
	public void delete(){
		if(selectionModel.isSelectionEmpty()) return;
		selectionModel.getSelected().clear();
	}
	
	
	
	public void open(){
		if(validar()){
			super.open();
		}else
			dispose();
	}
	
	public void commit(){
		model.commit();
	}
	
	public boolean  validar(){
		
		if(!model.validar()){
			MessageUtils.showMessage("Las ventas seleccionadas no son del mismo cliente y/o cobrador", "Revisión de facturas");
			return false;
		}
		return true;
	}

	public class RevisionDeFacturasModel extends PresentationModel{
		
		private Cobradores cobrador;
		private String cliente;
		private Date fecha=new Date();
		private int diaRevision=1;
		private int diaPago=1;
		private String comentario;
		private boolean eliminar=false;
		private int plazo;
		private boolean revision;
		
		private final EventList<Venta> ventas;		
		
		public RevisionDeFacturasModel(final List<Venta> list) {
			super(null);
			setBean(this);
			ventas=GlazedLists.eventList(list);
			getComponentModel("cliente").setEditable(false);			
			cargar();
		}
		
		public boolean validar(){
			
			String c=null;
			int cc=0;			
			for(Venta v:ventas){
				if(c==null){
					c=v.getClave();
					cc=v.getCobrador();
					continue;
				}
				if(!c.equals(v.getClave())) return false;
				if(cc!=v.getCobrador()) return false;				
			}
			return true;
		}
		
		private void cargar(){
			setCliente(null);
			setCobrador(null);
			setDiaPago(0);
			setDiaRevision(0);			
			Venta v=ventas.get(0);
			setCliente(v.getClave());
			setCobrador(Cobradores.getCobrador(v.getCobrador()));
			setDiaPago(v.getDiaPago());
			setDiaRevision(v.getDiaRevision());
			setFecha(v.getCredito().getFechaRevisionCxc());
			setRevision(v.getCredito().isRevision());
			setPlazo(v.getCredito().getPlazo());
			setFecha(v.getCredito().getFechaRevisionCxc());
		}

		public EventList<Venta> getVentas(){
			return ventas;
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

		

		public Cobradores getCobrador() {
			return cobrador;
		}

		public void setCobrador(Cobradores cobrador) {
			Object old=this.cobrador;
			this.cobrador = cobrador;
			firePropertyChange("cobrador", old, cobrador);
		}

		public int getDiaRevision() {
			return diaRevision;
		}
		public void setDiaRevision(int diaRevision) {
			Object oldValue=this.diaRevision;
			this.diaRevision = diaRevision;
			firePropertyChange("diaRevision", oldValue, diaRevision);
		}
		
		public int getDiaPago() {
			return diaPago;
		}
		public void setDiaPago(int diaPago) {
			Object oldValue=this.diaPago;
			this.diaPago = diaPago;
			firePropertyChange("diaPago", oldValue,diaPago);
		}

		public int getPlazo() {
			return plazo;
		}
		public void setPlazo(int plazo) {
			Object oldValue=this.plazo;
			this.plazo = plazo;
			firePropertyChange("plazo", oldValue, plazo);
		}

		public boolean isRevision() {
			return revision;
		}
		public void setRevision(boolean revision) {
			boolean oldValue=this.revision;
			this.revision = revision;
			firePropertyChange("revision", oldValue, revision);
		}

		public boolean isEliminar() {
			return eliminar;
		}

		public void setEliminar(boolean eliminar) {
			this.eliminar = eliminar;
		}

		public void commit(){
			if(validar()){
				for(Venta v:getVentas()){	
					System.out.println("Aplicando cambios");
					v.setCobrador(getCobrador().getNumero());
					v.setDiaPago(getDiaPago());
					v.setDiaRevision(getDiaRevision());					
					v.getCredito().setFechaRevisionCxc(getFecha());
					v.getCredito().setPlazo(getPlazo());
					v.getCredito().setRevision(isRevision());
					System.out.println("Nuevo plazo: "+getPlazo() );
					System.out.println("Plazo en venta: "+v.getCredito().getPlazo() );
					//v.getCredito().actualizar();
				}
				
			}
			
		}
		
		
		
	}

	public static void main(String[] args) {
		List<Venta> ventas=DatosDePrueba.ventasDePrueba();
		RevisionDeFacturasDialog dialog=new RevisionDeFacturasDialog(ventas);		
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			dialog.commit();
			for(Venta v:ventas){
				String msg=MessageFormat.format("Cobrador: {0} Dia Pago: {1} Dia Rev {2} Prox Rev: {3}", v.getCobrador(),v.getDiaPago(),v.getDiaRevision(),v.getCredito().getFechaRevisionCxc());
				System.out.println(msg);
			}
		}
		
	}
	

}
