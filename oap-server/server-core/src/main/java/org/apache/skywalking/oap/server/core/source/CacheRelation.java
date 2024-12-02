/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.server.core.source;

import lombok.Getter;
import lombok.Setter;
import org.apache.skywalking.oap.server.core.analysis.IDManager;
import org.apache.skywalking.oap.server.core.analysis.Layer;

import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.CACHE_RELATION;
import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.CACHE_RELATION_CATALOG_NAME;

@ScopeDeclaration(id = CACHE_RELATION, name = "CacheRelation", catalog = CACHE_RELATION_CATALOG_NAME)
@ScopeDefaultColumn.VirtualColumnDefinition(fieldName = "entityId", columnName = "entity_id", isID = true, type = String.class)
public class CacheRelation extends Source {

    @Override
    public int scope() {
        return DefaultScopeDefine.CACHE_RELATION;
    }

    @Getter
    @Setter
    private String srcServiceId;
    @Getter
    @Setter
    @ScopeDefaultColumn.DefinedByField(columnName = "source_name", requireDynamicActive = true)
    private String sourceServiceName;
    @Getter
    @Setter
    @ScopeDefaultColumn.DefinedByField(columnName = "dest_name", requireDynamicActive = true)
    private String destServiceName;
    @Getter
    @Setter
    private String destServiceId;
    @Getter
    @Setter
    private int componentId;
    @Getter
    @Setter
    private Layer sourceLayer;
    @Getter
    @Setter
    private Layer destLayer;

    @Getter
    @Setter
    private int latency;
    @Getter
    @Setter
    private boolean status;
    @Getter
    @Setter
    private DetectPoint detectPoint;

    @Override
    public String getEntityId() {
        return IDManager.ServiceID.buildRelationId(new IDManager.ServiceID.ServiceRelationDefine(srcServiceId, destServiceId));
    }

    @Override
    public void prepare() {
        srcServiceId = IDManager.ServiceID.buildId(sourceServiceName, sourceLayer.isNormal());
        destServiceId = IDManager.ServiceID.buildId(destServiceName, destLayer.isNormal());
    }
}

