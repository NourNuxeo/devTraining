package org.nuxeo.uni;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.uni.behavior.Valuable;

/**
 *
 */
public class VisualAdapter implements Valuable {

	protected DocumentModel doc;

	protected String titleXpath = "dc:title";
	protected String descriptionXpath = "dc:description";
	public static final String PRODUCT_ID_XPATH = "visual:productId";

	public VisualAdapter(DocumentModel doc) {
		this.doc = doc;
	}

	// Basic methods
	//
	// Note that we voluntarily expose only a subset of the DocumentModel API in this adapter.
	// You may wish to complete it without exposing everything!
	// For instance to avoid letting people change the document state using your adapter,
	// because this would be handled through workflows / buttons / events in your application.
	//

	public void save() {
		CoreSession session = doc.getCoreSession();
		doc = session.saveDocument(doc);
	}

	public DocumentRef getParentRef() {
		return doc.getParentRef();
	}

	// Technical properties retrieval
	public String getId() {
		return doc.getId();
	}

	public String getName() {
		return doc.getName();
	}

	public String getPath() {
		return doc.getPathAsString();
	}

	public String getState() {
		return doc.getCurrentLifeCycleState();
	}

	// Metadata get / set
	public String getProductId() {
		return (String) doc.getPropertyValue(PRODUCT_ID_XPATH);
	}

	public double getPrice() {
		return (double) doc.getPropertyValue(ProductAdapter.PRICE_XPATH);
	}

	public VisualAdapter refreshVisuals() throws OperationException {
		CoreSession session = doc.getCoreSession();
		HelloService service = Framework.getService(HelloService.class);
		AutomationService automation = Framework.getService(AutomationService.class);
		IdRef productId = new IdRef(getProductId());
		ProductAdapter product = session.getDocument(productId).getAdapter(ProductAdapter.class);

		String query = "SELECT * FROM visual WHERE ecm:mixinType != 'HiddenInNavigation' AND ecm:isProxy = 0 AND ecm:isVersion = 0 AND ecm:isTrashed = 0 "
				+ "AND collectionMember:collectionIds='" + getProductId() + "'";
		DocumentModelList dml = session.query(query);
		OperationContext ctx = new OperationContext(session);

		for(DocumentModel d : dml) {
			session.getDocument(productId);
			ctx.setInput(d);
			Map<String, Object> params = new HashMap<>();
			params.put("schema", "productSchema");
			params.put("sourceId", productId.value);
			automation.run(ctx, "Document.CopySchema", params);

			double computedPrice = service.computePrice(product);
			d.setPropertyValue(ProductAdapter.PRICE_XPATH, computedPrice);

			d = session.saveDocument(d);
		}
		session.save();
		return this;
	}

	public String getTitle() {
		return doc.getTitle();
	}

	public void setTitle(String value) {
		doc.setPropertyValue(titleXpath, value);
	}

	public String getDescription() {
		return (String) doc.getPropertyValue(descriptionXpath);
	}

	public void setDescription(String value) {
		doc.setPropertyValue(descriptionXpath, value);
	}
}
