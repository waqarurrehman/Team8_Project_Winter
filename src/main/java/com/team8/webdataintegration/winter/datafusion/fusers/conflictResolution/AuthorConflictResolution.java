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
package com.team8.webdataintegration.winter.datafusion.fusers.conflictresolution;

import java.util.*;

import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

public class AuthorConflictResolution<ValueType, RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends
        ConflictResolutionFunction<List<ValueType>, RecordType, SchemaElementType> {

    @Override
    public FusedValue<List<ValueType>, RecordType, SchemaElementType> resolveConflict(
            Collection<FusibleValue<List<ValueType>, RecordType, SchemaElementType>> values) {

        List<ValueType> longest = null;
        for (FusibleValue<List<ValueType>, RecordType, SchemaElementType> value : values) {
            if(longest == null) {
                longest = value.getValue();
            } else {
                if( value.getValue().size() > longest.size()) {
                    longest = value.getValue();
                }
            }
        }

        FusedValue<List<ValueType>, RecordType, SchemaElementType> fused = new FusedValue<>(longest);

        for (FusibleValue<List<ValueType>, RecordType, SchemaElementType> value : values) {
            fused.addOriginalRecord(value);
        }

        return fused;
    }

}
