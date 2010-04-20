TokenMakerMaker README
----------------------

This project is a work-in-progress, and currently has lots of issues.  It is
mush less mature than all other RSyntaxTextArea projects.  USE AT YOUR OWN RISK!

Unlike all other RSTA projects, this one requires Java 6.  Since this library
is a tool, and not something other applications would be dependent on, this is
not an issue.

Sporadic notes:


DEPENDENCIES:
-------------
In order to build this project, you need to also have the source for
RSyntaxTextArea in a project alongside this one.  Otherwise, this project is
self-contained (all other dependencies are found in the lib/ directory).


BUILDING:
---------
1. Use RSyntaxTextArea's Ant build script to create dist/rsyntaxtextarea.jar.
2. Use this project's build script to create TokenMakerMaker.

     cd <project-root>/RSyntaxTextArea
     ant
     cd <project-root>/TokenMakerMaker
     ant make-app


USING:
------
1. Run class org.fife.tmm.Main from Eclipse.
2. After building, from the command line:  java -jar tmm.jar



USAGE NOTES:
------------
The app is just a simple dialog with tabs for different "parts" of the
TokenMaker you are creating - general info, comments, keywords, etc.  I think
the general usage should be self-explanatory, but here are some things to note:
 
1. You can save and load your progress via Ctrl+O/Ctrl+S.  Your TokenMaker
   spec. is saved in an XML file.  This way you can come back and work on it
   later if you want, without having to dig into the generated flex.
 
2. Once the app starts, the first thing you'll want to do is go to
   File -> Options.  In the "General" panel you can specify a "working
   directory."  This is the location that generated *.flex, and their
   corresponding *.java files, will be placed.  Change this to wherever you
   want.  I believe old files with the same name will be overwritten without
   prompting you if it's okay, so be warned!  =)
 
3. On the "General" tab, the difference between "AbstractJFlexTokenMaker" and
   "AbstractJFlexCTokenMaker" is that the latter will cause your TokenMaker to
   auto-indent after lines ending in '{', as well as auto-align closing '}'
   chars when they are typed (assuming auto-indent is enabled in the
   RSyntaxTextArea it's running in).  It's supposed to be the option you want
   to pick if the language you are creating derives syntax from C, and uses
   curly braces to denote code blocks.
 
4. The app is supposed to be smart, and if you don't enter a value for a field
   that is required, you shouldn't be able to generate anything without an
   error prompt.  But there may be issues I haven't discovered yet.
 
So anyway, it's just something to play around with.  Let me know what features
you'd need in addition to what's already there - there are many possibilities -
defining what are operators, valid number formats, whether escapes are allowed
in strings, multi-line strings...  And of course report back any bugs.  Keep an
eye on the console output when you're running and look for anything dubious.  =)
