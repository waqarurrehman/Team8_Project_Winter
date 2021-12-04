package com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution;

import java.util.Collection;

import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

/**
 * Average {@link ConflictResolutionFunction}: Returns true if one of the values is true
 * 
 * @author Annalena Sailer
 * 
 * @param <RecordType>	the type that represents a record
 */

public class LogicalOr<RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends
		ConflictResolutionFunction<String, RecordType, SchemaElementType> {

	@Override
	public FusedValue<String, RecordType, SchemaElementType> resolveConflict(
			Collection<FusibleValue<String, RecordType, SchemaElementType>> values) {
		FusibleValue<String, RecordType, SchemaElementType> result = null;

		for (FusibleValue<String, RecordType, SchemaElementType> value : values) {
			if (result == null || value.getValue() == "Y") {
				result = value;
			}
		}

		return new FusedValue<>(result);
	}

}
