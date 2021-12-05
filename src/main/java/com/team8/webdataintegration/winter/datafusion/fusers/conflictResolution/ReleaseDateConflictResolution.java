package com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution;

import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

import java.time.LocalDateTime;
import java.util.Collection;

public class ReleaseDateConflictResolution<RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends
ConflictResolutionFunction<LocalDateTime, RecordType, SchemaElementType>  {

	@Override
	public FusedValue<LocalDateTime, RecordType, SchemaElementType> resolveConflict(
			Collection<FusibleValue<LocalDateTime, RecordType, SchemaElementType>> values) {


		LocalDateTime earliest = null;
		FusibleValue<LocalDateTime, RecordType, SchemaElementType> provenance = null;
		for(FusibleValue<LocalDateTime, RecordType, SchemaElementType> value : values) {
			if(earliest == null) {
				if(earliest == null) {
					earliest = value.getValue();
					provenance = value;
				}
				if(value.getValue().isBefore(earliest)) {
					earliest = value.getValue();
					provenance = value;
				}
			}
		}

		FusedValue<LocalDateTime, RecordType, SchemaElementType>  fval= new FusedValue<>(earliest);

		if(provenance != null) {
			fval.addOriginalRecord(provenance);
		}
		
		return fval;
	}

}
