package com.luxsoft.siipap.swing.views2;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabAdapter;
import net.infonode.tabbedpanel.TabRemovedEvent;
import net.infonode.tabbedpanel.TabStateChangedEvent;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;

import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * Vista que agrega una seccion de tareas y opciones representados
 * por un JXTaskPaneContainer y algunos JXTaskPane 
 * 
 * @author Ruben Cancino
 * 
 * TODO Hacer una Interfaz de las altas bajas y cambios
 *
 */
public class DefaultTaskView extends AbstractView{
	
	protected TabbedPanel tabPanel;	
	protected JXTaskPane consultas;
	protected JXTaskPane operaciones;
	protected JXTaskPane procesos;
	protected JXTaskPane filtros;
	protected JXTaskPane detalles;
	protected JXTaskPaneContainer taskContainer;
	
	
	
	@Override
	protected JComponent buildContent() {
		initActions();
		/**
		FormLayout layout=new FormLayout("f:180dlu,p:g","f:p:g");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();		
		builder.add(UIFactory.createStrippedScrollPane(createTaskPanel()),cc.xy(1,1 ));
		builder.add(createDocumentPanel(),cc.xy(2, 1));	
		return builder.getPanel();
		***/
		
		JPanel content=new JPanel(new BorderLayout());
		content.add(createTaskPanel(),BorderLayout.WEST);
		content.add(createDocumentPanel(),BorderLayout.CENTER);
		return content;
		
		
	}
	
	/**
	 * Crea el JXTaskPaneContainer que es accesible para las sub-clases
	 * mediante la variable de instancia taskContainer
	 * @return
	 */
	protected JComponent createTaskPanel(){
		
		taskContainer=new JXTaskPaneContainer();
		consultas=createTaskPanel("Consultas", "");
		consultas.setSpecial(true);
		operaciones=createTaskPanel("Operaciones", "");
		procesos=createTaskPanel("Procesos", "images2/overlays.png");
		filtros=createTaskPanel("Filtros", "images2/page_find.png");
		//filtros.setExpanded(false);
		detalles=createTaskPanel("Detalles", "images2/information.png");
		detalles.setExpanded(false);
		
		instalarTaskElements();
		
		taskContainer.add(consultas);
		taskContainer.add(operaciones);
		taskContainer.add(procesos);
		taskContainer.add(filtros);
		taskContainer.add(detalles);
		instalarTaskPanels(taskContainer);
		
		
		//JPanel content=new JPanel(new BorderLayout());
		//content.add(new JScrollPane(taskContainer),BorderLayout.CENTER);
		return new JScrollPane(taskContainer);
		
		//return taskContainer;
	}
	
	/**
	 * Template method para instalar elementos JXTaskPanel al JXTaxkPaneContainer
	 * , el container pasado es la instancia protected taskContainer.
	 * Este metodo permite la extensibilidad de esta clase
	 *  
	 * @param container
	 */
	protected void instalarTaskPanels(final JXTaskPaneContainer container){
		
	}
	
	/**
	 * Template method para instalar acciones y/o componentes a los task panels
	 * creados. Se tiene acceso por medio de variables protected a los taskpnels
	 * de opreciones,procesos,filtros y detalles
	 * 
	 */
	protected void instalarTaskElements(){
		
	}
	
	/**
	 * Crea un JXTaskPanel
	 * @param title
	 * @param iconPath
	 * @return
	 */
	protected JXTaskPane createTaskPanel(final String title, final String iconPath){
		final JXTaskPane pane=ComponentUtils.createStandarJXTaskPane();
		pane.setTitle(title);
		pane.setLayout(new VerticalLayout(5));
		pane.setIcon(getIconFromResource(iconPath));
		
		return pane;
	}
	
	/**
	 * Crea el panel de documentos o vistas internas, Esta implementacion crea un TabbedPanel
	 * que es accesible para las sub-clases mediante la  variable tabPanel
	 * 
	 * @return
	 */
	protected JComponent createDocumentPanel(){
		tabPanel=new TabbedPanel();
		tabPanel.addTabListener(new TabHandler());
		return tabPanel;
	}
	
