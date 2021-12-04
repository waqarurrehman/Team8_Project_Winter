package com.team8.webdataintegration.winter.datafusion.fusers.conflictResolution;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

public class UnionComaSepratedString<RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends
ConflictResolutionFunction<String, RecordType, SchemaElementType>  {

	@Override
	public FusedValue<String, RecordType, SchemaElementType> resolveConflict(
			Collection<FusibleValue<String, RecordType, SchemaElementType>> values) {
		StringBuilder str = new StringBuilder();
		Map<String,String> result = new HashMap<>();
		for (FusibleValue<String, RecordType, SchemaElementType> value : values) {
			for(String a : value.getValue().split(",")) {
				if(!result.containsKey(a.toUpperCase())) {
					result.put(a.toUpperCase(), a);
					if(str.toString().isEmpty()) {
						str.append(a);
					}else {
						str.append(","+a);
					}
				}
			}
		}
		
		
		FusedValue<String, RecordType, SchemaElementType>  fval= new FusedValue<>(str.toString());
		for (FusibleValue<String, RecordType, SchemaElementType> value : values) {
			fval.addOriginalRecord(value);
		}
		
		return fval;
	}

}
