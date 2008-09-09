package com.luxsoft.siipap.inventarios.reportes;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;
import org.springframework.util.ClassUtils;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.inventarios.reportes.ComsSinAnalizarReport.SucursalesInv;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class MovimientosPorMesSucForm extends SXAbstractDialog{

	final Map<String, Object>parametros;
	private JXDatePicker año;
	private JXDatePicker mes;
	private JComboBox sucursales;
	ValueModel articuloModel;

	public MovimientosPorMesSucForm() {
		super("Movimientos Costos por Mes y Suc");
		parametros=new HashMap<String, Object>();
	}
	
	
	private void initComponents(){
		articuloModel=new ValueHolder();
		año=new JXDatePicker();
		año.setFormats(new String[]{"yyyy"});
		mes=new JXDatePicker();
		mes.setFormats(new String[]{"MM"});
		sucursales=new JComboBox(SucursalesInv.values());
		sucursales.insertItemAt("Todas", 0);
		sucursales.setSelectedIndex(0);
	}
 
	@Override
	protected JComponent buildContent() {
		initComponents();
		JPanel panel =new JPanel(new BorderLayout());
	//	panel.add(buildHead(),BorderLayout.NORTH);
		panel.add(buildForma(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}

	private JComponent buildForma() {
		FormLayout layout=new FormLayout(
				"50dlu,4dlu,50dlu,40dlu,50dlu",
				"pref,20dlu,pref,20dlu,pref,20dlu,pref");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(new JLabel("Año"),cc.xyw(1,2,2));
		builder.add(año,cc.xyw(3, 2, 2));
		builder.add(new JLabel("Mes"),cc.xyw(1, 3, 2));
		builder.add(mes,cc.xyw(3, 3, 2));
		builder.addLabel("Sucursal",cc.xyw(1, 4, 2));
		builder.add(sucursales,cc.xyw(3, 4, 2));
		
		return builder.getPanel();
	}
	
	public Map<String, Object> getParametos(){
		return parametros;
	}
	
	
	public void doApply(){
		SimpleDateFormat fechaaño=new SimpleDateFormat("yyyy");
		SimpleDateFormat fechames=new SimpleDateFormat("MM");
		
		Integer fechainicial=new Integer(fechaaño.format(año.getDate()));
		Integer fechafinal=new Integer(fechames.format(mes.getDate()));

		parametros.put("AÑO",fechainicial.toString());
		parametros.put("MES",fechafinal.toString());
		String selected=sucursales.getSelectedItem().toString();
		if(selected.equalsIgnoreCase("Todas")){
			parametros.put("SUC","%");
		}else{
			SucursalesInv s=(SucursalesInv)sucursales.getSelectedItem();
			parametros.put("SUC",String.valueOf(s.getNumero()));
		}
		execute();
	}
	
	private void execute(){
		String path=ClassUtils.classPackageAsResourcePath(getClass());
		final String rep="/MovInv.jasper";
		ReportUtils.viewReport(path+rep, getParametos());
	}
	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Movimientos Costos Por Mes y Sucursal","Reporte Costos Por Mes y Sucursal");
	}

	public static void run(){
		MovimientosPorMesSucForm form= new MovimientosPorMesSucForm();
		form.open();
	}
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		run();
	}
	


	

}
