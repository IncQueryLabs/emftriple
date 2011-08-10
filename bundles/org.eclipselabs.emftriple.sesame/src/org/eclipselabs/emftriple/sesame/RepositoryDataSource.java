/*******************************************************************************
 * Copyright (c) 2011 Guillaume Hillairet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Guillaume Hillairet - initial API and implementation
 *******************************************************************************/
package org.eclipselabs.emftriple.sesame;

import org.eclipselabs.emftriple.datasources.AbstractDataSource;
import org.eclipselabs.emftriple.datasources.IResultSet;
import org.eclipselabs.emftriple.sail.util.SesameResultSet;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class RepositoryDataSource 
	extends AbstractDataSource<Graph, Statement> {

	protected RepositoryConnection connection;

	protected final Repository repository;

	protected RepositoryDataSource(Repository repository) {
		this.repository = repository;
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		try {
			connection = repository.getConnection();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		try {
			connection.setAutoCommit(true);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add(Iterable<Statement> triples, String namedGraphURI) {
		connect();

		try {
			if (namedGraphURI == null) {
				connection.add(triples);
			} else {
				connection.add(triples, new ValueFactoryImpl().createURI(namedGraphURI));
			}
		} catch (RepositoryException e) {
			try {
				connection.rollback();
			} catch (RepositoryException re) {
				re.printStackTrace();
			}
		}
	}

	@Override
	public void remove(Iterable<Statement> triples, String namedGraphURI) {
		connect();

		try {
			if (namedGraphURI == null) {
				connection.remove(triples);
			} else {
				connection.remove(triples, new ValueFactoryImpl().createURI(namedGraphURI));
			}
		} catch (RepositoryException e) {
			try {
				connection.rollback();
			} catch (RepositoryException re) {
				re.printStackTrace();
			}
		}
	}

	@Override
	public void delete(String graphURI) {
		connect();
		
		if (graphURI == null) {
			try {
				connection.clear();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		} else {
			try {
				connection.clear(new ValueFactoryImpl().createURI(graphURI));
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Graph getGraph(String graphURI) {
		return constructQuery("construct { ?s ?p ?o } where { ?s ?p ?o }", graphURI);
	}

	@Override
	public void connect() {
		if (!isConnected()) {
			setConnected(true);
			try {
				if (!repository.isWritable()) {
					repository.shutDown();
				}
//				repository.initialize();
				connection = repository.getConnection();
			} catch (RepositoryException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void disconnect() {
		setConnected(false);

		try {
			connection.close();
//			repository.shutDown();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean askQuery(String query, String graphURI) {
		connect();

		try {
			return connection.prepareBooleanQuery(QueryLanguage.SPARQL, query).evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Graph constructQuery(String query, String graphURI) {
		connect();

		GraphQueryResult aResult = null;
		try {
			aResult = connection.prepareGraphQuery(QueryLanguage.SPARQL, query)
					.evaluate();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		Graph g = new GraphImpl();
		try {
			for(;aResult.hasNext();)
				g.add(aResult.next());
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return g;
	}

	@Override
	public void constructQuery(String query, String graphURI, Graph aGraph) {
		connect();

		GraphQueryResult aResult = null;
		try {
			aResult = connection.prepareGraphQuery(QueryLanguage.SPARQL, query)
					.evaluate();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		try {
			for(;aResult.hasNext();)
				aGraph.add(aResult.next());
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void describeQuery(String query, String graphURI, Graph aGraph) {
		connect();

		GraphQueryResult aResult = null;	
		try {
			aResult = connection.prepareGraphQuery(QueryLanguage.SPARQL, query)
					.evaluate();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		try {
			for(;aResult.hasNext();)
				aGraph.add(aResult.next());
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Graph describeQuery(String query, String graph) {
		connect();

		GraphQueryResult aResult = null;	
		try {
			aResult = connection.prepareGraphQuery(QueryLanguage.SPARQL, query)
					.evaluate();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}

		Graph g = new GraphImpl();
		try {
			for(;aResult.hasNext();)
				g.add(aResult.next());
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return g;
	}

	@Override
	public IResultSet selectQuery(String query, String graph) {
		connect();
		
		IResultSet aResult = null;
		try {
			TupleQuery aQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
			aResult = new SesameResultSet(aQuery.evaluate());
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return aResult;
	}

	/* (non-Javadoc)
	 * @see org.eclipselabs.emftriple.datasources.IDataSource#update(java.lang.String)
	 */
	@Override
	public void update(String updateQuery) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipselabs.emftriple.datasources.IDataSource#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String resourceURI) {
		ValueFactory factory = repository.getValueFactory();
		if (factory == null) {
			factory = new ValueFactoryImpl();
		}
		
		try {
			return repository.getConnection().getStatements(factory.createURI(resourceURI), null, null, true).hasNext();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipselabs.emftriple.datasources.IDataSource#delete(java.lang.String, java.lang.String)
	 */
	@Override
	public void delete(String resourceURI, String graphURI) {
		ValueFactory factory = repository.getValueFactory();
		if (factory == null) {
			factory = new ValueFactoryImpl();
		}
		
		if (graphURI == null) {
			try {
				repository.getConnection().remove(factory.createURI(resourceURI), null, null);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		} else {
			try {
				repository.getConnection().remove(factory.createURI(resourceURI), null, null, factory.createURI(graphURI));
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean supportsTransaction() {
		return true;
	}

//	private final void checkIsConnected() {
//		try {
//			if (connection == null || !connection.isOpen() || !isConnected()) {
//				connect();
//			}
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public boolean supportsNamedGraph() {
		return true;
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public boolean supportsUpdateQuery() {
		return false;
	}

}
