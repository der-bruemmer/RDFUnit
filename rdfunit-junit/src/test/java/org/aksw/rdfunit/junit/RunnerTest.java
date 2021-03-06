package org.aksw.rdfunit.junit;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michael Leuthold
 * @version $Id: $Id
 */
public class RunnerTest {

    private static final RunNotifier notifier = new RunNotifier();
    private static final MockRunListener mockRunListener = new MockRunListener();


    @BeforeClass
    public static void addNotifierListener() throws RDFReaderException {
        notifier.addListener(mockRunListener);

        assertThat(TestRunner.beforeClassCalled).isEqualTo(0);

        Request.aClass(TestRunner.class).getRunner().run(notifier);
    }

    @Test
    public void runsFinished() {
        assertThat(mockRunListener.getTestsFinished()).isGreaterThan(0);
    }

    @Test
    public void runsFailed() {
        assertThat(mockRunListener.getTestsFailed()).isGreaterThan(0);
    }

    @Test
    public void methodNamesMatchPattern() {
        for (Description d : mockRunListener.getDescriptions()) {
            assertThat(d.getMethodName()).matches("\\[(getInputData|returningNull|returningMockReader)\\] .*");
        }
    }

    @Test
    public void failuresMatchPattern() {
        for (Failure f : mockRunListener.getFailures()) {
            assertThat(f.getDescription().getMethodName()).matches("\\[" +
                    "(getInputData|returningNull|returningMockReader)\\] .*");
        }
    }

    @Test
    public void beforeClassMethodIsCalledOnce() {
        assertThat(TestRunner.beforeClassCalled).isEqualTo(1);
    }

    @Test
    public void afterClassMethodIsCalledOnce() {
        assertThat(TestRunner.afterClassCalled).isEqualTo(1);
    }

    @Test
    public void returningNullMethodThrowsRuntimeExceptionWithNPE() {
        final Failure returningNull = findFirstFailureWhereDescriptionContains("returningNull");
        assertThat(returningNull.getException()).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void returningMockReaderMethodThrowsRuntimeExceptionWithNPE() {
        final Failure returningMockReader = findFirstFailureWhereDescriptionContains("returningMockReader");
        assertThat(returningMockReader.getException()).isInstanceOf(RuntimeException.class);
    }

    private Failure findFirstFailureWhereDescriptionContains(final String containedInDescription) {
        return FluentIterable.from(mockRunListener.getFailures())
                .filter(new Predicate<Failure>() {

                    @Override
                    public boolean apply(Failure failure) {
                        return failure.getDescription().getDisplayName().contains(containedInDescription);
                    }
                })
                .first().orNull();
    }

    @Test
    public void ignoredMethodIsNeverCalled() {
        assertThat(TestRunner.ignoredTestInputMethodNotCalled).isTrue();
    }

    @Test
    public void testInputNameValueIsUsed() {
        int size = FluentIterable.from(mockRunListener.getIgnored())
                .filter(new Predicate<Description>() {
                    @Override
                    public boolean apply(Description input) {
                        return input.getDisplayName().contains("inputs name");
                    }
                }).size();
        assertThat(size).isGreaterThan(0);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class TestRunner {

        private static int beforeClassCalled = 0;
        private static int afterClassCalled = 0;
        private static boolean ignoredTestInputMethodNotCalled = true;

        @BeforeClass
        public static void beforeClass() {
            beforeClassCalled++;
        }

        @AfterClass
        public static void afterClass() {
            afterClassCalled++;
        }

        @TestInput
        public RDFReader getInputData() {
            return new RDFModelReader(ModelFactory
                    .createDefaultModel()
                    .read("inputmodels/foaf.rdf"));
        }

        @TestInput
        public RDFReader returningNull() {
            return null;
        }

        @TestInput
        public RDFReader returningMockReader() {
            RDFReader mockReader = mock(RDFReader.class);
            try {
                when(mockReader.read()).thenThrow(new RDFReaderException("Failed to read (mock)!"));
            } catch (RDFReaderException e) {
                throw new RuntimeException(e);
            }
            return mockReader;
        }

        @TestInput
        @Ignore
        public RDFReader ignoredTestInput() {
            ignoredTestInputMethodNotCalled = false;
            return new RDFModelReader(ModelFactory.createDefaultModel());
        }

        @TestInput(name = "inputs name")
        @Ignore
        public RDFReader ignoredTestInputWithName() {
            return new RDFModelReader(ModelFactory.createDefaultModel());
        }
    }

    private static class MockRunListener extends RunListener {

        private final List<Description> descriptions = new ArrayList<>();
        private final List<Failure> failures = new ArrayList<>();
        private final List<Description> ignored = new ArrayList<>();

        @Override
        public void testFinished(Description description) throws Exception {
            descriptions.add(description);
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            failures.add(failure);
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            ignored.add(description);
        }

        public List<Description> getIgnored() {
            return ignored;
        }

        public List<Description> getDescriptions() {
            return descriptions;
        }

        public List<Failure> getFailures() {
            return failures;
        }

        public int getTestsFailed() {
            return failures.size();
        }

        public int getTestsFinished() {
            return descriptions.size();
        }
    }
}
