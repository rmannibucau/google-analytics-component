/**
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.components.google.analytics.source;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.talend.components.google.analytics.dataset.GoogleAnalyticsDataSet;
import org.talend.components.google.analytics.datastore.GoogleAnalyticsDataStore;
import org.talend.sdk.component.api.DecryptedServer;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit.ComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.WithMavenServers;
import org.talend.sdk.component.maven.Server;
import org.talend.sdk.component.runtime.manager.chain.Job;

@WithMavenServers
@WithComponents("org.talend.components.google.analytics")
class GoogleAnalyticsSourceSourceTest {

    @Injected
    private ComponentsHandler handler;

    // put the google credentials file content in your settings.xml (encrypted) and username=viewid
    @DecryptedServer("google-analytics")
    private Server credentials;

    @Test // todo: add a http mock
    void read() {
        final GoogleAnalyticsDataStore datastore = new GoogleAnalyticsDataStore();
        datastore.setApplication("Demo-Tacokit-Kickoff2018");
        datastore.setCredentials(credentials.getPassword());

        final GoogleAnalyticsDataSet dataset = new GoogleAnalyticsDataSet();
        dataset.setViewId(credentials.getUsername());
        dataset.setDatastore(datastore);
        dataset.setDimension("ga:pageTitle");
        dataset.setView(GoogleAnalyticsDataSet.View.SESSIONS);
        dataset.setFromDate("7DaysAgo");
        dataset.setEndDate("today");

        final GoogleAnalyticsSourceMapperConfiguration configuration = new GoogleAnalyticsSourceMapperConfiguration();
        configuration.setDataset(dataset);

        final String config = configurationByExample().forInstance(configuration).configured().toQueryString();

        Job.components()
           .component("source", "GoogleAnalytics://GoogleAnalyticsSource?" + config)
           .component("test", "test://collector")
           .connections()
           .from("source").to("test")
           .build()
           .run();
        final List<Record> records = handler.getCollectedData(Record.class);
        System.out.println(records); // for demo purposes
        assertTrue(records.size() > 1);
    }
}
