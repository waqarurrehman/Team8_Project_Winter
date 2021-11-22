package com.team8.webdataintegration.winter;

import com.team8.webdataintegration.winter.identityResolution.*;
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
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

public class BooksIdentityResolution_Example {

	private static final Logger logger = WinterLogManager.activateLogger("trace");

	public static void main(String[] args) {
		HashedDataSet<Book, Attribute> wikidata = new HashedDataSet<>();
		HashedDataSet<Book, Attribute> BBE = new HashedDataSet<>();
		HashedDataSet<Book, Attribute> fdb = new HashedDataSet<>();
		try {
			new BookXMLReader().loadFromXML(new File("usecase/books/input/target_wiki.xml"), "/books/book", wikidata);
			new BookXMLReader().loadFromXML(new File("usecase/books/input/target_bbe.xml"), "/books/book", BBE);
			new BookXMLReader().loadFromXML(new File("usecase/books/input/target_fdb.xml"), "/books/book", fdb);
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
		// load the gold standard (target_wiki 2 target_fdb)
		MatchingGoldStandard gsTest_wiki_fdb = new MatchingGoldStandard();
		try {
			gsTest_wiki_bbe.loadFromCSVFile(new File(
					"usecase/books/goldstandard/gs_wiki_2_bbe.csv"));
			gsTest_wiki_fdb.loadFromCSVFile(new File(
					"usecase/books/goldstandard/gs_fdb_2_bbe.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("loaded Gold Standards");
		// create a matching rule
		LinearCombinationMatchingRule<Book, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.8);
		matchingRule.activateDebugReport("usecase/books/output/debugResultsMatchingRule_wiki_2_bbe.csv", 10000, gsTest_wiki_bbe);

		try {
			matchingRule.addComparator(new BookCustomTitleComparator(), 0.6);
			matchingRule.addComparator(new BookCustomAuthorComparator(), 0.4);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		logger.info("Adding Standard Record Blocker");
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByTitleGenerator());
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
		//updating the gold standard for wiki and fdb dataset
		matchingRule.activateDebugReport("usecase/books/output/debugResultsMatchingRule_wiki_2_fdb.csv", 1000, gsTest_wiki_fdb);

		// Execute the matching for wiki and bbe dataset
		logger.info("Getting correspondence for wiki data and bbe fdb");
		Processable<Correspondence<Book, Attribute>> correspondences_fdb_bbe = engine.runIdentityResolution(
				fdb, BBE, null, matchingRule,
				blocker);
		logger.info("Getting correspondence for wiki data and fdb dataset operation completed");
		// write the correspondences to the output file
		try {
			logger.info("Writing the Correspondence files");
			new CSVCorrespondenceFormatter().writeCSV(new File("usecase/books/output/Wiki_2_BBE_correspondences.csv"), correspondences_wiki_bbe);
			new CSVCorrespondenceFormatter().writeCSV(new File("usecase/books/output/Wiki_2_fdb_correspondences.csv"), correspondences_fdb_bbe);
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
		logger.info("" + perfTest_wiki_bbe.getNumberOfCorrectTotal());
		logger.info("" + perfTest_wiki_bbe.getNumberOfPredicted());
		logger.info(String.format("Precision: %.4f", perfTest_wiki_bbe.getPrecision()));
		logger.info(String.format("Recall: %.4f", perfTest_wiki_bbe.getRecall()));
		logger.info(String.format("F1: %.4f", perfTest_wiki_bbe.getF1()));

		MatchingEvaluator<Book, Attribute> evaluator_wiki_fdb = new MatchingEvaluator<Book, Attribute>();
		Performance perfTest_wiki_fdb = evaluator_wiki_fdb.evaluateMatching(correspondences_fdb_bbe.get(),
				gsTest_wiki_fdb);

		// print the evaluation result
		logger.info("WikiData <-> fdb");
		logger.info(String.format("Precision: %.4f", perfTest_wiki_fdb.getPrecision()));
		logger.info(String.format("Recall: %.4f", perfTest_wiki_fdb.getRecall()));
		logger.info(String.format("F1: %.4f", perfTest_wiki_fdb.getF1()));

	}

}
