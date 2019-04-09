package org.nuxeo.uni;

import java.util.List;

import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.uni.util.ProductUtils;

/**
 *
 */
@Operation(id=UpdatePrices.ID, category=Constants.CAT_DOCUMENT, label="UpdatePrices", description="Updates prices depending on distributors")
public class UpdatePrices {

	public static final String ID = "Document.UpdatePrices";

	@Context
	protected CoreSession session;

	@Param(name = "products", required = false)
	protected List<ProductAdapter> products;

	private void updatePrice(DocumentModel doc) throws OperationException {
		VisualAdapter visual = doc.getAdapter(VisualAdapter.class);
		
		//here product is useless. It will be instanciated by refreshVisuals(). It's only there for convenient test sysouts.
		IdRef productRef = new IdRef(visual.getProductId());
		ProductAdapter product = session.getDocument(productRef).getAdapter(ProductAdapter.class);

		System.out.println("price was: " + product.getPrice());
		
		visual.refreshVisuals();
		
		//this log will be removed with the product instanciation.
		System.out.printf(product.getDistributorId() + "'s price is: %.2f", ProductUtils.getFormattedPrice(visual));
		System.out.println();
		visual.doc = session.saveDocument(visual.doc);
	}
	
	@OperationMethod
	public void run(DocumentModel doc) throws OperationException {
		updatePrice(doc);
		session.save();
	}
	
	@OperationMethod
	public void run(List<DocumentModel> products) throws OperationException {
		System.out.println(products);
		for(DocumentModel doc : products) {
			updatePrice(doc);
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
