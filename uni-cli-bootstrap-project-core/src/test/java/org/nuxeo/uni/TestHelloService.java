package org.nuxeo.uni;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
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
public class TestHelloService {
	
//	static Log logger = LogFactory.getLog(TestCoolOperation.class);
	
	@Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void serviceShouldHello() {
    	HelloService service = Framework.getService(HelloService.class);
    	Assert.assertNotNull(service);
    	Assert.assertEquals("Hello World!", service.helloWorld());
    }
    
    @Test
    public void getPriceTest() {
    	DocumentModel doc = session.createDocumentModel("/", "testProduct", "product");
    	doc.setPropertyValue("productSchema:price", 42);
    	
    	HelloService service = Framework.getService(HelloService.class);
    	
    	ProductAdapter product = doc.getAdapter(ProductAdapter.class);
    	Assert.assertEquals(42, service.getPrice(product), 0.00001);
    }
}
