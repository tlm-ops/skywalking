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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.skywalking.oap.server.core.analysis.MetricsExtension;
import org.apache.skywalking.oap.server.core.analysis.Stream;
import org.apache.skywalking.oap.server.core.analysis.metrics.IntList;
import org.apache.skywalking.oap.server.core.analysis.metrics.Metrics;
import org.apache.skywalking.oap.server.core.analysis.worker.MetricsStreamProcessor;
import org.apache.skywalking.oap.server.core.remote.grpc.proto.RemoteData;
import org.apache.skywalking.oap.server.core.source.DefaultScopeDefine;
import org.apache.skywalking.oap.server.core.storage.StorageID;
import org.apache.skywalking.oap.server.core.storage.annotation.BanyanDB;
import org.apache.skywalking.oap.server.core.storage.annotation.Column;
import org.apache.skywalking.oap.server.core.storage.annotation.ElasticSearch;
import org.apache.skywalking.oap.server.core.storage.type.Convert2Entity;
import org.apache.skywalking.oap.server.core.storage.type.Convert2Storage;
import org.apache.skywalking.oap.server.core.storage.type.StorageBuilder;

@Stream(name = CacheRelationClientSideMetrics.INDEX_NAME, scopeId = DefaultScopeDefine.CACHE_RELATION,
    builder = CacheRelationClientSideMetrics.Builder.class, processor = MetricsStreamProcessor.class)
@MetricsExtension(supportDownSampling = true, supportUpdate = false, timeRelativeID = true)
@EqualsAndHashCode(of = {
    "entityId"
}, callSuper = true)
public class CacheRelationClientSideMetrics extends Metrics {

    public static final String INDEX_NAME = "cache_relation_client_side";
    public static final String SOURCE_SERVICE_ID = "source_service_id";
    public static final String DEST_SERVICE_ID = "dest_service_id";
    public static final String COMPONENT_IDS = "component_ids";

    @Setter
    @Getter
    @Column(name = SOURCE_SERVICE_ID)
    private String sourceServiceId;
    @Setter
    @Getter
    @Column(name = DEST_SERVICE_ID)
    private String destServiceId;
    @Setter
    @Getter
    @Column(name = COMPONENT_IDS, storageOnly = true)
    @ElasticSearch.Keyword
    @BanyanDB.SeriesID(index = 1)
    private IntList componentIds = new IntList(3);
    @Setter
    @Getter
    @Column(name = ENTITY_ID, length = 512)
    @BanyanDB.SeriesID(index = 0)
    private String entityId;

    @Override
    protected StorageID id0() {
        return new StorageID()
            .append(TIME_BUCKET, getTimeBucket())
            .append(ENTITY_ID, getEntityId());
    }

    @Override
    public boolean combine(Metrics metrics) {
        return true;
    }

    @Override
    public void calculate() {

    }

    @Override
    public Metrics toHour() {
        CacheRelationClientSideMetrics metrics = new CacheRelationClientSideMetrics();
        metrics.setTimeBucket(toTimeBucketInHour());
        metrics.setSourceServiceId(getSourceServiceId());
        metrics.setDestServiceId(getDestServiceId());
        metrics.getComponentIds().copyFrom(getComponentIds());
        metrics.setEntityId(getEntityId());
        return metrics;
    }

    @Override
    public Metrics toDay() {
        CacheRelationClientSideMetrics metrics = new CacheRelationClientSideMetrics();
        metrics.setTimeBucket(toTimeBucketInDay());
        metrics.setSourceServiceId(getSourceServiceId());
        metrics.setDestServiceId(getDestServiceId());
        metrics.getComponentIds().copyFrom(getComponentIds());
        metrics.setEntityId(getEntityId());
        return metrics;
    }

    @Override
    public int remoteHashCode() {
        int n = 17;
        n = 31 * n + this.entityId.hashCode();
        return n;
    }

    @Override
    public void deserialize(RemoteData remoteData) {
        setComponentIds(new IntList(remoteData.getDataStrings(3)));

        setTimeBucket(remoteData.getDataLongs(0));

        setEntityId(remoteData.getDataStrings(0));
        setSourceServiceId(remoteData.getDataStrings(1));
        setDestServiceId(remoteData.getDataStrings(2));
    }

    @Override
    public RemoteData.Builder serialize() {
        RemoteData.Builder remoteBuilder = RemoteData.newBuilder();
        remoteBuilder.addDataStrings(getComponentIds().toStorageData());

        remoteBuilder.addDataLongs(getTimeBucket());

        remoteBuilder.addDataStrings(getEntityId());
        remoteBuilder.addDataStrings(getSourceServiceId());
        remoteBuilder.addDataStrings(getDestServiceId());
        return remoteBuilder;
    }

    public static class Builder implements StorageBuilder<CacheRelationClientSideMetrics> {
        @Override
        public CacheRelationClientSideMetrics storage2Entity(final Convert2Entity converter) {
            CacheRelationClientSideMetrics metrics = new CacheRelationClientSideMetrics();
            metrics.setSourceServiceId((String) converter.get(SOURCE_SERVICE_ID));
            metrics.setDestServiceId((String) converter.get(DEST_SERVICE_ID));
            metrics.setComponentIds(new IntList((String) converter.get(COMPONENT_IDS)));
            metrics.setTimeBucket(((Number) converter.get(TIME_BUCKET)).longValue());
            metrics.setEntityId((String) converter.get(ENTITY_ID));
            return metrics;
        }

        @Override
        public void entity2Storage(final CacheRelationClientSideMetrics storageData,
                                   final Convert2Storage converter) {
            converter.accept(SOURCE_SERVICE_ID, storageData.getSourceServiceId());
            converter.accept(DEST_SERVICE_ID, storageData.getDestServiceId());
            converter.accept(COMPONENT_IDS, storageData.getComponentIds());
            converter.accept(TIME_BUCKET, storageData.getTimeBucket());
            converter.accept(ENTITY_ID, storageData.getEntityId());
        }
    }
}
