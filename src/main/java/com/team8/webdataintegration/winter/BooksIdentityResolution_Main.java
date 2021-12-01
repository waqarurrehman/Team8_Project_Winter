package com.team8.webdataintegration.winter;

import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.slf4j.Logger;

public class BooksIdentityResolution_Main {

    private static final Logger logger = WinterLogManager.activateLogger("trace");

    private static final String wikiSourcePath = "usecase/books/input/target_wiki.xml";
    private static final String bbeSourcePath = "usecase/books/input/target_bbe.xml";
    private static final String fdbSourcePath = "usecase/books/input/target_fdb.xml";
    private static final String wikiBbeGoldstandardPath = "usecase/books/goldstandard/gs_wiki_2_bbe.csv";
    private static final String fdbBbeGoldstandardPath = "usecase/books/goldstandard/gs_fdb_2_bbe.csv";
    private static final String wikiBbeCorrespondencesPath = "usecase/books/output/Wiki_2_BBE_correspondences.csv";
    private static final String fdbBbeCorrespondencesPath = "usecase/books/output/BBE_2_FDB_correspondences.csv";
    private static final String sXPath_Book = "/books/book";
    private static final String sFusedXMLPath = "usecase/books/output/target_Fused.xml";

    public static void main(String[] args) {

        logger.info("Started  Execution Method Execution");

        BookUseCase _usecase = new BookUseCase(
                wikiSourcePath,
                bbeSourcePath,
                fdbSourcePath,
                wikiBbeGoldstandardPath,
                fdbBbeGoldstandardPath,
                wikiBbeCorrespondencesPath,
                fdbBbeCorrespondencesPath,
                sXPath_Book,
                sFusedXMLPath,
                logger
        );

        try {

            _usecase.RunIdentityResolution();
        } catch (Exception ex) {
            logger.info("Excepiton in Identity Resolution[" + ex.toString() + "]");
            ex.printStackTrace();
        }


        try {
            //_usecase.RunDataFusion();
        } catch (Exception ex) {
            logger.info("Excepiton in DataFusion[" + ex.toString() + "]");
            ex.printStackTrace();
        }


    }

}
