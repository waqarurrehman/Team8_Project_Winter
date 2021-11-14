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

public class BooksIdentityResolution_Main {
	
	private static final Logger logger = WinterLogManager.activateLogger("default");

	public static void main(String[] args) {
		HashedDataSet<Book, Attribute> wikidata = new HashedDataSet<>();
		HashedDataSet<Book, Attribute> BBE = new HashedDataSet<>();
		HashedDataSet<Book, Attribute> WDC = new HashedDataSet<>();
		try {
			new BookXMLReader().loadFromXML(new File("usecase/books/input/target_wiki.xml"), "/books/book", wikidata);
			new BookXMLReader().loadFromXML(new File("usecase/books/input/target_bbe.xml"), "/books/book", BBE);
			new BookXMLReader().loadFromXML(new File("usecase/books/input/target_wdc.xml"), "/books/book", WDC);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("loaded all the datasets now loading Gold Standards");
		// load the gold standard (target_wiki 2 target_bbe)
		MatchingGoldStandard gsTest_wiki_bbe = new MatchingGoldStandard();
		// load the gold standard (target_wiki 2 target_wdc)
		MatchingGoldStandard gsTest_wiki_WDC = new MatchingGoldStandard();
		try {
			gsTest_wiki_bbe.loadFromCSVFile(new File(
					"usecase/books/goldstandard/Wiki_2_BBE_test.csv"));
			gsTest_wiki_WDC.loadFromCSVFile(new File(
					"usecase/books/goldstandard/Wiki_2_WDC_test.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("loaded Gold Standards");
		// create a matching rule
		LinearCombinationMatchingRule<Book, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.7);
		matchingRule.activateDebugReport("usecase/books/output/debugResultsMatchingRule_wiki_2_bbe.csv", 1000, gsTest_wiki_bbe);
		
		try {
			logger.info("Adding Title and Author Comparator");
			matchingRule.addComparator(new BookTitleComparatorEqual(), 0.8);
			matchingRule.addComparator(new BookAuthorComparatorEqual(), 0.1);
			matchingRule.addComparator(new BookReleaseDateComparatorWeightedDateSimilarity(), 0.1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		logger.info("Adding Standard Record Blocker");
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByDecadeGenerator());
		//Write debug results to file:
		blocker.collectBlockSizeData("usecase/books/output/debugResultsBlocking.csv", 100);

		logger.info("Initilizaing the matching engine");
		// Initialize Matching Engine
		MatchingEngine<Book, Attribute> engine = new MatchingEngine<>();
		
		logger.info("Getting correspondence for wiki data and bbe dataset");
		// Execute the matching for wiki and bbe dataset
		Processable<Correspondence<Book, Attribute>> correspondences_wiki_bbe = engine.runIdentityResolution(
				wikidata, BBE, null, matchingRule,
				blocker);
		logger.info("Getting correspondence for wiki data and bbe dataset operation completed");
		//updating the gold standard for wiki and wdc dataset
		matchingRule.activateDebugReport("usecase/books/output/debugResultsMatchingRule_wiki_2_wdc.csv", 1000, gsTest_wiki_WDC);
		
		// Execute the matching for wiki and bbe dataset
		logger.info("Getting correspondence for wiki data and bbe WDC");
		Processable<Correspondence<Book, Attribute>> correspondences_wiki_WDC = engine.runIdentityResolution(
				wikidata, WDC, null, matchingRule,
				blocker);
		logger.info("Getting correspondence for wiki data and wdc dataset operation completed");
		// write the correspondences to the output file
		try {
			logger.info("Writing the Correspondence files");
			new CSVCorrespondenceFormatter().writeCSV(new File("usecase/book/output/Wiki_2_BBE_correspondences.csv"), correspondences_wiki_bbe);
			new CSVCorrespondenceFormatter().writeCSV(new File("usecase/book/output/Wiki_2_WDC_correspondences.csv"), correspondences_wiki_WDC);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// export Training Datasets
		try {
			matchingRule.exportTrainingData(wikidata, BBE,
					gsTest_wiki_bbe, new File("usecase/book/output/optimisation/Wiki_2_BBE_features.csv"));
			matchingRule.exportTrainingData(wikidata, BBE,
					gsTest_wiki_WDC, new File("usecase/book/output/optimisation/Wiki_2_WDC_features.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// evaluate your result 
		MatchingEvaluator<Book, Attribute> evaluator_wiki_bbe = new MatchingEvaluator<Book, Attribute>();
		Performance perfTest_wiki_bbe = evaluator_wiki_bbe.evaluateMatching(correspondences_wiki_bbe.get(),
				gsTest_wiki_bbe);

		// print the evaluation result
		logger.info("WikiData <-> BestBookEver");
		logger.info(String.format("Precision: %.4f", perfTest_wiki_bbe.getPrecision()));
		logger.info(String.format("Recall: %.4f", perfTest_wiki_bbe.getRecall()));
		logger.info(String.format("F1: %.4f", perfTest_wiki_bbe.getF1()));
		
		MatchingEvaluator<Book, Attribute> evaluator_wiki_WDC = new MatchingEvaluator<Book, Attribute>();
		Performance perfTest_wiki_WDC = evaluator_wiki_WDC.evaluateMatching(correspondences_wiki_WDC.get(),
				gsTest_wiki_bbe);

		// print the evaluation result
		logger.info("WikiData <-> WDC");
		logger.info(String.format("Precision: %.4f", perfTest_wiki_WDC.getPrecision()));
		logger.info(String.format("Recall: %.4f", perfTest_wiki_WDC.getRecall()));
		logger.info(String.format("F1: %.4f", perfTest_wiki_WDC.getF1()));

	}

}
