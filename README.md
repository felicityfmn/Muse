# Muses

A tool to control the look and feel of the UI. This is for UX and UI designers to be able
to manipulate and test different displays of the UI without having to rewrite code. When designing
a UI we must try and test different ideas by changing all the different UX aspects such as
colours, fonts, layouts and flow, etc. Conventionally this is done by creating numerous designs and
deciding on one and later testing in iterations, or rewriting one design over and over. While this
is an effective way to do it, it is not efficient because of how time consuming it is. With this tool,
the UI elements can be changed live without the need to write any code, thus different designs can
be tested without any risk of losing or changing the original state of the UI. You can think of it as
a makeover montage like in films, but for interfaces! All you need to do is use one of the programs
in the tool and with a set of controllers (sliders or buttons) change things like the background colour,
font size, toggling animations on and off, or the layout of buttons, menu bars and tiles. Each program
will have its own domain of the UI it can manipulate, and so you don't have the constraints of things like
pre-made themes or skins.


Muses is a project that consists of 4 programs, each of which control aspects of the aesthetics of the UI. These
programs are named after four out of the 9 Muses from ancient greek mythology, whose purpose was to guard and govern
the arts: literature, music, visual arts, and science.

For millennia , artists, scientists and great thinkers have drawn inspiration from the Muses to guide their efforts
into making their work pleasing to the senses, efficient and precise. Now, the Muses have come to us to lend us their
talents so that our UI can have a refined, fetching and remarkable design that also allows the user to customize it according
to their taste or brand.

### What are the programs

The Muses that comprise this tool are:

* Therpsichore: The muse of dance and chorus. This muse is all about coordination and harmony
so she is the governess of our UI's colour themes, ensuring that the combinations of colours
stay within the style guidelines. This consists of a CSS file that contains the rules of each element
of the UI, and a views file with the functions that can change the CSS states, via controllers.
* Clio: The muse of history. This muse's domain is the analytics dashboard and she controls the
display of information and layout.
* Erato: The muse of lyric poetry. This muse controls the aesthetics of the content, i.e. font style,
highlights, warning and error messages.
* Calliope: Though she is the muse of epic poetry, we've decided to use her as the Muse of epic imagery,
such as our background and loading screen animations.




## License


Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
