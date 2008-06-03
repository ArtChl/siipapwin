package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * 
 * @author Ruben Cancino
 * 
 */
public class InventarioMaq extends PersistentObject {

	private Date fecha = new Date();

	private EntradaDeMaterial entrada;

	private List<SalidaDeMaterial> traslados;

	private List<SalidaDeBobinas> salidaBobinas;

	private List<SalidaACorte> cortes;

	private List<EntradaDeHojas> entradasDeHojas;

	private List<SalidaDeHojas> salidaDeHojas;

	public List<SalidaACorte> getCortes() {
		return cortes;
	}

	public void setCortes(List<SalidaACorte> cortes) {
		this.cortes = cortes;
	}

	public EntradaDeMaterial getEntrada() {
		return entrada;
	}

	public void setEntrada(EntradaDeMaterial entrada) {
		this.entrada = entrada;
	}

	public List<EntradaDeHojas> getEntradasDeHojas() {
		return entradasDeHojas;
	}

	public void setEntradasDeHojas(List<EntradaDeHojas> entraDeHojas) {
		this.entradasDeHojas = entraDeHojas;
	}

	public List<SalidaDeBobinas> getSalidaBobinas() {
		return salidaBobinas;
	}

	public void setSalidaBobinas(List<SalidaDeBobinas> salidaBobinas) {
		this.salidaBobinas = salidaBobinas;
	}

	public List<SalidaDeHojas> getSalidaDeHojas() {
		return salidaDeHojas;
	}

	public void setSalidaDeHojas(List<SalidaDeHojas> salidaDeHojas) {
		this.salidaDeHojas = salidaDeHojas;
	}

	public List<SalidaDeMaterial> getTraslados() {
		return traslados;
	}

	public void setTraslados(List<SalidaDeMaterial> traslados) {
		this.traslados = traslados;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		Object old = this.fecha;
		this.fecha = fecha;
		getPropertyChangeSupport().firePropertyChange("fecha", old, fecha);
	}

	public BigDecimal getCortesVal() {
		BigDecimal c = BigDecimal.ZERO;
		if (getCortes() != null) {
			for (SalidaACorte s : getCortes()) {
				final Date forden = s.getOrden().getFecha();
				// System.out.println(forden);
				if (forden.compareTo(getFecha()) <= 0) {
					final Date fentrada = s.getEntradaReceptora().getFecha();
					// System.out.println(fentrada);
					if (fentrada.compareTo(getFecha()) <= 0)
						c = c.add(s.getKilos().abs());
				}
			}
		}
		return c;
	}

	public BigDecimal getProcesoVal() {
		BigDecimal c = BigDecimal.ZERO;
		if (getCortes() != null) {
			for (SalidaACorte s : getCortes()) {
				final Date forden = s.getOrden().getFecha();
				if (forden.compareTo(getFecha()) <= 0) {
					final Date fentrada = s.getEntradaReceptora().getFecha();
					if (fentrada.compareTo(getFecha()) > 0)
						c = c.add(s.getKilos().abs());
				}
			}
		}
		return c;
	}

	public BigDecimal getEntradasDeHojasVal() {
		BigDecimal red = BigDecimal.ZERO;
		if (getEntradasDeHojas() != null) {
			for (EntradaDeHojas e : getEntradasDeHojas()) {
				final Date fe = e.getFecha();
				if (fe.compareTo(getFecha()) <= 0) {
					red = red.add(e.getCantidad());
				}

			}
		}
		return red;
	}

	public BigDecimal getSalidaDeHojasVal() {
		BigDecimal res = BigDecimal.ZERO;
		if (getSalidaDeHojas() != null) {
			for (SalidaDeHojas s : getSalidaDeHojas()) {
				final Date fs = s.getFecha();
				if (fs.compareTo(getFecha()) <= 0) {
					res = res.add(s.getCantidad());
				}
			}
		}
		return res;
	}

	/** ** */

	private BigDecimal getEntradasDeHojasEnM2() {
		BigDecimal red = BigDecimal.ZERO;
		if (getEntradasDeHojas() != null) {
			for (EntradaDeHojas e : getEntradasDeHojas()) {
				final Date fe = e.getFecha();
				if (fe.compareTo(getFecha()) <= 0) {
					red = red.add(e.getMetros2());
				}

			}
		}
		return red;
	}

