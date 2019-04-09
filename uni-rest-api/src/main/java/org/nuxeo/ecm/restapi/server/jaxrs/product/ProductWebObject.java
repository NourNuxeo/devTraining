package  org.nuxeo.ecm.restapi.server.jaxrs.product;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.uni.HelloService;
import org.nuxeo.uni.ProductAdapter;


@WebObject(type = "productComputer")
@Produces(MediaType.TEXT_HTML)
public class ProductWebObject extends DefaultObject {
	@GET
	@Path("{productName}")
	public String computePrice(@PathParam("productName") String productName) {
		DocumentModel workflowInstance;
		String query = "SELECT * FROM product WHERE ecm:mixinType != 'HiddenInNavigation' AND ecm:isProxy = 0 AND ecm:isVersion = 0 AND ecm:isTrashed = 0 AND productSchema:productName = '" + productName + "'";
		DocumentModelList docs = getContext().getCoreSession().query(query);

		double result = -1;
		if(docs.size() == 1) {
			HelloService service = Framework.getService(HelloService.class);
			ProductAdapter product = docs.get(0).getAdapter(ProductAdapter.class);
			result = Framework.getService(HelloService.class).computePrice(product);
		}
		return String.valueOf(result);
	}
}
