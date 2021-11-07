package com.team8.webdagtaintegraion.winter;

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
import com.team8.webdagtaintegraion.winter.model.Book;
import com.team8.webdagtaintegraion.winter.model.BookXMLReader;
import com.team8.webdagtaintegraion.winter.identityResolution.BookBlockingKeyByDecadeGenerator;

public class BooksIdentityResolution_Main {
	
	private static final Logger logger = WinterLogManager.activateLogger("default");

	public static void main(String[] args) {
		HashedDataSet<Book, Attribute> wikidata = new HashedDataSet<>();
		try {
			new BookXMLReader().loadFromXML(new File("usecase/books/input/target_wiki.xml"), "/books/book", wikidata);
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
		HashedDataSet<Book, Attribute> BBE = new HashedDataSet<>();
		try {
			new BookXMLReader().loadFromXML(new File("usecase/books/input/target_bbe.xml"), "/books/book", BBE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// load the gold standard (test set)
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		try {
			gsTest.loadFromCSVFile(new File(
					"usecase/books/goldstandard/gs_academy_awards_2_actors_test.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create a matching rule
		LinearCombinationMatchingRule<Book, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.7);
		matchingRule.activateDebugReport("usecase/books/output/debugResultsMatchingRule.csv", 1000, gsTest);
		
		// add comparators
//		matchingRule.addComparator((m1,  m2, c) -> new TokenizingJaccardSimilarity().calculate(m1.getTitle(), m2.getTitle()) , 0.8);
//		matchingRule.addComparator((m1, m2, c) -> new YearSimilarity(10).calculate(m1.getDate(), m2.getDate()), 0.2);

		/*
		matchingRule.addComparator(new MovieDirectorComparatorLevenshtein(), 0.2);
		matchingRule.addComparator(new MovieTitleComparatorLevenshtein(), 0.8);
		*/

		// create a blocker (blocking strategy)
		StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByDecadeGenerator());
		//Write debug results to file:
		blocker.collectBlockSizeData("usecase/books/output/debugResultsBlocking.csv", 100);

		
		// Initialize Matching Engine
		MatchingEngine<Book, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		Processable<Correspondence<Book, Attribute>> correspondences = engine.runIdentityResolution(
				wikidata, BBE, null, matchingRule,
				blocker);
		
		// write the correspondences to the output file
		try {
			new CSVCorrespondenceFormatter().writeCSV(new File("usecase/book/output/DBPedia_2_BestBookEver_correspondences.csv"), correspondences);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// export Training Data
		try {
			matchingRule.exportTrainingData(wikidata, BBE,
					gsTest, new File("usecase/book/output/optimisation/DBPedia_2_BestBookEver_features.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// evaluate your result
		MatchingEvaluator<Book, Attribute> evaluator = new MatchingEvaluator<Book, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences.get(),
				gsTest);

		// print the evaluation result
		logger.info("DBPedia <-> BestBookEver");
		logger.info(String.format("Precision: %.4f", perfTest.getPrecision()));
		logger.info(String.format("Recall: %.4f", perfTest.getRecall()));
		logger.info(String.format("F1: %.4f", perfTest.getF1()));

	}

}
