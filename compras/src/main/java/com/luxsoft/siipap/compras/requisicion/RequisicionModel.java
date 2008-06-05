package com.luxsoft.siipap.compras.requisicion;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.luxsoft.siipap.compras.domain.Requisicion;

public class RequisicionModel implements IRequisicionModel{
	
	private final PresentationModel delegate;
	private final ValidationResultModel validationModel;
	
	public RequisicionModel(){
		this.delegate=new PresentationModel(new Requisicion());
		validationModel=new DefaultValidationResultModel();
	}

	public ComponentValueModel getComponentModel(String property) {
		return delegate.getComponentModel(property);
	}

	public ValueModel getModel(String property) {
		return delegate.getModel(property);
	}

	public ValidationResultModel getValidationModel() {
		 return validationModel;
	}

	public void validate() {
		
	}

}
