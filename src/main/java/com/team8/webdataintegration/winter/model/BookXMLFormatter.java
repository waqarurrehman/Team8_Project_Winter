package com.team8.webdataintegration.winter.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;




public class BookXMLFormatter extends XMLFormatter<Book> {

	
	AuthorXMLFormatter authorFormatter = new AuthorXMLFormatter();
	
	@Override
	public Element createRootElement(Document doc) {
		// TODO Auto-generated method stub
		return doc.createElement("books");
	}

	@Override
	public Element createElementFromRecord(Book record, Document doc) {
		
		Element book = doc.createElement("book");
		book.setAttribute("id", record.getIdentifier());

		//book.appendChild(createTextElement("id", record.getIdentifier(), doc));

		book.appendChild(createTextElementWithProvenance("title",
				record.getTitle(),
				record.getMergedAttributeProvenance(Book.TITLE), doc));
		book.appendChild(createTextElementWithProvenance("publisher",
				record.getPublisher(),
				record.getMergedAttributeProvenance(Book.PUBLISHER), doc));
		book.appendChild(createTextElementWithProvenance("Release_Date", record
				.getRelease_date().toString(), record
				.getMergedAttributeProvenance(Book.RELEASE_DATE), doc));

		book.appendChild(createActorsElement(record, doc));

		return book;
	}
	
	protected Element createTextElementWithProvenance(String name,
			String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	protected Element createActorsElement(Book record, Document doc) {
		Element authorRoot = authorFormatter.createRootElement(doc);
		authorRoot.setAttribute("provenance",
				record.getMergedAttributeProvenance(Book.AUTHORS));

		for (Author a : record.getAuthors()) {
			authorRoot.appendChild(authorFormatter
					.createElementFromRecord(a, doc));
		}

		return authorRoot;
	}

}
