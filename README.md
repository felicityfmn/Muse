# Reagent Sketch

A minimalistic sketch of Reagent code

This shows a basic single page application which
has a few HTML components and some dynamic CSS components

## Usage

Compiling the Clojurescript with figwheel in a terminal:

`clj -m figwheel.main -b dev -r`

from the root of the project then visit `http://localhost:9501/` (actually Figwheel will probably open it automatically)

`dev.cljs.edn` contains the Clojurescript compilation config

`figwheel-main.edn` contains Figwheel's options

and `deps.edn` contains the project's dependencies

This project can be imported into IntelliJ with:

`File > New > Project from existing sources` and select "deps" for the external model type


## License


Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
