package com.team8.webdataintegration.winter.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
		
		if(record.hasValue(Book.RELEASE_DATE)) {
			//DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
			//String sdate = dateFormat.format(record.getRelease_date()); 
			book.appendChild(createTextElementWithProvenance("release_date", 
					record.getRelease_date().toString(), 
			record.getMergedAttributeProvenance(Book.RELEASE_DATE), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("release_rate", 
					"", 
					"", doc));
		}
		
		if(record.hasValue(Book.PUBLISHER)) {
			book.appendChild(createTextElementWithProvenance("publisher",
				record.getPublisher(),
				record.getMergedAttributeProvenance(Book.PUBLISHER), doc));
		}else {
			
			book.appendChild(createTextElementWithProvenance("publisher",
					"",
					"" , doc));
			
		}
		
		if(record.hasValue(Book.GENRE)) {
			book.appendChild(createTextElementWithProvenance("genre",
				record.getGenre(),
				record.getMergedAttributeProvenance(Book.GENRE), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("genre",
					"",
					"", doc));
		}
		
		book.appendChild(createActorsElement(record, doc));
		
		if(record.hasValue(Book.PRICE)) {
		
			book.appendChild(createTextElementWithProvenance("price",
				record.getPrice(),
				record.getMergedAttributeProvenance(Book.PRICE), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("price",
					"",
					"", doc));
		}
		if(record.hasValue(Book.FORMATS)) {
			book.appendChild(createTextElementWithProvenance("formats",
				record.getFormats(),
				record.getMergedAttributeProvenance(Book.FORMATS), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("formats",
					"",
					"", doc));
		}
		
		if(record.hasValue(Book.LANGUAGE)) {
		book.appendChild(createTextElementWithProvenance("language",
				record.getLanguage(),
				record.getMergedAttributeProvenance(Book.LANGUAGE), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("language",
					"",
					"", doc));
		}
		
		if(record.hasValue(Book.PAGES)) {
			book.appendChild(createTextElementWithProvenance("pages",
				record.getPages(),
				record.getMergedAttributeProvenance(Book.PAGES), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("pages",
					"",
					"", doc));
		}
		
		if(record.hasValue(Book.ISBN)) {
			book.appendChild(createTextElementWithProvenance("isbn",
				record.getIsbn(),
				record.getMergedAttributeProvenance(Book.ISBN), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("isbn",
					"",
					"", doc));
		}
		
		if(record.hasValue(Book.RATING)) {
			book.appendChild(createTextElementWithProvenance("rating",
					String.format("%.2f", record.getRating()),
				record.getMergedAttributeProvenance(Book.RATING), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("rating",
					"",
					"", doc));
		}
		
		if(record.hasValue(Book.PARTOFASERIES)) {
			book.appendChild(createTextElementWithProvenance("part_of_a_series",
				record.getPart_of_Series(),
				record.getMergedAttributeProvenance(Book.PARTOFASERIES), doc));
		}else {
			book.appendChild(createTextElementWithProvenance("part_of_a_series",
					"",
					"", doc));
		}
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
