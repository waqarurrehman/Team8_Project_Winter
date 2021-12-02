package com.team8.webdataintegration.winter;

import com.team8.webdataintegration.winter.identityResolution.*;
import com.team8.webdataintegration.winter.model.Book;
import com.team8.webdataintegration.winter.model.BookXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.slf4j.Logger;

import java.io.File;

public class TestCustomComparators
{

    private static final Logger logger = WinterLogManager.activateLogger("trace");

    public static void runIdentityResoultion() throws Exception
    {
        // loading data
        logger.info("*\tLoading datasets\t*");
        HashedDataSet<Book, Attribute> fdb = new HashedDataSet<>();
        new BookXMLReader().loadFromXML(new File("usecase/books/input/target_fdb.xml"), "/books/book", fdb);
        HashedDataSet<Book, Attribute> bbe = new HashedDataSet<>();
        new BookXMLReader().loadFromXML(new File("usecase/books/input/target_bbe.xml"), "/books/book", bbe);
        HashedDataSet<Book, Attribute> wiki = new HashedDataSet<>();
        new BookXMLReader().loadFromXML(new File("usecase/books/input/target_wiki.xml"), "/books/book", wiki);

        MatchingGoldStandard fdbBbeGoldstandard = new MatchingGoldStandard();
        fdbBbeGoldstandard.loadFromCSVFile(new File("usecase/books/goldstandard/gs_fdb_2_bbe.csv"));
        MatchingGoldStandard wikiBbeGoldstandard = new MatchingGoldStandard();
        wikiBbeGoldstandard.loadFromCSVFile(new File("usecase/books/goldstandard/gs_wiki_2_bbe.csv"));

        // create a matching rule
        String options[] = new String[] { "-S" };
        String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Book, Attribute> matchingRule = new WekaMatchingRule<>(0.7, modelType, options);

        /*
        matchingRule.addComparator(new BookCustomTitleComparator());
        //matchingRule.addComparator(new BookTitleComparatorLowerJaccard());
        matchingRule.addComparator(new BookCustomAuthorComparator());
        */

        matchingRule.addComparator(new BookCustomAuthorComparator());
        //matchingRule.addComparator(new BookAuthorComparatorLowerJaccard());
        matchingRule.addComparator(new BookCustomTitleComparator());

        // train the matching rule's model
        logger.info("*\tLearning matching rule\t*");
        RuleLearner<Book, Attribute> learner = new RuleLearner<>();
        learner.learnMatchingRule(fdb, bbe, null, matchingRule, fdbBbeGoldstandard);
        logger.info(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByTitleGenerator());

        // Initialize Matching Engine
        MatchingEngine<Book, Attribute> engine = new MatchingEngine<>();

        // Execute the matching
        logger.info("*\tRunning identity resolution\t*");
        Processable<Correspondence<Book, Attribute>> correspondences = engine.runIdentityResolution(
                bbe, wiki, null, matchingRule,
                blocker);

        // write the correspondences to the output file
        new CSVCorrespondenceFormatter().writeCSV(new File("usecase/books/output/temptemp.csv"), correspondences);

        MatchingEvaluator<Book, Attribute> wikiBbeEvaluator = new MatchingEvaluator<Book, Attribute>();
        Performance wikiBbePerfTest = wikiBbeEvaluator.evaluateMatching(correspondences.get(),
                wikiBbeGoldstandard);

        // print the evaluation result
        logger.info(String.format("Precision: %.4f", wikiBbePerfTest.getPrecision()));
        logger.info(String.format("Recall: %.4f", wikiBbePerfTest.getRecall()));
        logger.info(String.format("F1: %.4f", wikiBbePerfTest.getF1()));
    }

    public static void main(String[] args) {
        try {
            runIdentityResoultion();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
