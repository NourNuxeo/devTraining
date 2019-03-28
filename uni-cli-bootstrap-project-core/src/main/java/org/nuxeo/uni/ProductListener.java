package org.nuxeo.uni;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;


public class ProductListener implements EventListener {

	public static final String UNAVAILABLE_FOLDER_PATH;
	public static final String UNAVAILABLE_FOLDER_PROPERTY_KEY = "hiddenProductsPath";
	private static String unavailableFolderId;
	
	public static String getUnavailableFolderId() {
		return unavailableFolderId;
	}


	static {
		InputStream propsInput = ProductListener.class.getClassLoader().getResourceAsStream("product.properties");
		Properties props = new Properties();
		try {
			props.load(propsInput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		UNAVAILABLE_FOLDER_PATH =  props.getProperty(UNAVAILABLE_FOLDER_PROPERTY_KEY);
	}

	@Override
	public void handleEvent(Event event) {
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}

		DocumentEventContext docCtx = (DocumentEventContext) ctx;
		ProductAdapter product = docCtx.getSourceDocument().getAdapter(ProductAdapter.class);
		if(event.getName().contentEquals(ProductAdapter.PRODUCT_AVAILABILITY_CHANGED_EVENT_ID)) {
			if(!product.isAvailable()) {
				CoreSession session = ctx.getCoreSession();
				String collectionID = product.doc.getId().toString();
				String query = 
						"SELECT * FROM visual WHERE ecm:mixinType != 'HiddenInNavigation' "
								+ "AND ecm:isProxy = 0 AND ecm:isVersion = 0 "
								+ "AND ecm:isTrashed = 0 "
								+ "AND collectionMember:collectionIds = '" + collectionID + "'";
				DocumentModelList dml = session.query(query);
				if(dml.size() > 0) {
					PathRef forbiddenRootPathRef = new PathRef("/" + UNAVAILABLE_FOLDER_PATH);

					if(!session.exists(forbiddenRootPathRef)){
						DocumentModel rootDocModel = session.createDocumentModel("/", UNAVAILABLE_FOLDER_PATH, "Folder");
						//						ACP acp = new ACPImpl();
						//						ACL acl = new ACLImpl();
						//						ACE ace = new ACE("group:Groupe1", SecurityConstants.EVERYTHING, false);
						//						acl.add(ace);
						//						acp.addACL(acl);
						rootDocModel = session.createDocument(rootDocModel);
						//						rootDocModel.setACP(acp, true);

					}
					
					if(unavailableFolderId == null) {
						unavailableFolderId = session.getDocument(forbiddenRootPathRef).getId();
					}

					DocumentModel docModel;
					PathRef forbiddenPathRef = new PathRef("/" + UNAVAILABLE_FOLDER_PATH + "/" + collectionID);
					if(!session.exists(forbiddenPathRef)){
						docModel = session.createDocumentModel("/" + UNAVAILABLE_FOLDER_PATH, collectionID, "Folder");
						docModel = session.createDocument(docModel);
					} else {
						docModel = session.getDocument(forbiddenPathRef);
					}
					for(DocumentModel doc : dml) {
						session.move(doc.getRef(), docModel.getRef(), null);
					}
				} 
				session.save();
//				System.out.println("FIRED @@@@@@@@@@@@");
			}
		}
	}
}
