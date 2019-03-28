package org.nuxeo.uni;


import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;


public class ProductListener implements EventListener {

	private static String forbidden;
	
	public static String getForbidden() {
		return forbidden;
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
					PathRef forbiddenRootPathRef = new PathRef("/" + ProductAdapter.UNAVAILABLE_FOLDER);
					
					if(!session.exists(forbiddenRootPathRef)){
						DocumentModel rootDocModel = session.createDocumentModel("/", ProductAdapter.UNAVAILABLE_FOLDER, "Folder");
//						ACP acp = new ACPImpl();
//						ACL acl = new ACLImpl();
//						ACE ace = new ACE("group:Groupe1", SecurityConstants.EVERYTHING, false);
//						acl.add(ace);
//						acp.addACL(acl);
						rootDocModel = session.createDocument(rootDocModel);
//						rootDocModel.setACP(acp, true);
						
					}
					
					if(forbidden == null) {
						forbidden = session.getDocument(forbiddenRootPathRef).getId();
						System.out.println("FORBIDDEN: " + forbidden);
					};
					
					DocumentModel docModel;
					PathRef forbiddenPathRef = new PathRef("/" + ProductAdapter.UNAVAILABLE_FOLDER + "/" + collectionID);
					if(!session.exists(forbiddenPathRef)){
						docModel = session.createDocumentModel("/" + ProductAdapter.UNAVAILABLE_FOLDER, collectionID, "Folder");
						docModel = session.createDocument(docModel);
					} else {
						docModel = session.getDocument(forbiddenPathRef);
					}
					for(DocumentModel doc : dml) {
						session.move(doc.getRef(), docModel.getRef(), null);
					}
				} 
				session.save();
				System.out.println("FIRED @@@@@@@@@@@@");
			}
		}
	}
}
