package com.luxsoft.siipap.cxc.swing.notas2;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasFactory;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.form.AbstractForm;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;


public class NCDevolucionForm extends AbstractForm{
	
	private JFormattedTextField devoNumero;
	private JFormattedTextField devoFecha;
	private JFormattedTextField devoComentario;
	private JFormattedTextField devoDescuento;
	
	
	private JFormattedTextField ventaNumero;
	private JFormattedTextField ventaFecha;
	private JFormattedTextField ventaTotal;
	private JFormattedTextField ventaSaldo;
	private JFormattedTextField ventaDescuentos;
	
	
	protected JXTable partidasGrid;
	
	
	
	private EventSelectionModel<DevolucionDet> selectionModel;

	public NCDevolucionForm(NCDevolucionFormModel model) {
		super(model);
	}
	
	protected JComponent buildFormPanel() {		
		PanelBuilder builder=new PanelBuilder(new FormLayout("max(p;450dlu):g","p,10dlu,p:g"));
		CellConstraints cc=new CellConstraints();
		builder.add(buildEditorPanel(),cc.xy(1, 1));
		builder.add(buildGridPanel(),cc.xy(1, 3));
		return builder.getPanel();
	}
	
	protected void initComponents(){
		NumberFormat nnf=NumberFormat.getIntegerInstance();
		nnf.setGroupingUsed(false);
		devoNumero=new JFormattedTextField(nnf);
		
		ComponentUtils.addF2Action(devoNumero,new AbstractAction("F2"){
			public void actionPerformed(ActionEvent e) {
				lookup();				
			}			
		});
		devoFecha=new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		devoComentario=new JFormattedTextField();
		
		NumberFormat pf=NumberFormat.getPercentInstance();
		pf.setMinimumFractionDigits(2);
		devoDescuento=new JFormattedTextField(pf);
		devoDescuento.setBorder(null);
		
		ventaNumero=new JFormattedTextField(nnf);
		ventaFecha =new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		ventaTotal=new JFormattedTextField();
		ventaSaldo=new JFormattedTextField(NumberFormat.getCurrencyInstance());
		
		NumberFormat nf=NumberFormat.getCurrencyInstance();				
		ventaDescuentos=new JFormattedTextField(nf);
		
		
		decorateDisable(devoFecha,devoComentario,ventaNumero,ventaFecha,ventaSaldo,ventaTotal,ventaDescuentos,devoDescuento);
	}

	
	protected JComponent buildEditorPanel() {
		FormLayout layout=new FormLayout(
				"r:max(40dlu;p),2dlu,max(p;60dlu) ,3dlu, " +
				"r:max(40dlu;p),2dlu,max(p;60dlu) ,3dlu, " +
				"r:max(40dlu;p),2dlu,p:g" 
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Cliente",add("cliente"),9);
		builder.nextLine();
		
		builder.appendSeparator("Devolución");
		builder.nextLine();
		JLabel l=builder.append("Número" ,devoNumero);
		decorateF2Label(l);		
		
		builder.append("Fecha Dev",devoFecha,true);		
		builder.append("Descripción",devoComentario,true);
		
		builder.appendSeparator("Venta");
		builder.nextLine();
		builder.append("Número",ventaNumero);
		builder.append("Fecha",ventaFecha,true);
		builder.append("Total",ventaTotal);
		builder.append("Descuentos",ventaDescuentos);
		builder.append("",devoDescuento);
		
		builder.append("Saldo",ventaSaldo,true);
		
		builder.appendSeparator("Nota de Crédito");
		builder.nextLine();
		builder.append("Fecha",add("fecha"));		
		builder.append("Importe",add("importe"),true);		
		builder.append("Iva",add("iva"));
		builder.append("Total",add("totalAsMoneda"),true);
		builder.append("Comentario 1",add("comentario"),9);
		builder.nextLine();
		builder.append("Comentario 2",add("comentario2"),9);
		builder.nextLine();
		builder.appendSeparator("Detalle de la devolución");
		return builder.getPanel();
	}
	
	
	protected JComponent buildGridPanel(){
		partidasGrid=ComponentUtils.getStandardTable();
		final TableFormat<DevolucionDet> tf=GlazedLists.tableFormat(DevolucionDet.class, getPartidasProps(), getPartidasLabels());
		
		final SortedList<DevolucionDet> sortedList=new SortedList<DevolucionDet>(getNCModel().getDetalles(),null);
		final EventTableModel<DevolucionDet> tm=new EventTableModel<DevolucionDet>(sortedList,tf);
		selectionModel=new EventSelectionModel<DevolucionDet>(sortedList);
		partidasGrid.setModel(tm);
		partidasGrid.setSelectionModel(selectionModel);	
		partidasGrid.packAll();
		ComponentUtils.decorateActions(partidasGrid);		
		new TableComparatorChooser<DevolucionDet>(partidasGrid,sortedList,true);
		JComponent c=UIFactory.createTablePanel(partidasGrid);
		//c.setPreferredSize(new Dimension(500,250));		
		return c;
	}

	protected String[] getPartidasProps(){
		return new String[]{"ventaDet.id","numero","articulo.clave","articulo.descripcion1","cantidad","ventaDet.precioFacturado","importe","devolucion.cliente"};
	}
	protected String[] getPartidasLabels(){
		return new String[]{"VentaDet ID","Número","Articulo","Descripción","Cantidad","Precio","Devolución","Cliente"};
	}
	
	public void lookup(){
		final NCSelectorDeDevoluciones selector=new NCSelectorDeDevoluciones();
		
		SwingWorker<List<DevolucionDet>, String> worker=new SwingWorker<List<DevolucionDet>, String>(){
			protected List<DevolucionDet> doInBackground() throws Exception {
				return getNCModel().buscarDevoluciones();
			}			
			protected void done() {				
				try {
					selector.getDevoluciones().clear();
					selector.getDevoluciones().addAll(get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		};
		TaskUtils.executeSwingWorker(worker);
		selector.open();
		if(!selector.hasBeenCanceled()){
			Devolucion d=selector.getSelected();
			getNCModel().getFormBean().setDevolucion(d);
			actualizarDevolucion(d);
			partidasGrid.packAll();
		}
	}
	
	public void insert(){
		
	}
	
	private NCDevolucionFormModel getNCModel(){
		return (NCDevolucionFormModel)getFormModel();
	}
	
	public void actualizarDevolucion(final Devolucion d){
		this.devoNumero.setValue(d.getNumero());
		devoFecha.setValue(d.getFecha());
		devoComentario.setValue(d.getComentario());
		//devoDescuento.setValue(d.getDescuento());
		ventaNumero.setValue(d.getVenta().getNumero());
		ventaFecha.setValue(d.getVenta().getFecha());
		ventaSaldo.setValue(d.getVenta().getSaldo());
		ventaDescuentos.setValue(d.getVenta().getDescuentos());
		ventaTotal.setValue(d.getVenta().getTotal());
	}
	
	
	public static class NCDevolucionFormModel extends AbstractGenericFormModel<NotaDeCredito, Long>{
		
		private final EventList<DevolucionDet> devoluciones;
		private final EventList<DevolucionDet> detalles;
		
		
		private NotasManager manager;	

		public NCDevolucionFormModel(Object bean) {
			super(bean);
			devoluciones=GlazedLists.threadSafeList(new BasicEventList<DevolucionDet>());
			detalles=GlazedLists.threadSafeList(new BasicEventList<DevolucionDet>());
			
		}
		
		public EventList<DevolucionDet> getDevoluciones(){
			return devoluciones;
		}
		public EventList<DevolucionDet> getDetalles(){
			return detalles;
		}

		@Override
		protected void initModels() {			
			super.initModels();
			getComponentModel("importe").setEnabled(false);
			getComponentModel("iva").setEnabled(false);
			getComponentModel("totalAsMoneda").setEnabled(false);
		}
		
		
		@Override
		protected void initEventHandling() {			
			super.initEventHandling();
			addBeanPropertyChangeListener("cliente",new ClienteHandler());
			addBeanPropertyChangeListener("devolucion",new DevolucionHandler());
		}
		
		public List<DevolucionDet> buscarDevoluciones(){
			return getManager().buscarDevoluciones(getFormBean().getCliente().getClave());
		}
		
		private void actualizarDevolucion(){		
			detalles.clear();
			if(getFormBean().getDevolucion()!=null){
				Devolucion d=getFormBean().getDevolucion();				
				//getManager().actualizarDevolucion(d);
				getFormBean().setImporte(d.getImporte());
				getFormBean().setImpuesto(d.getImpuesto().amount().doubleValue());
				getFormBean().setTotal(d.getTotal().amount().doubleValue());
				detalles.addAll(d.getPartidas());
			}
		}
		
		/**
		 * Actualiza la Nota por cambio de cliente, limpiando la devolución existente
		 * 
		 * @author Ruben Cancino
		 *
		 */
		private class ClienteHandler implements PropertyChangeListener{
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Cliente nuevo: "+evt.getNewValue());
				getFormBean().setDevolucion(null);
			}			
		}
		
		private class DevolucionHandler implements PropertyChangeListener{
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Devolucion seleccionada "+evt.getNewValue());
				actualizarDevolucion();
			}			
		}
		
		public NotasManager getManager() {
			return manager;
		}

		public void setManager(NotasManager manager) {
			this.manager = manager;
		}
		
		public void dispose(){
			devoluciones.clear();
			detalles.clear();
		}
		
		
	}
		
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		NotaDeCredito n=NotasFactory.getNotaDeCreidotDevo();
		Cliente c=new Cliente();
		c.setNombre("Union de credito");
		c.setClave("U050008");
		n.setCliente(c);
		n.setClave(c.getClave());
		NCDevolucionFormModel model=new NCDevolucionFormModel(n);
		model.setManager((NotasManager)ServiceLocator.getDaoContext().getBean("notasManager"));
		NCDevolucionForm form=new NCDevolucionForm(model);		
		FormDialog dialog=new FormDialog(form);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			NotaDeCredito nt=(NotaDeCredito)form.getBean();
			System.out.println(NotasFactory.toString(nt));
			NotasManager manager=(NotasManager)ServiceLocator.getDaoContext().getBean("notasManager");
			//manager.salvarNCDevoCRE(nt);
		}
		System.exit(0);
	}

	
	
	

}
