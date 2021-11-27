package com.team8.webdataintegration.winter.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

public class AuthorXMLFormatter extends XMLFormatter<Author> {

	@Override
	public Element createRootElement(Document doc) {
		// TODO Auto-generated method stub
		return doc.createElement("authors");
	}

	@Override
	public Element createElementFromRecord(Author record, Document doc) {
		// TODO Auto-generated method stub
		Element author = createTextElement("author", record.getAuthor_name(), doc);
		return author;
	}

}
