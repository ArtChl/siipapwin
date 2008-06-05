package com.luxsoft.siipap.swing.selectores;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXTable;

import com.jgoodies.uif.util.ComponentUtils;
import com.luxsoft.siipap.domain.ArticuloRow;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;


public class SelectorDeArticulos extends SXAbstractDialog{
	
	
	private ArticulosBrowser browser;
	
	public SelectorDeArticulos() {
		super("Catálogo de Artículos");		
	}
	
	@Override
	protected JComponent buildContent() {
		browser=new ArticulosBrowser();
		JComponent c=browser.getControl();
		JXTable table=browser.getGrid();
		table.getActionMap().put("select",new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				doAccept();
			}
		});
		table.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"select");
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					doAccept();
				}
			}
		});
		ComponentUtils.addAction(table, new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				browser.getInputField().requestFocus();				
			}			
		}, KeyStroke.getKeyStroke("F2"));		
		return c;
	}

	public List<ArticuloRow> getSelection(){
		return browser.getSelected();
	}
	
	
	protected void onWindowOpened(){
		load();
	}
	
	public void load(){
		browser.load();
	}

		
}
