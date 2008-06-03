package com.luxsoft.siipap.clipper.domain;

import java.util.Date;

public class Carter {
	
	private Long id;

	private int	CARNUMERO;
	private String	CARPOSNEG;
	private double	CARIDENOPE;
	private double	CARNUMFIS;
	private Date	CARFECHA;
	private String	CARTIPO;
	private String	CARSERIE;
	private double	CARSUCURSA;
	private String	CARCLAVCLI;
	private String	CARNOMBCLI;
	private String	CARCLAVSOC;
	private double	CARIMPORTE;
	private double	CARSALDO;
	private Date	CARVTO;
	private double	CARDIAREVI;
	private double	CARDIAPAGO;
	private double	CARPLAZO;
	private double	CARCLAVVEN;
	private double	CARCLAVCOB;
	private String	CARSTATUS;
	private Date	CARFECREVI;
	private String	CARCONMANI;
	private double	CARIMPMANI;
	private int MES;
	private int YEAR;
	
	public String getCARCLAVCLI() {
		return CARCLAVCLI;
	}
	public void setCARCLAVCLI(String carclavcli) {
		CARCLAVCLI = carclavcli;
	}
	public double getCARCLAVCOB() {
		return CARCLAVCOB;
	}
	public void setCARCLAVCOB(double carclavcob) {
		CARCLAVCOB = carclavcob;
	}
	public String getCARCLAVSOC() {
		return CARCLAVSOC;
	}
	public void setCARCLAVSOC(String carclavsoc) {
		CARCLAVSOC = carclavsoc;
	}
	public double getCARCLAVVEN() {
		return CARCLAVVEN;
	}
	public void setCARCLAVVEN(double carclavven) {
		CARCLAVVEN = carclavven;
	}
	public String getCARCONMANI() {
		return CARCONMANI;
	}
	public void setCARCONMANI(String carconmani) {
		CARCONMANI = carconmani;
	}
	public double getCARDIAPAGO() {
		return CARDIAPAGO;
	}
	public void setCARDIAPAGO(double cardiapago) {
		CARDIAPAGO = cardiapago;
	}
	public double getCARDIAREVI() {
		return CARDIAREVI;
	}
	public void setCARDIAREVI(double cardiarevi) {
		CARDIAREVI = cardiarevi;
	}
	public Date getCARFECHA() {
		return CARFECHA;
	}
	public void setCARFECHA(Date carfecha) {
		CARFECHA = carfecha;
	}
	public Date getCARFECREVI() {
		return CARFECREVI;
	}
	public void setCARFECREVI(Date carfecrevi) {
		CARFECREVI = carfecrevi;
	}
	public double getCARIDENOPE() {
		return CARIDENOPE;
	}
	public void setCARIDENOPE(double caridenope) {
		CARIDENOPE = caridenope;
	}
	public double getCARIMPMANI() {
		return CARIMPMANI;
	}
	public void setCARIMPMANI(double carimpmani) {
		CARIMPMANI = carimpmani;
	}
	public double getCARIMPORTE() {
		return CARIMPORTE;
	}
	public void setCARIMPORTE(double carimporte) {
		CARIMPORTE = carimporte;
	}
	public String getCARNOMBCLI() {
		return CARNOMBCLI;
	}
	public void setCARNOMBCLI(String carnombcli) {
		CARNOMBCLI = carnombcli;
	}
	
	public int getCARNUMERO() {
		return CARNUMERO;
	}
	public void setCARNUMERO(int carnumero) {
		CARNUMERO = carnumero;
	}
	public void setMES(int mes) {
		MES = mes;
	}
	public void setYEAR(int year) {
		YEAR = year;
	}
	public double getCARNUMFIS() {
		return CARNUMFIS;
	}
	public void setCARNUMFIS(double carnumfis) {
		CARNUMFIS = carnumfis;
	}
	public double getCARPLAZO() {
		return CARPLAZO;
	}
	public void setCARPLAZO(double carplazo) {
		CARPLAZO = carplazo;
	}
	public String getCARPOSNEG() {
		return CARPOSNEG;
	}
	public void setCARPOSNEG(String carposneg) {
		CARPOSNEG = carposneg;
	}
	public double getCARSALDO() {
		return CARSALDO;
	}
	public void setCARSALDO(double carsaldo) {
		CARSALDO = carsaldo;
	}
	public String getCARSERIE() {
		return CARSERIE;
	}
	public void setCARSERIE(String carserie) {
		CARSERIE = carserie;
	}
	public String getCARSTATUS() {
		return CARSTATUS;
	}
	public void setCARSTATUS(String carstatus) {
		CARSTATUS = carstatus;
	}
	public double getCARSUCURSA() {
		return CARSUCURSA;
	}
	public void setCARSUCURSA(double carsucursa) {
		CARSUCURSA = carsucursa;
	}
	public String getCARTIPO() {
		return CARTIPO;
	}
	public void setCARTIPO(String cartipo) {
		CARTIPO = cartipo;
	}
	public Date getCARVTO() {
		return CARVTO;
	}
	public void setCARVTO(Date carvto) {
		CARVTO = carvto;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getMES() {
		return MES;
	}
	public int getYEAR() {
		return YEAR;
	}
	
	

	
	
	
}
