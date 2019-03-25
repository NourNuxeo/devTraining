package org.nuxeo.uni;

public interface HelloService {
	String helloWorld();
	double computePrice(ProductAdapter product);
	double computeContributedPrice(ProductAdapter product);
}
