/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.tattletale.reporting;

import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.core.ArchiveTypes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * Reporting class that will generate package level reports like how {@link ClassDependsOnReport} does on class level reports.
 * <br />
 *
 *
 * @author Navin Surtani
 */

public class PackageDependsOnReport extends CLSReport
{
   /**NAME*/
   private static final String NAME = "Package Depends On";

   /**DIRECTORY*/
   private static final String DIRECTORY = "packagedependson";

   /**
    * Constructor
    * @param archives The archives
    * @param known The set of known archives
    * @param classloaderStructure The classloader structure
    */
   public PackageDependsOnReport(SortedSet<Archive> archives,
                                 List<Archive> known,
                                 String classloaderStructure)
   {
      super(DIRECTORY, ReportSeverity.INFO, archives, NAME, DIRECTORY, classloaderStructure, known);
   }

   /**
    * write out the header of the report's content
    * @param bw the writer to use
    * @throws IOException if an errror occurs
    */
   @Override
   void writeHtmlBodyHeader(BufferedWriter bw) throws IOException
   {
      bw.write("<body>" + Dump.NEW_LINE);
      bw.write(Dump.NEW_LINE);

      bw.write("<h1>" + NAME + "</h1>" + Dump.NEW_LINE);

      bw.write("<a href=\"../index.html\">Main</a>" + Dump.NEW_LINE);
      bw.write("<p>" + Dump.NEW_LINE);

   }

   /**
    * write out the report's content
    * @param bw the writer to use
    * @exception IOException if an error occurs
    */
   @Override
   void writeHtmlBodyContent(BufferedWriter bw) throws IOException
   {
      bw.write("<table>" + Dump.NEW_LINE);

      bw.write("  <tr>" + Dump.NEW_LINE);
      bw.write("     <th>Class</th>" + Dump.NEW_LINE);
      bw.write("     <th>Depends On</th>" + Dump.NEW_LINE);
      bw.write("  </tr>" + Dump.NEW_LINE);

      SortedMap<String, SortedSet<String>> result = new TreeMap<String, SortedSet<String>>();
      boolean odd = true;

      for(Archive archive : archives)
      {
         if(archive.getType() == ArchiveTypes.JAR)
         {
            SortedMap<String, SortedSet<String>> packageDependencies = archive.getPackageDependencies();
            Iterator<Map.Entry<String, SortedSet<String>>> dit = packageDependencies.entrySet().iterator();
            while (dit.hasNext())
            {
               Map.Entry<String, SortedSet<String>> entry = dit.next();
               String pack = entry.getKey();
               SortedSet<String> deps = entry.getValue();

               SortedSet<String> newDeps = new TreeSet<String>();

               for (String dep : deps)
               {
                  if (!dep.equals(pack))
                     newDeps.add(dep);
               }

               result.put(pack, newDeps);
            }


         }
      }
      Iterator<Map.Entry<String, SortedSet<String>>> rit = result.entrySet().iterator();

      while (rit.hasNext())
      {
         Map.Entry<String, SortedSet<String>> entry = rit.next();
         String clz = entry.getKey();
         SortedSet<String> deps = entry.getValue();

         if (odd)
         {
            bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
         }
         else
         {
            bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
         }
         bw.write("     <td>" + clz + "</a></td>" + Dump.NEW_LINE);
         bw.write("     <td>");

         Iterator<String> sit = deps.iterator();
         while (sit.hasNext())
         {
            String dep = sit.next();
            bw.write(dep);

            if (sit.hasNext())
               bw.write(", ");
         }

         bw.write("</td>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         odd = !odd;
      }

      bw.write("</table>" + Dump.NEW_LINE);

   }
}
