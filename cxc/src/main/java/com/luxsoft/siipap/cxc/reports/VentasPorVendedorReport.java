package com.luxsoft.siipap.cxc.reports;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXDatePicker;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.model.Sucursales;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

/**
 * 
 * 
 * @author OCTAVIO
 *
 */
public class VentasPorVendedorReport extends SWXAction{

	@Override
	protected void execute() {
		
		final ReportForm form=new ReportForm();
		form.open();
		if(!form.hasBeenCanceled()){
			if(logger.isDebugEnabled()){
				logger.debug("Parametros enviados: "+form.getParametros());
			}
			ReportUtils.viewReport("reportes/VentasPorVendedor.jasper", form.getParametros());
		}
		form.dispose();
	}
	

	public  class ReportForm extends SXAbstractDialog{
		
		private final Map<String, Object> parametros;
		
		
		private JXDatePicker fechaIni;
		private JXDatePicker fechaFin;
		private JTextField vendedor;
		private JComboBox tipoVenta;
		private JComboBox order1;
		private JComboBox order2;
		
		
	

		public JComboBox getTipoVenta() {
			return tipoVenta;
		}

		public ReportForm() {
			super("Cobranza Crédito");
			parametros=new HashMap<String, Object>();
		}
		
		private void initComponents(){			
			fechaIni=new JXDatePicker();
			fechaIni.setFormats(new String[]{"dd/MM/yyyy"});
			fechaFin=new JXDatePicker();
			fechaFin.setFormats(new String[]{"dd/MM/yyyy"});
			vendedor=new JTextField();
			tipoVenta=new JComboBox(Order2.values());
		/*	tipoVenta.addItem("CRE");
			tipoVenta.addItem("CON");
			tipoVenta.addItem("%M%");*/
			order1=new JComboBox(Order1.values());
			order2=new JComboBox();
			order2.addItem("ASC");
			order2.addItem("DESC");

			
			
		}

		@Override
		protected JComponent buildContent() {
			initComponents();
			JPanel panel=new JPanel(new BorderLayout());
			final FormLayout layout=new FormLayout(
					"l:40dlu,3dlu,p, 3dlu, " +
					"l:40dlu,3dlu,p:g " +
					"");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Fecha Inicial",fechaIni,5);
			builder.nextLine();
			builder.append("Fecha Final",fechaFin,5);
			builder.append("Vendedor",vendedor,5);
			builder.append("TipoVenta",tipoVenta,5);
			builder.append("Orden 1",order1,5);
			builder.append("Orden 2",order2,5);
			panel.add(builder.getPanel(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			
			return panel;
		}

		@Override
		public void doApply() {			

			parametros.put("FECHA_INI",fechaIni.getDate() );
			parametros.put("FECHA_FIN",fechaFin.getDate() );
			parametros.put("VENDEDOR", vendedor.getText());
			Order2 om=(Order2)tipoVenta.getSelectedItem();
			parametros.put("TP_VTA",om.getValor().toString());
			Order1 o=(Order1)order1.getSelectedItem();
			parametros.put("ORDER1",Integer.valueOf(o.getNumero()));
			parametros.put("ORDER2", order2.getSelectedItem());
			System.out.println("Parametros: "+parametros);
		}

		public Map<String, Object> getParametros() {
			return parametros;
		}
		
		@Override
		protected JComponent buildHeader() {
			return new HeaderPanel("Reporte Ventas Vendedor","Reporte De Ventas Por Vendedor");
		}

		
	}
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		VentasPorVendedorReport action=new VentasPorVendedorReport();
		action.execute();
	}
	
	private enum Order1{
		
	clave("ClaveCliente",1),
	nombre("NombreCliente",2),
	Venta("VentaNeta",7),
	;
	
	private final String descripcion;
	private final int numero;
	
	private Order1(final String descripcion, final int numero) {
		this.descripcion = descripcion;
		this.numero = numero;
	}
	
	public String toString(){
		return descripcion;
	}
	
	public int getNumero(){
		return numero;
	}
	
	public Integer[] todos(){
		return new Integer[]{1,4,6,7,8};
	}
	
	public static List<Order1> getOrder1(){
		ArrayList<Order1> l=new ArrayList<Order1>();
		for(Order1 c:values()){			
			l.add(c);
		}
		return l;
	}
	
	public static Order1 getOrder1(int id){
		for(Order1 c:values()){
			if(c.getNumero()==id)
				return c;
		}
		return null;
	}
	}
	
	private enum Order2{
		credito("CREDITO","CRE"),
		contado("CONTADO","%M%"),
		ambos("AMBOS","%"),;
		
		private final String item;
		private final String valor;
		
		private Order2(final String item, final String valor) {
			this.item = item;
			this.valor = valor;
		}
		
		public String toString(){
			return item;
		}
		
		public String getValor(){
			return valor;
		}
		
		public Integer[] todos(){
			return new Integer[]{1,4,6,7,8};
		}
		
		public static List<Order2> getOrder2(){
			ArrayList<Order2> l=new ArrayList<Order2>();
			for(Order2 c:values()){			
				l.add(c);
			}
			return l;
		}
		
		public static Order2 getOrder2(String id){
			for(Order2 c:values()){
				if(c.getValor()==id)
					return c;
			}
			return null;
		}
		
	}

}
