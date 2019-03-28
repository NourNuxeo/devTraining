package org.nuxeo.uni;

import org.nuxeo.ecm.collections.core.test.CollectionFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.PartialDeploy;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Deploy({"org.nuxeo.uni.uni-cli-bootstrap-project-core"})
@Deploy("org.nuxeo.ecm.platform.picture.api")
@Deploy("org.nuxeo.ecm.platform.picture.core")
@Deploy("org.nuxeo.ecm.platform.picture.convert")
@Deploy("org.nuxeo.ecm.platform.tag")
@Features(CollectionFeature.class)
@PartialDeploy(bundle = "studio.extensions.nalkotob-SANDBOX", 
extensions = { org.nuxeo.runtime.test.runner.TargetExtensions.ContentModel.class })

public class ProductFeature implements RunnerFeature {

}
