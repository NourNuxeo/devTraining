package org.nuxeo.uni;

import java.util.HashMap;

import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class HelloServiceImpl extends DefaultComponent implements HelloService {

	private HashMap<String, FactorDescriptor> factors;

	@Override
	public String helloWorld() {
		return "Hello World!";
	}
	
	@Override
	public double computePrice(ProductAdapter product) {
		return product.getPrice();
	}
	
	@Override
	public void activate(ComponentContext context) {
		factors = new HashMap<String, FactorDescriptor>();
	}
	
	@Override
	public void deactivate(ComponentContext context) {
		factors = null;
	}
	
	@Override
	public void registerContribution(Object contribution, String xp, ComponentInstance component) {
		FactorDescriptor factor = (FactorDescriptor) contribution;
		if (factor.distributorId != null) {
			factors.put(factor.distributorId, factor);
		}
	}

	@Override
	public double computeContributedPrice(ProductAdapter product) {
		return product.getPrice() * factors.get(product.getDistributorId()).value;
	}
}
