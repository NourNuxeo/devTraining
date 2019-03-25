package org.nuxeo.uni;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("factor")
public class FactorDescriptor {
	
	@XNode("@distributorId")
	public String distributorId;
	
	@XNode("@value")
	public double value;
	
}
