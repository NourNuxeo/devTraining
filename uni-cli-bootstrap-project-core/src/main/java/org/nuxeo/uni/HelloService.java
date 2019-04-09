package org.nuxeo.uni;

import java.util.Map;

public interface HelloService {
	String helloWorld();
	double getPrice(ProductAdapter product);
	double computePrice(ProductAdapter product);
	Map<String, FactorDescriptor> getDistributors();
}
