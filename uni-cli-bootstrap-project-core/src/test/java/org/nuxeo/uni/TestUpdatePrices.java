package org.nuxeo.uni;

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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.PartialDeploy;
import org.nuxeo.uni.util.ProductUtils;

import com.sun.jna.platform.unix.X11.Visual;

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class, ProductFeature.class})
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.uni.uni-cli-bootstrap-project-core")
@PartialDeploy(bundle = "studio.extensions.nalkotob-SANDBOX", 
extensions = { org.nuxeo.runtime.test.runner.TargetExtensions.ContentModel.class })
public class TestUpdatePrices {

	@Inject
	protected CoreSession session;

	@Inject
	protected AutomationService automationService;

	@Test
	public void shouldUpdatePrices() throws OperationException {
		List<ProductAdapter> products = new ArrayList<>();
		List<VisualAdapter> visuals = new ArrayList<>();
		OperationContext ctx = new OperationContext(session);

		for(int i = 1; i < 10; i++) {
			DocumentModel docProduct = session.createDocumentModel("/", "doc"+i, "product");
			ProductAdapter product = docProduct.getAdapter(ProductAdapter.class);
			product.setPrice(i);
			docProduct = session.createDocument(docProduct);
			session.save();
			
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
			for(int j = 0; j < 3; j++) {
				DocumentModel docVisual = session.createDocumentModel("/", "visual"+j, "visual");
				docVisual.setPropertyValue(VisualAdapter.PRODUCT_ID_XPATH, docProduct.getId());
				docVisual = session.createDocument(docVisual);
				
				Map<String, Object> params = new HashMap<>();
				params.put("collection", docProduct);
				ctx.setInput(docVisual);
				automationService.run(ctx, "Document.AddToCollection", params);
				
				params.clear();
				params.put("schema", "productSchema");
				params.put("sourceId", docProduct.getId());
				automationService.run(ctx, "Document.CopySchema", params);
				
				visuals.add(docVisual.getAdapter(VisualAdapter.class));
			}
			
			products.add(product);
			product.doc = session.saveDocument(product.doc);
		}

		session.save();
		for(int i = 0; i < visuals.size(); i+=3) {
			visuals.get(i).refreshVisuals();
		}
		visuals.clear();

		String query = "SELECT * FROM visual WHERE ecm:mixinType != 'HiddenInNavigation' AND ecm:isProxy = 0 AND ecm:isVersion = 0 AND ecm:isTrashed = 0 "
				+ "ORDER BY " + ProductAdapter.PRICE_XPATH;
		DocumentModelList dml = session.query(query);
		for(DocumentModel d : dml) {
			visuals.add(d.getAdapter(VisualAdapter.class));
			System.out.println(ProductUtils.getFormattedPrice(d.getAdapter(VisualAdapter.class)));
		}
		
//		ctx.setInput(documents);
//		automationService.run(ctx, UpdatePrices.ID);

		for(int i = 1, j = 0; i < 28; i+=3) {
			j++;
			if (i < 10) {
				Assert.assertEquals(j*1.1, visuals.get(i-1).getPrice(), 0.001);
				Assert.assertEquals(j*1.1, visuals.get(i).getPrice(), 0.001);
				Assert.assertEquals(j*1.1, visuals.get(i+1).getPrice(), 0.001);
			}                       
			else if (i < 19) {      
				Assert.assertEquals(j*2, visuals.get(i-1).getPrice(), 0.001);
				Assert.assertEquals(j*2, visuals.get(i).getPrice(), 0.001);
				Assert.assertEquals(j*2, visuals.get(i+1).getPrice(), 0.001);
			}                       
			else {                  
				Assert.assertEquals(j*10, visuals.get(i-1).getPrice(), 0.001);
				Assert.assertEquals(j*10, visuals.get(i).getPrice(), 0.001);
				Assert.assertEquals(j*10, visuals.get(i+1).getPrice(), 0.001);
			}
		}
	}

}
