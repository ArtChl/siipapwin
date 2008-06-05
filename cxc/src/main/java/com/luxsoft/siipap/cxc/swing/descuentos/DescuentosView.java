package com.luxsoft.siipap.cxc.swing.descuentos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.panel.CardPanel;
import com.jgoodies.uifextras.panel.ItemPanel;
import com.luxsoft.siipap.cxc.desc.DescuentosPorFacturaDialog;
import com.luxsoft.siipap.cxc.swing.Descuentos;
import com.luxsoft.siipap.cxc.swing.descuentos.DescuentosPanelFactory.DescuentoBrowser;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.CURD;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.controls.Header;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;

/**
 * Vista para el mantenimiento centralizado de descuentos, entre los patrones importantes que implementa para su
 * funcionamiento estan el FactoryMethod para crear instancias de tipo BasicCURDGridPanel y el Strategy para delegar
 * el comportamiento de CURD a cada uno de los BasicCURDGridPanel, existe uno para cada tipo de descuento.
 * 
 * En terminos generales funciona delegando el comportamiento y estado a instancias BasicCURDGridPanel
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentosView extends AbstractView implements CURD{
	
	public static final String SELECTED_PROPERTY_NAME="selected";
	
	private Descuentos selected;
	private DescuentosCURD strategy;
	private CardPanel cardPanel;
	private JComponent nullPanel;
	private JToolBar toolbar;
	private List<Action> actions;
	 
	
	private DescuentoBrowser volumenCURD;
	private DescuentoBrowser articulosCURD;
	private DescuentoBrowser clienteCURD;
	
	
	
	private void initComponents(){
		volumenCURD=DescuentosPanelFactory.getDescuentoPorVolumen();
		articulosCURD=DescuentosPanelFactory.getDescuentoPorArticulo();
		clienteCURD=DescuentosPanelFactory.getDescuentoPorCliente();
		
		nullPanel=new Header("Descuentos","Administrados por CXC").getHeader();
		actions=CommandUtils.createCommonCURD_Actions(this);
		toolbar=new JToolBar();
		toolbar.setFloatable(false);
		
		Action activarAction=new DispatchingAction(this,"activar");
		Action desactivarAction=new DispatchingAction(this,"desactivar");
		if(getActionConfigurer()!=null){
			getActionConfigurer().configure(activarAction, "activarDescuentosAction");
			getActionConfigurer().configure(desactivarAction, "desactivarDescuentosAction");
		}
		else{
			activarAction.putValue(Action.NAME, "Activar");
			activarAction.putValue(Action.SMALL_ICON, getIconFromResource("images2/accept.png"));
			desactivarAction.putValue(Action.NAME, "DesActivar");
			desactivarAction.putValue(Action.SMALL_ICON, getIconFromResource("images2/delete.png"));
		}
		actions.add(activarAction);
		actions.add(desactivarAction);
		
		for(Action a:actions){
			toolbar.add(a);
			a.setEnabled(false);
		}
		
			
	}
	
	private void initEventHandling(){
		propertySupport.addPropertyChangeListener(SELECTED_PROPERTY_NAME,descuentoHandler);
		propertySupport.addPropertyChangeListener(SELECTED_PROPERTY_NAME,actionHandler);
	}
	
	
	@Override
	protected JComponent buildContent() {
		initComponents();
		initEventHandling();
		JPanel panel=new JPanel(new BorderLayout(5,5));
		FormLayout layout=new FormLayout("f:max(100dlu;p),3dlu,f:p:g","t:p,p:g");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(buildNavegatorPanel(),cc.xy(1, 1));
		builder.add(buildDocumentPanel(),cc.xywh(3,1,1,2));
		builder.getPanel().setOpaque(false);
		
		panel.add(toolbar,BorderLayout.NORTH);
		panel.add(builder.getPanel(),BorderLayout.CENTER);
		
		return panel;
		
	}
	
	private JComponent buildNavegatorPanel(){
		Icon icon=getIconFromResource("images/misc/arrowGreen.gif");
		ItemPanel panel=new ItemPanel(icon);
		panel.setBackground(Color.WHITE);
		panel.setOpaque(false);
		panel.add(new SeleccionarDescuentoAction(Descuentos.VOLUMEN));
		panel.add(new SeleccionarDescuentoAction(Descuentos.CLIENTE));
		panel.add(new SeleccionarDescuentoAction(Descuentos.ARTICULO));
		final Action especialAction=new AbstractAction("Descuento por Venta"){
			public void actionPerformed(ActionEvent e) {
				final DescuentosPorFacturaDialog dialog=new DescuentosPorFacturaDialog();
				dialog.open();
			}
			
		};
		especialAction.putValue(Action.SHORT_DESCRIPTION, "Descuento por Venta");
		panel.add(especialAction);
		return panel;
	}
	
	private JComponent buildDocumentPanel(){
		cardPanel=new CardPanel();
		cardPanel.showCard(nullPanel);
		return cardPanel;
	}

	@Override
	protected void dispose() {		
		//logger.debug("Cerrando recursos de la vista descuentos:"+getId());
	}
	
	public Descuentos getSelected() {
		return selected;
	}
	public void setSelected(Descuentos selected) {
		Object old=this.selected;
		this.selected = selected;
		propertySupport.firePropertyChange("selected",old,selected);
	}
	
	private void updateActions(boolean val){
		for(Action a:actions){
			a.setEnabled(val);
		}
	}
	
	/**
	 * Actualiza el panel en el CardPanel en funcion del descuento seleccionado
	 * 
	 */
	private PropertyChangeListener descuentoHandler =new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {
			Descuentos val=(Descuentos)evt.getNewValue();
			
			switch (val) {
			case VOLUMEN:
				cardPanel.showCard(volumenCURD.getControl());
				strategy=volumenCURD;
				break;
			case ARTICULO:
				cardPanel.showCard(articulosCURD.getControl());
				strategy=articulosCURD;
				break;
			case CLIENTE:
				cardPanel.showCard(clienteCURD.getControl());
				strategy=clienteCURD;
				break;
			default:
				//setSelected(null);
				strategy=null;
				cardPanel.showCard(nullPanel);
				updateActions(false);
			}
		}
		
	};
	
	private PropertyChangeListener actionHandler=new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {
			updateActions(evt.getNewValue()!=null);
		}
		
	};
	
	
	private class SeleccionarDescuentoAction extends AbstractAction{
		
		private final Descuentos descuento;

		public SeleccionarDescuentoAction(final Descuentos descuento) {
			super(descuento.toString());
			this.descuento = descuento;
			
		}

		public void actionPerformed(ActionEvent e) {
			setSelected(descuento);
		}
		
	}


	public void delete() {
		strategy.delete();
		/**
		try {
			
		} catch (Exception e) {
			MessageUtils.showError("Error eliminando descuento",e);
		}**/
		
	}
	public void edit() {
		strategy.edit();
	}
	public void insert() {
		strategy.insert();
	}
	public void view() {
		strategy.view();
	}
	public void refresh() {
		strategy.refresh();
	}
	
	public void activar(){
		strategy.activar();
	}
	public void desactivar(){
		strategy.desactivar();
	}
	
	


	@Override
	public void close() {
		this.articulosCURD.getSource().clear();
		this.volumenCURD.getSource().clear();
		this.clienteCURD.getSource().clear();
		
	}




	public static interface DescuentosCURD extends CURD{
		
		public void activar();
		
		public void desactivar();
	}

}
