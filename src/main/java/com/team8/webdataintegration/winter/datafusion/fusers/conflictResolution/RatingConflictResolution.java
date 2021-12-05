package com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution;

import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

import java.util.Collection;

public class RatingConflictResolution<RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends
ConflictResolutionFunction<Double, RecordType, SchemaElementType>  {

	@Override
	public FusedValue<Double, RecordType, SchemaElementType> resolveConflict(
			Collection<FusibleValue<Double, RecordType, SchemaElementType>> values) {
		double i = 0;
		double sum = 0;
		for(FusibleValue<Double, RecordType, SchemaElementType> value : values) {
			if(value.getValue() != 0){
				sum += (Double) value.getValue();
				i++;
			}
		}

		FusedValue<Double, RecordType, SchemaElementType>  fval= new FusedValue<>(sum / i);

		
		return fval;
	}

}
