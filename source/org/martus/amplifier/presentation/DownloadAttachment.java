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
package org.martus.amplifier.presentation;

import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.martus.amplifier.search.AttachmentInfo;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServlet;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletSession;

public class DownloadAttachment extends AmplifierServlet
{
	public String selectTemplate(AmplifierServletRequest request, HttpServletResponse response, Context context)
		throws Exception
	{
		AmplifierServletSession session = request.getSession();
		Vector bulletins = (Vector)session.getAttribute("foundBulletins");
		int bulletinIndex = Integer.parseInt(request.getParameter("bulletinIndex"));
			
		BulletinInfo bulletin = (BulletinInfo)bulletins.get(bulletinIndex - 1);
		int attachmentIndex = Integer.parseInt(request.getParameter("attachmentIndex"));
		AttachmentInfo info = (AttachmentInfo)bulletin.getAttachments().get(attachmentIndex-1);
		
		context.put("attachment", info);
		return "DownloadAttachment.vm";
	}

}
