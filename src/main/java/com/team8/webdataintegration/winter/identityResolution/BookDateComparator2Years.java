/*
 * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.team8.webdataintegration.winter.identityResolution;

import com.team8.webdataintegration.winter.model.Book;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;

public class BookDateComparator2Years implements Comparator<Book, Attribute> {

	private static final long serialVersionUID = 1L;
	private YearSimilarity sim = new YearSimilarity(2);
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Book record1,
			Book record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {

		double similarity = 1;
		if(record1.getRelease_date() != null && record2.getRelease_date() != null) {
			similarity = sim.calculate(record1.getRelease_date(), record2.getRelease_date());
		}

		if(comparisonLog != null) {
			if(record1.getRelease_date() != null) {
				comparisonLog.setRecord1Value(record1.getRelease_date().toString());
			}
			if(record2.getRelease_date() != null) {
				comparisonLog.setRecord2Value(record2.getRelease_date().toString());
			}
			comparisonLog.setSimilarity(Double.toString(similarity));
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

	@Override
	public String getName(Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return Comparator.super.getName(schemaCorrespondence);
	}

}
