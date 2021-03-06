package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * <p>JunitXMLResultsAggregateWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class JunitXmlResultsAggregateWriter extends JunitXmlResultsStatusWriter {


    public JunitXmlResultsAggregateWriter(TestExecution testExecution, String filename) {
    	super(testExecution, filename);
    }

    public JunitXmlResultsAggregateWriter(TestExecution testExecution, OutputStream outputStream) {
    	super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsList() {
        StringBuffer results = new StringBuffer();
        String template = "\t<testcase name=\"%s\" classname=\""+testExecution.getTestExecutionUri()+"\">\n";
        
        for(TestCaseResult result : testExecution.getTestCaseResults()) {
        	AggregatedTestCaseResult aggregatedResult = (AggregatedTestCaseResult) result;
        	String testcaseElement = String.format(template,
        			aggregatedResult.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"));
            results.append(testcaseElement);

            if(aggregatedResult.getStatus().equals(TestCaseResultStatus.Fail)) {
            	results.append("\t\t<failure message=\""+aggregatedResult.getMessage()+"\" type=\""+aggregatedResult.getSeverity().name()+"\"/>\n");
            	results.append("\t\t<system-out>Errors:"+aggregatedResult.getErrorCount()+" Prevalence:"+aggregatedResult.getPrevalenceCount().orElse(-1L)+"</system-out>\n");
            } else if(aggregatedResult.getStatus().equals(TestCaseResultStatus.Error)||aggregatedResult.getStatus().name().equals("Timeout")) {
            	results.append("\t\t<error message=\""+aggregatedResult.getMessage()+"\" type=\""+aggregatedResult.getStatus().name()+"\"/>\n");
            }
            results.append("\t</testcase>\n");
        }

        return results;
    }
}
