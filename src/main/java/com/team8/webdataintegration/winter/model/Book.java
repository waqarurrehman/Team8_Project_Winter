package com.team8.webdataintegration.winter.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class Book extends AbstractRecord<Attribute> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public Book(String identifier, String provenance) {
		super(identifier, provenance);
		setAuthors(new LinkedList<>());
	}
	
	
	private String isbn;
	private String title;
	private LocalDateTime  release_date;
	private String language;
	private String pages;
	private String genre;
	private String price;
	private String formats;
	private String publisher;
	private List<Author> authors;
	
	
	
	
	private Map<Attribute, Collection<String>> provenance = new HashMap<>();
	private Collection<String> recordProvenance;

	public void setRecordProvenance(Collection<String> provenance) {
		recordProvenance = provenance;
	}

	public Collection<String> getRecordProvenance() {
		return recordProvenance;
	}

	public void setAttributeProvenance(Attribute attribute,
			Collection<String> provenance) {
		this.provenance.put(attribute, provenance);
	}

	public Collection<String> getAttributeProvenance(String attribute) {
		return provenance.get(attribute);
	}

	public String getMergedAttributeProvenance(Attribute attribute) {
		Collection<String> prov = provenance.get(attribute);

		if (prov != null) {
			return StringUtils.join(prov, "+");
		} else {
			return "";
		}
	}

	public static final Attribute ISBN = new Attribute("ISBN");
	public static final Attribute TITLE = new Attribute("Title");
	public static final Attribute RELEASE_DATE = new Attribute("Release_Date");
	public static final Attribute LANGUAGE = new Attribute("Languge");
	public static final Attribute PAGES = new Attribute("Pages");
	public static final Attribute GENRE = new Attribute("Genre");
	public static final Attribute FORMATS = new Attribute("Formats");
	public static final Attribute PRICE = new Attribute("Price");
	public static final Attribute PUBLISHER = new Attribute("Publisher");
	public static final Attribute AUTHORS = new Attribute("Authors");
	
	@Override
	public boolean hasValue(Attribute attribute) {
		if(attribute==TITLE)
			return getTitle() != null && !getTitle().isEmpty();
		else if(attribute==RELEASE_DATE)
			return getRelease_date() != null;
		else if(attribute==AUTHORS)
			return getAuthors() != null && getAuthors().size() > 0;
		else if(attribute==ISBN)
				return getIsbn() != null && !getIsbn().isEmpty();
		else if(attribute==LANGUAGE)
			return getLanguage() != null && !getLanguage().isEmpty();
		else if(attribute==PAGES)
			return getPages() != null && !getPages().isEmpty();
		else if(attribute==GENRE)
			return getGenre() != null && !getGenre().isEmpty();
		else if(attribute==PRICE)
			return getPrice() != null && !getPrice().isEmpty();
		else if(attribute==FORMATS)
			return getFormats() != null && !getFormats().isEmpty();
		else if(attribute==PUBLISHER)
			return getPublisher() != null && !getPublisher().isEmpty();
		else
			return false;
	}

	@Override
	public String toString() {
		return String.format("[Book %s: %s / %s / %s]", getIdentifier(),
				getTitle(), getRelease_date().toString(), getIsbn());
	   
	}

	@Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Book){
			return this.getIdentifier().equals(((Book) obj).getIdentifier());
		}else
			return false;
	}


	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getRelease_date() {
		return release_date;
	}

	public void setRelease_date(LocalDateTime release_date) {
		this.release_date = release_date;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getFormats() {
		return formats;
	}

	public void setFormats(String formats) {
		this.formats = formats;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

}
