package org.nuxeo.uni;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class VisualAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if ("visual".equals(doc.getType()) && doc.hasSchema("dublincore")){
            return new VisualAdapter(doc);
        }else{
            return null;
        }
    }
}
