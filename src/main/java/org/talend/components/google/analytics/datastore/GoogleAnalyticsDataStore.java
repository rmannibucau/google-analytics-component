package org.talend.components.google.analytics.datastore;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore("GoogleAnalyticsDataStore")
@OptionsOrder({
    "application",
    "credentials"
})
@Documentation("TODO fill the documentation for this configuration")
public class GoogleAnalyticsDataStore implements Serializable {
    @Option
    @Credential
    @Documentation("TBD")
    private String credentials;

    @Option
    @Documentation("TBD")
    private String application;
}