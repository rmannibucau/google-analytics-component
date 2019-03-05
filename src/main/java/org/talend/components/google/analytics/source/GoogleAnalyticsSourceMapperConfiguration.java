package org.talend.components.google.analytics.source;

import java.io.Serializable;

import org.talend.components.google.analytics.dataset.GoogleAnalyticsDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@OptionsOrder({
    "dataset"
})
@Documentation("TODO fill the documentation for this configuration")
public class GoogleAnalyticsSourceMapperConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private GoogleAnalyticsDataSet dataset;
}