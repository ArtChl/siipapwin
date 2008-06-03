package com.luxsoft.siipap.cxc.domain;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.VentaDet;
import com.luxsoft.utils.domain.MutableObject;

/**
 * Provision para descuentos por partida de venta
 * 
 * @author Ruben Cancino
 *
 */
public class ProvisionDet extends MutableObject{
	
	private Long id;
	private VentaDet ventaDet;
	private Provision provision;	
	private DescuentoPorArticulo descArticulo;
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	
	/**
	 * Importe de la provision sin contar el descuento 2, se ocupa como sugerencia en los pagos
	 */
	private CantidadMonetaria importeDesc1=CantidadMonetaria.pesos(0);
	
	//Datos del articulo
	private Articulo articulo;
	private String clave;
	private String familia;
	
	private double descuento;
	
	
	
	public ProvisionDet() {		
	}
	
	public ProvisionDet(final Provision p,final VentaDet ventaDet) {		
		this.ventaDet = ventaDet;
		setArticulo(ventaDet.getArticulo());
		setClave(ventaDet.getClave());		
		//setFamilia(ventaDet.getArticulo().getFamilia().getClave());
		setProvision(p);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Provision getProvision() {
		return provision;
	}
	public void setProvision(Provision provision) {
		this.provision = provision;
	}
	public VentaDet getVentaDet() {
		return ventaDet;
	}
	public void setVentaDet(VentaDet ventaDet) {
		this.ventaDet = ventaDet;
	}
	
	public DescuentoPorArticulo getDescArticulo() {
		return descArticulo;
	}
	public void setDescArticulo(DescuentoPorArticulo descArticulo) {
		this.descArticulo = descArticulo;
	}	
	
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		this.importe = importe;
	}
	
	
	
	public CantidadMonetaria getImporteDesc1() {
		return importeDesc1;
	}

	public void setImporteDesc1(CantidadMonetaria importeDesc1) {
		this.importeDesc1 = importeDesc1;
	}

	/**
	 * Calcula el importe de esta partida en funcion de los descuentos de la provision padre y los descuentos
	 * por articulo que puediera tener
	 * 
	 * @return
	 */
	public CantidadMonetaria calcularImporte(){		
		if(!getProvision().esValido()){
			CantidadMonetaria imp=new CantidadMonetaria(0.0,getProvision().getVenta().getTotal().getCurrency());
			setImporte(imp);
			return imp;
		}
		
		final CantidadMonetaria impV=new CantidadMonetaria(
				//Math.abs(getVentaDet().getImporteNeto()),getVentaDet().getVenta().getTotal().currency()
				getVentaDet().getImporteNeto(),getVentaDet().getVenta().getTotal().currency()
				);
		
		if(getDescArticulo()!=null){
			double desc1=getDescArticulo().getDescuento();
			//desc1=desc1-getProvision().getCargoCalculado();
			setDescuento(desc1);
			CantidadMonetaria monto=impV.multiply(desc1/100);			
			setImporte(monto);
			setImporteDesc1(monto);
			return monto;
			
		}else{			
			
			double desc1=getProvision().getDescuento1();
			//desc1=desc1-getProvision().getCargoCalculado();
			
			double desc2=getProvision().getDescuento2();
			
			CantidadMonetaria monto=impV.multiply(desc1/100);
			setImporteDesc1(monto);
			if(desc2>0){				
				desc1=(100-(100-desc1))+((100-desc1)*desc2/100);
				CantidadMonetaria nvoImporte=impV.subtract(monto);
				nvoImporte=nvoImporte.multiply(desc2/100);				
				monto=monto.add(nvoImporte);
			}
			setDescuento(desc1);
			setImporte(monto);
			return monto;
		}
			
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((ventaDet == null) ? 0 : ventaDet.hashCode());
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
		final ProvisionDet other = (ProvisionDet) obj;
		if (ventaDet == null) {
			if (other.ventaDet != null)
				return false;
		} else if (!ventaDet.equals(other.ventaDet))
			return false;
		return true;
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
	public String getFamilia() {
		return familia;
	}
	public void setFamilia(String familia) {
		this.familia = familia;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	
	
	
	

}
