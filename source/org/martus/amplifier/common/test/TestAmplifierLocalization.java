/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.amplifier.common.test;

import java.io.File;
import java.util.HashMap;

import org.martus.amplifier.common.AmplifierLocalization;
import org.martus.common.test.TestCaseEnhanced;
import org.martus.util.UnicodeWriter;


public class TestAmplifierLocalization extends TestCaseEnhanced
{
	public TestAmplifierLocalization(String name)
	{
		super(name);
	}
	
	public void testBuildLanguageMap()throws Exception
	{
		File languageFile = createTempFile();
		UnicodeWriter writer = new UnicodeWriter(languageFile);
		writer.writeln("en=English");	
		writer.writeln("fr=French");	
		writer.writeln("it=Italian");	
		writer.close();
		
		HashMap languages = AmplifierLocalization.buildLanguageMap(languageFile);
		
		assertTrue("Should have en key", languages.containsKey("en"));
		assertTrue("Should have fr key", languages.containsKey("fr"));
		assertTrue("Should have it key", languages.containsKey("it"));

		assertEquals("en should give us English", "English", languages.get("en"));		
		assertEquals("fr should give us French", "French", languages.get("fr"));		
		assertEquals("it should give us Italian", "Italian", languages.get("it"));		
	}
}