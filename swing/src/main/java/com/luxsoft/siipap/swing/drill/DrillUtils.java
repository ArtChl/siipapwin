package com.luxsoft.siipap.swing.drill;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;

@SuppressWarnings("unchecked")
public class DrillUtils {
	
	public static Drill createDrillView(final EventList source,final String[] props,final String cols[],final String[] filterProps ){
		return  createDrillView(source,GlazedLists.tableFormat(props, cols),GlazedLists.textFilterator(filterProps));
	}
	
	public static Drill createDrillView(final EventList source,final TableFormat tf,final TextFilterator filterator){
		Drill drill=new Drill(source,tf);
		drill.setTextFilterator(filterator);
		return drill;
	}
	
	public static Drill createDrillView(final EventList source,final String[] props,final String cols[] ){
		return  createDrillView(source,GlazedLists.tableFormat(props, cols),GlazedLists.textFilterator(cols));
	}
	
	
	public static Drill createDrillView(final EventList source,final TableFormat tf){		
		Drill drill=new Drill(source,tf);
		return drill;
	}

}
