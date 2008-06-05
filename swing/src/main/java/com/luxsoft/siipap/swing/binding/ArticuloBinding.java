package com.luxsoft.siipap.swing.binding;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.component.ToolBarButton;
import com.jgoodies.uif.util.ComponentUtils;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.controls.UpperCaseField;
import com.luxsoft.siipap.swing.selectores.SelectorDeArticulos;

public class ArticuloBinding extends AbstractControl{
	
	private JTextField tfClave;
	private JTextField tfNombre;
	private JButton btnF2;
	private Action lookupAction;
	private ValueModel articulo=new ValueHolder();
	
	private Logger logger=Logger.getLogger(getClass());
	
	public ArticuloBinding(ValueModel valueModel){
		this();
		articulo=valueModel;
	}
	
	public ArticuloBinding(){
		articulo.addValueChangeListener(articuloHandler);
	}

	@Override
	protected JComponent buildContent() {
		init();
		FormLayout layout=new FormLayout("f:p,2dlu,f:p:g,2dlu,f:20dlu","p");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		//builder.addLabel("Cliente: ", cc.xy(1, 1));
		builder.nextLine();
		builder.add(tfClave,cc.xy(1, 1));
		builder.nextLine();
		builder.add(tfNombre,cc.xy(3, 1));
		builder.nextLine();
		builder.add(btnF2,cc.xy(5, 1));
		return builder.getPanel();		
	}
	
	private void init(){
		lookupAction=new AbstractAction("lookup"){
			public void actionPerformed(ActionEvent e) {
				lookup();				
			}			
		};
		lookupAction.putValue(Action.SMALL_ICON, getIconFromResource("images/misc/tsearch_obj.gif"));
		tfClave=new UpperCaseField(7);
		tfClave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				getCliente();
			}			
		});
		tfNombre=new UpperCaseField(40);
		
		tfNombre.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(articulo.getValue()!=null){
					tfNombre.transferFocus();
				}
			}			
		});
		btnF2=new ToolBarButton(lookupAction);
		btnF2.setText("...");
		btnF2.setFocusable(false);
		//btnF2.setBorder(null);
		//btnF2.setOpaque(false);
		
		ComponentUtils.addAction(tfClave, lookupAction, KeyStroke.getKeyStroke("F2"));
		ComponentUtils.addAction(tfNombre, lookupAction, KeyStroke.getKeyStroke("F2"));
		
	}
	
	private void lookup(){
		JTextField tf=tfClave.hasFocus()?tfClave:tfNombre;
		if(tf.getText().length()==0){
			JOptionPane.showMessageDialog(this.getControl()
					, "Digite una clave o nombre para localizar al cliente desado"
					,"Selección de Cliente"
					,JOptionPane.WARNING_MESSAGE);
			return;
		}else{
			
			//JFrame owner=null;
			//if(Application.isLoaded())
				//owner=Application.instance().getMainFrame();
			SelectorDeArticulos selector=new SelectorDeArticulos();
			//selector.getInputField().setText(tf.getText());
			//selector.setPorClave(tfClave.hasFocus());
			selector.open();
			selector.setLocationRelativeTo(getControl());
			if(!selector.hasBeenCanceled() && (!selector.getSelection().isEmpty())){
				articulo.setValue(selector.getSelection().get(0));
			}
		}
		
	}
	
	private void getCliente(){
		if(articulo.getValue()!=null)
			transferFocus();
		if(tfClave.getText().length()==7){
			if(logger.isDebugEnabled()){
				logger.debug("Solicitando cliente: "+tfClave.getText());				
			}
		}else
			lookup();
		
	}
	
	private void transferFocus(){
		FocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
	}
	
	/**
	 * Observa los cambios en el modelo y actualiza los componentes
	 */
	private PropertyChangeListener articuloHandler=new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getNewValue()!=null){
				Articulo a=(Articulo)evt.getNewValue();
				tfClave.setText(a.getClave());
				tfNombre.setText(a.getDescripcion1());
				//transferFocus();
			}else{
				tfClave.setText("");
				tfNombre.setText("");
			}
			
		}
		
	};

}
