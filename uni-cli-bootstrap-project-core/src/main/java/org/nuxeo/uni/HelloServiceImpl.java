package org.nuxeo.uni;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class HelloServiceImpl extends DefaultComponent implements HelloService {

	private static final String DISTRIBUTORS_DIR_NAME = "distributors";
	private HashMap<String, FactorDescriptor> factors;
	
	@Override
	public Map<String, FactorDescriptor> getDistributors() {
		return factors;
	}

	@Override
	public String helloWorld() {
		return "Hello World!";
	}
	
	@Override
	public double getPrice(ProductAdapter product) {
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
	
	private void persistDistributor(FactorDescriptor factor) {
		DirectoryService dirService = Framework.getService(DirectoryService.class);
		Session session = dirService.open(DISTRIBUTORS_DIR_NAME);
		if(!session.hasEntry(factor.distributorId)) {
			Map<String, Object> params = new HashMap<>();
			params.put("name", factor.distributorId);
			params.put("factor", factor.factorValue);
			params.put("location ", factor.location);
			session.createEntry(params);
		}
	}

	@Override
	public double computePrice(ProductAdapter product) {
		return product.getPrice() * factors.get(product.getDistributorId()).factorValue;
	}
}
