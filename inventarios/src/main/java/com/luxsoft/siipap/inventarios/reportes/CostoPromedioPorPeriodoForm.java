package com.luxsoft.siipap.inventarios.reportes;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXHeader;
import org.springframework.util.ClassUtils;

import sun.awt.image.IntegerInterleavedRaster;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.swing.binding.ArticuloBinding;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class CostoPromedioPorPeriodoForm extends SXAbstractDialog {

	final Map<String, Object>parametros;
	private JXDatePicker año;
	private JXDatePicker mes;
	private JComponent articulo;
	ValueModel articuloModel;

	public CostoPromedioPorPeriodoForm() {
		super("Cobranza Credito");
		parametros=new HashMap<String, Object>();
	}
	
	
	@Override
	protected void setResizable() {
		setResizable(true);
	}


	private void initComponents(){
		articuloModel=new ValueHolder();
		articulo=Binder.createArticulosBinding(articuloModel);
		año=new JXDatePicker();
		año.setFormats(new String[]{"dd/MM/yyyy"});
		mes=new JXDatePicker();
		mes.setFormats(new String[]{"dd/MM/yyyy"});
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		JPanel panel =new JPanel(new BorderLayout());
		//panel.add(buildHead(),BorderLayout.NORTH);
		panel.add(buildForma(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}

	private JComponent buildForma() {
		FormLayout layout=new FormLayout(
				"50dlu,4dlu,50dlu,40dlu,50dlu,50dlu,4dlu,50dlu,40dlu",
				"pref,20dlu,pref");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(new JLabel("Clave"),cc.xyw(1, 2, 2));
		builder.add(articulo,cc.xyw(3, 2, 7));
		builder.add(new JLabel("Fecha Ini"),cc.xyw(1,3,2));
		builder.add(año,cc.xyw(3, 3, 2));
		builder.add(new JLabel("Fecha fin"),cc.xyw(6, 3, 2));
		builder.add(mes,cc.xyw(7, 3, 3));
		return builder.getPanel();
	}
	
	public Map<String, Object> getParametos(){
		return parametros;
	}
	
	public void doApply(){
		SimpleDateFormat fechaaño=new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat fechames=new SimpleDateFormat("dd/MM/yyyy");

		Articulo a=(Articulo)articuloModel .getValue();
		if(a!=null){
			parametros.put("ARTICULO",a.getClave());
		}
		parametros.put("FECHA_INI",año.getDate());
		System.out.println("ini"+año.getDate());
		parametros.put("FECHA_FIN",mes.getDate());
		System.out.println("ini"+mes.getDate()); 	
		execute();
	}
	
	private void execute(){
		String path=ClassUtils.classPackageAsResourcePath(getClass());
		final String rep="/CalculoDeCostoPromedioNvo2008.jasper";
		ReportUtils.viewReport(path+rep, getParametos());
	}

	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Costo Promedio por Periodo","Reporte Costo Promedio Por Periodo");
	}
	
	
	public static void run(){
		CostoPromedioPorPeriodoForm form=new CostoPromedioPorPeriodoForm();
		form.open();
	}
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		run();
		
	}
	


}
