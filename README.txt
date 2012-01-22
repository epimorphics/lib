Start of a general utility library to share across Epimorphics projects.

Will need some shaking down!

Currently just has low level utility pieces that der needed
across multiple projects.

Includes:
  - support for read/write models in modified Talis JSON format (com.epimorphics.json)
    [should be repackaged for RIOT compatibility]
    
  - schemagen'd versions of vocabularies we reuse a lot
    [no rebuild-from-source yet, I hate Maven so much]
    
  - some file bits for creating directorie, copying resources and the like
    [may duplicate facilities elsewhere but at the time they were originally 
     written it was quicker to do that than discover them] 
    
  - support for low level sending of SPARQL Update commands
    [extracted from BWQ Update Server, Andy's Fuseki stuff probably makes these redundant]
    
  - Wrappers for Model and RDFNode to make them easier to work with when embedded
    in scripting languages like Ruby and Velocity
    [Extracted from Modal. I've de-OSGI-ified the PrefixManager part of that but it doesn't make 
     much sense as an interface in a non-OSGI setting. Needs a proper redesign.]
     
...