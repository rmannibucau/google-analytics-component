package org.talend.components.google.analytics.dataset;

import java.io.Serializable;

import org.talend.components.google.analytics.datastore.GoogleAnalyticsDataStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@DataSet("GoogleAnalyticsDataSet")
@GridLayout({
    @GridLayout.Row("datastore"),
    @GridLayout.Row("viewId"),
    @GridLayout.Row({ "view", "dimension" }),
    @GridLayout.Row({ "fromDate", "endDate" })
})
@Documentation("TODO fill the documentation for this configuration")
public class GoogleAnalyticsDataSet implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private GoogleAnalyticsDataStore datastore;

    @Option
    @Required
    @Documentation("TBD")
    private String viewId;

    @Option
    @Required
    @Documentation("TBD")
    private View view = View.SESSIONS;

    @Option
    @Required
    @Documentation("TBD")
    private String dimension = "ga:pageTitle";

    @Option
    @Required
    @Documentation("TBD")
    private String fromDate = "7DaysAgo";

    @Option
    @Required
    @Documentation("TBD")
    private String endDate = "today";

    @Getter
    @RequiredArgsConstructor
    public enum View {
        SESSIONS("ga:sessions");

        private final String view;
    }
}