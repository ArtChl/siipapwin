package com.luxsoft.siipap.em.importar.ui;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jgoodies.uif.util.ScreenUtils;
import com.luxsoft.siipap.swing.controls.StatusBar;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

@SuppressWarnings("serial")
public class ReplicatorApp extends JFrame{
	
	private StatusBar statusBar;
	
	public void start(){
		VentasReplicaPanel mainPanel=new VentasReplicaPanel();
		getContentPane().add(mainPanel,BorderLayout.CENTER);
		statusBar=new StatusBar();
		getContentPane().add(statusBar.getStatusPanel(),BorderLayout.SOUTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		ScreenUtils.locateOnScreenCenter(this);
		setVisible(true);
	}
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable(){
			public void run() {
				 SWExtUIManager.setup();	 
				ReplicatorApp app=new ReplicatorApp();
				app.start();
			}
		});
	}

}
