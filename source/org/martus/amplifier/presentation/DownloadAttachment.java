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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.martus.amplifier.attachment.AttachmentManager;
import org.martus.amplifier.attachment.AttachmentStorageException;
import org.martus.amplifier.attachment.FileSystemAttachmentManager;
import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.amplifier.search.AttachmentInfo;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletResponse;
import org.martus.amplifier.velocity.AmplifierServletSession;
import org.martus.amplifier.velocity.WrappedServletRequest;
import org.martus.amplifier.velocity.WrappedServletResponse;
import org.martus.common.packet.UniversalId;
import org.martus.util.StreamCopier;

public class DownloadAttachment extends HttpServlet
{
	public DownloadAttachment()
	{
		this(AmplifierConfiguration.getInstance().getBasePath());
	}

	public DownloadAttachment(String testBasePathToUse)
	{
		super();
		basePath = testBasePathToUse;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		WrappedServletRequest ampRequest = new WrappedServletRequest(request);
		WrappedServletResponse ampResponse = new WrappedServletResponse(response);
		internalDoGet(ampRequest, ampResponse);	
	}

	public void internalDoGet(AmplifierServletRequest request, AmplifierServletResponse response) throws IOException
	{
		AmplifierServletSession session = request.getSession();
		List bulletins = (List)session.getAttribute("foundBulletins");
		int bulletinIndex = Integer.parseInt(request.getParameter("bulletinIndex"));
			
		BulletinInfo bulletin = (BulletinInfo)bulletins.get(bulletinIndex - 1);
		int attachmentIndex = Integer.parseInt(request.getParameter("attachmentIndex"));
		AttachmentInfo info = (AttachmentInfo)bulletin.getAttachments().get(attachmentIndex-1);
		if(basePath == null)
			basePath =	AmplifierConfiguration.getInstance().getBasePath();
		try
		{
			UniversalId uId = UniversalId.createFromAccountAndLocalId(info.getAccountId(), info.getLocalId());
			AttachmentManager manager = MartusAmplifier.attachmentManager;
			
			//MartusAmplifier.attachmentManager is set in tests but live code since
			//Two different classloaders construct the MartusAmplifier && DownloadAttachment
			//requesting the static member results in null 
			if(manager == null) 
				manager = new FileSystemAttachmentManager(basePath);
				
			InputStream in = manager.getAttachment(uId);
			OutputStream out = response.getOutputStream();
			new StreamCopier().copyStream(in, out);
			in.close();
			out.flush();
			out.close();
		}
		catch (AttachmentStorageException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String basePath = null;
}