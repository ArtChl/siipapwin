package com.luxsoft.siipap.inventarios.consultas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.collections.map.HashedMap;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.VerticalLayout;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.InventarioMaq;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.utils.DateUtils;


/**
 * Consulta que permite visualizar la historia del proceso de maquila
 * a partir de una entrada de maquilador para un producto en particular
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeEntrada extends SXAbstractDialog{
	
	
	private final AnalisisDeEntradaModel model;

	public AnalisisDeEntrada(final AnalisisDeEntradaModel model) {
		super("Inventario de Maquila");
		this.model=model;
		model.getInventarioModel()
		.addBeanPropertyChangeListener("fecha", new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				updateComponents();
			}
		});
	}

	@Override
	protected JComponent buildContent() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithClose(),BorderLayout.SOUTH);
		return panel;
	}
	
	private JComponent buildMainPanel(){
		final JPanel panel=new JPanel(new VerticalLayout(2));
		panel.add(buildMainForm());
		final FormLayout layout=new FormLayout(
				"p:g(.5),2dlu,p:g(.5)"
				,"f:100dlu,2dlu,f:100dlu");
		final PanelBuilder builder=new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		final CellConstraints cc=new CellConstraints();
		builder.add(buildSalidaACorteGrid(),cc.xy(1, 1));
		builder.add(buildEntradaDeHojasGrid(),cc.xy(3, 1));
		builder.add(buildSalidaDeMaterial(),cc.xy(1, 3));
		builder.add(buildSalidaDeHojasGrid(),cc.xy(3, 3));
		panel.add(builder.getPanel());
		
		return panel;
	}
	
	
	private JComponent buildMainForm(){
		final FormLayout layout=new FormLayout(
				"l:p,3dlu,f:max(p;70dlu) ,3dlu " +
				"l:p,3dlu,f:max(p;70dlu) ,3dlu " +
				"l:p,3dlu,f:max(p;70dlu) ,3dlu " +
				"r:p,3dlu,r:max(p;70dlu):g " +
				"","");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		builder.appendSeparator("Entrada de Material");
		final JComponent fecha=Binder.createDateComponent(model.getInventarioModel().getComponentModel("fecha"));
		builder.append("Fecha de Corte",fecha,true);
		builder.append("Entrada de Maq",getControl("entrada.entradaDeMaquilador"));
		builder.append("Fecha ",getControl("entrada.fecha"));
		builder.append("Id ",getControl("entrada.id"),true);		
		builder.append("Kilos",getControl("entrada.kilos"));
		builder.append("Precio Kg",getControl("precioPorKilo"),true);		
		builder.append("Metros2",getControl("entrada.metros2"));
		builder.append("Percio M2",getControl("precioPorM2"),true);
		builder.append("Importe",getControl("entrada.importe"));		
		builder.append("Factura",getControl("entrada.factura"));
		builder.append("Fabricante",getControl("entrada.fabricante"),true);
		
		builder.appendSeparator("Inventarios ");
		builder.nextLine();		
		final CellConstraints cc=new CellConstraints();
		builder.appendRow(new RowSpec("c:17dlu"));
		builder.add(new JLabel("Bobinas"),cc.xy(3, builder.getRow(), "c,f"));
		builder.add(new JLabel("Procesos"),cc.xy(7, builder.getRow(), "c,f"));
		builder.add(new JLabel("Hojas"),cc.xy(11, builder.getRow(), "c,f"));
		builder.add(new JLabel("Total"),cc.xy(15, builder.getRow(), "c,f"));		
		builder.nextLine();
		builder.append("Unidades",getControl("exitenciaKg"));
		builder.append("",getControl("procesoVal"));
		builder.append("",getControl("existenciaMil"),true);				
		builder.append("Costo",getControl("costoInventarioKg"));
		builder.append("",getControl("costoInventarioProcesoKg"));
		builder.append("",getControl("costoInventarioMil"));		
		builder.append("",getControl("costoTotal"),true);
		
		//builder.nextLine();
		if(model.getEntrada()!=null && model.getEntrada().getTrasladoOrigen()!=null){
			builder.appendSeparator("Traslado Origen");		
			builder.append("Entrada Maq",getControl("entrada.trasladoOrigen.entrada.entradaDeMaquilador"));
			builder.append("Id",getControl("entrada.trasladoOrigen.entrada.id"));
			builder.append("Fecha",getControl("entrada.trasladoOrigen.entrada.fecha"),true);		
		}
		 
		return builder.getPanel();
	}

	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Analisis de Maquila"
				,"Analisis del proceso de maquila a partir de una entrada de maquilador y una bobina");
	}
	
	private Map<String, JComponent> components=new HashMap<String, JComponent>();
	
	private JComponent getControl(String property){		
		if(components.get(property)==null){
			final String KEY="BEAN_PROPERTY";
			final JLabel l=new JLabel(model.getLabel(property));
			l.setHorizontalAlignment(JLabel.RIGHT);
			l.putClientProperty(KEY, property);
			l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
			components.put(property, l);
		}		
		return components.get(property);		
	}
	
	private JXTable salidaACorteGrid;
	private JXTable salidaDeBobinaGrid;
	private JXTable entradaDeHojasGrid;
	private JXTable salidaDeHojasGrid;
	
	private JComponent buildSalidaACorteGrid(){
		salidaACorteGrid=ComponentUtils.getStandardTable();
		final EventList source=model.getCortes();
		//final FilterList fsource=source;
		final String[] cols={"id","fecha","kilos","metros2","costo","orden.id","orden.fecha","destino.clave"};
		final String[] labels={"Id","Fecha","Kilos","M2","Costo","Orden ID","Fecha O","Destino"};
		final TableFormat tf=GlazedLists.tableFormat(SalidaACorte.class, cols,labels);
		final EventTableModel<SalidaACorte> tm =new EventTableModel<SalidaACorte>(source,tf);
		
		final JXTable grid=salidaACorteGrid;
		grid.setColumnControlVisible(false);
		grid.setModel(tm);
		grid.packAll();
		ComponentUtils.decorateActions(grid);
		return UIFactory.createTablePanel(grid);
		
	}
	
	private JComponent buildSalidaDeMaterial(){
		salidaDeBobinaGrid=ComponentUtils.getStandardTable();
		//final FilterList fsource=source;
		final EventList source=new BasicEventList();
		final String[] cols={"id","fecha","kilos","metros2","costo"};//,"destino.id","destino.FENT"};
		final String[] labels={"Id","Fecha","Kilos","M2","Costo"};//,"ComId","Fecha Com"};
		final TableFormat tf=GlazedLists.tableFormat(SalidaDeBobinas.class, cols,labels);
		final EventTableModel<SalidaDeBobinas> tm =new EventTableModel<SalidaDeBobinas>(source,tf);
		
		final JXTable grid=salidaDeBobinaGrid;
		grid.setColumnControlVisible(false);
		grid.setModel(tm);
		grid.packAll();
		ComponentUtils.decorateActions(grid);
		return UIFactory.createTablePanel(grid);
	}
	
	
	
	private JComponent buildEntradaDeHojasGrid(){
		
		final EventList source=model.getEntradaDeHojas();
		final String[] cols={"id","origen.id","fecha","clave","descripcion","cantidad","metros2","merma","costo"};
		final String[] labels={"Id","OrigenId","Fecha","Articulo","Descripcion","Cantidad","M2","Merma","Precio (MIL)"};
		final TableFormat tf=GlazedLists.tableFormat(EntradaDeHojas.class, cols,labels);
		final EventTableModel<EntradaDeHojas> tm =new EventTableModel<EntradaDeHojas>(source,tf);
		
		entradaDeHojasGrid=ComponentUtils.getStandardTable();
		final JXTable grid=entradaDeHojasGrid;
		grid.setModel(tm);
		grid.packAll();
		grid.setColumnControlVisible(false);
		ComponentUtils.decorateActions(grid);
		return UIFactory.createTablePanel(grid);
	}
	
	private JComponent buildSalidaDeHojasGrid(){
		final EventList source=model.getSalidasDeHojas();
		final String[] cols={"id","fecha","clave","descripcion","cantidad","metros2","origen.costo","costo","destino.id","destino.FENT","precioPorKiloFlete"};
		final String[] labels={"Id","Fecha","Articulo","Descripcion","Cantidad","M2","Precio (MIL)","Costo","Com","Fecha Com","Gastos"};
		final TableFormat tf=GlazedLists.tableFormat(SalidaDeHojas.class, cols,labels);
		final EventTableModel<SalidaDeHojas> tm =new EventTableModel<SalidaDeHojas>(source,tf);
		
		salidaDeHojasGrid=ComponentUtils.getStandardTable();
		final JXTable grid=salidaDeHojasGrid;
		grid.setColumnControlVisible(false);
		grid.packAll();
		grid.setModel(tm);
		grid.packAll();
		ComponentUtils.decorateActions(grid);
		return UIFactory.createTablePanel(grid);
	}
	
	
	private void updateComponents(){
		System.out.println("Actualizando componentes");
		for(Entry<String, JComponent> entry:components.entrySet()){
			JLabel l=(JLabel)entry.getValue();
			l.setText(model.getLabel(entry.getKey()));
		}
	}
	
	private String getDescriptionMsg(){
		final String pattern="Entrada :{0} Bobina: {1} {2}";
		return MessageFormat.format(pattern
				,model.getEntrada().getEntradaDeMaquilador()
				,model.getEntrada().getArticulo()
				,model.getEntrada().getArticulo().getDescripcion1());
	}
	
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		
		final InventarioMaq maq=ServiceLocator.getMaquilaManager()
		.cargarInventario(9691L, DateUtils.obtenerFecha("23/08/2007"));
		AnalisisDeEntradaModel model=new AnalisisDeEntradaModel(maq);
		//AnalisisDeEntradaModel model=new AnalisisDeEntradaModel();
		AnalisisDeEntrada form=new AnalisisDeEntrada(model);
		form.open();
		
	}

}
