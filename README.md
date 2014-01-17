This is a general dumping ground of small utility code reused across projects. 

## Caveats

Includes a *lot* of historical cruft which has been obsoleted by evolution of Jena.

Only documentation is the code and javadoc.

## Main starting points

   * [RDFUtil](https://github.com/epimorphics/lib/tree/master/src/main/java/com/epimorphics/rdfutil/RDFUtil.java)
Collection of utilities for common trivial RDF tasks, a couple of other utils in that directory might be handy but most are obsoleted

   * [json](https://github.com/epimorphics/lib/tree/master/src/main/java/com/epimorphics/json)
Some utils for working with Jena's json support including a hacked version of the Talis-format RDF writer which handles lists as JSON arrays. 
   
   * [vocabs](https://github.com/epimorphics/lib/tree/master/src/main/java/com/epimorphics/vocabs)
Schemagened versions of some common vocabularies, currently static rather than built form source. Should migrate to using maven schemagen plugin. 

   * [FileUtil](https://github.com/epimorphics/lib/tree/master/src/main/java/com/epimorphics/util/FileUtil.java) 
Some utilities for creating directories and copying files 

   * [NameUtils](https://github.com/epimorphics/lib/tree/master/src/main/java/com/epimorphics/util/NameUtils.java)
String bashing functions used when encoding names in http calls etc

   * [PrefixUtils](https://github.com/epimorphics/lib/tree/master/src/main/java/com/epimorphics/util/PrefixUtils.java)
Utilities for working with prefix mappings including merging and using them to expand SPARQL queries (pure string bashing level)

   * [tasks](https://github.com/epimorphics/lib/tree/master/src/main/java/com/epimorphics/tasks)
Support for incremental reporting of messgaes from async processes, e.g. used for reporting data converter process to an ajax web UI

   * [marshalling](https://github.com/epimorphics/lib/tree/master/src/main/java/com/epimorphics/webapi/marshalling)
Set of JAX-RS bindings for read/write of RDF when using Jersey.  This does mean that lib depends on Jersey which isn't nice. Could factor this out into the libraries that more naturally need Jersey anyway. 
