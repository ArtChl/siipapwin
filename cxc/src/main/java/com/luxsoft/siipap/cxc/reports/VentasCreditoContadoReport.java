package com.luxsoft.siipap.cxc.reports;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.model.Sucursales;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

@SuppressWarnings("serial")
public class VentasCreditoContadoReport extends SWXAction{
	
	@Override
	protected void execute() {
		
		final ReportForm form=new ReportForm();
		form.open();
		if(!form.hasBeenCanceled()){
			if(logger.isDebugEnabled()){
				logger.debug("Parametros enviados: "+form.getParametros());
			}
			if(form.getCk().isSelected()==true){
				ReportUtils.viewReport("reportes/VentasCreditoContado_Resumen.jasper", form.getParametros());
			}			
            if(form.getCk().isSelected()==false){
            	ReportUtils.viewReport("reportes/VentasCreditoContado.jasper", form.getParametros());	
			}
			
			
		}
		form.dispose();
	}
	
	
	

	public class ReportForm extends SXAbstractDialog{
		public  Map<String, Object>parametros;
		private JXDatePicker ini;
		private JXDatePicker fin;
		private JComponent cliente;
		private JComboBox sucursales;
		private JComboBox tipo_vta;
		private JComboBox orden1;
		private JComboBox orden2;
		private JCheckBox ck;
		ValueModel clienteModel;

		public ReportForm() {
			super("Ventas  Credito Contado");
			parametros=new HashMap<String, Object>();
		}
		
		
		@Override
		protected void setResizable() {
			setResizable(true);
		}


		private void initComponents(){
			clienteModel=new ValueHolder();
			cliente=Binder.createClientesBinding(clienteModel);
			ini=new JXDatePicker();
			ini.setFormats(new String[]{"dd/MM/yyyy"});
			fin=new JXDatePicker();
			fin.setFormats(new String[]{"dd/MM/yyyy"});
			sucursales=new JComboBox(Sucursales.values());
			sucursales.insertItemAt("Todas", 0);
			sucursales.setSelectedIndex(0);
			tipo_vta=new JComboBox(Order2.values());
			orden1=new JComboBox(Order1Det.values());
			orden2=new JComboBox();
			orden2.addItem("ASC");
			orden2.addItem("DESC");
			ck=new JCheckBox();
			ck.setSelected(false);
			
		}

		@Override
		protected JComponent buildContent() {
			initComponents();
			JPanel panel =new JPanel(new BorderLayout());
			panel.add(buildForma(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			return panel;
		}

		private JComponent buildForma() {
			FormLayout layout=new FormLayout(
					"50dlu,4dlu,50dlu,40dlu,50dlu,50dlu,4dlu,50dlu,40dlu",
					"pref,20dlu,pref,20dlu,pref,20dlu,pref");
			PanelBuilder builder=new PanelBuilder(layout);
			CellConstraints cc=new CellConstraints();
			builder.add(new JLabel("Clave"),cc.xyw(1, 2, 2));
			builder.add(cliente,cc.xyw(3, 2, 7));
			builder.add(new JLabel("Inicial"),cc.xyw(1,3,2));
			builder.add(ini,cc.xyw(3, 3, 2));
			builder.add(new JLabel("final"),cc.xyw(6, 3, 2));
			builder.add(fin,cc.xyw(7, 3, 3));
			builder.add(new JLabel("Tipo"),cc.xyw(1, 4, 2));
			builder.add(tipo_vta,cc.xyw(3, 4, 2));
			builder.add(new JLabel("Sucursal"),cc.xyw(6, 4, 2));
			builder.add(sucursales,cc.xyw(7, 4, 3));
			builder.add(new JLabel("Orden 1"),cc.xyw(1, 5, 2));
			builder.add(orden1,cc.xyw(3, 5, 2));
			builder.add(new JLabel("Orden 2"),cc.xyw(6, 5, 2));
			builder.add(orden2,cc.xyw(7, 5, 2));
			builder.add(new JLabel("Resumen"),cc.xyw(6, 6, 2));
			builder.add(ck,cc.xyw(7, 6, 2));
			
			return builder.getPanel();
		}
		
		public Map<String, Object> getParametros() {
			return parametros;
		}
		
		
		public void doApply(){
			String selected=sucursales.getSelectedItem().toString();
			Order2 om=(Order2)tipo_vta.getSelectedItem();

			parametros.put("FECHA_INI",ini.getDate());
			parametros.put("FECHA_FIN",fin.getDate());
			parametros.put("TIPO_VTA",om.getValor().toString());
			Cliente a=(Cliente)clienteModel.getValue();
			if(a!=null){
				parametros.put("CLIENTE",a.getClave());
			}
			
			if(selected.equalsIgnoreCase("Todas")){
				parametros.put("SUCURSAL","%");
			}else{
				Sucursales s=(Sucursales)sucursales.getSelectedItem();
				parametros.put("SUCURSAL",String.valueOf(s.getNumero()));
			}
			Order1Det o=(Order1Det)orden1.getSelectedItem();
			parametros.put("ORDER1",String.valueOf(o.getNumero()));
			parametros.put("ORDER2", orden2.getSelectedItem());
			System.out.println("Parametros: "+parametros);
		}
		
	

		@Override
		protected JComponent buildHeader() {
			return new HeaderPanel("reporte de Ventas Credito Contado  ","Reporte Ventas de Credito Contado Detalle Resumen");
		}


		public JCheckBox getCk() {
			return ck;
		}
	
		
		
		
	}
	
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
	VentasCreditoContadoReport c=new VentasCreditoContadoReport();
	c.execute();
		
	}
	
	private enum Order1Det{
		
		clave("ClaveCliente","1"),
		nombre("NombreCliente","2"),
		Venta("VentaNeta","7"),
		TC("Venta Total Cliente","8")
		;
		
		private final String descripcion;
		private final String numero;
		
		private Order1Det(final String descripcion, final String numero) {
			this.descripcion = descripcion;
			this.numero = numero;
		}
		
		public String toString(){
			return descripcion;
		}
		
		public String getNumero(){
			return numero;
		}
		
		public Integer[] todos(){
			return new Integer[]{1,4,6,7,8};
		}
		
		public static List<Order1Det> getOrder1(){
			ArrayList<Order1Det> l=new ArrayList<Order1Det>();
			for(Order1Det c:values()){			
				l.add(c);
			}
			return l;
		}
		
		public static Order1Det getOrder1(String id){
			for(Order1Det c:values()){
				if(c.getNumero()==id)
					return c;
			}
			return null;
		}
		}

	


	


}