	private BigDecimal getSalidaDeHojasEnM2() {
		BigDecimal res = BigDecimal.ZERO;
		if (getSalidaDeHojas() != null) {
			for (SalidaDeHojas s : getSalidaDeHojas()) {
				final Date fs = s.getFecha();
				if (fs.compareTo(getFecha()) <= 0) {
					res = res.add(s.getMetros2());
				}
			}
		}
		return res;
	}

	/***/

	public BigDecimal getExitenciaKg() {
		return getEntrada().getKilos().subtract(getCortesVal()).subtract(
				getProcesoVal());
	}

	public BigDecimal getExistenciaMil() {
		return getEntradasDeHojasVal().subtract(getSalidaDeHojasVal());
	}

	public BigDecimal getExistenciaDeHojasEnM2() {
		return getEntradasDeHojasEnM2().subtract(getSalidaDeHojasEnM2());
	}

	/** Costos ** */

	public BigDecimal getPrecioPorKilo() {
		final BigDecimal val = new BigDecimal(getEntrada().getPrecioPorKilo());
		// return val.setScale(4,RoundingMode.HALF_EVEN);
		return val;
	}

	public BigDecimal getPrecioPorM2() {
		final BigDecimal val = new BigDecimal(getEntrada().getPrecioPorM2());
		// return val.setScale(4,RoundingMode.HALF_EVEN);
		return val;
	}

	/**
	 * Costo del inventario en materia prima
	 * 
	 * @return
	 */
	public BigDecimal getCostoInventarioKg() {
		return getExitenciaKg().multiply(getPrecioPorKilo());
	}

	/**
	 * Costo del inventario en proceso en Kg
	 * 
	 * @return
	 */
	public BigDecimal getCostoInventarioProcesoKg() {
		return getProcesoVal().multiply(getPrecioPorKilo());
	}

	
	/**
	 * Costo total del inventario
	 * 
	 * @return
	 */
	public BigDecimal getCostoTotal() {
		return getCostoInventarioKg()
			.add(getCostoInventarioProcesoKg())
			.add(getCostoInventarioMil().amount());

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		InventarioMaq i = (InventarioMaq) obj;
		return new EqualsBuilder().append(getEntrada().getId(),
				i.getEntrada().getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 35).append(getEntrada().getId())
				.toHashCode();
	}

	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("Entrada de Mat:", getEntrada().getId()).append(
						"Articulo:", getEntrada().getArticulo()).append(
						"Corte al:", df.format(getFecha())).append(
						"Entrada (Kg)", getEntrada().getKilos()).append(
						"Costo Ent (Kg)", getEntrada().getImporte()).append(
						"Cortes (Kg)", getCortesVal()).append("Proceso (Kg)",
						getProcesoVal()).append("Costo Inv proceso (Kg)",
						getCostoInventarioProcesoKg()).append("Existencia Kg",
						getExitenciaKg()).append("Costo Inv Kg",
						getCostoInventarioKg()).append("Entradas (MIL)",
						getEntradasDeHojasVal()).append("Salidas (MIL)",
						getSalidaDeHojasVal()).append("Existencia (MIL)",
						getExistenciaMil()).append("Costo Inv (Mil): ",
						getCostoInventarioMil()).toString();
	}

	/** * Costo de las existencia en millares* */
	
	/**
	 * Costo del inventario hojeado
	 * 
	 * @return
	 */
	public CantidadMonetaria getCostoInventarioMil() {
		return getCostoEntradasDeHojas().abs().subtract(getCostoSalidaDeHojas().abs());
	}


	private CantidadMonetaria getCostoEntradasDeHojas() {
		CantidadMonetaria red = CantidadMonetaria.pesos(0);
		if (getEntradasDeHojas() != null) {
			for (EntradaDeHojas e : getEntradasDeHojas()) {
				final Date fe = e.getFecha();
				if (fe.compareTo(getFecha()) <= 0) {
					red = red.add(e.getCosto().multiply(e.getCantidad()));
				}

			}
		}
		System.out.println("Costo entrada hojas" +red);
		return red;
	}

	private CantidadMonetaria getCostoSalidaDeHojas() {
		CantidadMonetaria res = CantidadMonetaria.pesos(0);
		if (getSalidaDeHojas() != null) {
			for (SalidaDeHojas s : getSalidaDeHojas()) {
				final Date fs = s.getFecha();
				if (fs.compareTo(getFecha()) <= 0) {					
					res = res.add(s.getCosto());
				}
			}
		}
		System.out.println("Costo salida hojas: "+res);
		return res;
	}

}
