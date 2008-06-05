package com.luxsoft.siipap.cxc.swing.notas2;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.text.NumberFormatter;

import org.springframework.util.Assert;

import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form.AbstractForm;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para el mantenimiento partidas de notas de credito relacionadas
 * con ventas
 * 
 * @deprecated No es necesario 
 * 
 * @author Ruben Cancino
 *
 */
public class NCDForm extends AbstractForm{
	
	private JFormattedTextField numeroVenta;
	private JFormattedTextField fechaVenta;
	private JFormattedTextField totalVenta;
	private JFormattedTextField saldoVenta;
	private JCheckBox porDescuento;
	private DescHandler handler;
	private NotasManager manager;

	public NCDForm(final NotasDeCreditoDet det) {		
		super(new NCDFormModel(det));
		Assert.notNull(det.getNota());
		Assert.notNull(det.getNota().getCliente());
		handler=new DescHandler();
		initModels();
		
	}
	
	protected void initModels(){
		getFormModel().getPresentationModel().getModel("factura").addValueChangeListener(new VentaHandler());
		getFormModel().getPresentationModel().getModel("descuento").addValueChangeListener(handler);
	}
	
	
	@Override
	protected void initComponents() {
		super.initComponents();
		NumberFormat nf=NumberFormat.getIntegerInstance();
		nf.setMinimumFractionDigits(0);
		NumberFormatter formatter=new NumberFormatter(nf);
		formatter.setValueClass(Long.class);
		formatter.setCommitsOnValidEdit(false);
		numeroVenta=new JFormattedTextField(formatter);
		ComponentUtils.addF2Action(numeroVenta, new AbstractAction("f2"){
			public void actionPerformed(ActionEvent e) {
				lookup();
			}			
		});
		
		
		fechaVenta=new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
		fechaVenta.setEditable(false);
		fechaVenta.setFocusable(false);
		
		totalVenta=new JFormattedTextField(NumberFormat.getCurrencyInstance());
		totalVenta.setEditable(false);
		totalVenta.setFocusable(false);
		
		saldoVenta=new JFormattedTextField(NumberFormat.getCurrencyInstance());
		saldoVenta.setEditable(false);
		saldoVenta.setFocusable(false);
		porDescuento=new JCheckBox("Por descuento",true);
		porDescuento.addItemListener(handler);
	}

	protected void lookup(){
		NCSelectorDeVentas dialog=new NCSelectorDeVentas();
		cargarVentas(dialog.getVentas());
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			Venta v=dialog.getSelected();			
			getNCDModel().getFormBean().setFactura(v);
		}
		dialog.dispose();
	}
	
	private NCDFormModel getNCDModel(){
		return (NCDFormModel)getFormModel();
	}

	@Override
	protected JComponent buildFormPanel() {
		FormLayout layout=new FormLayout(
				"l:40dlu,3dlu,max(p;60dlu):g(.5),2dlu," +
				"l:40dlu,3dlu,max(p;60dlu):g(.5) "
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.appendSeparator("Venta");
		decorateF2Label(builder.append("Número",numeroVenta));
		builder.append("Fecha",fechaVenta,true);
		builder.append("Total",totalVenta);
		builder.append("Saldo",saldoVenta);
		builder.appendSeparator("Nota de Crédito");
		builder.append("Descuento",add("descuento"));
		builder.append(" ",porDescuento);
		builder.append("Importe",add("importe"));
		CellConstraints cc=new CellConstraints();
		builder.appendRow(builder.getLineGapSpec());
		builder.appendRow("top:30dlu");
		builder.nextLine(2);
		builder.append("Comentario");		
		builder.add(add("comentario"),cc.xyw(builder.getColumn(), builder.getRow(),5,"fill,fill"));
		//builder.append("Comentario",add("comentario"),5);
		builder.nextLine();
		return builder.getPanel();
	}
	
	
	
	@Override
	protected JComponent getCustomComponent(String property, ComponentValueModel vm) {
		if("comentario".equals(property))
			return new JScrollPane(BasicComponentFactory.createTextArea(vm,false));
		if("descuento".equals(property))
			return Binder.createDescuentoBinding(vm);
		return super.getCustomComponent(property, vm);
	}
	
	private class VentaHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {	
			Venta v=(Venta)evt.getNewValue();
			numeroVenta.setValue(v.getNumero());
			fechaVenta.setValue(v.getFecha());
			saldoVenta.setValue(v.getSaldo());
			totalVenta.setValue(v.getTotal().amount());
			numeroVenta.setEditable(false);
		}		
	}
	
	private class DescHandler implements ItemListener,PropertyChangeListener{
		public void itemStateChanged(ItemEvent e) {			
			if(e.getStateChange()==ItemEvent.SELECTED){				
				getFormModel().getPresentationModel().getComponentModel("descuento").setEnabled(true);
				getFormModel().getPresentationModel().getComponentModel("importe").setEnabled(false);
			}else{				
				getFormModel().getPresentationModel().getComponentModel("descuento").setEnabled(false);
				getFormModel().getPresentationModel().getComponentModel("importe").setEnabled(true);
				getNCDModel().getFormBean().setDescuento(0);
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			try {
				//Actualizar el importe
				double desc=(Double)evt.getNewValue();
				CantidadMonetaria total=getNCDModel().getFormBean().getFactura().getTotal();
				CantidadMonetaria descuento=total.abs().multiply((desc/100));
				getNCDModel().getFormBean().setImporte(descuento);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}

	public NotasManager getManager() {
		return manager;
	}

	public void setManager(NotasManager manager) {
		this.manager = manager;
	}
	
	
	
	private void cargarVentas(final EventList<Venta> ventas){
		Assert.notNull(getManager(),"No existe Manager");
		SwingWorker<List<Venta>,String> worker=new SwingWorker<List<Venta>, String>(){			
			protected List<Venta> doInBackground() throws Exception {
				return getManager().buscarVentasParaNota(getNCDModel().getFormBean().getNota().getCliente().getClave());
			}
			protected void done() {				
				try {
					ventas.addAll(get());
				} catch (Exception e) {
				}
			}			
		};
		TaskUtils.executeSwingWorker(worker);		
	}

	public static class NCDFormModel extends AbstractGenericFormModel<NotasDeCreditoDet, Long>{

		public NCDFormModel(Object bean) {
			super(bean);
		}
		
		
		
		@Override
		protected void initModels() {			
			super.initModels();
			getComponentModel("importe").setEnabled(false);
		}



		
	}



	

}
