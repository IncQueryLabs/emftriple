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
package org.eclipselabs.emftriple.sail.util;

import org.eclipselabs.emftriple.datasources.IResultSet;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

public class SesameResultSet implements IResultSet {

	private final TupleQueryResult result;

	public SesameResultSet(TupleQueryResult result)
	{
		this.result = result ;
	}

	@Override
	public boolean hasNext() {
		try {
			return result.hasNext();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Solution next() {
		try {
			return new SesameSolution(result.next());
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void remove() {
		try {
			result.remove();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
	}

	public static class SesameSolution implements Solution {

		private final BindingSet solution;

		public SesameSolution(BindingSet solution) {
			this.solution = solution;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Value get(String varName) {
			return solution.getValue(varName);
		}

		@Override
		public boolean isResource(String varName) {
			return solution.getValue(varName) instanceof URI;
		}

		@SuppressWarnings("unchecked")
		@Override
		public URI getResource(String varName) {
			final Value value = solution.getValue(varName);
			
			return (URI) value;
		}

		@Override
		public boolean isLiteral(String varName) {
			return solution.getValue(varName) instanceof Literal;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Literal getLiteral(String varName) {
			final Value value = solution.getValue(varName);
			
			return (Literal) value;
		}
		
		@Override
		public Iterable<String> getSolutionNames() {
			return solution.getBindingNames();
		}
	}

}
