package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Core SHACL Vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:43 PM
 * @version $Id: $Id
 */
@SuppressWarnings("ClassWithTooManyFields")
public final class SHACL {

    //namespace
    /** Constant <code>namespace="http://www.w3.org/ns/shacl#"</code> */
    public static final String namespace = "http://www.w3.org/ns/shacl#";

    //Classes

    public static final Resource Shape = resource("Shape");
    public static final Resource Argument = resource("Argument");
    public static final Resource Function = resource("Function");
    public static final Resource ResultAnnotation = resource("ResultAnnotation");
    public static final Resource ValidationResult = resource("ValidationResult");

    public static final Resource PropertyScope = resource("PropertyScope");
    public static final Resource InversePropertyScope = resource("InversePropertyScope");
    public static final Resource AllSubjectsScope = resource("AllSubjectsScope");
    public static final Resource AllObjectsScope = resource("AllObjectsScope");


    //properties
    public static final Property argument = property("argument");
    public static final Property cachable = property("cachable");
    public static final Property datatype = property("datatype");
    public static final Property defaultValue = property("defaultValue");
    public static final Property optional = property("optional");
    public static final Property returnType = property("returnType");
    public static final Property sparql = property("sparql");
    public static final Property valueType = property("valueType");

    public static final Property clazz = property("class");

    public static final Property severity = property("severity");
    public static final Property focusNode = property("focusNode");
    public static final Property subject = property("subject");
    public static final Property predicate = property("predicate");
    public static final Property object = property("object");
    public static final Property message = property("message");
    public static final Property sourceConstraint = property("sourceConstraint");

    public static final Property annotationProperty = property("annotationProperty");
    public static final Property annotationVarName = property("annotationVarName");
    public static final Property annotationValue = property("annotationValue");
    public static final Property scopeClass = property("scopeClass");
    public static final Property scopeNode = property("scopeNode");
    public static final Property scope = property("scope");
    //public static final Property cachable = property("cachable");


    private SHACL() {
    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
        /*
        public static final class Nodes {
            public static final Node Alt;
            public static final Node Bag;
            public static final Node Property;
            public static final Node Seq;
            public static final Node Statement;
            public static final Node List;
            public static final Node nil;
            public static final Node first;
            public static final Node rest;
            public static final Node subject;
            public static final Node predicate;
            public static final Node object;
            public static final Node type;
            public static final Node value;

            public Nodes() {
            }

            static {
                Alt = RDF.Alt.asNode();
                Bag = RDF.Bag.asNode();
                Property = RDF.Property.asNode();
                Seq = RDF.Seq.asNode();
                Statement = RDF.Statement.asNode();
                List = RDF.List.asNode();
                nil = RDF.nil.asNode();
                first = RDF.first.asNode();
                rest = RDF.rest.asNode();
                subject = RDF.subject.asNode();
                predicate = RDF.predicate.asNode();
                object = RDF.object.asNode();
                type = RDF.type.asNode();
                value = RDF.value.asNode();
            }
        } */


}