	/**
	 * Inserta instancias de InternalTaskView envueltas en una
	 * instancia de InternalTaskTab. Las subclases pueden ocupar este metodo
	 * para agregar vistas internas al document panel 
	 *  
	 * @param view
	 */
	protected InternalTaskTab createInternalTaskTab(final InternalTaskView view){
		final InternalTaskTab tab=new InternalTaskTab(view);		
		return tab;
	}
	
	protected void addTab(final InternalTaskTab tab){
		tabPanel.addTab(tab);
		tab.setSelected(true);
	}
	
	/**
	 * Template method para inicializar acciones antes de construir el panel
	 * principal de esta vista, las sub clases lo pueden implementar sabiendo
	 * que este metodo se ejecutara solo una vez dentro del cuerpo del metodo
	 * buildContent.
	 * 
	 */
	protected void initActions(){
		
	}	
	
	/************* Metodos para el comportamiento dinamico *******************/
	
	/**
	 *  Metodo ejecutado cuando una vista deja de ser la vista seleccionada
	 *  se utiliza para inicialmente para limiar los taskpanels
	 *  Lo adecuado en esta implementacion es que las sub clases que sobre 
	 *  escriban este metodo lo  manden llamar para garantizar la limpieza
	 *  de los taskpanels operaciones,procesos,filtros y detalle. 
	 * @param tab
	 */
	protected void vistaInternaDeSeleccionada(final TitledTab tab){		
		operaciones.removeAll();
		procesos.removeAll();
		filtros.removeAll();
		detalles.removeAll();
	}
	
	/**
	 * Metodo detonado cuando una vista interna implementada en un TitledTab
	 * es seleccionada. Se utiliza en primera instancia para agregar componentes
	 * a los taskpanels, esto si la vista seleccionada implementa InternalTaskView 
	 * Lo adecuado en esta implementacion es que las sub clases que sobre 
	 *  escriban este metodo lo  manden llamar para garantizar la correcta 
	 *  adicion de acciones y paneles a los taskpanels operaciones,procesos,filtros y detalle.
	 *  
	 * @param tab
	 */
	protected void vistaInternaSeleccionada(final TitledTab tab){
		
		if(tab instanceof InternalTaskTab){
			InternalTaskView s=((InternalTaskTab)tab).getTaskView();
			s.instalOperacionesAction(operaciones);
			s.instalProcesosActions(procesos);
			s.installFiltrosPanel(filtros);
			s.installDetallesPanel(detalles);
		}	
	}
	
	/**
	 * Metodo detonado cuando una vista interna es cerrada, Se utiliz para
	 * detonar el metodo close de la vista si esta implementa InternalTaskView
	 * 
	 * @param tab
	 */
	protected void vistaInternaCerrada(final TitledTab tab){		
		if(tab instanceof InternalTaskView){
			InternalTaskView s=((InternalTaskTab)tab).getTaskView();
			s.close();
		}	
	}
	
	/**
	 * Cierra las vistas internas 
	 */
	public void close(){		
		while(tabPanel.getSelectedTab()!=null){
			Tab tab=tabPanel.getSelectedTab();
			tabPanel.removeTab(tab);
		}
		if(logger.isDebugEnabled()){
			logger.debug("Cerrando las vistas internas para :"+getId());
		}
	}
	
	
	/**
	 * TabAdapter para controlar la apariencia de el taskpanel
	 * en funcion de la ventana seleccionada 
	 * 
	 * @author Ruben Cancino
	 *
	 */
	protected class TabHandler  extends TabAdapter{
		
		@Override
		public void tabSelected(TabStateChangedEvent event) {
			TitledTab tab=(TitledTab)event.getCurrentTab();
			if(tab!=null){
				vistaInternaSeleccionada(tab);			
			}
		}

		@Override
		public void tabDeselected(TabStateChangedEvent event) {
			TitledTab tab=(TitledTab)event.getCurrentTab();
			if(tab!=null){
				vistaInternaDeSeleccionada(tab);			
			}			
		}

		@Override
		public void tabRemoved(TabRemovedEvent event) {
			TitledTab tab=(TitledTab)event.getTab();
			if(tab!=null){
				vistaInternaCerrada(tab);			
			}			
		}
		
		
	}

}
