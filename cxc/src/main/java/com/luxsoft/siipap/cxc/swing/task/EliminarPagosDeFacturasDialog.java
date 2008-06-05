package com.luxsoft.siipap.cxc.swing.task;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.UniqueList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.UIFactory;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.Validator;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.cxc.swing.binding.CantidadMonetariaBinding;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

public class EliminarPagosDeFacturasDialog extends SXAbstractDialog{
	
	private final EliminarPagosDeFacturaModel model;
	private EventSelectionModel<NotaDeCredito> descuentosSelectionModel;
	private EventSelectionModel<Pago> pagoSelectionModel;
	
	private JFormattedTextField cliente;
	private JFormattedTextField numero;
	private JFormattedTextField numeroFiscal;
	
	private JFormattedTextField fecha;
	private JFormattedTextField vencimiento;	
	private JFormattedTextField total;	
	private JFormattedTextField saldo;
	
	

	public EliminarPagosDeFacturasDialog(final EliminarPagosDeFacturaModel model) {
		super("Eliminación de Pagos");
		this.model=model;
		
	}
	
	private void initComponents(){
		numero=new JFormattedTextField(NumberFormat.getIntegerInstance());
		numero.setValue(model.getValue("numero"));
		readOnly(numero);
		numeroFiscal=new JFormattedTextField(NumberFormat.getIntegerInstance());
		numeroFiscal.setValue(model.getValue("numeroFiscal"));
		readOnly(numeroFiscal);
		cliente=new JFormattedTextField();
		cliente.setValue(model.getValue("nombre"));
		readOnly(cliente);
		fecha=new JFormattedTextField(DateFormat.getDateInstance());
		fecha.setValue(model.getValue("fecha"));
		readOnly(fecha);
		vencimiento=new JFormattedTextField(DateFormat.getDateInstance());
		vencimiento.setValue(model.getValue("vencimiento"));
		readOnly(vencimiento);
		CantidadMonetariaBinding b=new CantidadMonetariaBinding(model.getModel("total"));
		total=b.getControl();
		//total.setValue(model.getValue("total").toString());
		readOnly(total);
		saldo=BasicComponentFactory.createFormattedTextField(model.getModel("saldo"), NumberFormat.getCurrencyInstance());
		readOnly(saldo);
		
	}
	private void readOnly(JTextField c){
		c.setEditable(false);
		c.setFocusable(false);
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		JPanel p=new JPanel(new BorderLayout());		
		p.add(buildMainPanel(),BorderLayout.CENTER);
		p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return p;
	}
	
	private JComponent buildMainPanel(){
		PanelBuilder builder=new PanelBuilder(new FormLayout("f:p:g","p,3dlu,p,3dlu,p,3dlu,20dlu"));
		CellConstraints cc=new CellConstraints();
		int row=1;
		builder.add(buildVentaPanel(),cc.xy(1, row));
		row+=2;
		builder.add(buildDescuentosPanel(),cc.xy(1, row));
		row+=2;
		builder.add(buildPagosPanel(),cc.xy(1, row));
		row+=2;
		builder.add(ValidationResultViewFactory.createReportList(model.getValidationModel()),cc.xy(1, row));
		row+=2;
		return builder.getPanel();
	}
	
