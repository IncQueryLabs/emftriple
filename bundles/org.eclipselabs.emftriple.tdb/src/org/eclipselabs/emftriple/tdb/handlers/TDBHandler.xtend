package org.eclipselabs.emftriple.tdb.handlers

import com.hp.hpl.jena.query.Dataset
import java.io.IOException
import java.util.Map
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl
import org.eclipselabs.emftriple.tdb.streams.TDBInputStream
import org.eclipselabs.emftriple.tdb.streams.TDBOutputStream

class TDBHandler extends URIHandlerImpl {

	private final Dataset dataset

	new(Dataset dataset) {
		this.dataset = dataset
	}

	override canHandle(URI uri) {
		true
	}

	override createOutputStream(URI uri, Map<?, ?> options) throws IOException {
		if (dataset == null) {
			throw new IOException("Dataset must be defined")
		}
		new TDBOutputStream(dataset, uri)
	}

	override createInputStream(URI uri, Map<?, ?> options) throws IOException {
		if (dataset == null) {
			throw new IOException("Dataset must be defined")
		}
		new TDBInputStream(dataset, uri)
	}

}