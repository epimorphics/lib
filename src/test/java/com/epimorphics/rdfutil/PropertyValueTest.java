package com.epimorphics.rdfutil;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyValueTest {

    private Model model;
    private ModelWrapper modelw;
    private Resource root;

    @BeforeEach
    public void before() {
        this.model = ModelFactory.createDefaultModel();
        model.setNsPrefix("ns", "http://example.org/");
        this.modelw = new ModelWrapper(model);
        this.root = model.createResource("ns" + "root");

        // name
        root.addProperty(model.createProperty("ns", "name"), model.createLiteral("lib", "en"));
        // id
        root.addProperty(model.createProperty("ns", "id"), model.createLiteral("a04652"));
        // comment
        root.addProperty(model.createProperty("ns", "comment"), model.createLiteral("general purpose library", "en"));
        root.addProperty(model.createProperty("ns", "comment"), model.createLiteral("bibliothèque à usage général", "fr"));
        root.addProperty(model.createProperty("ns", "comment"), model.createLiteral("biblioteca de propósito general", "es"));
        // usedBy
        root.addProperty(model.createProperty("ns", "usedBy"), model.createLiteral("registry-core"));
        root.addProperty(model.createProperty("ns", "usedBy"), model.createLiteral("appbase"));
        // tooltip
        root.addProperty(model.createProperty("ns", "tooltip"), model.createLiteral("see documentation"));
        root.addProperty(model.createProperty("ns", "tooltip"), model.createLiteral("voir la documentation", "fr"));
    }

    private PropertyValue getPropertyValue(String prop) {
        Property property = model.createProperty("ns", prop);
        RDFNodeWrapper propertyw = new RDFNodeWrapper(modelw, property);

        ExtendedIterator<RDFNodeWrapper> nodes = root.listProperties(property)
                .mapWith(Statement::getObject)
                .mapWith( node -> new RDFNodeWrapper(modelw, node));

        PropertyValue pv = new PropertyValue(propertyw);
        while (nodes.hasNext()) {
            pv.addValue(nodes.next());
        }

        return pv;
    }

    @Test
    public void isMultilingual_SingleLangString_ReturnsFalse() {
        PropertyValue pv = getPropertyValue("name");
        assertFalse(pv.isMultilingual());
    }

    @Test
    public void isMultilingual_SingleRawString_ReturnsFalse() {
        PropertyValue pv = getPropertyValue("id");
        assertFalse(pv.isMultilingual());
    }

    @Test
    public void isMultilingual_MultipleLangStrings_ReturnsTrue() {
        PropertyValue pv = getPropertyValue("comment");
        assertTrue(pv.isMultilingual());
    }

    @Test
    public void isMultilingual_MultipleRawStrings_ReturnsFalse() {
        PropertyValue pv = getPropertyValue("usedBy");
        assertFalse(pv.isMultilingual());
    }

    @Test
    public void isMultilingual_MixedStrings_ReturnsTrue() {
        PropertyValue pv = getPropertyValue("tooltip");
        assertTrue(pv.isMultilingual());
    }
}