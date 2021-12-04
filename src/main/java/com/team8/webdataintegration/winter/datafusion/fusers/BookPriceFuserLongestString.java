package com.team8.webdataintegration.winter.datafusion.fusers;

import java.util.List;
import java.util.Map;

import com.team8.webdataintegration.winter.model.Book;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.LongestString;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class BookPriceFuserLongestString extends AttributeValueFuser<String, Book, Attribute>  {

	public BookPriceFuserLongestString() {
		super(new LongestString<Book, Attribute>());
	}

	@Override
	public String getValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return  record.getPrice();
	}
	
	

	@Override
	public void fuse(RecordGroup<Book, Attribute> group, Book fusedRecord,
			Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		// TODO Auto-generated method stub
		
		FusedValue<String, Book, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		 
		if (fusedRecord != null ) {
			fusedRecord.setPrice(fused.getValue());
			fusedRecord.setAttributeProvenance(Book.PRICE, fused.getOriginalIds());
		}
		
		
	}

	@Override
	public boolean hasValue(Book record, Correspondence<Attribute, Matchable> correspondence) {
		// TODO Auto-generated method stub
		return record.hasValue(Book.PRICE);
	}


}
