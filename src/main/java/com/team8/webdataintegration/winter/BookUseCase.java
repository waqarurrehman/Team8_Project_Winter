package com.team8.webdataintegration.winter;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

import com.team8.webdataintegration.winter.identityResolution.*;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.*;
import org.slf4j.Logger;

import com.team8.webdataintegration.winter.datafusion.evaluation.*;
import com.team8.webdataintegration.winter.datafusion.fusers.*;
import com.team8.webdataintegration.winter.model.Book;
import com.team8.webdataintegration.winter.model.BookXMLFormatter;
import com.team8.webdataintegration.winter.model.BookXMLReader;

import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class BookUseCase {
	
	private String wikiSourcePath;
	private String bbeSourcePath;
	private String fdbSourcePath;
	private String wikiBbeGoldstandardPath;
	private String fdbBbeGoldstandardPath;
	private String wikiBbeCorrespondencesPath;
	private String fdbBbeCorrespondencesPath;
	private String sXPath_Book;
	private String sFusedXMLPath;
	
	
	private static Logger logger = null;
	
	public BookUseCase(String wikiSourcePath, String bbeSourcePath,String fdbSourcePath,
			String wikiBbeGoldstandardPath , String fdbBbeGoldstandardPath ,
			String wikiBbeCorrespondencesPath, String fdbBbeCorrespondencesPath,
			String XPath_Book,
			String fusedXMLPath,
			Logger logger
			) {
		
		this.wikiSourcePath = wikiSourcePath;
		this.bbeSourcePath = bbeSourcePath;
		this.fdbSourcePath = fdbSourcePath;
		this.wikiBbeGoldstandardPath = wikiBbeGoldstandardPath;
		this.fdbBbeGoldstandardPath = fdbBbeGoldstandardPath;
		this.wikiBbeCorrespondencesPath = wikiBbeCorrespondencesPath;
		this.fdbBbeCorrespondencesPath = fdbBbeCorrespondencesPath;
		this.sXPath_Book = XPath_Book;
		this.sFusedXMLPath = fusedXMLPath;
		this.logger = logger;
	}
	
	public void RunIdentityResolution() throws Exception {
		logger.info("Started  RunIdentityResolution Method Execution");
		HashedDataSet<Book, Attribute> wiki = new HashedDataSet<>();
		HashedDataSet<Book, Attribute> bbe = new HashedDataSet<>();
		HashedDataSet<Book, Attribute> fdb = new HashedDataSet<>();
		logger.info("Loading Data Set 1 ["+this.wikiSourcePath +"]");
		new BookXMLReader().loadFromXML(new File(this.wikiSourcePath), this.sXPath_Book, wiki);
		logger.info("Loading Data Set 2 ["+this.bbeSourcePath +"]");
		new BookXMLReader().loadFromXML(new File(this.bbeSourcePath),this.sXPath_Book, bbe);
		logger.info("Loading Data Set 3 ["+this.wikiSourcePath +"]");
		new BookXMLReader().loadFromXML(new File(this.fdbSourcePath), this.sXPath_Book, fdb);
		
		logger.info("Loading Gold Standard DS 1 to DS 2 ["+ wikiBbeGoldstandardPath +"]");
		// load the gold standard (target_wiki 2 target_bbe)
	    MatchingGoldStandard wikiBbeGoldstandard = new MatchingGoldStandard();
	    wikiBbeGoldstandard.loadFromCSVFile(new File(wikiBbeGoldstandardPath));

		MatchingGoldStandard wikiBbeGoldstandardTrain = new MatchingGoldStandard();
		wikiBbeGoldstandardTrain.loadFromCSVFile(new File("usecase/books/goldstandard/gs_wiki_2_bbe_train.csv"));

	    logger.info("Loading Gold Standard DS 2 to DS 3 ["+ fdbBbeGoldstandardPath +"]");
		// load the gold standard (target_wiki 2 target_fdb)
		MatchingGoldStandard fdbBbeGoldstandard = new MatchingGoldStandard();
		fdbBbeGoldstandard.loadFromCSVFile(new File(fdbBbeGoldstandardPath));

		MatchingGoldStandard fdbBbeGoldstandardTrain = new MatchingGoldStandard();
		fdbBbeGoldstandardTrain.loadFromCSVFile(new File("usecase/books/goldstandard/gs_fdb_2_bbe_train.csv"));




		// create a matching rule
		String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
		WekaMatchingRule<Book, Attribute> wikiBbeMatchingRule = new WekaMatchingRule<>(0.7, modelType, options);

		wikiBbeMatchingRule.addComparator(new BookCustomAuthorComparator());
		wikiBbeMatchingRule.addComparator(new BookCustomTitleComparator());
		wikiBbeMatchingRule.addComparator(new BookDateComparator2Years());
		//wikiBbeMatchingRule.addComparator(new BookAuthorComparatorJaroWinkler());
		//wikiBbeMatchingRule.addComparator(new BookAuthorComparatorLevenshtein());
		//wikiBbeMatchingRule.addComparator(new BookTitleComparatorLowerJaccard());

		// train the matching rule's model
		logger.info("*\tLearning matching rule\t*");
		RuleLearner<Book, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(wiki, bbe, null, wikiBbeMatchingRule, wikiBbeGoldstandardTrain);
		logger.info(String.format("Matching rule is:\n%s", wikiBbeMatchingRule.getModelDescription()));

		WekaMatchingRule<Book, Attribute> fdbBbeMatchingRule = new WekaMatchingRule<>(0.7, modelType, options);

		fdbBbeMatchingRule.addComparator(new BookCustomAuthorComparator());
		fdbBbeMatchingRule.addComparator(new BookCustomTitleComparator());
		fdbBbeMatchingRule.addComparator(new BookDateComparator2Years());

		// train the matching rule's model
		logger.info("*\tLearning matching rule\t*");
		RuleLearner<Book, Attribute> fdblearner = new RuleLearner<>();
		fdblearner.learnMatchingRule(bbe, fdb, null, fdbBbeMatchingRule, fdbBbeGoldstandardTrain);
		logger.info(String.format("Matching rule is:\n%s", fdbBbeMatchingRule.getModelDescription()));



		logger.info("Adding Standard Record Blocker");
		// create a blocker (blocking strategy)
		//StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByDecadeGenerator());
		StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByTitleGenerator());
		//Write debug results to file:
		blocker.collectBlockSizeData("usecase/books/output/debugResultsBlocking.csv", 100);
		
		logger.info("Initializing the matching engine");
		// Initialize Matching Engine
		MatchingEngine<Book, Attribute> engine = new MatchingEngine<>();

		logger.info("Getting correspondence for wiki data and bbe dataset");
		// Execute the matching for wiki and bbe dataset
		Processable<Correspondence<Book, Attribute>> wikiBbeCorrespondences = engine.runIdentityResolution(
				wiki, bbe, null, wikiBbeMatchingRule,
				blocker);
		
		// Execute the matching for wiki and bbe dataset
		logger.info("Getting correspondence for BBE and FDB");
		Processable<Correspondence<Book, Attribute>> fdbBbeCorrespondences = engine.runIdentityResolution(
				bbe, fdb, null, fdbBbeMatchingRule,
				blocker);
		logger.info("Getting correspondence for BBE dataset and FDB dataset operation completed");
		
		new CSVCorrespondenceFormatter().writeCSV(new File(wikiBbeCorrespondencesPath), wikiBbeCorrespondences);
		new CSVCorrespondenceFormatter().writeCSV(new File(fdbBbeCorrespondencesPath), fdbBbeCorrespondences);

		// evaluate your result 
		MatchingEvaluator<Book, Attribute> wikiBbeEvaluator = new MatchingEvaluator<Book, Attribute>();
		Performance wikiBbePerfTest = wikiBbeEvaluator.evaluateMatching(wikiBbeCorrespondences.get(),
						wikiBbeGoldstandard);

	    // print the evaluation result
		logger.info(this.wikiSourcePath +" <-> "+this.bbeSourcePath);
		logger.info(String.format("Precision: %.4f", wikiBbePerfTest.getPrecision()));
		logger.info(String.format("Recall: %.4f", wikiBbePerfTest.getRecall()));
		logger.info(String.format("F1: %.4f", wikiBbePerfTest.getF1()));

		MatchingEvaluator<Book, Attribute> fdbBbeEvaluator = new MatchingEvaluator<Book, Attribute>();
		Performance fdbBbePerfTest = fdbBbeEvaluator.evaluateMatching(fdbBbeCorrespondences.get(),
				fdbBbeGoldstandard);

		// print the evaluation result
		logger.info(this.bbeSourcePath +" <-> "+this.fdbSourcePath);
		logger.info(String.format("Precision: %.4f", fdbBbePerfTest.getPrecision()));
		logger.info(String.format("Recall: %.4f", fdbBbePerfTest.getRecall()));
		logger.info(String.format("F1: %.4f", fdbBbePerfTest.getF1()));

		logger.info("Ended  RunIdentityResolution Method Execution");
	}
	
	public void RunDataFusion() throws Exception {
		
		logger.info("Starting   RunDataFusion  Method Execution");
		FusibleDataSet<Book, Attribute> wiki = new FusibleHashedDataSet<>();
		new BookXMLReader().loadFromXML(new File(this.wikiSourcePath), this.sXPath_Book, wiki);
		wiki.printDataSetDensityReport();

		FusibleDataSet<Book, Attribute> bbe = new FusibleHashedDataSet<>();
		new BookXMLReader().loadFromXML(new File(this.bbeSourcePath), this.sXPath_Book, bbe);
		bbe.printDataSetDensityReport();

		FusibleDataSet<Book, Attribute> fdb = new FusibleHashedDataSet<>();
		new BookXMLReader().loadFromXML(new File(this.fdbSourcePath), this.sXPath_Book , fdb);
		fdb.printDataSetDensityReport();

		/*ds1.setScore(3.0);
		ds2.setScore(1.0);
		ds3.setScore(2.0);
		*/
		
		// Date (e.g. last update)
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				        .appendPattern("yyyy-MM-dd")
				        .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
				        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
				        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
				        .toFormatter(Locale.ENGLISH);
				
		wiki.setDate(LocalDateTime.parse("2012-01-01", formatter));
		bbe.setDate(LocalDateTime.parse("2010-01-01", formatter));
		fdb.setDate(LocalDateTime.parse("2008-01-01", formatter));
		
		
		// load correspondences
		logger.info("load correspondences");
		CorrespondenceSet<Book, Attribute> correspondences = new CorrespondenceSet<>();
		correspondences.loadCorrespondences(new File(this.wikiBbeCorrespondencesPath),wiki, bbe);
		correspondences.loadCorrespondences(new File(this.fdbBbeCorrespondencesPath),bbe, fdb);

		// write group size distribution
		
		correspondences.printGroupSizeDistribution();

		for(RecordGroup<Book, Attribute> l : correspondences.getRecordGroups()) {
			if(l.getSize() > 3) {
				for(Book b : l.getRecords()) {
					System.out.println(b.getIdentifier() + " " + b.getTitle() + " " + b.getAuthors().get(0).getAuthor_name() + " " + b.getRelease_date());
				}
				System.out.println();
			}
		}

		// define the fusion strategy
		logger.info("define the fusion strategy");
		DataFusionStrategy<Book, Attribute> strategy = new DataFusionStrategy<>(new BookXMLReader());
				
		// add attribute fusers
		strategy.addAttributeFuser(Book.TITLE, new BookTileFuserShortestString(),new BookTitleEvaluationRule());
		strategy.addAttributeFuser(Book.PUBLISHER,new BookPubihserFuserUnionComaSeparated(), new BookPublisherEvaluationRule());
		strategy.addAttributeFuser(Book.RELEASE_DATE, new BookReleaseDateFuserVoting(),new BookReleaseDateEvaluationRule());
		strategy.addAttributeFuser(Book.AUTHORS,new BookAuthorsFuserUnion(),new BookAuthorsEvaluationRule());
		strategy.addAttributeFuser(Book.GENRE, new BookGenreFuserUnionComaSeparated(),new BookGenreEvaluationRule());
		strategy.addAttributeFuser(Book.LANGUAGE, new BookLanguageUnionComaSeparate(),new BookLanguageEvaluationRule());
		strategy.addAttributeFuser(Book.PAGES, new BookPagesFuserLongestString(),new BookPageEvaluationRule());
		strategy.addAttributeFuser(Book.PRICE, new BookPriceFuserLongestString(),new BookPriceEvaluationRule());
		strategy.addAttributeFuser(Book.FORMATS, new BookFormatFuserLongestString(),new BookFormatEvaluationRule());
		strategy.addAttributeFuser(Book.ISBN, new BookISBNFuserLongestStirng(),new BookISBNEvaluationRule());
		strategy.addAttributeFuser(Book.RATING, new BookRatingFuserLongestString(),new BookRatingEvaluationRule());
		strategy.addAttributeFuser(Book.PARTOFASERIES, new BookPartOfASeriesFuserLongestString(),new BookPartOfASeriesEvaluationRule());
				// create the fusion engine
		logger.info("create the fusion engine");
		DataFusionEngine<Book, Attribute> engine = new DataFusionEngine<>(strategy);
		logger.info("printClusterConsistency");
		engine.printClusterConsistencyReport(correspondences, null);
		// run the fusion
		logger.info("Running the Fusion Engine");
		FusibleDataSet<Book, Attribute> fusedDataSet = engine.run(correspondences, null);
		fusedDataSet.printDataSetDensityReport();

		// write the result
		new BookXMLFormatter().writeXML(new File(this.sFusedXMLPath), fusedDataSet);
		
		engine.writeRecordGroupsByConsistency(new File("usecase/books/output/recordGroupConsistencies.csv"), correspondences, null);
		
		// load the gold standard
		DataSet<Book, Attribute> gs = new FusibleHashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("usecase/books/goldstandard/fused.xml"), this.sXPath_Book, gs);
		
		// evaluate
		DataFusionEvaluator<Book, Attribute> evaluator = new DataFusionEvaluator<>(
				strategy, new RecordGroupFactory<Book, Attribute>());
		double accuracy = evaluator.evaluate(fusedDataSet, gs, null);

		logger.info(String.format("Accuracy: %.2f", accuracy));

		logger.info("Ended   RunDataFusion  Method Execution");
				
				
		
	}
	

}
