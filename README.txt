Project: net.java.games.*
Purpose: Open source game libraries
Authors:
    -- plugin utils design and release author:  
            Jeff Kesselman, Game Technology Architect, 
            Advanced Software Technologies Group,
            Sun Microsystems.
    -- this file updated on 006/06/2003 by Jeff Kesselman


Introduction:

This is the utils project that contains ueful shared functionality
for the other Java Games Initiative APIs.

Build Requirements:

This project has been built in the follwing environment.
 -- Win32 (Win 2000 in the case of our machine)
 -- Sun J2SDK 1.4 (available at java.sun.com)
 -- ANT 1.4.1 (available at apache.org)


Directory Organization:

The root contains a master ANT build.xml.
After a build you will see the following sub directories:
 -- apidocs   Where the javadocs get built to
 -- lib    Where dependant libraries are kept.
 -- bin    Where the actual API is built to
 -- src    The source files.
 -- src/test   Execution directories and data for tests. 

Build instructions:

To clean: ant clean
To build:  ant all (or just ant)
To build docs: ant javadoc
To test: ant test
    
Release Info:
    Initial Release:  This release contains an implementation of a flexible plugin
	management system.  See the apidocs for more information.


