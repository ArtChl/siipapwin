package com.luxsoft.siipap.swing.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.Application;

public class DataBaseLocator extends AbstractControl{
	
	

	@Override
	protected JComponent buildContent() {
		JLabel l=new JLabel();
		Icon i=getIconFromResource("images2/server_go.png");
		l.setIcon(i);
		l.setText("DB:");
		sincronize();
		/*
		l.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					System.out.println("Sincronizando");
					sincronize();
				}
			}			
		});
		*/
		return l;
	}
	
	public void sincronize(){
		SwingWorker worker=new SwingWorker(){			
			protected Object doInBackground() throws Exception {
				if(Application.isLoaded()){
					return ServiceLocator.getJdbcTemplate().execute(new ConnectionCallback(){

						public Object doInConnection(Connection con) throws SQLException, DataAccessException {
							String s=con.getMetaData().getURL();
							return StringUtils.substringAfter(s, "1521:").toUpperCase();
						}
						
					});
				}else
					return "ERROR";
			}

			@Override
			protected void done() {
				try {
					//getControl().setToolTipText(get().toString());
					((JLabel)getControl()).setText(get().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		};
		worker.execute();
	}
	
	

}
