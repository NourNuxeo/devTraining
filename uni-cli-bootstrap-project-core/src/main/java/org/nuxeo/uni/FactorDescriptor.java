package org.nuxeo.uni;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("factor")
public class FactorDescriptor {
	
	public FactorDescriptor() {}
	
	public FactorDescriptor(String distributorId, double factorValue, String location) {
		super();
		this.distributorId = distributorId;
		this.factorValue = factorValue;
		this.location = location;
	}

	@XNode("@distributorId")
	public String distributorId;
	
	@XNode("@factorValue")
	public double factorValue;
	
	@XNode("@location")
	public String location;
	
}
