package com.luxsoft.siipap.inventarios.reportes;

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
import org.springframework.util.ClassUtils;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.MovAlmacen;
import com.luxsoft.siipap.domain.Sucursales;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class KardexReport extends SXAbstractDialog {
	Map<String, Object> parametros;
//	private JComponent articulo;
	private ValueModel articuloModel=new ValueHolder();
	
	private JXDatePicker fecha_ini;
	private JXDatePicker fecha_fin;
	private JComboBox sucursales;
	private JComboBox movAlmacen;
	private JTextField articulo;
	
	
	
	
	
	public KardexReport() {
		super("Kardex");
		parametros=new HashMap<String, Object>();
	}

	
	private void initComponents(){
//	articuloModel=new ValueHolder();
//	articulo=Binder.createArticulosBinding(articuloModel);
	 articulo=new JTextField(20);	
	 fecha_ini=new JXDatePicker();
	 fecha_ini.setFormats(new String[]{"dd/MM/yyyy"});
	 fecha_fin=new JXDatePicker();
	 fecha_fin.setFormats(new String[]{"dd/MM/yyyy"});
	 sucursales=new JComboBox(SucursalesInv.values());
	 sucursales.insertItemAt("Todas", 0);
	 sucursales.setSelectedIndex(0);
	 movAlmacen=new JComboBox(MovAlmacen.values());
	 movAlmacen.insertItemAt("Todos", 0);
	 movAlmacen.setSelectedIndex(0);
	 
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
		return new HeaderPanel("Kardex","Reporte de Kardex");
	}

	
	private JComponent buildForma(){
		FormLayout layout =new FormLayout(
				"50dlu,4dlu,50dlu,40dlu,50dlu",
				"pref,3dlu,pref,3dlu,pref,3dlu,pref");		
		CellConstraints cc=new CellConstraints();
		PanelBuilder builder=new PanelBuilder(layout);
		builder.addLabel("Fecha Inicial",cc.xyw(1, 1, 2));
		builder.add(fecha_ini,cc.xyw(3, 1, 3));
		builder.addLabel("Fecha Final",cc.xyw(1, 3, 2));
		builder.add(fecha_fin,cc.xyw(3, 3, 3));
//		builder.addLabel("Sucursal",cc.xyw(1, 5, 2));
//		builder.add(sucursales,cc.xyw(3, 5, 3));
		builder.addLabel("Articulo",cc.xyw(1, 5, 2));
		builder.add(articulo,cc.xyw(3,5,3));
		
		return builder.getPanel();
	}
	
	

	public void doApply() {
		parametros.put("FECHA_INI",fecha_ini.getDate());
		parametros.put("FECHA_FIN",fecha_fin.getDate());
		parametros.put("SUCURSAL","%");
		parametros.put("ARTICULOS",articulo.getText());
	
		execute();	
		
	}
    
    private void execute(){
    	String path=ClassUtils.classPackageAsResourcePath(getClass());
		final String rep="/KRDX.jasper";
		ReportUtils.viewReport(path+rep, getParametros());
	}


	public Map<String, Object> getParametros() {
		return parametros;
	}
	
    public static void run(){
    	KardexReport inv=new KardexReport();
    	inv.open();
    }
	
    public static void main(String[] args) {
    	SWExtUIManager.setup();
    	run();        	
    }
    
    private enum SucursalesInv {
    	
    	OFICINAS("Oficinas ","1"),
    	ANDRADE("Andrade","3"),
    	BOLIVAR("Bolivar","5"),
    	QUERETARO("Queretaro","9"),
    	CALLE4("Calle 4","10"),
    	ERMITA("Ermita","11"),
    	TACUBA("Tacuba","12"),	
    	;
    	
    	private final String descripcion;
    	private final String numero;
    	
    	private SucursalesInv(final String descripcion, final String numero) {
    		this.descripcion = descripcion;
    		this.numero = numero;
    	}
    	
    	public String toString(){
    		return descripcion;
    	}
    	
    	public String getNumero(){		
    		return numero;
    	}
    	
    	public String[] todos(){
    		return new String[]{"1","4","6","7","8"};
    	}
    	
    	public static List<SucursalesInv> getSucursales(){
    		ArrayList<SucursalesInv> l=new ArrayList<SucursalesInv>();
    		for(SucursalesInv c:values()){			
    			l.add(c);
    		}
    		return l;
    	}
    	
    	public static SucursalesInv getSucursal(String id){
    		for(SucursalesInv c:values()){
    			if(c.getNumero()==id)
    				return c;
    		}
    		return null;
    	}
    	
    

    	

    }

  

}
