/*
 * PluginTest.java
 *
 * Created on April 21, 2003, 4:59 PM
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
package net.java.games.util.plugins.test;

/**
 *
 * @author  jeff
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import net.java.games.util.plugins.Plugins;


class ClassRenderer implements ListCellRenderer { 
    JLabel label = new JLabel();
    public Component getListCellRendererComponent(JList jList, Object obj, int param, 
        boolean param3, boolean param4) 
    {
        label.setText(((Class)obj).getName());
        label.setForeground(Color.BLACK);
        label.setBackground(Color.WHITE);
        if (PluginTest.DEBUG) {
            System.out.println("Rendering: "+label.getText());
        }
        return label;
    }
}

class ListUpdater implements Runnable{
    Object[] objList;
    DefaultListModel mdl;
    public ListUpdater(JList jlist, Object[] list){
        objList = list;
        mdl = (DefaultListModel)jlist.getModel();
    }
    
    public void run() {
        mdl.clear();
        for(int i=0;i<objList.length;i++){
            mdl.addElement(objList[i]);
        }
    }
}

public class PluginTest {
    static final boolean DEBUG = false;
    Plugins plugins = new Plugins(new File("test_plugins"));
    JList plist;
    Class[] piList; // holder for current list of plugins
    /** Creates a new instance of PluginTest */
    public PluginTest() {
        JFrame f = new JFrame("PluginTest");
        plist = new JList(new DefaultListModel());
        plist.setCellRenderer(new ClassRenderer());
        Container c = f.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(new JScrollPane(plist),BorderLayout.CENTER);
        JPanel p = new JPanel();
        c.add(p,BorderLayout.SOUTH);
        p.setLayout(new FlowLayout());
        f.pack();
        f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
        f.setVisible(true);
        doListAll();
    }
    
    private void doListAll(){
        SwingUtilities.invokeLater(new ListUpdater(plist,plugins.get()));
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new PluginTest();
    }
    
}
