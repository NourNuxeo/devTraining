package org.nuxeo.uni;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.uni.uni-cli-bootstrap-project-core")
public class TestDocumentManipulation {
	
//	static Log logger = LogFactory.getLog(TestCoolOperation.class);
	
	@Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldCreateDocument() {
    	DocumentModel docModel = session.createDocumentModel("/", "testDocument", "File");
    	docModel = session.createDocument(docModel);
    	IdRef docId = new IdRef(docModel.getId());
    	session.getDocument(docId);
    	docModel.setPropertyValue("dc:title", "ChocoChoc");
    	PathRef docRef = new PathRef(docModel.getPathAsString());
    	session.getDocument(docRef);
    	
    	session.saveDocument(docModel);
    	session.save();
    	
    	String query = "SELECT * FROM File";
    	DocumentModelList dml = session.query(query);
    	System.out.println(dml.get(0));
    	Assert.assertEquals(1, dml.size());
    	
    	Property title = docModel.getProperty("dc:title");
    	System.out.println(title.getXPath());
    	System.out.println(title.getValue());
    }
}
