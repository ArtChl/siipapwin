package com.luxsoft.siipap.inventarios.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Dimension;


import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.infonode.tabbedpanel.TabAdapter;
import net.infonode.tabbedpanel.TabDragEvent;
import net.infonode.tabbedpanel.TabEvent;
import net.infonode.tabbedpanel.TabListener;
import net.infonode.tabbedpanel.TabRemovedEvent;
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
import com.luxsoft.siipap.swing.actions.DispatchingAction;

/**
 * Consulta de Inventarios sobre movimientos de tipo com
 * procesados en maquila
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeMovimientos extends AbstractView{
	
	private TabbedPanel tabPanel;
	private AnalisisDeXcoDecs analisisXcoDecsView;
	private TitledTab salidasXcoDecTab;
	
	private Action showXcoDecsAction;
	
	private JXTaskPane operaciones;
	private JXTaskPane filtros;
	
	private void initActions(){
		showXcoDecsAction=new DispatchingAction(this,"showXcoDecs");
		getActionConfigurer().configure(showXcoDecsAction, InvActions.ShowXcoDecsView.getId());
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
		
		operaciones=new JXTaskPane();
		operaciones.setLayout(new VerticalLayout(5));
		operaciones.setTitle("Análisis");
		operaciones.setIcon(getIconFromResource("images2/businesssetup.png"));
		operaciones.add(showXcoDecsAction);
		
		final JXTaskPane mantenimient=new JXTaskPane();
		mantenimient.setTitle("Procesos");
		mantenimient.setIcon(getIconFromResource("images2/overlays.png"));
		
		filtros=new JXTaskPane();		
		filtros.setTitle("Filtros");
		filtros.setIcon(getIconFromResource("images2/page_find.png"));
		
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
	
	public void showXcoDecs(){
		this.tabPanel.addTab(getXcoDecTabl());
		getXcoDecTabl().setSelected(true);
	}
	
	
	private TitledTab getXcoDecTabl(){
		if(salidasXcoDecTab==null){
			this.analisisXcoDecsView=new AnalisisDeXcoDecs();
			salidasXcoDecTab=new TitledTab("XCO/DEC",null,analisisXcoDecsView.getContent(),null);
			salidasXcoDecTab.putClientProperty(AnalisisSupport.SUPPORT_KEY, analisisXcoDecsView);
			//operaciones.add(analisisXcoDecsView.getReloadAction());
			//operaciones.add(analisisXcoDecsView.getSeleccionarPeriodo());
			//operaciones.revalidate();
			
			//filtros.add(analisisXcoDecsView.createFiltrosPanel());
		}
		return salidasXcoDecTab ;
	}
	
	
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
