
# Changelog

All notable changes to this project from 2026-05-11 onward will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

## [4.0.3]

### Security

* Update transitive dependency on org.apache.httpcomponents.core5:httpclient5 5.2.1 to 5.6.3 to address CVE.
* Update transitive dependency on org.apache.httpcomponents.core5:httpcore5 5.2.1 to 5.4.3 to address CVE.
* Update transitive dependency on org.apache.httpcomponents.core5:httpcore5-h2 5.2.1 to 5.4.3 to address CVE.

## [4.0.2]

### Security

* Update transitive dependency on org.apache.thrift:libthrift to 0.23.0 to address CVE.
* Update jackson-databind to 2.22.1 to address CVEs

## 4.0.1

### Security

* Update jackson-core 2.18.0 -> 2.18.6 to address CVEs

## 4.0.0

### Changed

* Update to Jena 5.6.0. Updated to Jersey 3.x which will involve some namespace changes in downstream code that references the javax.ws.rs.* namespace. Updated OpenCSV to 5.12. NOTE that from this release a minimum Java version of Java 17 is required.

## 3.1.7

### Security

* Override jackson versions used by jena 3.9.0 dependency to avoid severe CVEs. There are remaining CVEs logged against jena 3.9.0 but they are less severe and exploitable in our usage.

## 3.1.6

### Changed

* Replaced Apache `commons-lang` by `commons-text` to avoid CVE-2025-48924 (though that wouldn't afffect our usage). Preferable over upgrading `commons-lang3` because the relevant module in `commons-lang3` is deprecated in favour of `commons-text`.
