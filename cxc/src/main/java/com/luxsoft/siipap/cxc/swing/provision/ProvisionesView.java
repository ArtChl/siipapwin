package com.luxsoft.siipap.cxc.swing.provision;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.CURD;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.crud.BasicCURDGridPanel;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;



public class ProvisionesView extends AbstractView implements CURD{
	
	
	private ProvisionesViewModel model;
	private BasicCURDGridPanel curdPanel;
	
	
	@Override
	protected JComponent buildContent() {		
		JPanel content=new JPanel(new BorderLayout());		
		FormLayout layout=new FormLayout("f:180dlu,p:g","f:p:g");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(createFilterPanel(),cc.xy(1,1 ));
		builder.add(createBrowserPanel(),cc.xy(2, 1));		
		content.add(builder.getPanel(),BorderLayout.CENTER);		
		return content;
	}
	
	private JComponent createFilterPanel(){
		
		JXTaskPaneContainer container=new JXTaskPaneContainer();
		
		JXTaskPane parametros=new JXTaskPane();
		parametros.setTitle("Selección");
		{
			FormLayout layout=new FormLayout(
					"r:p,3dlu,f:p:g"
					,"p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p");
			PanelBuilder builder=new PanelBuilder(layout);			
			CellConstraints cc=new CellConstraints();
			int row=1;			
			row+=2;		
			builder.add(new JLabel("Fecha Inicial "),cc.xy(1, row));			
			builder.add(Binder.createDateComponent(model.getParametrosModel().getModel("fechaInicial")),cc.xy(3, row));
			row+=2;			
			builder.add(new JLabel("Fecha Final "),cc.xy(1, row));			
			builder.add(Binder.createDateComponent(model.getParametrosModel().getModel("fechaFinal")),cc.xy(3, row));		
			
			JComponent c1=builder.getPanel();
			c1.setOpaque(false);
			parametros.add(c1);
		}		
		container.add(parametros);
		
		JXTaskPane porTipo=new JXTaskPane();
		porTipo.setTitle("Filtro: Por Tipo de Venta");
		porTipo.setExpanded(false);
		
		container.add(porTipo);		
		return container;
	}
	
	private JComponent createBrowserPanel(){	
		
		JPanel p=new JPanel(new BorderLayout(5,10));
		final List<Action> actions=CommandUtils.createCommonCURD_Actions(this);
		ToolBarBuilder tb=new ToolBarBuilder();
		for(Action a:actions){
			tb.add(a);
		}
		p.add(tb.getToolBar(),BorderLayout.NORTH);
		
		final String[] props={"id","venta.numero","venta.clave","venta.nombre","venta.fecha","venta.total","provision","vencimiento","descuento1Real","descuento1","descuento2","diasAtraso"};
		final String[] cols={"Id","Factura","Cliente","Nombre","Fecha (V)","Total","Provisión","Vencimiento","DescReal 1","Desc 1","Desc 2","Atraso"};
		TableFormat tf=GlazedLists.tableFormat(props, cols);
		
		curdPanel=new BasicCURDGridPanel(model.getProvisiones(),tf){
			@Override
			protected void select(List selected) {				
				edit();
			}			
		};
		
		TextFilterator textf=GlazedLists.textFilterator(props);
		curdPanel.setTextFilterator(textf);
		p.add(curdPanel.getControl(),BorderLayout.CENTER);
		p.setBorder(Borders.DLU4_BORDER);
		return p;
	}


	@Override
	protected void dispose() {		
		model.dispose();
	}	

	public void delete() {
	
		
	}

	public void edit() {
		System.out.println("Editing :"+curdPanel.getSelected());
	}

	public void insert() {
		
		
	}

	public void view() {
		
		
	}
	
	public void refresh(){
		SwingWorker worker=new SwingWorker(){
			protected Object doInBackground() throws Exception {
				model.load();
				return "OK";
			}			
			protected void done() {
				curdPanel.pack();
			}			
		};
		TaskUtils.executeSwingWorker(worker);
	}
	

	public ProvisionesViewModel getModel() {
		return model;
	}

	public void setModel(ProvisionesViewModel model) {
		this.model = model;
	}
	
	/**
	
	private class NotasGridPanel extends BasicCURDGridPanel{

		public NotasGridPanel(EventList source, TableFormat tableFormat) {
			super(source, tableFormat);
			
		}

		@SuppressWarnings("unchecked")
		@Override
		protected EventList pipeLists(EventList source) {
			final FilterList filterList=new FilterList(source,tiposEditor);
			return filterList;
		}
		
	}
	**/
}
