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

package org.martus.amplifier.presentation.test;

import java.util.Vector;

import org.apache.velocity.context.Context;
import org.martus.amplifier.common.FindBulletinsFields;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.presentation.AbstractSearchResultsServlet;
import org.martus.amplifier.presentation.DoSearch;
import org.martus.amplifier.presentation.SearchResults;
import org.martus.amplifier.presentation.SimpleSearch;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServletSession;
import org.martus.common.packet.UniversalId;
import org.martus.common.test.TestCaseEnhanced;

public class TestSearchResults extends TestCaseEnhanced
{
	public TestSearchResults(String name)
	{
		super(name);
	}

	public void testSorting() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = new MockContext();
		
		String fieldToSortBy = "title";

		BulletinList data = new BulletinList();
		AmplifierServletSession session = request.getSession();
		session.setAttribute(tagFoundBulletins, data.getList());
		request.putParameter(SearchResultConstants.RESULT_SORTBY_KEY, fieldToSortBy);
		SearchResults sr = new SearchResults();
		
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("SearchResults.vm", templateName);

		BulletinInfo item1FromContext = (BulletinInfo)((Vector)context.get(tagFoundBulletins)).get(0);
		assertEquals("Bulletins not sorted by title in context?", bulletin1Title, item1FromContext.get(fieldToSortBy));

		BulletinInfo item1FromSession = (BulletinInfo)((Vector)session.getAttribute(tagFoundBulletins)).get(0);
		assertEquals("Bulletins not sorted by title in session?", bulletin1Title, item1FromSession.get(fieldToSortBy));
		
		assertEquals("Didn't save sort in session?", fieldToSortBy, session.getAttribute(SearchResultConstants.RESULT_SORTBY_KEY));
	}
	
	public void testReuseSortFromSession() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = new MockContext();
		
		String fieldToSortBy = "title";

		AmplifierServletSession session = request.getSession();
		session.setAttribute(SearchResultConstants.RESULT_SORTBY_KEY, fieldToSortBy);
		session.setAttribute(tagFoundBulletins, new BulletinList().getList());
		SearchResults sr = new SearchResults();
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("SearchResults.vm", templateName);
		
		assertEquals("Didn't reuse sort from session?", fieldToSortBy, session.getAttribute(SearchResultConstants.RESULT_SORTBY_KEY));
	}
	
	public void testNoResults() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = new MockContext();

		SearchResults sr = new SearchResults();
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("NoSearchResults.vm", templateName);
	}

	public void testSearchedFor() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		String basicSearchString = "mybasicsearch";
		request.putParameter("query", basicSearchString);
		Context context = new MockContext();
		
		DoSearch servlet = new DoSearch();
		servlet.configureSessionFromRequest(request);
		assertEquals("Didn't get back correct search string from session", basicSearchString, request.getSession().getAttribute("searchedFor"));
		AbstractSearchResultsServlet.setSearchedForInContext(request.getSession(), context);
		assertEquals("Didn't get back correct search string from context", basicSearchString, context.get("searchedFor"));		
	}

	public void testSetSearchResultsContext() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		Context context = new MockContext();
		String mySearchByTag = "myOwnSearchTag";
		request.putParameter(SearchResultConstants.RESULT_SORTBY_KEY, mySearchByTag);	
		Vector fakeBulletins = new Vector();
		fakeBulletins.add("hello");
		fakeBulletins.add("there");
		SearchResults.setSearchResultsContext(fakeBulletins, request, context);

		assertEquals("wrong second bulletin in session?", fakeBulletins.get(1), ((Vector)request.getSession().getAttribute("foundBulletins")).get(1));		

		assertEquals("wrong first bulletin in context?", fakeBulletins.get(0), ((Vector)context.get("foundBulletins")).get(0));		

		assertEquals("wrong # of bulletins in context?", fakeBulletins.size(), ((Integer)context.get("totalBulletins")).intValue());		

		Vector sortByFields = FindBulletinsFields.getSortByFieldDisplayNames();
		assertEquals("SortingBy Fields not the same #?", sortByFields.size(), ((Vector)context.get("sortByFields")).size());

		assertEquals("SearchTag not the same?", mySearchByTag, context.get("currentlySortingBy"));		
	}
	
	public void testPopulateSimpleSearch() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;		
		Context context = new MockContext();
		
		SimpleSearch servlet = new SimpleSearch();					
		String templateName = servlet.selectTemplate(request, response, context);
					
		assertEquals("SimpleSearch.vm", templateName);				
		assertEquals("The defaultSimpleSearch is empty", "", context.get("defaultSimpleSearch"));		
		
		String sampleQuery = "this is what the user is searching for";		
		request.getSession().setAttribute("simpleQuery", sampleQuery);		
		
		servlet = new SimpleSearch();
		servlet.selectTemplate(request, response, context);
		
		assertEquals("The defaultSimpleSearch match.", sampleQuery, context.get("defaultSimpleSearch"));				
	}	


	final UniversalId uid1 = UniversalId.createDummyUniversalId();
	final UniversalId uid2 = UniversalId.createDummyUniversalId();
	final UniversalId uid3 = UniversalId.createDummyUniversalId();
	final String bulletin1Title = "title 1";
	final String bulletin2Title = "title 2";
	final String bulletin3Title = "title 3";
	final String bulletin1Language = "en";
	final String bulletin2Language = "es";
	final String bulletin3Language = "un";

	final String tagFoundBulletins = "foundBulletins";


	class BulletinList
	{
		public Vector getList()
		{
			Vector infos = new Vector();
			BulletinInfo bulletinInfo1 = new BulletinInfo(uid1);
			bulletinInfo1.set("title", bulletin1Title);
			bulletinInfo1.set("language", bulletin1Language);
			
			BulletinInfo bulletinInfo2 = new BulletinInfo(uid2);
			bulletinInfo2.set("title", bulletin2Title);
			bulletinInfo2.set("language", bulletin2Language);
			
			BulletinInfo bulletinInfo3 = new BulletinInfo(uid3);
			bulletinInfo3.set("title", bulletin3Title);
			bulletinInfo3.set("language", bulletin3Language);

			infos.add(bulletinInfo3);
			infos.add(bulletinInfo2);
			infos.add(bulletinInfo1);
			return infos;
		}
	}
}
