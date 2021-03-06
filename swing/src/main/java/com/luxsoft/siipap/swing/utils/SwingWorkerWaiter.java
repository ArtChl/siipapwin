package com.luxsoft.siipap.swing.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import javax.swing.SwingWorker;

import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

public class SwingWorkerWaiter implements PropertyChangeListener{
	
	private WeakReference<SXAbstractDialog> dialog;
	
	public SwingWorkerWaiter(SXAbstractDialog owner){
		dialog=new WeakReference<SXAbstractDialog>(owner);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if("state".equals(evt.getPropertyName())
			&& SwingWorker.StateValue.DONE.equals(evt.getNewValue())){
			if(dialog.get()!=null)
				dialog.get().close();
			
		}else if(SwingWorker.StateValue.STARTED.equals(evt.getNewValue())){
			if(dialog.get()!=null)
				dialog.get().open();
		}
				
			
		
		
	}

}
