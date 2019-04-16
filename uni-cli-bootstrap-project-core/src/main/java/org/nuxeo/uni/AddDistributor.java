package org.nuxeo.uni;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id=AddDistributor.ID, category=Constants.CAT_DOCUMENT, label="add a product distributor", description="Add a distributor to the distributors' directory.")
public class AddDistributor {

    public static final String ID = "Document.AddDistributor";

	public static final String DISTRIBUTOR_ID = "distributorId";

	public static final String DISTRIBUTOR_FACTOR_VALUE = "factorValue";

	public static final String DISTRIBUTOR_LOCATION = "location";

    @Context
    protected CoreSession session;

    @Param(name = "path", required = false)
    protected String path;

    @Param(name = "name", required = true)
    protected String name;
    
    @Param(name = "location", required = true)
    protected String location;
    
    @Param(name = "value", required = true)
    protected String value;

    @OperationMethod
    public void run() {
        Map<String, String> params = new HashMap<>();
        params.put(DISTRIBUTOR_ID, name);
        params.put(DISTRIBUTOR_LOCATION, location);
        params.put(DISTRIBUTOR_FACTOR_VALUE, value);
        run(params);
    }
    
    @OperationMethod
    public void run(Map<String, String> distributorMap) {
//    	SAXReader reader = new SAXReader();
//		try {
//			Document docXML = reader.read(this.getClass().getClassLoader().getResourceAsStream("OSGI-INF/cooloperation-service-contrib.xml"));
//			Element root = docXML.getRootElement();
//			Element factors = (Element) root.selectSingleNode("extension[@target='org.nuxeo.uni.HelloService' and @point='updateFactor']");
//			Element newDistributor = factors.addElement("factor");
//			newDistributor.addAttribute(DISTRIBUTOR_ID, distributorMap.get(DISTRIBUTOR_ID));
//			newDistributor.addAttribute(DISTRIBUTOR_FACTOR_VALUE, distributorMap.get(DISTRIBUTOR_FACTOR_VALUE));
//			newDistributor.addAttribute(DISTRIBUTOR_LOCATION, distributorMap.get(DISTRIBUTOR_LOCATION));
//			
//			OutputFormat format = OutputFormat.createPrettyPrint();
//			
//			XMLWriter writer = new XMLWriter(new PrintWriter(this.getClass().getClassLoader().getResource("OSGI-INF/cooloperation-service-contrib.xml").getPath()), format);
//			writer.write(docXML);
//			writer.flush();
//			System.out.println(this.getClass().getClassLoader().getResource("OSGI-INF/cooloperation-service-contrib.xml").getPath());
			
			FactorDescriptor newFactor = new FactorDescriptor(
					distributorMap.get(DISTRIBUTOR_ID),
					Double.parseDouble(distributorMap.get(DISTRIBUTOR_FACTOR_VALUE)),
					distributorMap.get(DISTRIBUTOR_LOCATION));
			
			HelloService helloService = Framework.getService(HelloService.class);
			((HelloServiceImpl)helloService).registerContribution(newFactor, null, null);
			Map<String, FactorDescriptor> distributors = helloService.getDistributors();
			for(String s : distributors.keySet()) {
				System.out.println(distributors.get(s));
			}
//		} catch (DocumentException e) {
//			System.out.println("couldn't read Resource");
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			System.out.println("couldn't write Resource");
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("couldn't write Resource because of dom4j writer exception");
//			e.printStackTrace();
//		}
    }
}
