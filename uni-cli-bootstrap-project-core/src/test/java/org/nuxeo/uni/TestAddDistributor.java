package org.nuxeo.uni;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;


@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class, ProductFeature.class})
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.uni.uni-cli-bootstrap-project-core")
public class TestAddDistributor {

	@Inject
	protected CoreSession session;

	@Inject
	protected AutomationService automationService;

	private Document docXML;

	@Before
	public void saveBaseXML() {
		SAXReader reader = new SAXReader();
		try {
			docXML = reader.read(this.getClass().getClassLoader().getResourceAsStream("OSGI-INF/cooloperation-service-contrib.xml"));
		} catch (DocumentException e) {
			System.out.println("couldn't read Resource");
			e.printStackTrace();
		}
	}

	@After
	public void restoreXML() throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new PrintWriter(this.getClass().getClassLoader().getResource("OSGI-INF/cooloperation-service-contrib.xml").getPath()), format);
		writer.write(docXML);
		writer.flush();
	}

	@Test
	public void shouldAddADistributorAndUpdateXML() throws OperationException {
		HelloServiceImpl hello = (HelloServiceImpl) Framework.getService(HelloService.class);
		Assert.assertEquals(3, hello.getDistributors().size());
		OperationContext ctx = new OperationContext(session);
		Map<String, Object> params = new HashMap<>();
		params.put("name", "Rambo");
		params.put("location", "*U*S*A*");
		params.put("value", "999");
		automationService.run(ctx, AddDistributor.ID, params);
		Assert.assertEquals(4, hello.getDistributors().size());
		FactorDescriptor rambo = hello.getDistributors().get(params.get("name"));
		Assert.assertNotNull(rambo);
		Assert.assertEquals(rambo.distributorId, params.get("name"));
		Assert.assertEquals(rambo.location, params.get("location"));
		Assert.assertEquals(rambo.factorValue, Double.parseDouble((String) params.get("value")), 0.0001);

//		SAXReader reader = new SAXReader();
//		try { 
//			Document docXML = reader.read(this.getClass().getClassLoader().getResourceAsStream("OSGI-INF/cooloperation-service-contrib.xml"));
//			Element root = docXML.getRootElement();
//			Element factors = (Element) root.selectSingleNode("extension[@target='org.nuxeo.uni.HelloService' and @point='updateFactor']");
//			List<Element> children = factors.elements("factor");
//			Assert.assertEquals(4, children.size());
//			boolean found = false;
//			for(Element f : children) {
//				System.out.println(f);
//				if(
//						f.attributeValue("distributorId").contentEquals((String)params.get("name"))
//						&& f.attributeValue("factorValue").contentEquals((String)params.get("value"))
//						&& (f.attributeValue("location").contentEquals((String)params.get("location")))) {
//					found = true;
//				}
//			}
//			Assert.assertEquals(4, children.size());
//			Assert.assertTrue(found);
//		} catch (DocumentException e) {
//			System.out.println("couldn't read Resource");
//			e.printStackTrace();
//		}
	}

	//    @Test
	//    public void shouldCallWithParameters() throws OperationException {
	//        final String path = "/default-domain";
	//        OperationContext ctx = new OperationContext(session);
	//        Map<String, Object> params = new HashMap<>();
	//        params.put("path", path);
	//        DocumentModel doc = (DocumentModel) automationService.run(ctx, AddDistributor.ID, params);
	//        assertEquals(path, doc.getPathAsString());
	//    }
}
