package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * <p>JunitXMLResultsStatusWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class JunitXmlResultsStatusWriter extends JunitXmlResultsWriter {

    public JunitXmlResultsStatusWriter(TestExecution testExecution, String filename) {
    	super(testExecution, filename);
    }

    public JunitXmlResultsStatusWriter(TestExecution testExecution, OutputStream outputStream) {
    	super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsList() {
        StringBuffer results = new StringBuffer();
        String template = "\t<testcase name=\"%s\" classname=\""+testExecution.getTestExecutionUri()+"\">\n";
        
        for(TestCaseResult result : testExecution.getTestCaseResults()) {
        	StatusTestCaseResult statusResult = (StatusTestCaseResult) result;
        	String testcaseElement = String.format(template,
        			statusResult.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"));
            results.append(testcaseElement);

            if(statusResult.getStatus().equals(TestCaseResultStatus.Fail)) {
            	results.append("\t\t<failure message=\""+statusResult.getMessage()+"\" type=\""+statusResult.getSeverity().name()+"\"/>\n");
            } else if(statusResult.getStatus().equals(TestCaseResultStatus.Error)||statusResult.getStatus().equals(TestCaseResultStatus.Timeout)) {
            	results.append("\t\t<error message=\""+statusResult.getMessage()+"\" type=\""+statusResult.getStatus().name()+"\"/>\n");
            }
            results.append("\t</testcase>\n");
        }

        return results;
    }

    protected String getStatusClass(TestCaseResultStatus status) {

        switch (status) {
            case Success:
                return "success";
            case Fail:
                return "danger";
            case Timeout:
                return "warning";
            case Error:
                return "warning";
            default:
                return "";
        }
    }

}
