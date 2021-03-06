package com.team8.webdataintegration.winter.identityResolution;

import com.team8.webdataintegration.winter.model.Author;
import com.team8.webdataintegration.winter.model.Book;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BookAuthorComparatorJaroWinkler implements Comparator<Book, Attribute> {

	private static final long serialVersionUID = 1L;
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(Book record1, Book record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
		double sim = apply(record1.getAuthors().get(0).getAuthor_name(), record2.getAuthors().get(0).getAuthor_name());
		if(comparisonLog != null) {
			comparisonLog.setRecord1Value(record1.getAuthors().get(0).getAuthor_name());
			comparisonLog.setRecord2Value(record2.getAuthors().get(0).getAuthor_name());
			comparisonLog.setSimilarity(Double.toString(sim));
		}
		return sim;
	}

	public Double apply(final CharSequence left, final CharSequence right) {
		final double defaultScalingFactor = 0.1;
		final double percentageRoundValue = 100.0;

		if (left == null || right == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		int[] mtp = matches(left, right);
		double m = mtp[0];
		if (m == 0) {
			return 0D;
		}
		double j = ((m / left.length() + m / right.length() + (m - mtp[1]) / m)) / 3;
		double jw = j < 0.7D ? j : j + Math.min(defaultScalingFactor, 1D / mtp[3]) * mtp[2] * (1D - j);
		return Math.round(jw * percentageRoundValue) / percentageRoundValue;
	}

	/**
	 * This method returns the Jaro-Winkler string matches, transpositions, prefix, max array.
	 *
	 * @param first the first string to be matched
	 * @param second the second string to be machted
	 * @return mtp array containing: matches, transpositions, prefix, and max length
	 */
	protected static int[] matches(final CharSequence first, final CharSequence second) {
		CharSequence max, min;
		if (first.length() > second.length()) {
			max = first;
			min = second;
		} else {
			max = second;
			min = first;
		}
		int range = Math.max(max.length() / 2 - 1, 0);
		int[] matchIndexes = new int[min.length()];
		Arrays.fill(matchIndexes, -1);
		boolean[] matchFlags = new boolean[max.length()];
		int matches = 0;
		for (int mi = 0; mi < min.length(); mi++) {
			char c1 = min.charAt(mi);
			for (int xi = Math.max(mi - range, 0), xn = Math.min(mi + range + 1, max.length()); xi < xn; xi++) {
				if (!matchFlags[xi] && c1 == max.charAt(xi)) {
					matchIndexes[mi] = xi;
					matchFlags[xi] = true;
					matches++;
					break;
				}
			}
		}
		char[] ms1 = new char[matches];
		char[] ms2 = new char[matches];
		for (int i = 0, si = 0; i < min.length(); i++) {
			if (matchIndexes[i] != -1) {
				ms1[si] = min.charAt(i);
				si++;
			}
		}
		for (int i = 0, si = 0; i < max.length(); i++) {
			if (matchFlags[i]) {
				ms2[si] = max.charAt(i);
				si++;
			}
		}
		int transpositions = 0;
		for (int mi = 0; mi < ms1.length; mi++) {
			if (ms1[mi] != ms2[mi]) {
				transpositions++;
			}
		}
		int prefix = 0;
		for (int mi = 0; mi < min.length(); mi++) {
			if (first.charAt(mi) == second.charAt(mi)) {
				prefix++;
			} else {
				break;
			}
		}
		return new int[] { matches, transpositions / 2, prefix, max.length() };
	}
	
	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}
}
