package com.luxsoft.siipap.cxc.swing.consultas;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXHeader;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.swing.binding.ClienteBinding;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

/**
 * Ejectura el reporte de Estado de Cuenta
 * 
 * @author Ruben Cancino
 *
 */
public class EstadoDeCuentaForm extends SXAbstractDialog{
	
	private final Map<String, Object> parametros;
	
	private JXDatePicker datePicker;
	private ValueModel clienteModel;
	private ClienteBinding clienteBinding;

	public EstadoDeCuentaForm() {
		super("Estado de Cuenta");
		parametros=new HashMap<String, Object>();
	}
	
	protected void initComponents(){
		datePicker=new JXDatePicker();
		datePicker.setFormats(new String[]{"dd/MM/yyyy","dd/MM/yy"});
		clienteModel=new ValueHolder();
		clienteBinding=new ClienteBinding(clienteModel);
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildFormPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	protected JComponent buildFormPanel(){
		FormLayout layout=new FormLayout(
				"l:40dlu,2dlu,60dlu ,2dlu," +
				"l:40dlu,2dlu,p:g"
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Cliente",clienteBinding.getControl(),5);
		builder.nextLine();
		builder.append("Corte",datePicker);
		return builder.getPanel();
	}
	
	@Override
	protected JComponent buildHeader() {		
		return new JXHeader("Estado de cuenta","Reporte de estado de cuenta por cliente");
	}

	public Map<String, Object> getParametros() {
		return parametros;
	}
	
	public void doApply(){
		parametros.put("FECHA", datePicker.getDate());
		Cliente c=(Cliente)clienteModel.getValue();
		if(c!=null){
			parametros.put("CLIENTE",c.getClave());
		}
		execute();
	}
	
	
	public   void execute(){		
		ReportUtils.viewReport("reportes/EstadoDeCuenta.jasper", getParametros());
	}
	
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		new EstadoDeCuentaForm().execute();
		//System.exit(0);
	}

}
