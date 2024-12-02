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

package org.apache.skywalking.oap.server.core.analysis.manual.relation.cache;

import org.apache.skywalking.oap.server.core.analysis.Layer;
import org.apache.skywalking.oap.server.core.analysis.SourceDispatcher;
import org.apache.skywalking.oap.server.core.analysis.metrics.IntList;
import org.apache.skywalking.oap.server.core.analysis.worker.MetricsStreamProcessor;
import org.apache.skywalking.oap.server.core.source.CacheRelation;

public class CacheCallRelationDispatcher implements SourceDispatcher<CacheRelation> {

    @Override
    public void dispatch(CacheRelation source) {
        if (source.getDestLayer() != Layer.CACHE) {
            return;
        }
        serverSide(source);
    }

    private void serverSide(CacheRelation source) {
        CacheRelationClientSideMetrics metrics = new CacheRelationClientSideMetrics();
        metrics.setTimeBucket(source.getTimeBucket());
        metrics.setSourceServiceId(source.getSrcServiceId());
        metrics.setDestServiceId(source.getDestServiceId());
        final IntList componentIds = metrics.getComponentIds();
        componentIds.add(source.getComponentId());
        metrics.setEntityId(source.getEntityId());
        MetricsStreamProcessor.getInstance().in(metrics);
    }
}
