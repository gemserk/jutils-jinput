/*
 * PluginLodaer.java
 *
 * Created on April 18, 2003, 11:32 AM
 */
/*****************************************************************************
 * Copyright (c) 2003 Sun Microsystems, Inc.  All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistribution of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materails provided with the distribution.
 *
 * Neither the name Sun Microsystems, Inc. or the names of the contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANT OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMEN, ARE HEREBY EXCLUDED.  SUN MICROSYSTEMS, INC. ("SUN") AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS
 * A RESULT OF USING, MODIFYING OR DESTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.  IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES.  HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OUR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for us in
 * the design, construction, operation or maintenance of any nuclear facility
 *
 *****************************************************************************/
package net.java.games.util.plugins;

/**
 *
 * @author  jeff
 */
import java.io.*;
import java.net.*;

/** This class is used internally by the Plugin system.
 * End users of the system are unlikely to need to be aware
 * of it.
 *
 *
 * This is the class loader used to keep the namespaces of
 * different plugins isolated from each other and from the
 * main app code. One plugin loader is created per Jar
 * file in the sub-directory tree of the plugin directory.
 *
 * In addition to isolating java classes this loader also isolates
 * DLLs such that plugins with conflicting DLL names may be
 * used by simply placing the plugin and its associated DLL
 * in a  sub-folder of its own.
 *
 * This class also currently implements methods for testing
 * classes for inheritance of superclasses or interfaces.
 * This code is genericly useful and should really be moved
 * to a seperate ClassUtils class.
 * @author Jeffrey Kesselman
 */
public class PluginLoader extends URLClassLoader {
    static final boolean DEBUG = false;
    File parentDir;
    boolean localDLLs = true;
    /** Creates a new instance of PluginLodaer
     * If the system property "net.java.games.util.plugins.nolocalnative" is
     * not set then the laoder will look for requried native libs in the
     * same directory as the plugin jar.  (Useful for handling name
     * collision between plugins).  If it IS set however, then it will
     * fall back to the default way of loading natives.  (Necessary for
     * Java Web Start.)
     * @param jf The JarFile to load the Plugins from.
     * @throws MalformedURLException Will throw this exception if jf does not refer to a
     * legitimate Jar file.
     */
    public PluginLoader(File jf) throws MalformedURLException {
        super(new URL[] {jf.toURL()},
              Thread.currentThread().getContextClassLoader());
        parentDir = jf.getParentFile();
        if (System.getProperty("net.java.games.util.plugins.nolocalnative")
            !=null){
          localDLLs = false;
        }
    }

    /** This method is queried by the System.loadLibrary()
     * code to find the actual native name and path to the
     * native library to load.
     *
     * This subclass implementation of this method ensures that
     * the native library will be loaded from, and only from,
     * the parent directory of the Jar file this loader was
     * created to support.  This allows different Plugins
     * with supporting DLLs of the same name to co-exist, each
     * in their own subdirectory.
     *
     * Setting the global  "localDLLs" by setting the property
     * net.java.games.util.plugins.nolocalnative defeats this behavior.
     * This is necessary for Java Web Start apps which have strong
     * restrictions on where and how native libs can be loaded.
     *
     * @param libname The JNI name of the native library to locate.
     * @return Returns a string describing the actual loation of the
     * native library in the native file system.
     */
    protected String findLibrary(String libname){
      if (localDLLs) {
        String libpath = parentDir.getPath() + File.separator +
            System.mapLibraryName(libname);
        if (DEBUG) {
          System.out.println("Returning libname of: " + libpath);
        }
        return libpath;
      } else {
        return super.findLibrary(libname);
      }
    }

    /** This function is called as part of scanning the Jar for
     * plugins.  It checks to make sure the class passed in is
     * a legitimate plugin, which is to say that it meets
     * the following criteria:
     *
     * (1) Is not itself an interface
     * (2) Implements the Plugin marker interface either directly
     *    or through inheritance.
     *
     * interface, either
     * @param pc The potential plug-in class to vette.
     * @return Returns true if the class meets the criteria for a
     * plugin. Otherwise returns false.
     */
    public boolean attemptPluginDefine(Class pc){
        return ((!pc.isInterface()) && classImplementsPlugin(pc));
    }

    private boolean classImplementsPlugin(Class testClass){
        if (testClass == null) return false; // end of tree
        if (DEBUG) {
            System.out.println("testing class "+testClass.getName());
        }
        Class[] implementedInterfaces = testClass.getInterfaces();
        for(int i=0;i<implementedInterfaces.length;i++){
            if (DEBUG) {
                System.out.println("examining interface: "+implementedInterfaces[i]);
            }
            if (implementedInterfaces[i]==Plugin.class) {
                if (DEBUG) {
                    System.out.println("returning true from classImplementsPlugin");
                }
                return true;
            }
        }
        for(int i=0;i<implementedInterfaces.length;i++){
            if (classImplementsPlugin(implementedInterfaces[i])){
                return true;
            }
        }
        return classImplementsPlugin(testClass.getSuperclass());
    }

}
