package com.luxsoft.siipap.inventarios.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.utils.domain.MutableObject;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class InventarioMensual extends MutableObject implements Comparable<InventarioMensual>{
	
	private Long id;
	
	private int year;
	private int mes;
	
	private Articulo articulo;
	private String clave;
	private int factor;
	
	private BigDecimal inicial=BigDecimal.ZERO;
	private CantidadMonetaria costoInicial=CantidadMonetaria.pesos(0);
	
	private BigDecimal cxp=BigDecimal.ZERO;
	private CantidadMonetaria costoCxp=CantidadMonetaria.pesos(0);
	
	private BigDecimal maq=BigDecimal.ZERO;
	private CantidadMonetaria costoMaq=CantidadMonetaria.pesos(0);
	private CantidadMonetaria gastosMaq=CantidadMonetaria.pesos(0);
	private BigDecimal kilosMaq=BigDecimal.ZERO;
	
	private BigDecimal saldo=BigDecimal.ZERO;
	private CantidadMonetaria costo=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria costoPromedio=CantidadMonetaria.pesos(0);
	
	private BigDecimal ventas=BigDecimal.ZERO;
	
	private BigDecimal comsSinAnalizar=BigDecimal.ZERO;
	
	private BigDecimal salidasNV=BigDecimal.ZERO;
	
	private BigDecimal movimientos=BigDecimal.ZERO;
	
	private BigDecimal movimientosCosto=BigDecimal.ZERO;
	
	private Date creado=new Date();
	private Date modificado=new Date();
	
	private int version;
	
	//private PropertyChangeSupport support=new PropertyChangeSupport(this);
	
	public InventarioMensual(){
		
	}
	
	public InventarioMensual(int year, int mes, Articulo articulo) {
		super();
		this.year = year;
		this.mes = mes;
		this.articulo = articulo;
		setClave(articulo.getClave());
	}
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Articulo getArticulo() {
		return articulo;
	}
	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	
	public CantidadMonetaria getCosto() {
		return costo;
	}
	public void setCosto(CantidadMonetaria costo) {
		this.costo = costo;
	}
	public CantidadMonetaria getCostoCxp() {
		return costoCxp;
	}
	public void setCostoCxp(CantidadMonetaria costoCxp) {
		this.costoCxp = costoCxp;
	}
	public CantidadMonetaria getCostoInicial() {
		return costoInicial;
	}
	public void setCostoInicial(CantidadMonetaria costoInicial) {
		this.costoInicial = costoInicial;
	}
	public CantidadMonetaria getCostoMaq() {
		return costoMaq;
	}
	public void setCostoMaq(CantidadMonetaria costoMaq) {
		this.costoMaq = costoMaq;
	}
	public CantidadMonetaria getCostoPromedio() {
		return costoPromedio;
	}
	public void setCostoPromedio(CantidadMonetaria costoPromedio) {
		this.costoPromedio = costoPromedio;
	}
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	public BigDecimal getCxp() {
		return cxp;
	}
	public void setCxp(BigDecimal cxp) {
		this.cxp = cxp;
	}
	public int getFactor() {
		return factor;
	}
	public void setFactor(int factor) {
		this.factor = factor;
	}
	public BigDecimal getInicial() {
		return inicial;
	}
	public void setInicial(BigDecimal inicial) {
		this.inicial = inicial;
	}
	public BigDecimal getMaq() {
		return maq;
	}
	public void setMaq(BigDecimal maq) {
		this.maq = maq;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		Assert.isTrue(mes>=1 && mes<=12,"El rango de mes es 1 - 12");
		this.mes = mes;
	}
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public void setCostoInicialConCUnitario(final CantidadMonetaria cu){
		setCostoInicial(cu.multiply(getInicial()));
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getClave())
		.append(getYear())
		.append(getMes())
		.append("Ini",getInicial())
		.append("Fin",getSaldo())
		.append("Costo",getCosto())
		.append("CostoP",getCostoPromedio())
		.toString();
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((clave == null) ? 0 : clave.hashCode());
		result = PRIME * result + mes;
		result = PRIME * result + year;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final InventarioMensual other = (InventarioMensual) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		if (mes != other.mes)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	
	public CantidadMonetaria getGastosMaq() {
		return gastosMaq;
	}
	public void setGastosMaq(CantidadMonetaria gastosMaq) {
		this.gastosMaq = gastosMaq;
	}

	public BigDecimal getKilosMaq() {
		return kilosMaq;
	}
	public void setKilosMaq(BigDecimal kilosMaq) {
		this.kilosMaq = kilosMaq;
	}

	public void actualizarPromedio(){
		
		final BigDecimal ini=getInicial()!=null?getInicial():BigDecimal.ZERO;
		final BigDecimal cxp=getCxp()!=null?getCxp().abs():BigDecimal.ZERO;
		final BigDecimal maq=getMaq()!=null?getMaq().abs():BigDecimal.ZERO;
		BigDecimal cantidad=ini.add(cxp).add(maq);
		final CantidadMonetaria importe=getCostoInicial()
		.add(getCostoCxp().abs())
		.add(getCostoIntegradMaq().abs());
		boolean fixCosto=false;
		if(cantidad.doubleValue()!=0){
			if(ini.doubleValue()!=0 && getCostoInicial().amount().doubleValue()==0){
				cantidad=cantidad.subtract(ini);
				fixCosto=true;
			}
			setCostoPromedio(cantidad.doubleValue()!=0?importe.divide(cantidad):CantidadMonetaria.pesos(0));
		}else
			setCostoPromedio(CantidadMonetaria.pesos(0));
		if(fixCosto)
			setCostoInicial(getCostoPromedio().multiply(getInicial()));
		// Si el saldo al final del periodo es 0 y existieron compras/maquila en el periodo
		BigDecimal costoCxP_Maquila=getCostoCxp().add(getCostoIntegradMaq()).amount();
		if(getSaldo().doubleValue()==0 
				&& costoCxP_Maquila.doubleValue()!=0 
				&& getCostoInicial().amount().doubleValue()==0){
			double cantidad2=cxp.doubleValue()+maq.doubleValue();
			
			CantidadMonetaria costo2=CantidadMonetaria.pesos(costoCxP_Maquila.doubleValue());
			setCostoPromedio(costo2.divide(cantidad2));
		}
	}
	
	public void actualizar(){
		actualizarPromedio();
		if(getSaldo()!=null)
			setCosto(getCostoPromedio().multiply(getSaldo()));
		if(getMovimientos()!=null){
			setMovimientosCosto(getMovimientos().multiply(getCostoPromedio().amount()));
		}
	}
	
	/**
	 * Actualiza unicamente el valor del inventario final y el valor de los movimientos internos 
	 *
	 */
	public void actualizarCostos(){
		if(getSaldo()!=null)
			setCosto(getCostoPromedio().multiply(getSaldo()));
		if(getMovimientos()!=null){
			setMovimientosCosto(getMovimientos().multiply(getCostoPromedio().amount()));
		}if(getInicial()!=null){
			setCostoInicial(getCostoPromedio().multiply(getInicial()));
		}
	}
	
	public CantidadMonetaria getCostoIntegradMaq(){
		return getCostoMaq().abs()
			.add(getGastosMaq().abs());
	}
	
	public CantidadMonetaria getCompras(){
		final CantidadMonetaria cxp=getCostoCxp();
		final CantidadMonetaria maq1=getCostoIntegradMaq();
		return cxp.add(maq1);		
	}
	
	public CantidadMonetaria getCostoDeVentas(){
		final CantidadMonetaria invIni=getCostoInicial();
		final CantidadMonetaria compras=getCompras();
		final CantidadMonetaria invFinal=getCosto();
		final CantidadMonetaria coms=getComsSA();
		return invIni.add(compras).add(coms).subtract(invFinal);
	}
	
	public double getUtilidad(){
		final BigDecimal cv=getCostoDeVentas().amount();
		//		final BigDecimal total=
		return 0;
	}

	public int compareTo(InventarioMensual o) {
		if(getClave().equals(o.getClave())){
			if(getYear()==o.getYear()){
				Integer m1=getMes();
				Integer m2=o.getMes();
				return m1.compareTo(m2);
			}else{
				Integer m1=getYear();
				Integer m2=o.getYear();
				return m1.compareTo(m2);
			}				
		}else{
			return getClave().compareTo(o.getClave());
		}
		
	}

	public BigDecimal getVentas() {
		return ventas;
	}
	public void setVentas(BigDecimal ventas) {
		this.ventas = ventas;
	}
	
	public BigDecimal getCostoDeVentasF(){
		return getCostoPromedio().multiply(getVentas()).amount();
	}

	public BigDecimal getComsSinAnalizar() {
		return comsSinAnalizar;
	}

	public void setComsSinAnalizar(BigDecimal comsSinAnalizar) {
		this.comsSinAnalizar = comsSinAnalizar;
	}
	
	public CantidadMonetaria getComsSA(){
		return getCostoPromedio().multiply(getComsSinAnalizar());
	}

	/**
	 * Movimientos en unidades durante el periodo
	 * 
	 * @return
	 */
	public BigDecimal getMovimientos() {
		return movimientos;
	}
	public void setMovimientos(BigDecimal movimientos) {
		this.movimientos = movimientos;
	}

	/**
	 * Importe o costo de los movimientos durante el periodo
	 * 
	 * @return
	 */
	public BigDecimal getMovimientosCosto() {
		return movimientosCosto;
	}
	public void setMovimientosCosto(BigDecimal movimientosImp) {
		this.movimientosCosto = movimientosImp;
	}
	
	

}
