package com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution;

import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PublisherConflictResolution<RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends
ConflictResolutionFunction<String, RecordType, SchemaElementType>  {

	@Override
	public FusedValue<String, RecordType, SchemaElementType> resolveConflict(
			Collection<FusibleValue<String, RecordType, SchemaElementType>> values) {
		double max = 0;
		String result = "";
		FusibleValue<String, RecordType, SchemaElementType> provenance = null;
		for (FusibleValue<String, RecordType, SchemaElementType> value : values) {
			if(value.getDataset().getScore() > max) {
				max = value.getDataset().getScore();
				result = value.getValue();
				provenance = value;
			}
		}

		FusedValue<String, RecordType, SchemaElementType>  fval= new FusedValue<>(result);
		if(provenance != null) {
			fval.addOriginalRecord(provenance);
		}
		
		return fval;
	}

}
