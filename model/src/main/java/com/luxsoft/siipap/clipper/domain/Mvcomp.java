package com.luxsoft.siipap.clipper.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.MutableObject;





/** 
 * Maestro de Entradas y Salidas por Compras a proveedores
 * 
 * @author Ruben Cancino
 *
 */
public class Mvcomp extends MutableObject implements Serializable {

    private Long id;
    private Integer MVCSUCUR;
    private String MVCTIPO;
    private Long MVCNUMER;
    private Date MVCFECHA;
    private String MVCPROVEE;
    private String MVCNOMBPRO;
    private String MVCCOMEN;
    private long MVCSUCCOM;
    private String MVCTIPCOM;
    private long MVCNUMCOM;
    private Date MVCFECCOM;
    private long MVCFACREM;
    private Date MVCFECREM;
    private String MVCUSUAR;
    private Date MVCFEREAL;
    private long MVCNUMREC;
    
    private String periodo;
    private Date creado;
    
    
    
    public Set<Almace> partidas;


    public Mvcomp() {
    }

    public Long getId() {
        return this.id;
    }
    	
	public Long getMVCNUMER() {
		return MVCNUMER;
	}
	public void setMVCNUMER(Long mvcnumer) {
		MVCNUMER = mvcnumer;
	}
	
	/**
	 * Numero de la sucursal origen del movimiento
	 * @return
	 */
	public Integer getMVCSUCUR() {
		return MVCSUCUR;
	}
	public void setMVCSUCUR(Integer mvcsucur) {
		MVCSUCUR = mvcsucur;
	}
	
	
	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getMVCCOMEN() {
		return MVCCOMEN;
	}
	public void setMVCCOMEN(String mvccomen) {
		MVCCOMEN = mvccomen;
	}
	public long getMVCFACREM() {
		return MVCFACREM;
	}
	public void setMVCFACREM(long mvcfacrem) {
		MVCFACREM = mvcfacrem;
	}
	public Date getMVCFECCOM() {
		return MVCFECCOM;
	}
	public void setMVCFECCOM(Date mvcfeccom) {
		MVCFECCOM = mvcfeccom;
	}
	public Date getMVCFECHA() {
		return MVCFECHA;
	}
	public void setMVCFECHA(Date mvcfecha) {
		MVCFECHA = mvcfecha;
	}
	public Date getMVCFECREM() {
		return MVCFECREM;
	}
	public void setMVCFECREM(Date mvcfecrem) {
		MVCFECREM = mvcfecrem;
	}
	public Date getMVCFEREAL() {
		return MVCFEREAL;
	}
	public void setMVCFEREAL(Date mvcfereal) {
		MVCFEREAL = mvcfereal;
	}
	public String getMVCNOMBPRO() {
		return MVCNOMBPRO;
	}
	public void setMVCNOMBPRO(String mvcnombpro) {
		MVCNOMBPRO = mvcnombpro;
	}
	public long getMVCNUMCOM() {
		return MVCNUMCOM;
	}
	public void setMVCNUMCOM(long mvcnumcom) {
		MVCNUMCOM = mvcnumcom;
	}
	
	public String getMVCPROVEE() {
		return MVCPROVEE;
	}
	public void setMVCPROVEE(String mvcprovee) {
		MVCPROVEE = mvcprovee;
	}
	public long getMVCSUCCOM() {
		return MVCSUCCOM;
	}
	public void setMVCSUCCOM(long mvcsuccom) {
		MVCSUCCOM = mvcsuccom;
	}
	
	public String getMVCTIPCOM() {
		return MVCTIPCOM;
	}
	public void setMVCTIPCOM(String mvctipcom) {
		MVCTIPCOM = mvctipcom;
	}
	public String getMVCTIPO() {
		return MVCTIPO;
	}
	public void setMVCTIPO(String mvctipo) {
		MVCTIPO = mvctipo;
	}
	public String getMVCUSUAR() {
		return MVCUSUAR;
	}
	public void setMVCUSUAR(String mvcusuar) {
		MVCUSUAR = mvcusuar;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	
	
	public long getMVCNUMREC() {
		return MVCNUMREC;
	}

	public void setMVCNUMREC(long mvcnumrec) {
		MVCNUMREC = mvcnumrec;
	}

	public Set<Almace> getPartidas() {
		if(partidas==null)
			partidas=new HashSet<Almace>();
		return partidas;
	}
	
	@SuppressWarnings("unused")
	private void setPartidas(Set<Almace> partidas) {
		this.partidas = partidas;
	}
	
	public void agregarAlmace(final Almace a){
		a.setMvcomp(this);
		getPartidas().add(a);
	}
	
	public boolean equals(Object obj) {
        boolean equals=false;
        if(obj!=null && Mvcomp.class.isAssignableFrom(obj.getClass())){
            Mvcomp name2=(Mvcomp)obj;
            equals=new EqualsBuilder()
                     .append(MVCSUCUR,name2.getMVCSUCUR())
                     .append(MVCTIPO,name2.getMVCTIPO())
					 .append(MVCNUMER,name2.getMVCNUMER())
                     .isEquals();
        }
        return equals;
    }

    public int hashCode() {        
        return new HashCodeBuilder(17,37)
			.append(MVCSUCUR)
			.append(MVCTIPO)
			.append(MVCNUMER)
        	.toHashCode();
    }

	
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
            .append(this.MVCSUCUR)
            .append(this.MVCTIPO)
            .append(this.MVCNUMER)
            .append(this.MVCPROVEE)
            .append(this.MVCNOMBPRO)
            .append(this.MVCFECHA)
            .append(this.MVCFACREM)
            .toString();
    }

}
