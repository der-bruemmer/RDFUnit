package org.aksw.rdfunit.statistics;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.utils.UriToPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Provides statistics on the namespaces used in a dataset
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 1:20 PM
 * @version $Id: $Id
 */
public final class NamespaceStatistics {

    private static final Logger log = LoggerFactory.getLogger(NamespaceStatistics.class);

    private final boolean skipUnknownNamespaces;

    private final Collection<String> excludePrefixes = Arrays.asList("rdf", "rdfs", "owl", "rdfa");
    private final Collection<DatasetStatistics> datasetStatistics;

    private NamespaceStatistics(Collection<DatasetStatistics> datasetStatisticses, boolean skipUnknownNamespaces) {

        this.datasetStatistics = Collections.unmodifiableCollection(datasetStatisticses);
        this.skipUnknownNamespaces = skipUnknownNamespaces;
    }

    /**
     * <p>createOntologyNSStatisticsKnown.</p>
     *
     * @return a {@link org.aksw.rdfunit.statistics.NamespaceStatistics} object.
     */
    public static NamespaceStatistics createOntologyNSStatisticsKnown() {
        Collection<DatasetStatistics> datasetStatistics = Arrays.asList(new DatasetStatisticsClasses(), new DatasetStatisticsProperties());
        return new NamespaceStatistics(datasetStatistics, true);
    }

    /**
     * <p>createOntologyNSStatisticsAll.</p>
     *
     * @return a {@link org.aksw.rdfunit.statistics.NamespaceStatistics} object.
     */
    public static NamespaceStatistics createOntologyNSStatisticsAll() {
        Collection<DatasetStatistics> datasetStatistics = Arrays.asList(new DatasetStatisticsClasses(), new DatasetStatisticsProperties());
        return new NamespaceStatistics(datasetStatistics, false);
    }

    /**
     * <p>createCompleteNSStatisticsKnown.</p>
     *
     * @return a {@link org.aksw.rdfunit.statistics.NamespaceStatistics} object.
     */
    public static NamespaceStatistics createCompleteNSStatisticsKnown() {
        Collection<DatasetStatistics> datasetStatistics = new ArrayList<>();
        datasetStatistics.add(new DatasetStatisticsAllIris());
        return new NamespaceStatistics(datasetStatistics, true);
    }

    /**
     * <p>createCompleteNSStatisticsAll.</p>
     *
     * @return a {@link org.aksw.rdfunit.statistics.NamespaceStatistics} object.
     */
    public static NamespaceStatistics createCompleteNSStatisticsAll() {
        Collection<DatasetStatistics> datasetStatistics = new ArrayList<>();
        datasetStatistics.add(new DatasetStatisticsAllIris());
        return new NamespaceStatistics(datasetStatistics, false);
    }


    /**
     * <p>getNamespaces.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @return a {@link java.util.Collection} object.
     */
    public Collection<SchemaSource> getNamespaces(QueryExecutionFactory qef) {

        Set<String> namespaces = new HashSet<>();

        for (DatasetStatistics dt : datasetStatistics) {

            for (String n : dt.getStatisticsMap(qef).keySet()) {
                namespaces.add(getNamespaceFromURI(n));
            }

        }

        return getIdentifiedSchemata(namespaces);
    }


    private Collection<SchemaSource> getIdentifiedSchemata(Collection<String> namespaces) {
        Collection<SchemaSource> sources = new ArrayList<>();

        for (String namespace : namespaces) {

            SchemaSource source = SchemaService.getSourceFromUri(namespace);

            // If not null, get it from SchemaService
            if (source != null) {

                // Skip some schemas that don't add anything
                if (excludePrefixes.contains(source.getPrefix())) {
                    continue;
                }
                sources.add(source);
            } else {
                if (skipUnknownNamespaces) {
                    log.warn("Undefined namespace in LOV or schemaDecl.csv: " + namespace);
                } else {
                    sources.add(SchemaSourceFactory.createSchemaSourceDereference(UriToPathUtils.getAutoPrefixForURI(namespace), namespace));
                }
            }
        }

        return sources;
    }

    /**
     * Gets namespace from uRI.
     *
     * @param uri the uri
     * @return the namespace from uRI
     */
    public String getNamespaceFromURI(String uri) {
        String breakChar = "/";
        if (uri.contains("#")) {
            breakChar = "#";
        }

        int pos = Math.min(uri.lastIndexOf(breakChar), uri.length());
        return uri.substring(0, pos + 1);
    }
}