	private JComponent buildVentaPanel(){
		DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("50dlu,3dlu,60dlu,3dlu:g,50dlu,3dlu,60dlu",""));
		builder.append("Cliente",cliente,5);
		builder.nextLine();
		builder.append("Factura",numero);
		builder.append("N. Fiscal",numeroFiscal,true);
		builder.append("Fecha",fecha);
		builder.append("Vencimiento",vencimiento,true);
		builder.append("Total",total);
		builder.append("Saldo",saldo);
		return builder.getPanel();
	}
	
	
	
	@SuppressWarnings("unchecked")
	private JComponent buildDescuentosPanel(){
		final String props[]={"id","numero","tipo","serie","descuento","importe"};
		final String labels[]={"Id","Numero","Tipo","Serie","Descuento","Importe"};
		TableFormat tf=GlazedLists.tableFormat(props,labels);
		EventTableModel tm=new EventTableModel(model.getDescuentos(),tf);
		JTable descGrid=new JTable(tm);
		descuentosSelectionModel=new EventSelectionModel(model.getDescuentos());
		descGrid.setSelectionModel(descuentosSelectionModel);
		Action deleteDescAction=new com.luxsoft.siipap.swing.actions.DispatchingAction(this,"eliminarDescuento");
		ComponentUtils.addAction(descGrid, deleteDescAction, KeyStroke.getKeyStroke("DELETE"));
		JComponent c=UIFactory.createTablePanel(descGrid);
		return c;
	}
	
	@SuppressWarnings("unchecked")
	private JComponent buildPagosPanel(){
		final String props[]={"id","descFormaDePago","referencia","importe"};
		final String labels[]={"Id","Forma de Pago","Referencia","Importe"};
		TableFormat tf=GlazedLists.tableFormat(props,labels);
		EventTableModel tm=new EventTableModel(model.getPagos(),tf);
		final JTable pagosGrid=new JTable(tm);
		pagoSelectionModel=new EventSelectionModel(model.getPagos());
		pagosGrid.setSelectionModel(descuentosSelectionModel);
		Action deleteDescAction=new com.luxsoft.siipap.swing.actions.DispatchingAction(this,"eliminarPago");
		ComponentUtils.addAction(pagosGrid, deleteDescAction, KeyStroke.getKeyStroke("DELETE"));
		JComponent c=UIFactory.createTablePanel(pagosGrid);
		return c;
	}
	
	public void eliminarDescuento(){
		if(descuentosSelectionModel.getSelected().isEmpty())return;
		ListIterator<NotaDeCredito> iter=descuentosSelectionModel.getSelected().listIterator();
		while(iter.hasNext()){
			NotaDeCredito n=iter.next();
			if(n.getNumero()>0){
				MessageUtils.showMessage("No se puede eliminar la nota de descuento por que ya esta aplicada", "Eliminar Descuento");
				return;
			}
			//OK ELIMINAR EL DESCUENTO
			model.eliminarDescuento(n);
			iter.remove();
		}		
	}
	
	public void eliminarPago(){
		if(pagoSelectionModel.getSelected().isEmpty())return;
		ListIterator<Pago> iter=pagoSelectionModel.getSelected().listIterator();
		while(iter.hasNext()){
			Pago p=iter.next();
			model.eliminarPago(p);
			iter.remove();
		}
	}
	
	public static class EliminarPagosDeFacturaModel extends PresentationModel implements Validator{
		
		private EventList<Pago> pagos;
		private EventList<NotaDeCredito> descuentos;
		private CXCManager manager;
		private final ValidationResultModel validationModel;
		
		private EventList<NotaDeCredito> descuentosEliminados;
		private EventList<Pago> pagosEliminados;
		

		public EliminarPagosDeFacturaModel(Venta v) {
			super(v);
			validationModel=new DefaultValidationResultModel();
			initEventList();
			initEventHandling();
		}
		
		@SuppressWarnings("unchecked")
		private void initEventHandling(){
			addBeanPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					validationModel.setResult(validate());
				}				
			});
			pagos.addListEventListener(saldoHandler);
			descuentos.addListEventListener(saldoHandler);
		}
		
		private Venta getVenta(){
			return (Venta)getBean();
			
		}
		
		private void initEventList(){
			pagos=GlazedLists.eventList(getVenta().getPagosAplicados());
			descuentos=GlazedLists.eventList(getVenta().getNotasAplicadas());
			
			descuentosEliminados=new UniqueList<NotaDeCredito>(new BasicEventList<NotaDeCredito>(),GlazedLists.beanPropertyComparator(NotaDeCredito.class, "id"));
			pagosEliminados=new UniqueList<Pago>(new BasicEventList<Pago>(),GlazedLists.beanPropertyComparator(Pago.class, "id"));
			
		}
		
		public ValidationResultModel getValidationModel(){
			return this.validationModel;
		}
		
		public void load(){
			
		}
		
		public void eliminarDescuento(NotaDeCredito n){
			descuentosEliminados.add(n);
		}
		public void eliminarPago(Pago p){
			pagosEliminados.add(p);
		}

		public EventList<NotaDeCredito> getDescuentos() {
			return descuentos;
		}

		public EventList<Pago> getPagos() {
			return pagos;
		}

		public CXCManager getManager() {
			return manager;
		}
		public void setManager(CXCManager manager) {
			this.manager = manager;
		}

		public ValidationResult validate() {
			// TODO Auto-generated method stub
			return null;
		}
		
		/**
		 * Actualiza el saldo de la venta 
		 *
		 */
		private void updateSaldo(){
			CantidadMonetaria monto=null;
			for(Pago p:getPagos()){
				if(monto==null){
					monto=p.getImporte();
				}
				monto=monto.add(p.getImporte());					
			}
			for(NotaDeCredito d:getDescuentos()){
				CantidadMonetaria dd=new CantidadMonetaria(d.getTotalAsBigDecimal().abs().doubleValue(),d.getImporte().currency());
				monto=monto.add(dd);
			}
			
			CantidadMonetaria t=getVenta().getTotal().subtract(monto);
			getVenta().setSaldo(t.amount());
		}
		
		private ListEventListener saldoHandler=new ListEventListener(){
			public void listChanged(ListEvent listChanges) {
				while(listChanges.hasNext()){
					updateSaldo();
				}
			}
			
		};
		
		
		
	}
	
	public static void main(String[] args) {
		Venta v=DatosDePrueba.ventasDePrueba().get(0);
		EliminarPagosDeFacturaModel model=new EliminarPagosDeFacturaModel(v);
		EliminarPagosDeFacturasDialog dialog=new EliminarPagosDeFacturasDialog(model);
		dialog.open();
		
	}

}
