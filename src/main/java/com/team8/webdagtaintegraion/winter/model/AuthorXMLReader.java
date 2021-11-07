package com.team8.webdagtaintegraion.winter.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

public class AuthorXMLReader extends XMLMatchableReader<Author, Attribute> {
	
	

	@Override
	protected void initialiseDataset(DataSet<Author, Attribute> dataset) {
		// TODO Auto-generated method stub
		super.initialiseDataset(dataset);
		
		dataset.addAttribute(Author.PSEUDONYM);
		dataset.addAttribute(Author.BIRTHNAME);
		dataset.addAttribute(Author.BIRTHDATE);
		dataset.addAttribute(Author.AUTHORNAME);
		
	}


	@Override
	public Author createModelFromElement(Node node, String provenanceInfo) {
		// TODO Auto-generated method stub
		Author author= new Author();
		author.setAuthor_name(getValueFromChildElement(node, "author"));
		author.setPseudonym(getValueFromChildElement(node, "pseudonym"));
		author.setBirth_name(getValueFromChildElement(node, "birth_name"));
	
		try {
			String date = getValueFromChildElement(node, "birth_date");
			if (date != null && !date.isEmpty()) {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				        .appendPattern("MM/dd/yyyy['T'HH:mm:ss.SSS]")
				        .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
				        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
						.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
						.optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
				        .toFormatter(Locale.ENGLISH);
				LocalDateTime dt = LocalDateTime.parse(date, formatter);
				author.setBirth_date(dt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return author;
	}

	
	
	
	

}
