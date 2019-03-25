package org.nuxeo.uni;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.PartialDeploy;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.uni.uni-cli-bootstrap-project-core")
@PartialDeploy(bundle = "studio.extensions.nalkotob-SANDBOX", 
extensions = { org.nuxeo.runtime.test.runner.TargetExtensions.ContentModel.class })
public class TestUpdatePrices {

	@Inject
	protected CoreSession session;

	@Inject
	protected AutomationService automationService;

//	@Test
//	public void shouldCallTheOperation() throws OperationException {
//		OperationContext ctx = new OperationContext(session);
//		DocumentModel doc = (DocumentModel) automationService.run(ctx, UpdatePrices.ID);
//		assertEquals("/", doc.getPathAsString());
//	}
//
//	@Test
//	public void shouldCallWithParameters() throws OperationException {
//		final String path = "/default-domain";
//		OperationContext ctx = new OperationContext(session);
//		Map<String, Object> params = new HashMap<>();
//		params.put("path", path);
//		DocumentModel doc = (DocumentModel) automationService.run(ctx, UpdatePrices.ID, params);
//		assertEquals(path, doc.getPathAsString());
//	}

	@Test
	public void shouldUpdatePrices() throws OperationException {
		List<ProductAdapter> products = new ArrayList<>();
		for(int i = 1; i < 10; i++) {
			DocumentModel doc = session.createDocumentModel("/", "doc"+i, "product");
			ProductAdapter product = doc.getAdapter(ProductAdapter.class);
			product.setPrice(i);
			switch (i) {
			case 1:
			case 2:
			case 3:
				product.setDistributor("coolGuys", "Creuse");
				break;
			case 4:
			case 5:
			case 6:
				product.setDistributor("richGuys", "Paris");
				break;
			case 7:
			case 8:
			case 9:
				product.setDistributor("apple", "Cuppertino");
			default:
				break;
			}
			product.doc = session.createDocument(product.doc);
			products.add(product);
		}

		OperationContext ctx = new OperationContext(session);
		ctx.setInput(products);
		automationService.run(ctx, UpdatePrices.ID);

		for(int i = 1; i < 10; i++) {
			ProductAdapter product = products.get(i-1);
			switch (i) {
			case 1:
			case 2:
			case 3:
				Assert.assertEquals(i*1.1, product.getPrice(), 0.001);
				break;
			case 4:
			case 5:
			case 6:
				Assert.assertEquals(i*2, product.getPrice(), 0.001);
				break;
			case 7:
			case 8:
			case 9:
				Assert.assertEquals(i*10, product.getPrice(), 0.001);
			default:
				break;
			}
		}
	}

	//    @Test
	//    public void shouldDoublePrices() throws OperationException {
	//    	List<ProductAdapter> products = new ArrayList<>();
	//    	for(int i = 1; i <= 10; i++) {
	//    		DocumentModel doc = session.createDocumentModel("/", "doc"+i, "product");
	//    		ProductAdapter product = doc.getAdapter(ProductAdapter.class);
	//    		product.setPrice(i);
	//    		product.doc = session.createDocument(product.doc);
	//    		products.add(product);
	//    	}
	//    	
	//    	OperationContext ctx = new OperationContext(session);
	//    	ctx.setInput(products);
	//    	automationService.run(ctx, UpdatePrices.ID);
	//    	
	//    	for(int i = 1; i <= 10; i++) {
	//    		Assert.assertEquals(i*2, products.get(i-1).getPrice(), 0.001);
	//    	}
	//    }
}
