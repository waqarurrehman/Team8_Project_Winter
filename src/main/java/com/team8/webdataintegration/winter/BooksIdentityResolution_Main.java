package com.team8.webdataintegration.winter;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.xml.sax.SAXException;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import com.team8.webdataintegration.winter.model.Book;
import com.team8.webdataintegration.winter.model.BookXMLReader;
import com.team8.webdataintegration.winter.identityResolution.BookAuthorComparatorEqual;
import com.team8.webdataintegration.winter.identityResolution.BookBlockingKeyByDecadeGenerator;
import com.team8.webdataintegration.winter.identityResolution.BookReleaseDateComparatorWeightedDateSimilarity;
import com.team8.webdataintegration.winter.identityResolution.BookTitleComparatorEqual;
import com.team8.webdataintegration.winter.BookUseCase;

public class BooksIdentityResolution_Main {
	
	private static final Logger logger = WinterLogManager.activateLogger("default");
	
	private static final String sDS1_Path = "usecase/books/input/target_wiki.xml";
	private static final String sDS2_Path = "usecase/books/input/target_bbe.xml";
	private static final String sDS3_Path = "usecase/books/input/target_fdb.xml";
	private static final String sIdentityResolution_GoldStandard_DS1_2_DS2 = "usecase/books/goldstandard/gs_wiki_2_bbe.csv";
	private static final String sIdentityResolution_GoldStandard_DS2_2_DS3 = "usecase/books/goldstandard/gs_fdb_2_bbe.csv";
	private static final String sCorrespondance_DS1_2_DS2_Path = "usecase/books/output/Wiki_2_BBE_correspondences_New.csv";
	private static final String sCorrespondance_DS2_2_DS3_Path = "usecase/books/output/BBE_2_FDB_correspondences_New.csv";
	private static final String sXPath_Book = "/books/book";
	private static final String sFusedXMLPath = "usecase/books/output/target_Fused.xml";
	public static void main(String[] args) {
		
		logger.info("Started  Execution Method Execution");
		
		BookUseCase _usecase = new BookUseCase( 
				sDS1_Path,
				sDS2_Path, 
				sDS3_Path ,
				sIdentityResolution_GoldStandard_DS1_2_DS2,
				sIdentityResolution_GoldStandard_DS2_2_DS3,
				sCorrespondance_DS1_2_DS2_Path,
				sCorrespondance_DS2_2_DS3_Path,
				sXPath_Book,
				sFusedXMLPath,
				logger
				);
		
		try {
			
			_usecase.RunIdentityResolution();
		}catch( Exception ex) {
			logger.info("Excepiton in Identity Resolution["+ex.toString()+"]");
			ex.printStackTrace();
		}
		
		
		try {
			_usecase.RunDataFusion();
		}catch(Exception ex) {
			logger.info("Excepiton in DataFusion["+ex.toString()+"]");
			ex.printStackTrace();
		}
		

	}

}
