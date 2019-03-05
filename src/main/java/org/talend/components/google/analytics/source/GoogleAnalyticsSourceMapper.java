package org.talend.components.google.analytics.source;

import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.util.List;

import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParserFactory;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

@Version(1)
@Icon(Icon.IconType.STAR)
@PartitionMapper(name = "GoogleAnalyticsSource")
@Documentation("TODO fill the documentation for this mapper")
public class GoogleAnalyticsSourceMapper implements Serializable {
    private final GoogleAnalyticsSourceMapperConfiguration configuration;
    private final RecordBuilderFactory recordBuilderFactory;
    private final JsonGeneratorFactory generatorFactory;
    private final JsonParserFactory parserFactory;

    public GoogleAnalyticsSourceMapper(@Option("configuration") final GoogleAnalyticsSourceMapperConfiguration configuration,
                        final RecordBuilderFactory recordBuilderFactory,
                                       final JsonGeneratorFactory generatorFactory,
                                       final JsonParserFactory parserFactory) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
        this.generatorFactory = generatorFactory;
        this.parserFactory = parserFactory;
    }

    @Assessor
    public long estimateSize() {
        return 1L;
    }

    @Split
    public List<GoogleAnalyticsSourceMapper> split() {
        return singletonList(this);
    }

    @Emitter
    public GoogleAnalyticsSourceSource createWorker() {
        return new GoogleAnalyticsSourceSource(configuration, recordBuilderFactory, generatorFactory, parserFactory);
    }
}