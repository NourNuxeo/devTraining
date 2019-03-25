package org.nuxeo.uni;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.PartialDeploy;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
//@Deploy({"org.nuxeo.uni.uni-cli-bootstrap-project-core", "studio.extensions.nalkotob-SANDBOX", "org.nuxeo.ecm.platform.filemanager.core"})
@Deploy({"org.nuxeo.uni.uni-cli-bootstrap-project-core"})
@PartialDeploy(bundle = "studio.extensions.nalkotob-SANDBOX", 
extensions = { org.nuxeo.runtime.test.runner.TargetExtensions.ContentModel.class })
public class TestProductAdapter {
  @Inject
  CoreSession session;

  @Test
  public void shouldCallTheAdapter() {
    String doctype = "product";
    String testTitle = "My Adapter Title";

    DocumentModel doc = session.createDocumentModel("/", "test-adapter", doctype);
    ProductAdapter adapter = doc.getAdapter(ProductAdapter.class);
    adapter.setTitle(testTitle);
    Assert.assertEquals("Document title does not match when using the adapter", testTitle, adapter.getTitle());
  }
}
