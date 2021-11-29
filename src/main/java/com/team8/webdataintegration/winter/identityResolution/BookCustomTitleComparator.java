package com.team8.webdataintegration.winter.identityResolution;

import com.team8.webdataintegration.winter.model.Book;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinEditDistance;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;

public class BookCustomTitleComparator implements Comparator<Book, Attribute> {

    private static final double WEIGHT_TITLE = 1.0;
    private static final double WEIGHT_SUBTITLE = 1.0;
    private static final double TITLE_COMPARATOR_CUTOFF = 0.5;
    private static final long serialVersionUID = 1L;
    LevenshteinSimilarity levSim = new LevenshteinSimilarity();
    LevenshteinEditDistance lev = new LevenshteinEditDistance();

    private ComparatorLogger comparisonLog;

    @Override
    public double compare(
            Book record1,
            Book record2,
            Correspondence<Attribute, Matchable> schemaCorrespondences) {

        String[] s1 = record1.getTitle().toLowerCase().split(":");
        String[] s2 = record2.getTitle().toLowerCase().split(":");

        if (this.comparisonLog != null) {
            this.comparisonLog.setComparatorName(getClass().getName());
            this.comparisonLog.setRecord1Value(record1.getTitle());
            this.comparisonLog.setRecord2Value(record2.getTitle());
        }

        double similarity;

        if (s1.length == 1 && s2.length == 1) {
            similarity = compareBookTitle(s1[0], s2[0]);
        } else if (s1.length > 1 && s2.length == 1 || s1.length == 1 && s2.length > 1) {
            similarity = compareBookTitle(s1[0], s2[0]);
            if(similarity != 0) {
                similarity = (WEIGHT_TITLE * similarity + WEIGHT_SUBTITLE) / (WEIGHT_TITLE + WEIGHT_SUBTITLE) ;
            }
        } else {
            double sim1 = compareBookTitle(s1[0], s2[0]);
            double sim2 = compareBookTitle(s1[1], s2[1]);
            if(sim1 == 0 || sim2 == 0) {
                similarity = 0;
            } else {
                similarity = (WEIGHT_TITLE * sim1 + WEIGHT_SUBTITLE * sim2) / (WEIGHT_TITLE + WEIGHT_SUBTITLE);
            }
        }

        if (this.comparisonLog != null) {
            this.comparisonLog.setSimilarity(Double.toString(similarity));
        }
        return similarity;
    }

    @Override
    public ComparatorLogger getComparisonLog() {
        return this.comparisonLog;
    }

    @Override
    public void setComparisonLog(ComparatorLogger comparatorLog) {
        this.comparisonLog = comparatorLog;
    }

    public double compareBookTitle(String titleA, String titleB) {
        titleA = titleA.replaceAll("[^a-zA-Z0-9 ]", "");
        titleB = titleB.replaceAll("[^a-zA-Z0-9 ]", "");
        String[] tokensA = titleA.split(" ");
        String[] tokensB = titleB.split(" ");
        double sim = 0;

        String end = ".*(i|x|v|[0-9])";
        if(titleA.matches(end) && !titleB.matches(end) || !titleA.matches(end) && titleB.matches(end)) {
            return 0;
        }
        if(tokensA.length != tokensB.length) {
            if(lev.calculate(titleA, titleB) < Math.max(titleA.length(), titleB.length()) / 4) {
                return levSim.calculate(titleA, titleB);
            } else {
                return 0;
            }
        } else {
            double sum = 0;
            for(int i = 0; i<tokensA.length; i++) {
                double s = levSim.calculate(tokensA[i], tokensB[i]);
                if(Double.isNaN(s)) {
                    s = 1;
                }
                if(s < TITLE_COMPARATOR_CUTOFF) {
                    return 0;
                } else {
                    sum += s;
                }
            }
            sim = sum / (1.0 * tokensA.length);
        }
        return sim;
    }

}