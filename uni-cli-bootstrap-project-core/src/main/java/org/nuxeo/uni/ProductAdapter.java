package org.nuxeo.uni;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.uni.behavior.Valuable;

/**
 *
 */
public class ProductAdapter implements Valuable {
	private static final String AVAILABLE_XPATH = "productSchema:availability";
	public static final String DISTRIBUTOR_XPATH = "product:distributor";
	public static final String DISTRIBUTOR_NAME_XPATH = "name";
	public static final String PRICE_XPATH = "productSchema:price";
	protected String titleXpath = "dc:title";
	protected String descriptionXpath = "dc:description";
	protected DocumentModel doc;
	public static final String PRODUCT_AVAILABILITY_CHANGED_EVENT_ID = "productUnavailable";
	

	public ProductAdapter(DocumentModel doc) {
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

	public double getPrice() {
		return (double) doc.getPropertyValue(PRICE_XPATH);
	}

	public void setPrice(double value) {
		doc.setPropertyValue(PRICE_XPATH, value);
	}

	public String getDistributorId() {
		Map<String, Serializable> distributor = (Map<String, Serializable>)doc.getPropertyValue(DISTRIBUTOR_XPATH);
		return (String) distributor.get(DISTRIBUTOR_NAME_XPATH);
	}
	
	public void setDistributor(String name, String location) {
		Map<String, Serializable> distributor = new HashMap<>();
		distributor.put("name", name);
		distributor.put("location", location);
		doc.setPropertyValue(DISTRIBUTOR_XPATH, (Serializable) distributor);
	}
	
	public void setAvailability(boolean availability) {
		if(availability != isAvailable()) {
			doc.setPropertyValue(AVAILABLE_XPATH, availability);
			EventProducer eventProducer = Framework.getService(EventProducer.class);
			CoreSession session = doc.getCoreSession();
			DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), doc);
			Event event = ctx.newEvent(PRODUCT_AVAILABILITY_CHANGED_EVENT_ID);
			eventProducer.fireEvent(event);
		}
	}

	public boolean isAvailable() {
		boolean result = (doc.getPropertyValue(AVAILABLE_XPATH) != null) ?
				(boolean) doc.getPropertyValue(AVAILABLE_XPATH) : true;
		return result;
	}
}
