# Geo-Spatial Queries

Lib''s GeoSpatial queries are handled by translating from an
abstract geo query ("abstract" in the sense that it is not
specified how the query is implemented) to graph patterns
for the ASQ. The implementation may, for example, consist of
SPARQL filters or use spatial indexes such as Jena Spatial.

A `GeoQuery` object has three components:

> A (string) name identifying which query operation is requested.

> A sequence of variables that are used/bound by the generated query
> (written here as the only element if the sequence is singleton)
> The first varianle of the query will be the one bound to the
> selected items of the geo-spatial query.

> A sequence of numbers which are parameters to the query.

eg

> `GeoQuery q = new GeoQuery(GeoQuery.withinBox, new Var("it"), 10.1, 11.2, 12.3 )`

`GeoQuery` defines some known abstract spatial query names

> `GeoQuery.withinBox`

> A query withinBox(var, x, y, r) binds var to all of the
> items within Manhatten radius r of the point x, y.

> `GeoQuery.withinCircle`

> A query withinBox(var, x, y, r) binds var to all of the
> items within ordinary radius r of the point x, y.

A `GeoQuery` is translated to appropriate `GraphPattern`s in
an ASQ by using a `GeoQuery.Build` object:

		public interface Build {
			void spatialApply(GeoQuery gq, AbstractSparqlQuery asq);
		}

Given a GeoQuery `Q`, an `ASQ` `A`, and a `GeoQuery.Build` `B`, then 

> `Q.translateOn(A, B) : ASQ`

will make a copy of `A` and then add to it graph patterns
which will implement the effects of `Q`, returning the
modified copy as its result. 

As an example:

	static final class ExampleBuild implements GeoQuery.Build {

		final Property spatial;

		public BuildTest(Property spatial) {
			this.spatial = spatial;
		}

		@Override public void SpatialApply(GeoQuery gq,	AbstractSparqlQuery asq) {
			Var S = gq.getVar();
			URI P = new URI(spatial.getURI());
			TermAtomic O = TermList.fromNumbers(gq.getArgs());
			GraphPattern spatialPattern = new Basic(new Triple(S, P, O));
			asq.addEarlyPattern(spatialPattern);
		}
	}

new BuildTest(P) updates the ASQ it is applied to in the style of
Jena Spatial where the spatial property P used is passed in when
the BuildTest is constructed.

`GeoQuery` defines the built-in Builds

> `GeoQuery.BuildWithinBoxBySparql`

> `GeoQuery.BuildWithinCircleBySparql`

> `GeoQuery.BuildWithinBoxByIndex`

> `GeoQuery.BuildWithinCircleByIndex`

which implement withinBox and withinCircle queres by
using Jena Spatial ("ByIndex") or by using SPARQL
filter expressions ("BySparql").

