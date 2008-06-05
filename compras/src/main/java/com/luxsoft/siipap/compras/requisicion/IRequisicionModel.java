package com.luxsoft.siipap.compras.requisicion;

import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResultModel;

public interface IRequisicionModel {
	
	
	public ValueModel getModel(String property);
	
	public ComponentValueModel getComponentModel(String property);
	
	public ValidationResultModel getValidationModel();
	
	public void validate();

}
