package com.luxsoft.siipap.cxc.task;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXDatePicker;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;

/**
 * Genera la poiliza disco para Credito
 * 
 * @author Ruben Cancino
 *
 */
public class GenerarPolizaCre extends SWXAction{

	@Override
	protected void execute() {
		final InputForm form=new InputForm(getLastDir());
		form.open();
		if(!form.hasBeenCanceled()){
			final Date d=form.datePicker.getDate();
			final File dir=form.getDir();
			form.setDir(dir);
			logger.info("Generando poliza para :"+form.datePicker.getFormats()[0].format(d)+" En el directorio:"+dir.getAbsolutePath());
			final SwingWorker worker=new SwingWorker(){
				
				@Override
				protected Object doInBackground() throws Exception {
					ContaUtils.generarPolizaDeCobranzaCredito(d, form.getDir());
					return "OK";
				}
				@Override
				protected void done() {
					try {
						if(get().equals("OK")){
							MessageUtils.showMessage("Poliza generada para:"+form.datePicker.getFormats()[0].format(d), "Polizas");
						}
					} catch (Exception e) {
						MessageUtils.showError("Error al crear poliza", e);
						e.printStackTrace();
					}
					
				}
				
			};
			TaskUtils.executeSwingWorker(worker);
		}		
	}
	
	public File getLastDir(){
		return new File(Preferences.userRoot().get("polizas.dir", System.getProperty("user.home")));
	}
	
	
	
	private class InputForm extends SXAbstractDialog{
		
		public InputForm(File dir) {
			super("Parametros de poliza");
			this.dir=dir;
			
		}

		private Date fecha;
		private File dir;
		
		
		protected JXDatePicker datePicker;
		private JTextField directorio;
		private JButton btn;
		
		private void init(){
			datePicker=new JXDatePicker();
			datePicker.setFormats(new String[]{"dd/MM/yyyy"});
			directorio=new JTextField(25);
			directorio.setText(getDirPath());
			directorio.setEditable(false);			
			btn=new JButton("...");
			btn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					selectDir();
				}				
			});
		}
		
		private String getDirPath(){
			try {
				return dir.getCanonicalPath();
			} catch (IOException e) {				
				e.printStackTrace();
				return "";
			}
		}

		@Override
		protected JComponent buildContent() {
			init();
			final JPanel panel=new JPanel(new BorderLayout());
			panel.add(buildMainPanel(),BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			return panel;
		}
		
		private JComponent buildMainPanel(){
			final FormLayout layout=new FormLayout(
					"p,2dlu,p ,3dlu," +
					"p,2dlu,p,30dlu "
					,""
					);
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append("Fecha",datePicker,true);
			builder.append("Directorio",directorio);
			builder.append(btn);
			return builder.getPanel();
		}
		
		
		private void selectDir(){
			//final String recDir=System.getProperty("polizas.dir",System.getProperty("user.home"));
			
			final JFileChooser chooser=new JFileChooser(getDir());
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int res=chooser.showDialog(getContentPane(), "Aceptar");
			if(res==JFileChooser.APPROVE_OPTION){
				final File f=chooser.getSelectedFile();
				setDir(f);
				directorio.setText(f.getAbsolutePath());
				try {
					Preferences.userRoot().put("polizas.dir", f.getCanonicalPath());
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}
		}

		public File getDir() {			
			return dir;
		}

		public void setDir(File dir) {
			this.dir = dir;
			try {
				directorio.setText(dir.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Date getFecha() {
			return fecha;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}
		
		
	};
	
	public static void main(String[] args) {
		GenerarPolizaCre pol=new GenerarPolizaCre();
		pol.execute();
	}

}
