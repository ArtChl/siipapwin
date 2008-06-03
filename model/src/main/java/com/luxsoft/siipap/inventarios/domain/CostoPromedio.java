package com.luxsoft.siipap.inventarios.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.utils.domain.PersistentObject;

public class CostoPromedio extends PersistentObject implements Comparable{
	
	private Articulo articulo;
	private String periodo;
	
	private int year=Periodo.obtenerYear(new Date());
	private int mes=Periodo.obtenerMes(new Date());
	
	private CantidadMonetaria costo=CantidadMonetaria.pesos(0);
	
	
	
	
	private Set<Entrada> entradas=new HashSet<Entrada>();
	
	

	public CostoPromedio() {		
	}
	
	public CostoPromedio(Articulo articulo, String periodo) {
		this.articulo = articulo;
		this.periodo = periodo;
	}
	
	public CostoPromedio(Articulo articulo, Periodo periodo) {
		this(articulo,format(periodo));
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public Set<Entrada> getEntradas() {
		return entradas;
	}

	@SuppressWarnings("unused")
	public void setEntradas(Set<Entrada> entradas) {
		this.entradas = entradas;
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
	
	
	
	public void recalcularCostoPromedio(Long existenciaInicial,CostoPromedio inicial){
		Assert.notNull(inicial,"No puede recalcular con inicial nulo");
		Assert.notNull(inicial.getCosto(),"Costo del Promdedio incial no puede ser nulo");
		long saldo=existenciaInicial;
		
		if(getEntradas().isEmpty()){
			setCosto(inicial.getCosto());
			return;
		}
		BigDecimal factor=null;
		CantidadMonetaria importe=null;		
		
		for(Entrada e:getEntradas()){
			if(importe==null){
				factor=BigDecimal.valueOf(e.getALMUNIXUNI());
				BigDecimal exis=BigDecimal.valueOf(existenciaInicial).divide(factor,3,RoundingMode.HALF_EVEN);
				importe=inicial.getCosto().multiply(exis);
			}
			saldo+=e.getALMCANTI();
			importe=importe.add(e.calcularImporte());
			
		}
		if(saldo!=0 && getEntradas().size()>0){
			BigDecimal sal=BigDecimal.valueOf(saldo).divide(factor,3,RoundingMode.HALF_EVEN);
			CantidadMonetaria cp=importe.divide(sal);			
			setCosto(cp);
		}		
	}
	
	public void recalcularCostoPromedio(){
		long saldo=0;
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		BigDecimal factor=null;
		factor=null;
		for(Entrada e:getEntradas()){
			saldo+=e.getALMCANTI();
			importe=importe.add(e.calcularImporte());
			if(factor==null){
				factor=BigDecimal.valueOf(e.getALMUNIXUNI().longValue(),3);
			}
		}
		if(saldo!=0){
			BigDecimal sal=BigDecimal.valueOf(saldo,3).divide(factor);
			CantidadMonetaria cp=importe.divide(sal);			
			setCosto(cp);
		}
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
		CostoPromedio cp=(CostoPromedio)obj;
		return new EqualsBuilder()
			.append(getArticulo(),cp.getArticulo())
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
	
	public static boolean isEnero(String periodo){
		Periodo p=parse(periodo);
		return isEnero(p);
	}
	public static boolean isEnero(Periodo p){
		int mes=Periodo.obtenerMes(p.getFechaInicial());
		return mes==0;
	}

	public int compareTo(Object o) {
		if(o==null)return -1;
		if(o==this)return 0;
		CostoPromedio cp=(CostoPromedio)o;
		if(getArticulo().equals(cp.getArticulo())){
			return getPeriodo().compareToIgnoreCase(cp.getPeriodo());
		}
		return getArticulo().getClave().compareToIgnoreCase(cp.getArticulo().getClave());
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
