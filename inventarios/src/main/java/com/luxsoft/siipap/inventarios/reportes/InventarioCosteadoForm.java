package com.luxsoft.siipap.inventarios.reportes;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;
import org.springframework.util.ClassUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.domain.Sucursales;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class InventarioCosteadoForm extends SXAbstractDialog {
	Map<String, Object> parametros;
	private JXDatePicker año;
	private JXDatePicker mes;
	private JComboBox sucursales;
	
	
	
	
	public InventarioCosteadoForm() {
		super("Inventario Costeado");
		parametros=new HashMap<String, Object>();
	}

	
	private void initComponents(){
	 año=new JXDatePicker();
	 año.setFormats(new String[]{"yyyy"});
	 mes=new JXDatePicker();
	 mes.setFormats(new String[]{"MM"});
	 sucursales=new JComboBox(Sucursales.values());
	 sucursales.insertItemAt("Todas", 0);
	 sucursales.setSelectedIndex(0);
	}
	
	
	protected JComponent buildContent() {
		initComponents();
	    JPanel panel=new JPanel(new BorderLayout());
	    panel.add(buildForma(),BorderLayout.CENTER);
	    panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
	    
	    
		return panel;
	}
	
	protected JComponent buildHeader() {
		// TODO Auto-generated method stub
		return new HeaderPanel("Inventario Costeado","Reporte Inventario Costeado");
	}

	
	private JComponent buildForma(){
		FormLayout layout =new FormLayout(
				"50dlu,4dlu,50dlu,40dlu,50dlu",
				"pref,3dlu,pref,3dlu,pref");		
		CellConstraints cc=new CellConstraints();
		PanelBuilder builder=new PanelBuilder(layout);
		builder.addLabel("Año",cc.xyw(1, 1, 2));
		builder.add(año,cc.xyw(3, 1, 3));
		builder.addLabel("Mes",cc.xyw(1, 3, 2));
		builder.add(mes,cc.xyw(3, 3, 3));
		builder.addLabel("Sucursal",cc.xyw(1, 5, 2));
		builder.add(sucursales,cc.xyw(3, 5, 3));
		return builder.getPanel();
	}
	
	

	public void doApply() {
		SimpleDateFormat fechaaño=new SimpleDateFormat("yyyy");
		SimpleDateFormat fechames=new SimpleDateFormat("MM");
		
		Integer fechainicial=new Integer(fechaaño.format(año.getDate()));
		Integer fechafinal=new Integer(fechames.format(mes.getDate()));

		parametros.put("AÑO",fechainicial);
		parametros.put("MES",fechafinal);
		String selected=sucursales.getSelectedItem().toString();
		if(selected.equalsIgnoreCase("Todas")){
			parametros.put("SUCURSAL","%");
			parametros.put("SUCURSAL_LABEL", "Todas");
		}else{
			Sucursales s=(Sucursales)sucursales.getSelectedItem();
			parametros.put("SUCURSAL",String.valueOf(s.getNumero()));
			parametros.put("SUCURSAL_LABEL", s.name());
		}
		
		execute();	
		
	}
    
    private void execute(){
    	String path=ClassUtils.classPackageAsResourcePath(getClass());
		final String rep="/InventarioCosteado.jasper";
		ReportUtils.viewReport(path+rep, getParametros());
	}


	public Map<String, Object> getParametros() {
		return parametros;
	}
	
    public static void run(){
    	InventarioCosteadoForm inv=new InventarioCosteadoForm();
    	inv.open();
    }
	
    public static void main(String[] args) {
    	SWExtUIManager.setup();
    	run();        	
    }
	
	

}
