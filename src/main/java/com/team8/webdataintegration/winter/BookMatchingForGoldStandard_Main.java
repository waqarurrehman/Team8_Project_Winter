package com.team8.webdataintegration.winter;

import com.team8.webdataintegration.winter.identityResolution.BookBlockingKeyByTitleGenerator;
import com.team8.webdataintegration.winter.identityResolution.BookTitleComparatorLowerJaccard;
import com.team8.webdataintegration.winter.model.Book;
import com.team8.webdataintegration.winter.model.BookXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.slf4j.Logger;

import java.io.File;

public class BookMatchingForGoldStandard_Main
{
    /*
     * Logging Options:
     * 		default: 	level INFO	- console
     * 		trace:		level TRACE     - console
     * 		infoFile:	level INFO	- console/file
     * 		traceFile:	level TRACE	- console/file
     *
     * To set the log level to trace and write the log to winter.log and console,
     * activate the "traceFile" logger as follows:
     *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
     *
     */

    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main( String[] args ) throws Exception
    {
        // loading data
        logger.info("*\tLoading datasets\t*");
        HashedDataSet<Book, Attribute> fdb = new HashedDataSet<>();
        new BookXMLReader().loadFromXML(new File("usecase/books/input/target_fdb.xml"), "/books/book", fdb);
        HashedDataSet<Book, Attribute> bbe = new HashedDataSet<>();
        new BookXMLReader().loadFromXML(new File("usecase/books/input/target_bbe.xml"), "/books/book", bbe);

        // create a matching rule
        LinearCombinationMatchingRule<Book, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
                0.5);

        // add comparators
        matchingRule.addComparator(new BookTitleComparatorLowerJaccard(), 1);

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Book, Attribute> blocker = new StandardRecordBlocker<Book, Attribute>(new BookBlockingKeyByTitleGenerator());
		//NoBlocker<Book, Attribute> blocker = new NoBlocker<>();
//		SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByTitleGenerator(), 1);
        //blocker.setMeasureBlockSizes(true);

        // Initialize Matching Engine
        MatchingEngine<Book, Attribute> engine = new MatchingEngine<>();

        // Execute the matching
        logger.info("*\tRunning identity resolution\t*");
        Processable<Correspondence<Book, Attribute>> correspondences = engine.runIdentityResolution(
                fdb, bbe, null, matchingRule,
                blocker);

        // Create a top-1 global matching
//		  correspondences = engine.getTopKInstanceCorrespondences(correspondences, 1, 0.0);

//		 Alternative: Create a maximum-weight, bipartite matching
//		 MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
//		 maxWeight.run();
//		 correspondences = maxWeight.getResult();

        // write the correspondences to the output file
        new CSVCorrespondenceFormatter().writeCSV(new File("usecase/result.csv"), correspondences);

    }
}
