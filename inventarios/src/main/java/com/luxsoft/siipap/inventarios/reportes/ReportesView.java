package com.luxsoft.siipap.inventarios.reportes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.ViewMap;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.DockingUtils;

public class ReportesView extends AbstractView{
	
	protected RootWindow rootWindow;	
	protected JXTaskPane operaciones;
	
	
	
	
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
		operaciones.setTitle("Reportes");
		
		operaciones.add(createR1());
		operaciones.add(createR2());
		operaciones.add(createR3());
		operaciones.add(createR4());
		operaciones.add(create5());
		operaciones.add(create6());
		
		operaciones.setIcon(getIconFromResource("images2/businesssetup.png"));
		
		container.add(operaciones);
		
		return container;
	}
	
	private ViewMap viewMap;
	
	
	public JComponent createDocumentPanel(){
		viewMap=new ViewMap();
		rootWindow=new RootWindow(viewMap);
		DockingUtils.configRootWindow(rootWindow);
		DockingUtils.configTabWindowProperties(rootWindow);
		return rootWindow;
	}
	
	
	
	public void close(){
		if(viewMap.getViewCount()>0){
			for(int index=0;index<=viewMap.getViewCount();index++){
				View v=viewMap.getViewAtIndex(index);
				v.close();
				rootWindow.removeView(v);
			}
		}
	}
	
	private Action createR1(){
		final AbstractAction a=new AbstractAction("run"){
			public void actionPerformed(ActionEvent e) {
				CostoPromedioPorPeriodoForm.run();				
			}			
		};
		a.putValue(Action.NAME,"Costo Promedio");
		a.putValue(Action.SMALL_ICON, getIconFromResource("images2/report_go.png"));
		return a;
	}
	
	private Action createR2(){
		final AbstractAction a=new AbstractAction("run"){
			public void actionPerformed(ActionEvent e) {
				MovimientosCostosPorMesForm.run();				
			}			
		};
		a.putValue(Action.NAME,"Movimientos costeados");
		a.putValue(Action.SMALL_ICON, getIconFromResource("images2/report_go.png"));
		return a;
	}
	
	private Action createR3(){
		final AbstractAction a=new AbstractAction("run"){
			public void actionPerformed(ActionEvent e) {
				InventarioCosteadoForm.run();				
			}			
		};
		a.putValue(Action.NAME,"Inventario costeado");
		a.putValue(Action.SMALL_ICON, getIconFromResource("images2/report_go.png"));
		return a;
	}
	
	private Action createR4(){
		final AbstractAction a=new AbstractAction("run"){
			public void actionPerformed(ActionEvent e) {
				MovimientosAlmacenForm.run();				
			}			
		};
		a.putValue(Action.NAME,"Movimientos De Inventario");
		a.putValue(Action.SMALL_ICON, getIconFromResource("images2/report_go.png"));
		return a;
	}
	
	private Action create5(){
		final AbstractAction a=new AbstractAction("run"){
			public void actionPerformed(ActionEvent e) {
				ComsSinAnalizarReport.run();
				
			}
			
		};
		a.putValue(Action.NAME, "Coms Sin Analizar");
		a.putValue(Action.SMALL_ICON, getIconFromResource("images2/report_go.png"));
		
		return a;
	}
	
	private Action create6(){
		final AbstractAction a=new AbstractAction("run"){
			public void actionPerformed(ActionEvent e) {
				KardexReport.run();
			}
			
		};
		a.putValue(Action.NAME, "Kardex");
		a.putValue(Action.SMALL_ICON, getIconFromResource("images2/report_go.png"));
		
		return a;
	}
	

}
