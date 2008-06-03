package com.luxsoft.siipap.ventas.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;
import org.hibernate.validator.Range;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;


public class Devolucion extends PersistentObject{
	
	private Venta venta;
	
	private Date fecha=currentTime();
	
	private Long numero;
	
	private String cliente;
	
	@Range (min=0,max=99)
	private double descuento1=0;
	
	@Range (min=0,max=99)
	private double descuento2=0;
	
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria impuesto=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria total=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria impCortes=CantidadMonetaria.pesos(0);
	
	private Integer cortes=new Integer(0);
	
	/**
	 * 
	 */
	@Length (max=50,message="El tamaño máximo del comentario es de 50 caracteres")
	private String comentario;
	
	private Date creado=currentTime();
	
	private int version;
	
	private int sucursal;
	private String tipoVenta;
	private String serieVenta;
	private long numeroVenta;
	
	
	private Date fechaReal;	
	
	
	
	private int mes;
	
	private int year;
	
	private Set<DevolucionDet> partidas;
	
		
	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public CantidadMonetaria getImporte() {
		return importe;
	}

	public void setImporte(CantidadMonetaria importe) {
		Object oldValue=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe", oldValue, importe);
	}

	public CantidadMonetaria getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(CantidadMonetaria impuesto) {
		Object oldValue=this.impuesto;
		this.impuesto = impuesto;
		getPropertyChangeSupport().firePropertyChange("impuesto", oldValue, impuesto);
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public CantidadMonetaria getTotal() {
		return total;
	}

	public void setTotal(CantidadMonetaria total) {
		Object oldValue=this.total;
		this.total = total;
		getPropertyChangeSupport().firePropertyChange("total", oldValue, total);
	}

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		Devolucion v=(Devolucion)obj;
		return new EqualsBuilder()
		.append(getSucursal(),v.getSucursal())
		.append(getNumero(),v.getNumero())
		
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getSucursal())
		.append(getNumero())		
		.toHashCode();
	}
	
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Id:",getId())
		.append("Devolucion:",getNumero())
		.append("Sucursal: "+getSucursal())	
		.append("Venta: "+getNumeroVenta())		
		.append("Total: "+getTotal())
		.append("Fecha"+getFecha())
		.append("Year",getYear())
		.append("Mes",getMes())
		.toString();	
	}

	public Set<DevolucionDet> getPartidas() {
		if(partidas==null)
			partidas=new HashSet<DevolucionDet>();
		return partidas;
	}

	public void setPartidas(Set<DevolucionDet> partidas) {
		this.partidas = partidas;
	}
	
	public void addPartida(DevolucionDet det){
		det.setDevolucion(this);
		getPartidas().add(det);
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		Object oldValue=this.comentario;
		this.comentario = comentario;
		getPropertyChangeSupport().firePropertyChange("comentario", oldValue, comentario);
	}

	public Date getFechaReal() {
		return fechaReal;
	}

	public void setFechaReal(Date fechaReal) {
		this.fechaReal = fechaReal;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	

	public String getSerieVenta() {
		return serieVenta;
	}

	public void setSerieVenta(String serieVenta) {
		this.serieVenta = serieVenta;
	}

	public int getSucursal() {
		return sucursal;
	}

	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}

	public String getTipoVenta() {
		return tipoVenta;
	}

	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public long getNumeroVenta() {
		return numeroVenta;
	}

	public void setNumeroVenta(long numeroVenta) {
		this.numeroVenta = numeroVenta;
	}
	
	
	public void actualizarImporte(){		
		CantidadMonetaria importePartidas=calcularImporteDePartidas().add(getImpCortes());
		if(venta.getSerie().equals("E"))		
			importePartidas.add(getImpCortes());
		CantidadMonetaria subTotal=MonedasUtils.aplicarDescuentosEnCascada(importePartidas, getDescuento1()/100,getDescuento2()/100);
		if(!venta.getSerie().equals("E"))
			subTotal.add(getImpCortes());
		setImporte(subTotal);
		setImpuesto(MonedasUtils.calcularImpuesto(subTotal));
		setTotal(MonedasUtils.calcularTotal(subTotal)); 
	}
	
	public CantidadMonetaria calcularImporteDePartidas(){
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		for(DevolucionDet det:getPartidas()){
			det.actualizar();			
			importe=importe.add(CantidadMonetaria.pesos(det.getImporte()));			
		}		
		return importe.abs();
	}
	
	public Integer getCortes() {
		return cortes;
	}
	public void setCortes(Integer cortes) {
		Object oldValue=this.cortes;
		this.cortes = cortes;
		getPropertyChangeSupport().firePropertyChange("cortes", oldValue, cortes);
		if(cortes!=null)
			setImpCortes(getVenta().getPrecioCorte().multiply(getCortes()));
	}

	public CantidadMonetaria getImpCortes() {
		return impCortes;
	}
	public void setImpCortes(CantidadMonetaria impCortes) {
		Object oldValue=this.impCortes;
		this.impCortes = impCortes;
		getPropertyChangeSupport().firePropertyChange("impCortes", oldValue, impCortes);
	}

	public double getDescuento1() {
		return descuento1;
	}

	public void setDescuento1(double descuento1) {
		Object oldValue=this.descuento1;
		this.descuento1 = descuento1;
		getPropertyChangeSupport().firePropertyChange("descuento1", oldValue, descuento1);
	}

	public double getDescuento2() {
		return descuento2;
	}
	public void setDescuento2(double descuento2) {
		double oldValue=this.descuento2;
		this.descuento2 = descuento2;
		getPropertyChangeSupport().firePropertyChange("descuento2", oldValue, descuento2);
	}
	
	/**
	 * Actualiza los datos generales de las partidas
	 *
	 */
	public void updateData(){
		for(DevolucionDet det:getPartidas()){
			det.setImporte(det.getCantidad()*det.getVentaDet().getPrecioFacturado());
		}
	}
	
	public double getDescuentoNeto(){
		double totalAntes=0;
		for(DevolucionDet det:getPartidas()){
			totalAntes=totalAntes+=det.getCantidad()*det.getVentaDet().getPrecioFacturado();
		}		
		totalAntes=totalAntes+getImpCortes().amount().doubleValue();
		double descNeto=Math.abs(getImporte().amount().doubleValue()/totalAntes);		
		double neto= (1-descNeto)*-100;
		System.out.println("Total partidas: "+totalAntes);
		System.out.println("Total c/desc: "+getImporte());
		System.out.println("Descuento 1:"+getDescuento1());
		System.out.println("Descuento 2:"+getDescuento2());
		System.out.println("Desc neto:"+neto);
		return neto;
		
	}

}
