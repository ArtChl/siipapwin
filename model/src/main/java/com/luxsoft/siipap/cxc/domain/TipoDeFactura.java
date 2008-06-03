package com.luxsoft.siipap.cxc.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;



public enum TipoDeFactura {
	
	Tipo_A("A","Tipo A","CON")
	,Tipo_B("B","Tipo B","CON")
	,Tipo_C("C","Tipo C","CON")
	,Tipo_D("D","Tipo D","CON")
	,Tipo_E("E","Tipo E","CRE")
	,Tipo_F("F","Tipo F","CRE")
	,Tipo_G("G","Tipo G","CRE")
	,Tipo_Q("Q","Tipo Q","CON")
	,Tipo_R("R","Tipo R","CON")
	,Tipo_S("S","Tipo S","CRE")
	,Tipo_T("T","Tipo T","CON")
	,Tipo_P("P","Tipo P","CRE")
	,Tipo_N("N","Tipo N","CRE")
	,Tipo_X("X","Tipo X","CRE")
	,Tipo_M("M","Tipo M","CON")
	;
	
	
	
	private final String id;
	private final String desc;
	private final String tipo;
	
	private TipoDeFactura(String id,String desc,String tipo){
		this.id=id;
		this.desc=desc;
		this.tipo=tipo;
	}
	
	public String toString(){
		return id+" "+desc+" "+tipo;
	}

	public String getDesc() {
		return desc;
	}

	public String getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}
	
	
	public static List<String> asStringList(){
		List<String> list=new ArrayList<String>();
		for(TipoDeFactura t:values()){
			list.add(t.getId());
		}
		return list;
	}
	
	public static TipoDeFactura parse(final String id){
		Collection<TipoDeFactura> col=Arrays.asList(values());
		Object t=CollectionUtils.find(col, new Predicate(){

			public boolean evaluate(Object object) {
				TipoDeFactura tt=(TipoDeFactura)object;
				return tt.getId().equals(id);
			}
			
		});
		return (TipoDeFactura)t;
	}

}
