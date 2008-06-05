package com.luxsoft.siipap.compras;

import java.awt.BorderLayout;
import java.awt.Dimension;

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


import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.utils.CommandUtils;

/**
 * Vista estandar para las operaciones de compras
 * 
 * @author Ruben Cancino
 * 
 * TODO Hacer una Interfaz de las altas bajas y cambios
 *
 */
public class ComprasDefaultView extends AbstractView{
	
	protected TabbedPanel tabPanel;	
	protected JXTaskPane operaciones;
	protected JXTaskPane filtros;
	protected JXTaskPane detalles;
	
	
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
		
		operaciones=new JXTaskPane();
		operaciones.setLayout(new VerticalLayout(5));
		operaciones.setTitle("Operaciones");
		operaciones.add(CommandUtils.createInsertAction(this, "insertar"));
		operaciones.add(CommandUtils.createDeleteAction(this, "eliminar"));
		operaciones.add(CommandUtils.createEditAction(this, "editar"));
		operaciones.add(CommandUtils.createViewAction(this, "consultar"));
		operaciones.add(CommandUtils.createLoadAction(this, "load"));
		operaciones.add(CommandUtils.createPrintAction(this, "print"));
		operaciones.setIcon(getIconFromResource("images2/businesssetup.png"));
		
		
		final JXTaskPane mantenimient=new JXTaskPane();
		mantenimient.setTitle("Procesos");
		mantenimient.setIcon(getIconFromResource("images2/overlays.png"));
		
		filtros=new JXTaskPane();		
		filtros.setTitle("Filtros");
		filtros.setIcon(getIconFromResource("images2/page_find.png"));
		
		detalles=new JXTaskPane();
		detalles.setTitle("Detalles");
		detalles.setLayout(new VerticalLayout(5));
		detalles.setIcon(getIconFromResource("images2/information.png"));
		
		container.add(operaciones);
		container.add(mantenimient);
		container.add(filtros);
		container.add(detalles);
		
		
		
		return container;
	}
	
	public JComponent createDocumentPanel(){
		tabPanel=new TabbedPanel();
		tabPanel.addTabListener(new TabHandler());
		return tabPanel;
	}
	
	protected void insertar(){
		
	}
	protected void eliminar(){
		
	}
	
	protected void editar(){
		
	}
	
	protected void consultar(){
		
	}
	
	protected void load(){
		
	}
	
	protected void print(){
		
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
				AnalisisView s=(AnalisisView)tab.getClientProperty(AnalisisView.SUPPORT_KEY);
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
