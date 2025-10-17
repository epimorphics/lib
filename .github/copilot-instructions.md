# Epimorphics Lib - AI Coding Instructions

## Project Overview
This is a utility library for RDF/Semantic Web applications built on Apache Jena. It's a mature project with historical cruft that provides essential utilities across multiple Epimorphics projects.

## Architecture & Key Components

### Core RDF Utilities (`com.epimorphics.rdfutil`)
- **RDFUtil.java**: Central utility class with static helper methods for common RDF operations
- **Wrappers**: ModelWrapper, DatasetWrapper, RDFNodeWrapper - simplify Jena usage from scripting languages
- **PropertyValue/PropertyValueSet**: Type-safe property access patterns
- Pattern: Use `RDFUtil.getAPropertyValue(resource, Property[])` for flexible property lookups with fallbacks

### JSON Integration (`com.epimorphics.json`) 
- **JSONWritable interface**: Self-serializing objects using Jena's streaming JSON writers
- **RDFJSONModWriter**: Custom Jena RDF/JSON format with array handling for lists
- Pattern: Implement `JSONWritable.writeTo(JSFullWriter out)` for API objects

### Web API Marshalling (`com.epimorphics.webapi.marshalling`)
- JAX-RS MessageBodyWriter implementations for RDF formats (RDF/XML, Turtle, JSON-LD)
- Pattern: Jersey integration with `@Provider` annotations for automatic content negotiation
- Note: Creates Jersey dependency - consider factoring out if not needed

### Geographic Utilities (`com.epimorphics.geo`)
- **OsGridRef**: Ordnance Survey grid reference parsing/formatting with WGS84/OSGB36 conversion
- **GeoPoint, LatLonE**: Coordinate system transformations
- Pattern: `OsGridRef.parse(gridRef).toLatLon()` for UK geographic data

### SPARQL Query Building (`com.epimorphics.sparql`)
- **QueryShape**: Fluent API for constructing SPARQL queries programmatically  
- **Templates**: Query templating system with variable substitution
- Pattern: Build queries incrementally with method chaining, use transforms for parameterization

### Task Management (`com.epimorphics.tasks`)
- **ProgressMonitor**: Interface for async task progress reporting
- **LiveProgressMonitor**: Implementation for web UI progress updates
- Pattern: Use for long-running operations that need user feedback

### Vocabulary Management (`com.epimorphics.vocabs`)
- Static vocabulary classes generated from RDF schemas (SKOS, Cube, etc.)
- Pattern: Use `SKOS.prefLabel` instead of string URIs for type safety
- Note: Should migrate to maven schemagen plugin

## Development Patterns

### License Headers
- All Java files must include Apache 2.0 license header
- Use `./apply-license.sh` script to add headers to new files
- Header template in `license-header` file

### Dependency Management
- **Jena version**: Currently 5.5.0 (latest stable version)
- **Jackson version**: 2.18.0 (compatible with Jena 5.x)
- **Jersey version**: 3.1.8 (Jakarta EE compatible)
- **Java 17**: Target compilation level (minimum required for Jena 5.x)
- **OpenCSV**: Migrated to 5.9 with new API
- Pattern: Uses modern Jakarta EE APIs instead of legacy javax APIs

### Testing
- Standard JUnit 4 pattern with `TestUtil` helper class
- Test data in `src/test/data/` directory
- Pattern: Use `TestUtil.resourceFixture()` for consistent test resource creation

### Error Handling
- Custom `EpiException` for library-specific errors
- Pattern: Wrap and re-throw with meaningful context

## Build & Development

### Maven Commands
```bash
mvn clean compile    # Basic compilation
mvn test            # Run test suite - ALWAYS run to validate changes
mvn package         # Create JAR
```

### Testing Requirements
- **Always run `mvn test` before committing changes** - the test suite validates library functionality
- Tests cover core RDF utilities, JSON serialization, geographic transformations, and SPARQL operations
- Use existing test patterns as templates for new functionality

### Key Dependencies
- Apache Jena 5.5.0 (RDF processing - modern API)
- Jakarta WS-RS 3.1.0 + Jersey 3.1.8 (Jakarta EE web services)
- OpenCSV 5.9 (CSV processing with builder pattern)
- Commons Text 1.13.1 (string utilities)
- Jackson 2.18.0 (JSON processing)

## Project-Specific Conventions

### Package Structure
- `rdfutil`: Core RDF manipulation utilities
- `json`: JSON serialization and RDF/JSON handling  
- `webapi`: JAX-RS integration for web services
- `geo`: Geographic coordinate processing
- `sparql`: Query building and templating
- `tasks`: Async operation progress tracking
- `util`: General purpose utilities
- `vocabs`: RDF vocabulary definitions

### Common Patterns
- **Property arrays**: Use `RDFUtil.labelProps[]` for multi-property fallback lookups
- **Language matching**: `findLangMatchValue()` for internationalized content
- **Wrapper pattern**: ModelWrapper/DatasetWrapper for scripting language integration
- **Streaming JSON**: Use JSONWritable interface for memory-efficient serialization

### Migration Notes (v4.0.0)
- **Java 17 minimum**: Upgraded from Java 8 to Java 17
- **Jena 5.x**: Major API changes - some deprecated XSD types removed
- **Jakarta EE**: Migrated from `javax.ws.rs` to `jakarta.ws.rs` packages  
- **OpenCSV 5.x**: Builder pattern replaces constructor-based API
- **Breaking changes**: Version 4.0.0 indicates intentional breaking changes for modernization

### Historical Notes
- Contains some obsolete code superseded by newer Jena features
- Some utilities replaced by "appbase" in newer projects
- Maintain backward compatibility within major versions
- Document deprecations in JavaDoc