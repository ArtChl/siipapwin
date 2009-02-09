package com.luxsoft.siipap.swing.utils;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.controls.BooleanCellRenderer;

public class Renderers {
	
	public static TableCellRenderer buildIntegerRenderer(){
		return new IntegerRenderer();
	}
	
	public static TableCellRenderer buildDefaultNumberRenderer(){
		return new DefaultNumberRenderer();
	}
	public static TableCellRenderer buildRightAllignRenderer(){
		return new RightAlligmentRenderer();
	}
	
	public static TableCellRenderer getCantidadMonetariaTableCellRenderer(){
		return new CantidadMonetariaRenderer();
	}
	public static TableCellRenderer getCantidadNormalTableCellRenderer(){
		return new CantidadMonetariaStdRenderer();
	}
	
	public static TableCellRenderer getUtilidadPorcRenderer(){
		return new UtilidadPorcentageRenderer();
	}
	public static TableCellRenderer getPorcentageRenderer(){
		return new PorcentageRenderer();
	}
	
	public static TableCellRenderer getBooleanRenderer(){
		return new BooleanCellRenderer();
	}
	
	public static  class IntegerRenderer extends DefaultTableCellRenderer{
		
		private NumberFormat nf;

		public IntegerRenderer() {
			super();
			nf=NumberFormat.getInstance();
			nf.setMaximumFractionDigits(0);
			nf.setGroupingUsed(false);
		}

		@Override
		protected void setValue(Object value) {			
			if(value instanceof CantidadMonetaria){
				CantidadMonetaria monto=(CantidadMonetaria)value;
				long  vv=monto.getAmount().longValue();
				long nv=vv;
				setValue(nf.format(nv));
				setHorizontalAlignment(JLabel.RIGHT);
			}else if(value instanceof Number){
				Number val=(Number)value;
				long  vv=val.longValue();
				long nv=vv;
				setValue(nf.format(nv));
				setHorizontalAlignment(JLabel.RIGHT);
				
			}else{
				setHorizontalAlignment(JLabel.LEFT);
				super.setValue(value);
			}
				
		}
		
		
	}
	
	public static  class DefaultNumberRenderer extends DefaultTableCellRenderer{
		
		private NumberFormat nf;

		public DefaultNumberRenderer() {
			super();
			nf=NumberFormat.getInstance();
			nf.setMaximumFractionDigits(0);
		}

		@Override
		protected void setValue(Object value) {			
			if(value instanceof CantidadMonetaria){
				CantidadMonetaria monto=(CantidadMonetaria)value;
				long  vv=monto.getAmount().longValue();
				long nv=vv/1000;
				setValue(nf.format(nv));
				setHorizontalAlignment(JLabel.RIGHT);
			}else if(value instanceof Number){
				Number val=(Number)value;
				long  vv=val.longValue();
				long nv=vv/1000;
				setValue(nf.format(nv));
				setHorizontalAlignment(JLabel.RIGHT);
				
			}else{
				setHorizontalAlignment(JLabel.LEFT);
				super.setValue(value);
			}
				
		}
		
		
	}
	
	public static  class CantidadMonetariaRenderer extends DefaultTableCellRenderer{
		
		private NumberFormat nf;

		public CantidadMonetariaRenderer() {
			super();
			nf=NumberFormat.getCurrencyInstance();
			nf.setMaximumFractionDigits(2);
		}

		@Override
		protected void setValue(Object value) {			
			if(value instanceof CantidadMonetaria){
				CantidadMonetaria monto=(CantidadMonetaria)value;				
				setValue(nf.format(monto.amount().doubleValue()));
				setHorizontalAlignment(JLabel.RIGHT);
			}else{
				setHorizontalAlignment(JLabel.LEFT);
				super.setValue(value);
			}
				
		}
		
		
	}
	
	private static class RightAlligmentRenderer extends DefaultTableCellRenderer{

		public RightAlligmentRenderer() {
			super();
			setHorizontalAlignment(JLabel.RIGHT);
		}		
		
		
	}
	
	public static class UtilidadPorcentageRenderer extends DefaultTableCellRenderer{
		
		private NumberFormat nf;

		public UtilidadPorcentageRenderer() {
			super();
			setHorizontalAlignment(JLabel.RIGHT);
			nf=NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			
		}

		@Override
		protected void setValue(Object value) {
			if(value instanceof Number){
				Number nn=(Number)value;
				double dd=nn.doubleValue();
				if(dd<0)
					setForeground(Color.red);
				else
					setForeground(Color.black);
				setValue(nf.format(dd));
			}else
				super.setValue(value);
		}
		
		
		
	}
	
	public static class PorcentageRenderer extends DefaultTableCellRenderer{
		
		private NumberFormat nf;

		public PorcentageRenderer() {
			super();
			setHorizontalAlignment(JLabel.RIGHT);
			//nf=NumberFormat.getNumberInstance();
			nf=NumberFormat.getPercentInstance();
			DecimalFormat df=(DecimalFormat)nf;
			df.setMultiplier(1);
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);
			
		}

		@Override
		protected void setValue(Object value) {
			if(value instanceof Number){
				Number nn=(Number)value;
				double dd=nn.doubleValue();				
				setValue(nf.format(dd));
			}else
				super.setValue(value);
		}
		
		
		
	}
	
	public static  class CantidadMonetariaStdRenderer extends DefaultTableCellRenderer{
		
		private NumberFormat nf;

		public CantidadMonetariaStdRenderer() {
			super();
			nf=NumberFormat.getCurrencyInstance();
			nf.setMaximumFractionDigits(2);
		}

		@Override
		protected void setValue(Object value) {			
			if(value instanceof Number){
				double monto=((Number)value).doubleValue();				
				setValue(nf.format(monto));
				setHorizontalAlignment(JLabel.RIGHT);
			}else{
				setHorizontalAlignment(JLabel.LEFT);
				super.setValue(value);
			}
				
		}
		
		
	}
	
	public static  class BigDecimalStdRenderer extends DefaultTableCellRenderer{
		
		private DecimalFormat nf;

		public BigDecimalStdRenderer() {
			super();
			nf=(DecimalFormat)NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);
		}

		@Override
		protected void setValue(Object value) {			
			if(value instanceof Number){
				double monto=((Number)value).doubleValue();				
				setValue(nf.format(monto));
				setHorizontalAlignment(JLabel.RIGHT);
			}else{
				setHorizontalAlignment(JLabel.LEFT);
				super.setValue(value);
			}
				
		}
		
		
	}

}
