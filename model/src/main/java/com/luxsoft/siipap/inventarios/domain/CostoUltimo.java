package com.luxsoft.siipap.inventarios.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.utils.domain.PersistentObject;

public class CostoUltimo extends PersistentObject implements Comparable{
	
	private Articulo articulo;
	private String periodo;
	private CantidadMonetaria costo=CantidadMonetaria.pesos(0);
	

	public CostoUltimo() {		
	}
	
	public CostoUltimo(Articulo articulo, String periodo) {
		this.articulo = articulo;
		this.periodo = periodo;
	}
	
	public CostoUltimo(Articulo articulo, Periodo periodo) {
		this(articulo,format(periodo));
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	

	
	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	
	public void setPeriodo(Periodo p){
		SimpleDateFormat df=new SimpleDateFormat("MM/yyyy");
		setPeriodo(df.format(p.getFechaInicial()));
	}
	
	public Periodo periodo(){
		SimpleDateFormat df=new SimpleDateFormat("MM/yyyy");
		Periodo p=Periodo.getPeriodo(getPeriodo(),df);
		return p;
	}
	
	
	

	public CantidadMonetaria getCosto() {
		return costo;
	}

	public void setCosto(CantidadMonetaria costo) {
		this.costo = costo;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this) return true;
		CostoUltimo cp=(CostoUltimo)obj;
		return new EqualsBuilder()
			.append(getArticulo().getClave(),cp.getArticulo().getClave())
			.append(getPeriodo(),cp.getPeriodo())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
			.append(getArticulo())
			.append(getPeriodo())
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
			.append(getPeriodo())
			.append(getArticulo())
			.append(getCosto())
			.toString();
	}
	
	public int compareTo(Object o) {
		if(o==null)return -1;
		if(o==this)return 0;
		CostoUltimo cu=(CostoUltimo)o;
		if(getArticulo().equals(cu.getArticulo())){
			return getPeriodo().compareToIgnoreCase(cu.getPeriodo());
		}
		return getArticulo().getClave().compareToIgnoreCase(cu.getArticulo().getClave());
	}
	
	protected static DateFormat dateFormat =new SimpleDateFormat("MM/yyyy");
	
	public static String format(final Periodo periodo){
		return format(periodo.getFechaInicial());
	}
	public static String format(final Date date){
		return dateFormat.format(date);
	}
	public static Periodo parse(final String periodo){
		//Date  dateFormat.parse(periodo);
		return Periodo.getPeriodo(periodo,dateFormat);
	}

	

}
