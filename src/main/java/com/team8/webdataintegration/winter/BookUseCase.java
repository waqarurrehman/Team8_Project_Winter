package com.team8.webdataintegration.winter;

import java.io.File;

import org.slf4j.Logger;

import com.team8.webdataintegration.winter.identityResolution.BookAuthorComparatorEqual;
import com.team8.webdataintegration.winter.identityResolution.BookAuthorComparatorLowerJaccard;
import com.team8.webdataintegration.winter.identityResolution.BookBlockingKeyByDecadeGenerator;
import com.team8.webdataintegration.winter.identityResolution.BookBlockingKeyByTitleGenerator;
import com.team8.webdataintegration.winter.identityResolution.BookReleaseDateComparatorWeightedDateSimilarity;
import com.team8.webdataintegration.winter.identityResolution.BookTitleComparatorEqual;
import com.team8.webdataintegration.winter.identityResolution.BookTitleComparatorLowerJaccard;
import com.team8.webdataintegration.winter.model.Book;
import com.team8.webdataintegration.winter.model.BookXMLReader;

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

public class BookUseCase {
	
	private String sDS1_Path;
	private String sDS2_Path;
	private String sDS3_Path;
	private String sIdentityResolution_GoldStandard_DS1_2_DS2;
	private String sIdentityResolution_GoldStandard_DS2_2_DS3;
	private String sCorrespondance_DS1_2_DS2_Path;
	private String sCorrespondance_DS2_2_DS3_Path;
	
	
	private static Logger logger = null;
	
	public BookUseCase(String dS1_Path, String dS2_Path,String dS3_Path,
			String gs_DS1_DS2 , String gs_DS2_DS3 ,
			String correspndanceDS1_DS2, String correspondanceDS2_DS3 ,
			Logger logger
			) {
		
		this.sDS1_Path = dS1_Path;
		this.sDS2_Path = dS2_Path;
		this.sDS3_Path = dS3_Path;
		this.sIdentityResolution_GoldStandard_DS1_2_DS2 = gs_DS1_DS2;
		this.sIdentityResolution_GoldStandard_DS2_2_DS3 = gs_DS2_DS3;
		this.sCorrespondance_DS1_2_DS2_Path = correspndanceDS1_DS2;
		this.sCorrespondance_DS2_2_DS3_Path = correspondanceDS2_DS3;
		this.logger = logger;
		
		
	}
	
	public void RunIdentityResolution() throws Exception {
		logger.info("Started  RunIdentityResolution Method Execution");
		HashedDataSet<Book, Attribute> DS1 = new HashedDataSet<>();
		HashedDataSet<Book, Attribute> DS2 = new HashedDataSet<>();
		HashedDataSet<Book, Attribute> DS3 = new HashedDataSet<>();
		logger.info("Loading Data Set 1 ["+this.sDS1_Path+"]");
		new BookXMLReader().loadFromXML(new File(this.sDS1_Path), "/books/book", DS1);
		logger.info("Loading Data Set 2 ["+this.sDS2_Path+"]");
		new BookXMLReader().loadFromXML(new File(this.sDS2_Path), "/books/book", DS2);
		logger.info("Loading Data Set 3 ["+this.sDS1_Path+"]");
		new BookXMLReader().loadFromXML(new File(this.sDS3_Path), "/books/book", DS3);
		
		logger.info("Loading Gold Standard DS 1 to DS 2 ["+sIdentityResolution_GoldStandard_DS1_2_DS2+"]");
		// load the gold standard (target_wiki 2 target_bbe)
	    MatchingGoldStandard gsTest_DS1_DS2 = new MatchingGoldStandard();
	    gsTest_DS1_DS2.loadFromCSVFile(new File(sIdentityResolution_GoldStandard_DS1_2_DS2));
		
	    logger.info("Loading Gold Standard DS 2 to DS 3 ["+sIdentityResolution_GoldStandard_DS2_2_DS3+"]");
		// load the gold standard (target_wiki 2 target_fdb)
		MatchingGoldStandard gsTest_DS2_DS3 = new MatchingGoldStandard();
		gsTest_DS2_DS3.loadFromCSVFile(new File(sIdentityResolution_GoldStandard_DS2_2_DS3));
		
		LinearCombinationMatchingRule<Book, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.85);
		matchingRule.activateDebugReport("usecase/books/output/debugResultsMatchingRule_wiki_2_bbe.csv", 1000, gsTest_DS1_DS2);
		
