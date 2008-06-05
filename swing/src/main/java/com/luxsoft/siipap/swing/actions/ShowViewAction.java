package com.luxsoft.siipap.swing.actions;

import java.text.MessageFormat;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.luxsoft.siipap.swing.AbstractView;

/**
 * Accion mostrar instancias de tipo com.luxsoft.siipap.swing.View
 * que estan controladas e inicializadas por el contenedor de Spring
 *  
 * 
 * @author Ruben Cancino 
 *
 */
public class ShowViewAction extends SWXAction{
	
	private String viewId;
	private Logger logger=Logger.getLogger(getClass());
	
	public ShowViewAction(){
		
	}
	
	public ShowViewAction(final String viewId){
		this.viewId=viewId;
	}
/*
	public void actionPerformed(ActionEvent e) {
		if(Application.isLoaded()){
			AbstractView view=getView();
			if(view!=null){
				Application.instance().getMainPage().addView(view);
			}
		}else{
			JOptionPane.showMessageDialog(null,"Esta accion solo es funcional si el objeto Application esta inicializado");
		}
	}
	*/
	
	@Override
	protected void execute() {
		AbstractView view=getView();
		showView(view);
		
	}
	
	protected void showView(final AbstractView view){
		if(view!=null){
			getApplication().getMainPage().addView(view);
		}
	}
	
	
	protected AbstractView getView(){		
		if(getContext().containsBean(getViewId())){
			Object bean=getContextBean(getViewId());
			if(bean instanceof AbstractView)
				return (AbstractView)bean;
			else{
				String msg=MessageFormat.format("El bean {0} ne es de tipo AbstractView\n es de tipo: {1}", getViewId(),bean.getClass().getName());
				JOptionPane.showMessageDialog(getApplication().getMainFrame(),msg);
				logger.error(msg);
				return null;
			}
		}else{
			if(logger.isDebugEnabled()){
				String msg=MessageFormat.format("No existe la vista {0} dada de alta en  el conexto de Spring",getViewId());
				JOptionPane.showMessageDialog(getApplication().getMainFrame(),msg);
				logger.debug(msg);
			}
			
			return null;
		}
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	
	
	

}
