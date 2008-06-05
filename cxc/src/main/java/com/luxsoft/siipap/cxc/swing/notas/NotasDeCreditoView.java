package com.luxsoft.siipap.cxc.swing.notas;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
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
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.crud.BasicCURDGridPanel;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;



public class NotasDeCreditoView extends AbstractView implements CURD{
	
	
	private NotasDeCreditoModel model;
	private BasicCURDGridPanel curdPanel;
	private TiposDeNotasMatcherEditor tiposEditor;
	
	
	
	/** Vistas **/
		
		
	
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
		
		final JXTaskPane altas=new JXTaskPane();
		altas.setTitle("Altas");
		Action a1=new DispatchingAction(this,"alta");
		a1.putValue(Action.NAME, "Bonificación");
		Action a2=new DispatchingAction(this,"alta");
		a2.putValue(Action.NAME, "Devolución");
		Action a3=new DispatchingAction(this,"alta");
		a3.putValue(Action.NAME, "Descuento anual");
		Action a4=new DispatchingAction(this,"alta");
		a4.putValue(Action.NAME, "Cargo");
		
		altas.add(a1);
		altas.add(a2);
		altas.add(a3);
		altas.add(a4);
		
		container.add(altas);
		
		final JXTaskPane contenido=new JXTaskPane();
		contenido.setTitle("Contenido");
		
		JXTaskPane porTipo=new JXTaskPane();
		porTipo.setTitle("Filtro: Por Tipo de Nota");
		porTipo.setExpanded(false);
		tiposEditor=new TiposDeNotasMatcherEditor();
		porTipo.add(tiposEditor.getSelector());
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
		Action printDesc=new DispatchingAction(this,"imprimirPorDes");
		getActionConfigurer().configure(printDesc, "imprimirNotasPorDescuentoAction");
		tb.add(printDesc);
		
		p.add(tb.getToolBar(),BorderLayout.NORTH);
		
		final String[] props={"id","clave","cliente.nombre","fecha","origen","serie","tipo","numero","numeroFiscal"};
		final String[] cols={"Id","Cliente","Nombre","Fecha","Origen","Serie","Tipo","Numero","Numero F."};
		TableFormat tf=GlazedLists.tableFormat(props, cols);
		
		curdPanel=new NotasGridPanel(model.getNotas(),tf){
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
		NotasFormFactory.generarNota();
		
	}

	public void view() {
		
		
	}
	
	public void imprimirPorDes(){
		NotasTaskManager.imprimirNcPorDescuento();
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
	

	public NotasDeCreditoModel getModel() {
		return model;
	}

	public void setModel(NotasDeCreditoModel model) {
		this.model = model;
	}
	
	/** Collaboradores **/
	
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
	
}
