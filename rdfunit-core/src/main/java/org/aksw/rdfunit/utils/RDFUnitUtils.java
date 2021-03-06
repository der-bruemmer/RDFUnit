package org.aksw.rdfunit.utils;

import org.aksw.rdfunit.prefix.LOVEndpoint;
import org.aksw.rdfunit.prefix.SchemaEntry;
import org.aksw.rdfunit.services.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;

/**
 * <p>RDFUnitUtils class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/24/13 11:25 AM
 * @version $Id: $Id
 */
public final class RDFUnitUtils {
    private static final Logger log = LoggerFactory.getLogger(RDFUnitUtils.class);

    private RDFUnitUtils() {
    }

    /**
     * <p>fillSchemaServiceFromFile.</p>
     *
     * @param additionalCSV a {@link java.lang.String} object.
     */
    public static void fillSchemaServiceFromFile(String additionalCSV) {

        try (InputStream inputStream = new FileInputStream(additionalCSV)) {
            fillSchemaServiceFromFile(inputStream);
        } catch (IOException e) {
            log.error("File " + additionalCSV + " not fount!", e);
        }
    }

    /**
     * <p>fillSchemaServiceFromFile.</p>
     *
     * @param additionalCSV a {@link java.io.InputStream} object.
     */
    public static void fillSchemaServiceFromFile(InputStream additionalCSV) {

        int count = 0;

        if (additionalCSV != null) {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(additionalCSV, "UTF-8"))) {

                String line;

                while ((line = in.readLine()) != null) {
                    // skip comments & empty lines
                    if (line.startsWith("#") || line.trim().isEmpty()) {
                        continue;
                    }

                    count++;

                    String[] parts = line.split(",");
                    switch (parts.length) {
                        case 2:
                            SchemaService.addSchemaDecl(parts[0], parts[1]);
                            break;
                        case 3:
                            SchemaService.addSchemaDecl(parts[0], parts[1], parts[2]);
                            break;
                        default:
                            log.error("Invalid schema declaration in " + additionalCSV + ". Line: " + line);
                            count--;
                    }
                }

            } catch (UnsupportedEncodingException e) {
                log.debug("UnsupportedEncodingException: ", e);
                return;
            } catch (IOException e) {
                log.debug("IOException: ", e);
            }

            log.info("Loaded " + count + " schema declarations from: " + additionalCSV);
        }

        if (additionalCSV != null) {
            try {
                additionalCSV.close();
            } catch (IOException e) {
                log.debug("IOException: ", e);
            }
        }
    }

    /**
     * <p>fillSchemaServiceFromLOV.</p>
     */
    public static void fillSchemaServiceFromLOV() {

        int count = SchemaService.getSize();
        for (SchemaEntry entry : new LOVEndpoint().getAllLOVEntries()) {
            SchemaService.addSchemaDecl(entry.getPrefix(), entry.getVocabularyNamespace(), entry.getVocabularyDefinedBy());
        }

        count = SchemaService.getSize() - count;

        log.info("Loaded " + count + " additional schema declarations from LOV SPARQL Endpoint");
    }



    /**
     * <p>getFirstItemInCollection.</p>
     *
     * @param collection a {@link java.util.Collection} object.
     * @param <T> a T object.
     * @return a T object.
     */
    public static <T> T getFirstItemInCollection(Collection<T> collection) {
        //noinspection LoopStatementThatDoesntLoop
        for (T item : collection) {
            return item;
        }
        return null;
    }
}
