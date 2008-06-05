package com.luxsoft.siipap.alt.clientes;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXFrame.StartPosition;

import com.jgoodies.uif.builder.MenuBuilder;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.luxsoft.siipap.swing.Application;
import com.luxsoft.siipap.swing.controls.DataBaseLocator;
import com.luxsoft.siipap.swing.controls.MemoryMonitor;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ResourcesUtils;

public class AltAWindow {
	
	private JXFrame frame;
	private AltA altA;
	private MemoryMonitor mMonitor;
	private DataBaseLocator dbLocator;
	private JXStatusBar statusBar;
	private JXBusyLabel busyLabel;
	public AltAWindow(){
		
	}
	
	public void open(boolean exitOnClose){
		if(frame==null){
			frame=new JXFrame("Análisis de Clientes",exitOnClose);
			frame.addComponent(getAltA().getContent());
			getAltA().setWindow(this);
			frame.setJMenuBar(buildMenubar());
			frame.setStatusBar(getStatusBar());
			frame.setToolBar(buildToolbar());
			frame.setStartPosition(StartPosition.CenterInParent);
			frame.setIconImage(CommandUtils.getImageFromResource("images/siipapwin/cxc24.jpg"));
			setTryIcon();
			frame.addWindowListener(new TryHandler());
			frame.pack();
			altA.seleccionar();
		}
		frame.setVisible(true);
	}
	
	public JXStatusBar getStatusBar(){
		if(statusBar==null){
			mMonitor=new MemoryMonitor();
			dbLocator=new DataBaseLocator();			
			statusBar=new JXStatusBar();
			statusBar.add(dbLocator.getControl());
			statusBar.add(mMonitor.getControl());
			statusBar.add(getBusyLabel());
		}				
		return statusBar;
	}
	
	public JXBusyLabel getBusyLabel(){
		if(busyLabel==null){
			busyLabel=new JXBusyLabel();
			busyLabel.setBusy(false);
		}
		return busyLabel;
	}
	
	private JToolBar buildToolbar(){
		ToolBarBuilder builder=new ToolBarBuilder();
		builder.add(getAltA().getReloadAction());
		builder.add(getAltA().getSeleccionarClienteAction());
		builder.add(getAltA().getActualizarDatos());
		return builder.getToolBar();
	}
	
	private JMenuBar buildMenubar(){
		JMenuBar bar=new JMenuBar();
		MenuBuilder builder=new MenuBuilder("Ventas",'V');
		bar.add(builder.getMenu());
		
		return bar;
	}
	
	private void exit(){
		if(!Application.isLoaded()){
			frame.setVisible(false);
			frame.dispose();
			System.exit(0);
		}
	}
	
	private TrayIcon tryIcon;
	
	private void setTryIcon(){
		if(SystemTray.isSupported()){
			SystemTray tray=SystemTray.getSystemTray();
			Image img=ResourcesUtils.getImageFromResource("images/siipapwin/siipapw24.jpg");			
		    ActionListener exitListener=new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					exit();					
				}
		    	
		    };
		    PopupMenu popup=new PopupMenu();
		    MenuItem exitItem=new MenuItem("Salir");
		    exitItem.addActionListener(exitListener);
		    MenuItem showItem=new MenuItem("Mostrar");
		    showItem.addActionListener(new ShowWindow());
		    popup.add(showItem);
		    popup.add(exitItem);

		    tryIcon=new TrayIcon(img,"Consulta de Clientes",popup);
		    
		    ActionListener actionListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	tryIcon.displayMessage("Action Event", 
		                "An Action Event Has Been Performed!",
		                TrayIcon.MessageType.INFO);
		        }
		    };		            
		    tryIcon.setImageAutoSize(true);
		    //tryIcon.addActionListener(actionListener);
		    try {
		        tray.add(tryIcon);
		    } catch (AWTException e) {
		        System.err.println("TrayIcon could not be added.");
		    }


			
		}
	}

	public JXFrame getFrame() {		
		return frame;
	}


	public AltA getAltA() {
		return altA;
	}
	public void setAltA(AltA altA) {
		this.altA = altA;
	}
	
	private class TryHandler extends WindowAdapter{
		@Override
		public void windowIconified(WindowEvent e) {
			getFrame().setExtendedState(JFrame.ICONIFIED);
			e.getWindow().setVisible(false);
		}		
	}
	
	private class ShowWindow implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			getFrame().setExtendedState(JFrame.NORMAL);
			getFrame().setVisible(true);
			
		}
		
	}

}
