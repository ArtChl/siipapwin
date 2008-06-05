package com.luxsoft.siipap.cxc.swing.descuentos;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.ObservableElementList.Connector;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.cxc.managers.DescuentosManager;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.BooleanCellRenderer;
import com.luxsoft.siipap.swing.crud.BasicCURDGridPanel;
import com.luxsoft.siipap.swing.form.AbstractForm;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.swing.utils.TaskUtils;

/**
 * Forma para el mantenimiento de descuentos por articulo. El mantenimiento 
 * es en grupo de articulos asignados a un cliente
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentosPorArticulosForm extends AbstractForm{
	
	private BasicCURDGridPanel curdPanel;
	//private EventList finalList;
	
	private JTextField lineaField=new JTextField();
	private JTextField claseField=new JTextField();
	private JTextField marcaField=new JTextField();
	private JTextField claveField=new JTextField();
	
	

	public DescuentosPorArticulosForm(final DescuentosPorArticulosModel model) {
		super(model);
	}
	
	@Override
	protected void initComponents() {		
		lineaField=new JTextField();
		claseField=new JTextField();
		marcaField=new JTextField();
		claveField=new JTextField();
	}

	@Override
	protected void initEventHandling() {		
		super.initEventHandling();
		getDescModel().getComponentModel("cliente").addValueChangeListener(new ClienteHandler());
		
		
	}

	@Override
	protected JComponent buildFormPanel() {
		FormLayout layout=new FormLayout("p","p,3dlu,f:max(p;120dlu)");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(buildMasterPanel(),cc.xy(1, 1));
		builder.add(buildDetailPanel(),cc.xy(1, 3));
		curdPanel.pack();
		return builder.getPanel();
	}
	
	private JComponent buildMasterPanel(){
		
		FormLayout layout=new FormLayout(
				"l:40dlu,2dlu,40dlu,2dlu, " +
				"l:40dlu,2dlu,40dlu,2dlu, " +
				"l:40dlu,2dlu,max(p;200dlu):g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.appendSeparator("Selección");
		builder.append("Cliente",add("cliente"),9);
		builder.nextLine();
		builder.append("Descuento",add("descuento"));		
		builder.append("Precio Kilo",add("precioKilo"));
		//builder.appendLabelComponentsGapColumn();
		builder.append(add("porPrecioKilo"));		
		builder.nextLine();		
		builder.append("Activo",add("activo"),true);
		
		builder.appendSeparator("Filtros");
		builder.nextLine();
		builder.append("Línea",lineaField);
		builder.append("Clase",claseField,true);
		builder.append("Marca",marcaField);
		builder.append("Manual",claveField,true);
		return builder.getPanel();
	}
	
	private JComponent buildDetailPanel(){
		JPanel p=new JPanel(new BorderLayout());
		ToolBarBuilder builder=new ToolBarBuilder();		
		builder.add(CommandUtils.createInsertAction(this, "insert"));
		p.add(buildGridPanel(),BorderLayout.CENTER);
		p.add(builder.getToolBar(),BorderLayout.NORTH);
		return p;
	}
	
	public void insert(){
		loadArticulos();
	}
	
	private JComponent buildGridPanel(){
		final String[] props={"id","articulo.lineaClave","articulo.claseClave","articulo.marcaClave","articulo.clave"
				,"articulo.descripcion1","articulo.gramos","descuento","articulo.precioKiloCred","precioKilo","activo","precioPorMillar","costo","margen"};
		final String[] labels={"Id","Línea","Clase","Marca","Clave","Descripción","Gramos","Descuento","Precio L.(Kg)","P.Kilo","Activo","Precio (Mil)"
				,"Costo (Mil)","Margen"};
		
		EventList<DescuentoPorArticulo> eventList=decorateWithFilters(getDescModel().getFormBean().getDescuentos());
		
		final TableFormat tf=GlazedLists.tableFormat(props, labels);
		final Connector<DescuentoPorArticulo> connector=GlazedLists.beanConnector(DescuentoPorArticulo.class);
		final EventList<DescuentoPorArticulo> source=new ObservableElementList<DescuentoPorArticulo>(eventList,connector);
		//finalList=source;
		curdPanel=new BasicCURDGridPanel(source,tf){
			@SuppressWarnings("unchecked")
			protected void delete(EventList selected) {
				getDescModel().delete(selected);
			}
			public void insert() {				
				loadArticulos();
			}
			
		};
		JComponent c=curdPanel.getControl();
		curdPanel.getGrid().getColumnModel().getColumn(10).setCellRenderer(new BooleanCellRenderer());
		curdPanel.getGrid().getColumnModel().getColumn(7).setCellRenderer(Renderers.getPorcentageRenderer());
		curdPanel.getGrid().getColumnModel().getColumn(9).setCellRenderer(Renderers.getCantidadMonetariaTableCellRenderer());
		curdPanel.getGrid().getColumnModel().getColumn(8).setCellRenderer(Renderers.getCantidadMonetariaTableCellRenderer());
		curdPanel.getGrid().getColumnModel().getColumn(11).setCellRenderer(Renderers.getCantidadMonetariaTableCellRenderer());
		curdPanel.getGrid().getColumnModel().getColumn(12).setCellRenderer(Renderers.getCantidadMonetariaTableCellRenderer());
		curdPanel.getGrid().getColumnModel().getColumn(13).setCellRenderer(Renderers.getUtilidadPorcRenderer());
		return c;
	}	
	
	
	@Override
	protected JComponent getCustomComponent(String property, ComponentValueModel vm) {
		if("cliente".equals(property)){
			return Binder.createClientesBinding(vm);
		}else if("descuento".equals(property)){
			return Binder.createDescuentoBinding(vm);
		}		
		return super.getCustomComponent(property, vm);
	}
	
	@SuppressWarnings("unchecked")
	private EventList<DescuentoPorArticulo> decorateWithFilters(final EventList<DescuentoPorArticulo> list){
		
		final SortedList sortedList=new SortedList(list,null);
		
		//Primer filtro por linea
		final TextFilterator lineaFilterator=GlazedLists.textFilterator(new String[]{"articulo.lineaClave"});
		final TextComponentMatcherEditor lineaEditor=new TextComponentMatcherEditor(lineaField,lineaFilterator);
		final FilterList lineaList=new FilterList(sortedList,lineaEditor);
		
		//Primer filtro por clase
		final TextFilterator claseFilterator=GlazedLists.textFilterator(new String[]{"articulo.claseClave"});
		final TextComponentMatcherEditor claseEditor=new TextComponentMatcherEditor(claseField,claseFilterator);
		final FilterList claseList=new FilterList(lineaList,claseEditor);

		//Primer filtro por marca
		final TextFilterator marcaFilterator=GlazedLists.textFilterator(new String[]{"articulo.marcaClave"});
		final TextComponentMatcherEditor marcaEditor=new TextComponentMatcherEditor(marcaField,marcaFilterator);
		final FilterList marcaList=new FilterList(claseList,marcaEditor);

		
		//Segundo fitro por clave
		final TextFilterator claveFilterator=GlazedLists.textFilterator(new String[]{"articulo.clave","articulo.descripcion1","articulo.gramos","descuento"});
		final TextComponentMatcherEditor claveEditor=new TextComponentMatcherEditor(claveField,claveFilterator);
		final FilterList claveList=new FilterList(marcaList,claveEditor);
		getDescModel().registerFilterList(claveList);
		return claveList;
		
	}
	


	@Override
	protected void annotateComponent(JComponent c, String property) {
		String key="Descuento";
		ValidationComponentUtils.setMessageKey(c, key);
	}

	private DescuentosPorArticulosModel getDescModel(){
		return (DescuentosPorArticulosModel)getFormModel();
	}
	
	private void load(){
		SwingWorker<List<DescuentoPorArticulo>, String> worker=new SwingWorker<List<DescuentoPorArticulo>, String>(){
			@Override
			protected List<DescuentoPorArticulo> doInBackground() throws Exception {				
				return getDescModel().loadDescuentos();
			}
			@Override
			protected void done() {
				try {					
					loadDescuentos(get(),true);
				} catch (Exception e) {
					e.printStackTrace();
					MessageUtils.showError("Error cargando descuentos ",e);
				}
			}
		};		
		TaskUtils.executeSwingWorker(worker);
	}
	
	
	
	public void loadArticulos(){
		SwingWorker<List<Articulo>, String> worker=new SwingWorker<List<Articulo>, String>(){			
			protected List<Articulo> doInBackground() throws Exception {				
				List<Articulo> articulos=getDescModel().buscarArticulos();
				return articulos;
			}
			@Override
			protected void done() {
				try {
					final ArticulosParaDescuentos dialog=new ArticulosParaDescuentos();
					dialog.loadData(get());
					dialog.open();
					if(!dialog.hasBeenCanceled()){
						List<Articulo> selected=dialog.getSelected();						
						List<DescuentoPorArticulo> descuentos=getDescModel().crearDescuentos(selected);
						loadDescuentos(descuentos,false);
						getDescModel().cargarCostos();
						curdPanel.pack();
						//getDescModel().getFormBean().getDescuentos().addAll(descuentos);
					}					
				} catch (Exception e) {
					MessageUtils.showError("Error cargando articulos para descuentos",e);
				}
			}
		};		
		TaskUtils.executeSwingWorker(worker);
	}
	
	@SuppressWarnings("unchecked")
	private void loadDescuentos(List<DescuentoPorArticulo> desc,final boolean clean){		
		this.curdPanel.getSource().getReadWriteLock().writeLock().lock();
		try{
			if(clean)
				curdPanel.getSource().clear();
			curdPanel.getSource().addAll(desc);	
			curdPanel.pack();
		}finally{
			this.curdPanel.getSource().getReadWriteLock().writeLock().unlock();
		}
	}
	
	private class ClienteHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			System.out.println("Detectamos cambio de cliente cargando descuentos para: "+evt.getNewValue()+" Source:"+evt.getSource());
			load();			
		}		
	}
	
	
	public static Cliente showForm(){
		return showForm(null);
	}
	
	public static Cliente showForm(final Object bean){
		final DescuentosManager manager=(DescuentosManager)ServiceLocator.getDaoContext().getBean("descuentosManager");
		final DescuentosPorArticulosModel model=new DescuentosPorArticulosModel();
		if(bean!=null)
			model.getFormBean().setCliente((Cliente)bean);
		model.setManager(manager);
		final DescuentosPorArticulosForm form=new DescuentosPorArticulosForm(model);
		final FormDialog dialog=new FormDialog(form);
		dialog.setTitle("Descuentos por Artículo");
		dialog.setDescription("Consulta y alta en grupo para uno o varios descuentos por articulo");
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			//model.commit();
			return model.getFormBean().getCliente();
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		showForm();
	}

}
