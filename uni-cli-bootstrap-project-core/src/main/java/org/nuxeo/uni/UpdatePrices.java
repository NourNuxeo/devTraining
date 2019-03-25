package org.nuxeo.uni;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id=UpdatePrices.ID, category=Constants.CAT_DOCUMENT, label="UpdatePrices", description="Updates prices depending on distributors")
public class UpdatePrices {

	private static final double ROUND_PRECISION = 100;

	public static final String ID = "Document.UpdatePrices";

	@Context
	protected CoreSession session;

	@Param(name = "products", required = false)
	protected List<ProductAdapter> products;
	//    @Param(name = "path", required = false)
	//    protected String path;

	//    @OperationMethod
	//    public DocumentModel run() {
	//        if (StringUtils.isBlank(path)) {
	//            return session.getRootDocument();
	//        } else {
	//            return session.getDocument(new PathRef(path));
	//        }
	//    }

	private void updatePrice(ProductAdapter product) {
		System.out.println("price was: " + product.getPrice());
		HelloService service = Framework.getService(HelloService.class);
		product.setPrice(service.computeContributedPrice(product));
		System.out.printf(product.getDistributorId() + "'s price is: %.2f", Math.round(product.getPrice()*ROUND_PRECISION) / ROUND_PRECISION);
		System.out.println();
		product.doc = session.saveDocument(product.doc);
	}
	
	@OperationMethod
	public void run(ProductAdapter product) {
		updatePrice(product);
		session.save();
	}
	
	@OperationMethod
	public void run(List<ProductAdapter> products) {
		for(ProductAdapter p : products) {
			updatePrice(p);
		}
		session.save();
	}

	//    @OperationMethod
	//    public void run(List<ProductAdapter> products) {
	//    	for(ProductAdapter p : products) {
	//    		System.out.println("price was: " + p.getPrice());
	//    		p.setPrice(p.getPrice()*2);
	//    		System.out.println("price is: " + p.getPrice());
	//    		p.doc = session.saveDocument(p.doc);
	////    		saveProduct(p);
	//    	}
	//    	session.save();
	//    }

	//    private void saveProduct(ProductAdapter p) {
	//    	try {
	//    		p.doc = session.saveDocument(p.doc);
	//    	} catch (DocumentNotFoundException e) {
	//    		p.doc = session.createDocument(p.doc);
	//    	}
	//    }
}
