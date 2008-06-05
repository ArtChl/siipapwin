package com.luxsoft.siipap.cxc.swing.notas2;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

/**
 * Vista para el mantenimiento y administracion de las
 * notas de credito/cargo de tipo CREDITO
 *  
 * @author Ruben Cancino
 *
 */
public class NotasDeCreditoView extends AbstractView{
	
	//private NotasManager manager;
	private final NCFilterGrid ncfGrid;
	private final NCMantenimiento mantenimiento;
	private JLabel periodo;
	
	private Action porBonificacion;

	public NotasDeCreditoView(final NCFilterGrid ncfGrid,final NCMantenimiento mantenimiento) {
		super();
		this.ncfGrid = ncfGrid;
		this.mantenimiento=mantenimiento;
	}

	@Override
	protected JComponent buildContent() {
		JPanel content=new JPanel(new BorderLayout());		
		FormLayout layout=new FormLayout("f:180dlu,p:g","f:p:g");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(createCommandPanel(),cc.xy(1,1 ));
		builder.add(createBrowserPanel(),cc.xy(2, 1));		
		content.add(builder.getPanel(),BorderLayout.CENTER);
		content.setPreferredSize(new Dimension(600,450));
		return content;
	}
	
	private JComponent createCommandPanel(){
		final JXTaskPaneContainer container=new JXTaskPaneContainer();
		
		final JXTaskPane operaciones=new JXTaskPane();
		operaciones.setLayout(new VerticalLayout(5));
		operaciones.setTitle("Mantenimiento");
		operaciones.setIcon(getIconFromResource("images2/businesssetup.png"));		
		operaciones.add(getNCFilterGrid().getLoadAction());
		operaciones.add(getNCFilterGrid().getPeriodoAction());		
		//operaciones.add(DefaultComponentFactory.getInstance().createSeparator("Altas"));
		operaciones.add(getPorBonificacion());
		operaciones.add(mantenimiento.getPorDevolucion());
		operaciones.add(mantenimiento.getPorDescuento());
		operaciones.add(mantenimiento.getNotaDeCargo());
		operaciones.add(getNCFilterGrid().getDeleteAction());
		
		
		final JXTaskPane mantenimient=new JXTaskPane();
		mantenimient.setTitle("Procesos");
		mantenimient.setIcon(getIconFromResource("images2/overlays.png"));
		mantenimient.add(getNCFilterGrid().getReImprimir());
		
		final JXTaskPane filtros=new JXTaskPane();		
		filtros.setTitle("Filtros");
		filtros.setIcon(getIconFromResource("images2/page_find.png"));
		filtros.add(createFilterPanel());
		
		container.add(operaciones);
		container.add(filtros);
		container.add(mantenimient);
		
		return container;
	}
	
	private JComponent createFilterPanel(){
		DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:40dlu,2dlu,max(p;50dlu):g",""));
		periodo=getNCFilterGrid().getPeriodoLabel();		
		periodo.setHorizontalAlignment(JTextField.CENTER);
		builder.append("Periodo",periodo,true);
		builder.append("Cliente",getNCFilterGrid().getCliente(),true);
		builder.append("Numero",getNCFilterGrid().getNumero(),true);
		builder.append("Tipo",getNCFilterGrid().getTipo(),true);
		builder.append("Fecha",getNCFilterGrid().getDateEditor().getPicker(),true);
		builder.getPanel().setOpaque(false);
		return builder.getPanel();
	}
	
	private JComponent createBrowserPanel(){		
		final JComponent c=UIFactory.createTablePanel(getNCFilterGrid().getGrid());
		return c;
	}
	
	private NCFilterGrid getNCFilterGrid(){		
		return ncfGrid;
	}
	
	
	
	public void setPeriodo(final String s){
		periodo.setText(s);
	}
	
	public void deleteNota(final NotaDeCredito nota){
		
	}
	
	public Action getPorBonificacion() {
		if(porBonificacion==null){
			porBonificacion=new DispatchingAction(this,"altaBonificacion");
			CommandUtils.configAction(porBonificacion, CXCActions.CrearNotaPorBonificacionCRE.getId(), "images2/money_delete.png");
		}
		return porBonificacion;
	}
	
	public void altaBonificacion(){
		NotaDeCredito n=mantenimiento.altaBonificacion();
		if(n!=null){
			getNCFilterGrid().getNotas().add(n);
		}
	}
	

	public static void main(String[] args) {
		SWExtUIManager.setup();
		
		
		final NCMantenimiento m=new NCMantenimiento();
		//m.setManager((NotasManager)ServiceLocator.getDaoContext().getBean("notasManager"));
		final NCFilterGrid grid=new NCFilterGrid();
		grid.setMantenimiento(m);
		final NotasDeCreditoView view=new NotasDeCreditoView(grid,m);
		
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){

			@Override
			protected JComponent buildContent() {
				return view.getContent();
			}

			@Override
			protected void configureWindowClosing() {				
				super.configureWindowClosing();
				setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			}

			@Override
			protected void setResizable() {
				setResizable(true);
			}
			
			
			
			
		};
		
		dialog.open();
		System.exit(0);

	}
	

}
