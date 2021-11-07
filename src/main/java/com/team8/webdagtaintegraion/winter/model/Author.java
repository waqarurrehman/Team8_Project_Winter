package com.team8.webdagtaintegraion.winter.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;


public class Author extends AbstractRecord<Attribute> implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private String pseudonym;
	private String birth_name;
	private LocalDateTime birth_date;
	private String author_name;
	
	public String getPseudonym() {
		return pseudonym;
	}
	public void setPseudonym(String pseudonym) {
		this.pseudonym = pseudonym;
	}
	public String getBirth_name() {
		return birth_name;
	}
	public void setBirth_name(String birth_name) {
		this.birth_name = birth_name;
	}
	public LocalDateTime getBirth_date() {
		return birth_date;
	}
	public void setBirth_date(LocalDateTime birth_date) {
		this.birth_date = birth_date;
	}
	
	public String getAuthor_name() {
		return author_name;
	}
	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}
	
	
	@Override
	public int hashCode() {
		int result = 31 + ((pseudonym == null) ? 0 : pseudonym.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		if (pseudonym == null) {
			if (other.pseudonym != null)
				return false;
		} else if (!pseudonym.equals(other.pseudonym))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString(java.lang.Object)
	 */
	@Override
	public String toString() {

		return String.format("[Author %s: %s / %s / %s]", getIdentifier(), getPseudonym(),
				getBirth_name(), getBirth_date());
	}

	public static final Attribute PSEUDONYM = new Attribute("Pseudonym");
	public static final Attribute BIRTHNAME = new Attribute("Birth_name");
	public static final Attribute BIRTHDATE = new Attribute("Birth_date");
	public static final Attribute AUTHORNAME = new Attribute("Author_Name");
	
	@Override
	public boolean hasValue(Attribute attribute) {
		// TODO Auto-generated method stub
		if(attribute==PSEUDONYM)
			return pseudonym!=null;
		else if(attribute==BIRTHNAME) 
			return birth_name!=null;
		else if(attribute==BIRTHDATE)
			return birth_date!=null;
		else if(attribute==AUTHORNAME)
			return getAuthor_name()!=null && !getAuthor_name().isEmpty();
		return false;
	}
	
	
	

}
