package com.luxsoft.siipap.inventarios.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.infonode.tabbedpanel.TabAdapter;
import net.infonode.tabbedpanel.TabStateChangedEvent;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.inventarios.InvActions;
import com.luxsoft.siipap.swing.AbstractView;

/**
 * Vista controladora de consultas relacionadas con analisis de costos
 * 
 * Implementa el patron Mediator  
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeCostos extends AbstractView{
	
	private TabbedPanel tabPanel;
	private JXTaskPane consultas;
	private JXTaskPane operaciones;
	private JXTaskPane filtros;
	
	private AnalisisDeCostosGlobal globalView;
	private ACPorArticulo porArticuloView;
	private ACInventario inventarioCosteado;
	
	private void initActions(){
		
	}
	
	@Override
	protected JComponent buildContent() {
		initActions();
		JPanel content=new JPanel(new BorderLayout());		
		FormLayout layout=new FormLayout("f:180dlu,p:g","f:p:g");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(createTaskPanel(),cc.xy(1,1 ));
		builder.add(createDocumentPanel(),cc.xy(2, 1));		
		content.add(builder.getPanel(),BorderLayout.CENTER);
		content.setPreferredSize(new Dimension(600,450));
		return content;
	}
	
	private JComponent createTaskPanel(){
		
		final JXTaskPaneContainer container=new JXTaskPaneContainer();
		
		consultas=new JXTaskPane();
		consultas.setTitle("Analisis");
		consultas.setLayout(new VerticalLayout(5));
		consultas.setIcon(getIconFromResource("images2/lightbulb_add.png"));
		consultas.add(showPorArticuloViewAction());
		consultas.add(showGlobalViewAction());
		consultas.add(showInventarioViewAction());
		
		operaciones=new JXTaskPane();
		operaciones.setLayout(new VerticalLayout(5));
		operaciones.setTitle("Operaciones");
		operaciones.setIcon(getIconFromResource("images2/businesssetup.png"));
		
		
		final JXTaskPane mantenimient=new JXTaskPane();
		mantenimient.setTitle("Procesos");
		mantenimient.setIcon(getIconFromResource("images2/overlays.png"));
		
		filtros=new JXTaskPane();		
		filtros.setTitle("Filtros");
		filtros.setIcon(getIconFromResource("images2/page_find.png"));
		
		container.add(consultas);
		container.add(operaciones);
		container.add(filtros);
		container.add(mantenimient);
		
		
		return container;
	}
	
	public JComponent createDocumentPanel(){
		tabPanel=new TabbedPanel();
		tabPanel.addTabListener(new TabHandler());
		return tabPanel;
	}
	
	private Action showPorArticuloViewAction(){
		Action a=new AbstractAction("Por Articulo"){
			public void actionPerformed(ActionEvent e) {
				if(getPorArticuloView()!=null){
					tabPanel.addTab(getPorArticuloView().getTab());
					getPorArticuloView().getTab().setSelected(true);
				}				
			}			
		};
		getActionConfigurer().configure(a, InvActions.ShowAnalisisDeCostosPorArticuloView.getId());
		return a;
		
	}
	
	private Action showGlobalViewAction(){
		Action a=new AbstractAction("Global"){
			public void actionPerformed(ActionEvent e) {
				if(getGlobalView()!=null){
					tabPanel.addTab(getGlobalView().getTab());
					getGlobalView().getTab().setSelected(true);
				}
			}
		};
		getActionConfigurer().configure(a, InvActions.ShowAnalisisDeCostosGlobal.getId());
		return a;		
	}
	
	
	private Action showInventarioViewAction(){
		Action a=new AbstractAction("Inventario"){
			public void actionPerformed(ActionEvent e) {
				if(getInventarioCosteado()!=null){
					tabPanel.addTab(getInventarioCosteado().getTab());
					getInventarioCosteado().getTab().setSelected(true);
				}
			}
		};
		getActionConfigurer().configure(a, InvActions.ShowInventarioCosteado.getId());
		return a;		
	}
	
	public void close(){				
		if(getPorArticuloView()!=null){
			tabPanel.removeTab(getPorArticuloView().getTab());
			getPorArticuloView().close();
		}
		if(getGlobalView()!=null){
			tabPanel.removeTab(getGlobalView().getTab());
			getGlobalView().close();
		}
		if(getInventarioCosteado()!=null){
			tabPanel.removeTab(getInventarioCosteado().getTab());
			getInventarioCosteado().close();
			setInventarioCosteado(null);
		}
	}
	
	public ACPorArticulo getPorArticuloView() {
		return porArticuloView;
	}

	public void setPorArticuloView(ACPorArticulo porArticuloView) {
		this.porArticuloView = porArticuloView;
	}
	public AnalisisDeCostosGlobal getGlobalView() {
		return globalView;
	}

	public ACInventario getInventarioCosteado() {
		if(inventarioCosteado==null){
			inventarioCosteado=new ACInventario();
		}
		return inventarioCosteado;
	}

	public void setInventarioCosteado(ACInventario inventarioCosteado) {
		this.inventarioCosteado = inventarioCosteado;
	}

	public void setGlobalView(AnalisisDeCostosGlobal globalView) {
		this.globalView = globalView;
	}
	
	
	/**
	 * TabAdapter para controlar la apariencia de el taskpanel
	 * en funcion de la ventana seleccionada 
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class TabHandler  extends TabAdapter{
		
		@Override
		public void tabDeselected(TabStateChangedEvent event) {
			TitledTab tab=(TitledTab)event.getCurrentTab();
			if(tab!=null){				
				AnalisisSupport s=(AnalisisSupport)tab.getClientProperty(AnalisisSupport.SUPPORT_KEY);
				if(s!=null){
					operaciones.removeAll();
					filtros.removeAll();
					operaciones.revalidate();
					filtros.revalidate();
					for(Action a:s.getOperaciones()){
						operaciones.add(a);
						operaciones.revalidate();
					}
					filtros.add(s.getFilterPanel());
					filtros.revalidate();
				}				
			}			
		}
		
		
	}



	



	

}