		logger.info("Adding Title and Author Comparator");
		matchingRule.addComparator(new BookTitleComparatorLowerJaccard(), 0.70);
		logger.info("Adding Authro and Author Comparator");
		matchingRule.addComparator(new BookAuthorComparatorLowerJaccard(), 0.25);
		logger.info("Adding Authro and Release Date Comparator");
		matchingRule.addComparator(new BookReleaseDateComparatorWeightedDateSimilarity(), 0.05);
		
		logger.info("Adding Standard Record Blocker");
		// create a blocker (blocking strategy)
		//StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByDecadeGenerator());
		StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByTitleGenerator());
		//Write debug results to file:
		blocker.collectBlockSizeData("usecase/books/output/debugResultsBlocking.csv", 100);
		
		logger.info("Initilizaing the matching engine");
		// Initialize Matching Engine
		MatchingEngine<Book, Attribute> engine = new MatchingEngine<>();
		
		logger.info("Getting correspondence for wiki data and bbe dataset");
		// Execute the matching for wiki and bbe dataset
		Processable<Correspondence<Book, Attribute>> correspondences_DS1_DS2 = engine.runIdentityResolution(
				DS1, DS2, null, matchingRule,
				blocker);
		
		logger.info("Getting correspondence for wiki data and bbe dataset operation completed");
		//updating the gold standard for wiki and wdc dataset
		matchingRule.activateDebugReport("usecase/books/output/debugResultsMatchingRule_bbe_2_fdb.csv", 1000, gsTest_DS2_DS3);
		
		// Execute the matching for wiki and bbe dataset
		logger.info("Getting correspondence for BBE and FDB");
		Processable<Correspondence<Book, Attribute>> correspondences_DS2_DS3 = engine.runIdentityResolution(
				DS2, DS3, null, matchingRule,
				blocker);
		logger.info("Getting correspondence for BBE dataset and FDB  dataset operation completed");
		
		new CSVCorrespondenceFormatter().writeCSV(new File(sCorrespondance_DS1_2_DS2_Path), correspondences_DS1_DS2);
		new CSVCorrespondenceFormatter().writeCSV(new File(sCorrespondance_DS2_2_DS3_Path), correspondences_DS2_DS3);
		
		
		matchingRule.exportTrainingData(DS1,DS2,
				gsTest_DS1_DS2, new File("usecase/books/output/optimisation/DS1_2_DS2_features.csv"));
		matchingRule.exportTrainingData(DS2, DS3,
				gsTest_DS2_DS3, new File("usecase/books/output/optimisation/DS2_2_DS3_features.csv"));
		
		// evaluate your result 
		MatchingEvaluator<Book, Attribute> evaluator_wiki_bbe = new MatchingEvaluator<Book, Attribute>();
		Performance perfTest_DS1_DS2 = evaluator_wiki_bbe.evaluateMatching(correspondences_DS1_DS2.get(),
						gsTest_DS1_DS2);

	    // print the evaluation result
		logger.info(this.sDS1_Path +" <-> "+this.sDS2_Path);
		logger.info(String.format("Precision: %.4f", perfTest_DS1_DS2.getPrecision()));
		logger.info(String.format("Recall: %.4f", perfTest_DS1_DS2.getRecall()));
		logger.info(String.format("F1: %.4f", perfTest_DS1_DS2.getF1()));
				
		MatchingEvaluator<Book, Attribute> evaluator_wiki_WDC = new MatchingEvaluator<Book, Attribute>();
		Performance perfTest_DS2_DS3 = evaluator_wiki_WDC.evaluateMatching(correspondences_DS2_DS3.get(),
				gsTest_DS2_DS3);

		// print the evaluation result
		logger.info(this.sDS2_Path +" <-> "+this.sDS3_Path);
		logger.info(String.format("Precision: %.4f", perfTest_DS2_DS3.getPrecision()));
		logger.info(String.format("Recall: %.4f", perfTest_DS2_DS3.getRecall()));
		logger.info(String.format("F1: %.4f", perfTest_DS2_DS3.getF1()));
		
		
		logger.info("Ended  RunIdentityResolution Method Execution");
	}
	
	public void RunDataFusion() throws Exception {
		
	}
	

}
