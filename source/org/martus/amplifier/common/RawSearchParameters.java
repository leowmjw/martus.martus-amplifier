/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2002,2003, Beneficent
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

package org.martus.amplifier.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletSession;
import org.martus.util.MartusFlexidate;

public class RawSearchParameters
{
	public RawSearchParameters(String simpleSearch)
	{
		inputParameters = getDefaultAdvancedFields();
		inputParameters.put(SearchResultConstants.THESE_WORD_TAG, simpleSearch);
	}

	public RawSearchParameters(AmplifierServletRequest request)
	{
		inputParameters = loadFromRequest(request);
	}
	
	String get(String key)
	{
		return (String) inputParameters.get(key);
	}
	
	String getFormattedString(SearchParameters.LuceneQueryFormatter formatter)
	{
		return formatter.getFormattedString(getParameters());
	}

	Map getParameters()
	{
		return inputParameters;
	}
	
	private Map loadFromRequest(AmplifierServletRequest request)
	{
		Map inputParameters = new HashMap();
		for(int i=0; i< SearchResultConstants.ADVANCED_KEYS.length; i++)
		{
			String value = request.getParameter(SearchResultConstants.ADVANCED_KEYS[i]);
			if (value != null)
			{				
				inputParameters.put(SearchResultConstants.ADVANCED_KEYS[i], value);				
			}
		}
		
		return inputParameters;
	}

	public void saveSearchInSession(AmplifierServletSession session)
	{
		AdvancedSearchInfo info = new AdvancedSearchInfo(getParameters());
		session.setAttribute("defaultAdvancedSearch", info);	
	}

	public static void clearSimpleSearch(AmplifierServletSession session)
	{
		session.setAttribute("simpleQuery", "");
		session.setAttribute("defaultSimpleSearch", "");
	}

	public static void clearAdvancedSearch(AmplifierServletSession session)
	{
		AdvancedSearchInfo info = new AdvancedSearchInfo(getDefaultAdvancedFields());
		session.setAttribute("defaultAdvancedSearch", info);	
	}

	public static HashMap getDefaultAdvancedFields()
	{
		HashMap defaultMap = new HashMap();
		defaultMap.put(SearchResultConstants.EXACTPHRASE_TAG, "");
		defaultMap.put(SearchResultConstants.ANYWORD_TAG, "");
		defaultMap.put(SearchResultConstants.THESE_WORD_TAG, "");	
		defaultMap.put(SearchResultConstants.WITHOUTWORDS_TAG, "");
		defaultMap.put(SearchResultConstants.RESULT_FIELDS_KEY, SearchResultConstants.IN_ALL_FIELDS);
		defaultMap.put(SearchResultConstants.RESULT_ENTRY_DATE_KEY, SearchResultConstants.ENTRY_ANYTIME_TAG);
		defaultMap.put(SearchResultConstants.RESULT_LANGUAGE_KEY, SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL);
		defaultMap.put(SearchResultConstants.RESULT_SORTBY_KEY, SearchResultConstants.SORT_BY_TITLE_TAG);

		defaultMap.put(SearchResultConstants.RESULT_START_DAY_KEY, "1");
		defaultMap.put(SearchResultConstants.RESULT_START_MONTH_KEY, "1");
		defaultMap.put(SearchResultConstants.RESULT_START_YEAR_KEY, "1970");
		
		defaultMap.put(SearchResultConstants.RESULT_END_DAY_KEY, Today.getDayString());
		defaultMap.put(SearchResultConstants.RESULT_END_MONTH_KEY, Today.getMonth());
		defaultMap.put(SearchResultConstants.RESULT_END_YEAR_KEY, Today.getYearString());
				
		return defaultMap;	
	}
	
	public String getFieldToSearchIn()
	{
		return get(SearchResultConstants.RESULT_FIELDS_KEY);
	}
	
	public String getLanguage()
	{
		return get(SearchResultConstants.RESULT_LANGUAGE_KEY);
	}
	
	public String getEntryDate()
	{
		return get(SearchResultConstants.RESULT_ENTRY_DATE_KEY);
	}
	
	public String getSortBy()
	{
		return get(SearchResultConstants.RESULT_SORTBY_KEY);
	}

	public String getStartDate()
	{			
		String yearTag = SearchResultConstants.RESULT_START_YEAR_KEY;
		String monthTag = SearchResultConstants.RESULT_START_MONTH_KEY;
		String dayTag = SearchResultConstants.RESULT_START_DAY_KEY;
		return getDateFromRawParameters(yearTag, monthTag, dayTag);
	}

	public String getEndDate()
	{	
		String yearTag = SearchResultConstants.RESULT_END_YEAR_KEY;
		String monthTag = SearchResultConstants.RESULT_END_MONTH_KEY;
		String dayTag = SearchResultConstants.RESULT_END_DAY_KEY;
		return getDateFromRawParameters(yearTag, monthTag, dayTag);
	}

	private String getDateFromRawParameters(String yearTag, String monthTag, String dayTag)
	{
		int year = Integer.parseInt(get(yearTag));
		int month = Integer.parseInt(get(monthTag));
		int day = Integer.parseInt(get(dayTag));
		Date startDate = SearchParameters.getDate(year, month, day);
		return MartusFlexidate.toStoredDateFormat(startDate);
	}

	Map inputParameters;
}