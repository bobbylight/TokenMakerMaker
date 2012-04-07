TokenMakerMaker README
----------------------

This project is a work-in-progress, and currently has lots of issues.  It is
much less mature than all other RSyntaxTextArea projects.  USE AT YOUR OWN RISK!

TokenMakerMaker provides a simple GUI interface that allows you to define a
programming language.  It can then generate a .flex and .java file that
RSyntaxTextArea can use for syntax highlighting that language.  This removes
the need for you to learn JFlex and/or the (mostly undocumented) manual tweaks
you need to do to JFlex output to make it usable in RSyntaxTextArea.  The
trade-off is a lack of flexibility: TokenMakerMaker can be used to define most
basic language constructs (comments, keywords, strings, etc.), but it doesn't
expose the full power of what you can do with JFlex (nor will it ever try to).
 
Unlike all other RSTA projects, this one requires Java 6.  Since this library
is a tool, and not something other applications would be dependent on, this is
not an issue.

Sporadic notes:


DEPENDENCIES:
-------------
In order to build this project, you need to also have the source for
RSyntaxTextArea in a project alongside this one.  Otherwise, this project is
self-contained (all other dependencies are found in the lib/ directory).


BUILDING WITH ANT:
------------------
1. Use RSyntaxTextArea's Ant build script to create dist/rsyntaxtextarea.jar.
2. Use this project's build script to create TokenMakerMaker.

     cd <project-root>/RSyntaxTextArea
     ant
     cd <project-root>/TokenMakerMaker
     ant make-app


USING:
------
1. Run class org.fife.tmm.Main from Eclipse.  No other work required.  OR...
2. After building with Ant (see above), from the command line:
   java -jar tmm.jar



USAGE NOTES:
------------
The app is just a simple dialog with tabs for different "parts" of the
TokenMaker you are creating - general info, comments, keywords, etc.  I think
the general usage should be self-explanatory, but here are some things to note:
 
1. You can save and load your progress via Ctrl+O/Ctrl+S.  Your TokenMaker
   spec. is saved in an XML file.  This way you can come back and work on it
   later if you want, without having to dig into the generated flex.
 
2. Once the app starts, the first thing you'll want to do is go to
   File -> Options.  You'll want to provide correct values for the fields in
   the "General" panel:
   
     - The full path to javac (javac.exe on Windows).  If TMM is launched
       with a JDK instead of just a JRE, this should be pre-filled in.
       Otherwise, you'll have to specify one yourself.  If this is left blank,
       then TMM will generate the .flex and .java files for your TokenMaker,
       but it will not be able to generate the corresponding class file, and it
       won't be able to launch the "preview" editor to try out your TokenMaker.
       In this case you'll be notified when clicking "Generate" about the need
       to configure the javac location to use this functionality.
      
     - "Source output directory" is where TokenMakerMaker will place the
       generated .flex and .java files.  You can point this directly to a
       source directory in your project, for example.
      
     - "Class output directory" is where TokenMakerMaker will place the
       generated .class file when it compiles it for the editor preview.
       You can point this to your "bin" or "classes" directory in your
       project, or you can simply point it to "C:\temp" or "/tmp".

   I believe old files with the same name will be overwritten without
   prompting you if it's okay, so be warned!  =)
 
3. On the "General" tab, the difference between "C-derived syntax" and
   "All others" is that the former will cause your TokenMaker to auto-indent
   after lines ending in '{', as well as auto-align closing '}'
   chars when they are typed (assuming auto-indent is enabled in the
   RSyntaxTextArea it's running in).  It's supposed to be the option you want
   to pick if the language you are creating derives syntax from C, and uses
   curly braces to denote code blocks.
 
4. The app is supposed to be smart, and if you don't enter a value for a field
   that is required, you shouldn't be able to generate anything without an
   error prompt.  But there may be issues I haven't discovered yet.
 
So anyway, it's just something to play around with.  Let me know what features
you'd need in addition to what's already there - there are many possibilities -
valid number formats, whether escapes are allowed in strings, multi-line
strings...  And of course report back any bugs.  Keep an eye on the console
output when you're running and look for anything dubious.  =)
