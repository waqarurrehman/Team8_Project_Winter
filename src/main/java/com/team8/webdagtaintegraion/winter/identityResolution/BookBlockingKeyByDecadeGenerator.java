package com.team8.webdagtaintegraion.winter.identityResolution;

import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.BlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import com.team8.webdagtaintegraion.winter.model.Book;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  

public class BookBlockingKeyByDecadeGenerator extends
RecordBlockingKeyGenerator<Book, Attribute> {

private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
	* @see de.uni_mannheim.informatik.wdi.matching.blocking.generators.BlockingKeyGenerator#generateBlockingKeys(de.uni_mannheim.informatik.wdi.model.Matchable, de.uni_mannheim.informatik.wdi.model.Result, de.uni_mannheim.informatik.wdi.processing.DatasetIterator)
	*/
	@Override
	public void generateBlockingKeys(Book record, Processable<Correspondence<Attribute, Matchable>> correspondences,
		DataIterator<Pair<String, Book>> resultCollector) {
		
	    resultCollector.next(new Pair<>(Integer.toString(
	    		record.getRelease_date().getYear() / 10), record)
	    		  
	    		);
	    		
	    		
	    	   
	}

}
