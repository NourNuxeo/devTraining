package org.nuxeo.uni;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class ProductAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if ("product".equals(doc.getType()) && doc.hasSchema("dublincore")){
            return new ProductAdapter(doc);
        }else{
            return null;
        }
    }
}
