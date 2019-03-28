package org.nuxeo.uni;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.operations.collections.AddToCollectionOperation;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.EventListenerDescriptor;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class, ProductFeature.class})

public class TestProductListener {

    protected final List<String> events = Arrays.asList("productUnavailable");

    @Inject
    protected EventService s;
    
    @Inject
    protected CoreSession session;

    @Inject
	protected AutomationService automationService;
    
    @Test
    public void listenerRegistration() {
        EventListenerDescriptor listener = s.getEventListener("productlistener");
        assertNotNull(listener);
        assertTrue(events.stream().allMatch(listener::acceptEvent));
    }
    
    @Test
    public void testAccess() throws OperationException, InterruptedException {
    	EventListenerDescriptor listener = s.getEventListener("productlistener");
    	
    	DocumentModel docCollection = session.createDocumentModel("/", "ChocoStickProduct", "product");
    	docCollection = session.createDocument(docCollection);
    	ProductAdapter product = docCollection.getAdapter(ProductAdapter.class);
    	docCollection.getSchemas();
    	
    	DocumentModel docVisual = session.createDocumentModel("/", "ChocoVisual", "visual");
    	docVisual.setPropertyValue("title", "ChocoVisual");
    	docVisual = session.createDocument(docVisual);
    	
    	OperationContext ctx = new OperationContext(session);
    	ctx.setInput(docVisual);
    	Map<String, Object> params = new HashMap<>();
    	params.put("collection", docCollection);
    	automationService.run(ctx, AddToCollectionOperation.ID, params);
    	docVisual = session.saveDocument(docVisual);
    	
    	
    	// :-)
    	session.save();
    	product.setAvailability(false);
    	
    	Assert.assertFalse(session.exists(new PathRef(docVisual.getPathAsString())));
    	
//    	System.out.println("@@@@@@@@@@@ " + docVisual.getPathAsString());
    	session.getDocument(new PathRef("/" + ProductListener.UNAVAILABLE_FOLDER_PATH 
    			+ "/" + docCollection.getId() 
    			+ docVisual.getPathAsString()));
    	
    	session.getPrincipal().setGroups(Arrays.asList("Groupe1"));
    	
    	
//    	System.out.println("shouldn't find");
//    	String path = "/" + ProductListener.UNAVAILABLE_FOLDER_PATH 
//    			+ "/" + docCollection.getId() 
//    			+ docVisual.getPathAsString();
//    	System.out.println(path);
    	
    	
    	String query = "SELECT * FROM visual";
    	DocumentModelList dml = session.query(query);
    	
//    	for(DocumentModel d : dml) {
//    		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//    		System.out.println(d);
//    	}
//    	
    	Assert.assertEquals(dml.size(), 0);
    	
//    	System.out.println(session.getDocument(new PathRef("/" + ProductAdapter.UNAVAILABLE_FOLDER)).getId());
//    	System.out.println(session.getDocument(new PathRef("/" + ProductAdapter.UNAVAILABLE_FOLDER)));
    }
}
