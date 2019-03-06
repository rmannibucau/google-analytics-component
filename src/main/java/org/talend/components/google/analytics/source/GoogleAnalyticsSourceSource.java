package org.talend.components.google.analytics.source;

import static java.util.Collections.emptyIterator;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.talend.sdk.component.api.record.Schema.Type.ARRAY;
import static org.talend.sdk.component.api.record.Schema.Type.RECORD;
import static org.talend.sdk.component.api.record.Schema.Type.STRING;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParserFactory;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import com.github.rmannibucau.google.json.JsonpJsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.ColumnHeader;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.MetricHeaderEntry;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;

@Documentation("TODO fill the documentation for this source")
public class GoogleAnalyticsSourceSource implements Serializable {

    private final GoogleAnalyticsSourceMapperConfiguration configuration;

    private final RecordBuilderFactory builderFactory;

    private final JsonGeneratorFactory generatorFactory;

    private final JsonParserFactory parserFactory;

    private volatile Iterator<Record> reports;

    public GoogleAnalyticsSourceSource(@Option("configuration") final GoogleAnalyticsSourceMapperConfiguration configuration,
            final RecordBuilderFactory builderFactory, final JsonGeneratorFactory generatorFactory,
            final JsonParserFactory parserFactory) {
        this.configuration = configuration;
        this.builderFactory = builderFactory;
        this.generatorFactory = generatorFactory;
        this.parserFactory = parserFactory;
    }

    @Producer
    public Record next() throws Exception {
        if (reports == null) {
            reports = loadReports();
        }
        if (!reports.hasNext()) {
            return null;
        }
        return reports.next();
    }

    private Iterator<Record> loadReports() throws Exception {
        final JsonFactory jsonFactory = JsonpJsonFactory.of(parserFactory, generatorFactory);
        final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final GoogleCredential credential = GoogleCredential.fromStream(
                new ByteArrayInputStream(
                        configuration.getDataset().getDatastore().getCredentials().getBytes(StandardCharsets.UTF_8)),
                httpTransport, jsonFactory).createScoped(AnalyticsReportingScopes.all());
        final AnalyticsReporting reporting = new AnalyticsReporting.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(configuration.getDataset().getDatastore().getApplication()).build();

        final DateRange dateRange = new DateRange();
        dateRange.setStartDate(configuration.getDataset().getFromDate());
        dateRange.setEndDate(configuration.getDataset().getEndDate());

        final String view = configuration.getDataset().getView().getView();
        final Metric sessions = new Metric().setExpression(view).setAlias(view.replace("ga:", ""));

        final Dimension pageTitle = new Dimension().setName(configuration.getDataset().getDimension());

        final ReportRequest request = new ReportRequest().setViewId(configuration.getDataset().getViewId())
                .setDateRanges(singletonList(dateRange)).setMetrics(singletonList(sessions))
                .setDimensions(singletonList(pageTitle));

        final GetReportsRequest getReport = new GetReportsRequest().setReportRequests(singletonList(request));

        final GetReportsResponse response = reporting.reports().batchGet(getReport).execute();
        final Collection<Record> records = new ArrayList<>(
                response.getReports().stream().mapToInt(r -> r.getData().getRowCount()).sum());
        for (final Report report : response.getReports()) {
            final List<ReportRow> rows = report.getData().getRows();
            if (rows == null) {
                return emptyIterator();
            }

            final ColumnHeader header = report.getColumnHeader();
            final List<String> dimensionHeaders = header.getDimensions().stream().map(this::normalize).collect(toList());
            final List<String> metricHeaders = header.getMetricHeader().getMetricHeaderEntries().stream()
                        .map(MetricHeaderEntry::getName).map(this::normalize).map(it -> "metric_" + it).collect(toList());
            final Schema schema = createSchema(dimensionHeaders, metricHeaders);
            final Map<String, Schema.Entry> entries = schema.getEntries().stream()
                    .collect(toMap(Schema.Entry::getName, identity()));

            records.addAll(rows.stream().map(row -> fillRecord(dimensionHeaders, metricHeaders, entries, schema, row))
                    .collect(toList()));
        }
        return records.iterator();
    }

    private Record fillRecord(final List<String> dimensionHeaders, final List<String> metricHeaders,
            final Map<String, Schema.Entry> entries, final Schema schema, final ReportRow row) {
        final Record.Builder builder = builderFactory.newRecordBuilder(schema);
        for (int i = 0; i < dimensionHeaders.size(); i++) {
            builder.withString(entries.get(dimensionHeaders.get(i)), row.getDimensions().get(i));
        }
        for (int i = 0; i < metricHeaders.size(); i++) {
            builder.withArray(entries.get(metricHeaders.get(i)),
                    Collection.class.cast(row.getMetrics().get(i).getValues()));
        }
        return builder.build();
    }

    private Schema createSchema(final Collection<String> dimensionHeaders, final List<String> metricHeaders) {
        final Schema.Builder builder = builderFactory.newSchemaBuilder(RECORD);
        dimensionHeaders.stream().map(this::normalize)
                .forEach(name -> builder.withEntry(builderFactory.newEntryBuilder().withType(STRING).withName(name).build()));
        metricHeaders.forEach(name -> builder.withEntry(builderFactory.newEntryBuilder().withType(ARRAY)
                .withElementSchema(builderFactory.newSchemaBuilder(STRING).build())
                .withName(name).build()));
        return builder.build();
    }

    private String normalize(final String s) {
        return s.replace(':', '_');
    }
}