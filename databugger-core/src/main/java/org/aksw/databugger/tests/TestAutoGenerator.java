package org.aksw.databugger.tests;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import org.aksw.databugger.Utils;
import org.aksw.databugger.enums.TestGeneration;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternParameter;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.sources.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * User: Dimitris Kontokostas
 * Takes a sparqlPattern and a SPARQL query and based on the data
 * returned from that query we generate test cases
 * Created: 9/20/13 2:48 PM
 */
public class TestAutoGenerator {
    protected static Logger log = LoggerFactory.getLogger(TestAutoGenerator.class);

    final public String URI;
    final public String description;
    final public String query;
    final public String patternID;

    public TestAutoGenerator(String uri, String description, String query, String patternID) {
        this.URI = uri;
        this.description = description;
        this.query = query;
        this.patternID = patternID;
    }

    /**
     * Checks if the the generator is valid (provides correct parameters)
     * */
    public boolean isValid() {
        Pattern pattern = PatternService.getPattern(patternID);
        Query q = null;
        if ( pattern == null ) {
            log.error(URI + " : Pattern " + patternID + " does not exist");
            return false;
        }
        try {
            q = QueryFactory.create(Utils.getAllPrefixes() + query);
        } catch (Exception e) {
            log.error(URI + " Cannot parse query");
            return false;
        }

        List<Var> sv = q.getProjectVars();
        if (sv.size() != pattern.parameters.size()) {
            log.error(URI + " Select variables are different than Pattern parameters");
            return false;
        }


        return true;
    }

    public List<UnitTest> generate(Source source) {
        List<UnitTest> tests = new ArrayList<UnitTest>();

        Query q = QueryFactory.create(Utils.getAllPrefixes() + query);
        QueryExecution qe = source.getExecutionFactory().createQueryExecution(q);
        ResultSet rs = qe.execSelect();
        Pattern pattern = PatternService.getPattern(patternID);

        while (rs.hasNext()) {
            QuerySolution row = rs.next();

            List<String> bindings = new ArrayList<String>();
            List<String> references = new ArrayList<String>();
            for (PatternParameter p : pattern.parameters) {
                if (row.contains(p.id)) {
                    RDFNode n = row.get(p.id);
                    if (n.isResource()) {
                        bindings.add("<" + n.toString() + ">");
                        references.add(n.toString());
                    }
                    else
                        bindings.add(n.toString());
                } else {
                    break;
                }


            }

            try {
                String sparql = pattern.instantiateSparqlPattern(bindings);
                String sparqlPrev = pattern.instantiateSparqlPatternPrevalence(bindings);
                tests.add(new UnitTest(
                        patternID,
                        TestGeneration.AutoGenerated,
                        this.URI,
                        source.getSourceType(),
                        source.getUri(),
                        new TestAnnotation(),
                        sparql,
                        sparqlPrev,
                        references));
            } catch (Exception e) {

            }

        }


        return tests;
    }
}
