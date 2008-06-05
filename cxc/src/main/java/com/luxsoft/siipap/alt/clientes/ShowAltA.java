package com.luxsoft.siipap.alt.clientes;

import com.luxsoft.siipap.swing.Application;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class ShowAltA extends SWXAction{
	
	private AltAWindow window;
	

	@Override
	protected void execute() {
		if(Application.isLoaded()){
			getWindow().open(false);			
		}
		getWindow().open(true);		
	}

	public AltAWindow getWindow() {
		return window;
	}

	public void setWindow(AltAWindow window) {
		this.window = window;
	}
	
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		ShowAltA alt=new ShowAltA();
		AltAModel model=new AltAModel();
		AltA altA=new AltA(model);
		AltAWindow window=new AltAWindow();
		window.setAltA(altA);
		alt.setWindow(window);
		alt.execute();
	}	
	

}
