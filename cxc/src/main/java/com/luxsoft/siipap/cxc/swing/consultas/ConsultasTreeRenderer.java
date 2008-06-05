package com.luxsoft.siipap.cxc.swing.consultas;

import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;



public class ConsultasTreeRenderer extends DefaultTreeCellRenderer{
	
	private Icon cIcon;
	private Icon rIcon;
	private Icon cI;
	private Icon rI;
	
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		Component c=  super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		
		if(value.toString().equals("Consultas")&& getConsutaNodeIcon()!=null){
			setIcon(getConsutasNodeIcon());
		}else if(value.toString().equals("Reportes") && getReportsIcon()!=null){
			setIcon(getReportIcon());
		}
		if(value instanceof ConsultasView.ConsultaNode){				
			setIcon(getConsutaNodeIcon());
		}
		return c;
	}
	
	private Icon getConsutasNodeIcon(){
		if(cIcon==null){
			cIcon=getIconFromResource("images2/database_table.png");				
		}
		return cIcon;
	}
	
	private Icon getConsutaNodeIcon(){
		if(cI==null){
			cI=getIconFromResource("images2/layout_content.png");
		}
		return cI;
	}
	
	private Icon getReportsIcon(){
		if(rIcon==null){
			rIcon=getIconFromResource("images2/report.png");
		}
		return rIcon;
	}
	
	private Icon getReportIcon(){
		if(rI==null){
			rI=getIconFromResource("images2/report.png");
		}
		return rI;
	}
	
	public Icon getIconFromResource(String path){
		try {
			ClassLoader cl=getClass().getClassLoader();
			URL url=cl.getResource(path);
			Icon icon=new ImageIcon(url);
			return icon;
		} catch (Exception e) {			
			return null;
		}		
	}


}
