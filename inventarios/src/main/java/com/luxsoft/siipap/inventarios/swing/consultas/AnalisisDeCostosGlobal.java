package com.luxsoft.siipap.inventarios.swing.consultas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import net.infonode.tabbedpanel.titledtab.TitledTab;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.VerticalLayout;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.ActionLabel;
import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.utils.CommandUtils;

/**
 * Vista que presenta un resumen global de los coms analizados
 * en cxp y maquila
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeCostosGlobal extends AbstractView implements AnalisisSupport{
	
	private Action seleccionarPeriodo;
	private Action loadData;
	private TitledTab tab;
	
	private void initActions(){
		seleccionarPeriodo=new DispatchingAction(this,"seleccionarPeriodo");
		getActionConfigurer().configure(seleccionarPeriodo, "seleccionarPeriodo");		
		loadData=CommandUtils.createLoadAction(this, "load");
	}
	
	public TitledTab getTab(){
		if(tab==null){
			tab=new TitledTab("Global",getIconFromResource("images2/box.png"),getContent(),null);
			tab.putClientProperty(AnalisisSupport.SUPPORT_KEY,this);
		}
		return tab;
	}

	@Override
	protected JComponent buildContent() {
		initActions();
		JXPanel panel=new JXPanel(new BorderLayout());
		panel.add(buildHeaderPanel(),BorderLayout.NORTH);
		panel.add(buildMainPanel(),BorderLayout.CENTER);		
		return panel;
	}
	
	private JComponent buildMainPanel(){
		final FormLayout layout=new FormLayout(
				"c:p:g(.5),2dlu,c:p:g(.5)",
				"40dlu,c:p,5dlu,c:p,5dlu,c:p");
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		
		Color c=UIManager.getColor("TitledBorder.titleColor");
		
		ActionLabel l1=new ActionLabel("Analizados CxP");
		ActionLabel l2=new ActionLabel("Analizados Maq (Hoj)");
		ActionLabel l3=new ActionLabel("Analizados Maq (Bob)");
		Font font=l1.getFont().deriveFont(Font.BOLD).deriveFont(7);
		l1.setForeground(c);
		l1.setFont(font);
		l2.setForeground(c);
		l2.setFont(font);
		l3.setForeground(c);
		l3.setFont(font);
		l1.setHorizontalAlignment(JLabel.RIGHT);
		l2.setHorizontalAlignment(JLabel.RIGHT);
		l3.setHorizontalAlignment(JLabel.RIGHT);
		
		builder.add(l1,cc.xy(1, 2));
		builder.add(l2,cc.xy(1, 4));
		builder.add(l3,cc.xy(1, 6));
		
		return builder.getPanel();
	}
	
	private JXHeader header;
	
	private JComponent buildHeaderPanel(){
		header=new JXHeader("Análisis global de costos","Costos globales de CxP y Maquila");
		return header;
	}

	public Action[] getOperaciones() {		
		return new Action[]{
				
		};
	}
	
	public JComponent getFilterPanel() {
		FormLayout layout=new FormLayout(
				"l:p,2dlu,R:p:g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();		
		builder.append("Periodo",new JTextField(20),true);
		JPanel	filtrosPanel= builder.getPanel();
		filtrosPanel.setOpaque(false);
		return filtrosPanel;
	}

}
