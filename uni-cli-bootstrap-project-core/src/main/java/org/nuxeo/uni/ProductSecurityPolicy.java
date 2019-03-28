package org.nuxeo.uni;

import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.Access;
import org.nuxeo.ecm.core.model.Document;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Predicate;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery.Transformer;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.query.sql.model.WhereClause;
import org.nuxeo.ecm.core.security.AbstractSecurityPolicy;
import org.nuxeo.ecm.core.security.SecurityPolicy;

public class ProductSecurityPolicy extends AbstractSecurityPolicy implements SecurityPolicy {
	
	private static final String REJECTED_GROUP = "Groupe1";

	@Override
	public Access checkPermission(Document doc, ACP mergedAcp,
			NuxeoPrincipal principal, String permission,
			String[] resolvedPermissions, String[] additionalPrincipals) {
		// Note that doc is NOT a DocumentModel
		if (principal.getAllGroups().contains(REJECTED_GROUP) 
				&& doc.getPath().startsWith("/" + ProductAdapter.UNAVAILABLE_FOLDER)) {
			return Access.DENY;
		}
		return Access.UNKNOWN;
	}

	@Override
	public boolean isRestrictingPermission(String permission) {
		// could only restrict Browse permission, or others
		return true;
	}

	@Override
	public boolean isExpressibleInQuery(String repositoryName) {
		return true;
	}

	@Override
	public SQLQuery.Transformer getQueryTransformer(String repositoryName) {
		return PRODUCT_TRANSFORMER;
	}

	public static final Transformer PRODUCT_TRANSFORMER = new ProductTransformer();

	/**
	 * Transformer that filters unavailable {@link}ProductAdapter for users from @REJECTED_GROUP
	 */
	public static class ProductTransformer implements SQLQuery.Transformer {
		
		@Override
		public SQLQuery transform(NuxeoPrincipal principal, SQLQuery query) {
			Predicate NO_ACCESS = new Predicate(
					new Reference(NXQL.ECM_ANCESTORID), Operator.NOTEQ, new StringLiteral(ProductListener.getForbidden()));
//					new Reference(NXQL.ECM_NAME), Operator.NOTILIKE, new StringLiteral("Choco%"));
//			System.out.println("NO ACCESS: " + ProductListener.getForbidden());
			WhereClause where = query.where;
			if(principal.getAllGroups().contains(REJECTED_GROUP)) {
				System.out.println("YOU ARE REJECTED");
				Predicate builtPredicate;
				builtPredicate = (where == null || where.predicate == null) ? 
					NO_ACCESS :
					new Predicate(NO_ACCESS, Operator.AND, where.predicate);
				where = new WhereClause(builtPredicate);
			} 
			return new SQLQuery(query.select, query.from, where,
					query.groupBy, query.having, query.orderBy, query.limit, query.offset);
		}
	}

}
