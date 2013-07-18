/*
 * Copyright (C) 2013 tengcomplex at gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drinschinz.twittertorssamantha;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


public class Main 
{
	static final String APPVERSION = "0.3";
	static final String APPNAME = "twittertorssamantha";
	
	static final Properties properties = new Properties();
	static final List<Uid>userids = new ArrayList<Uid>();
	static Twitter twitter;
	static URL rssamanthaurl;
	
	static class Uid
	{
		String uid,channel;
		
		Uid(String u, String c)
		{
			uid = u;
			channel = c;
		}
		
		public String toString()
		{
			return "uid:"+uid+" channel:"+channel;
		}
	}
	
	public static void main(final String [] args)
	{
		try
		{	
			if(args.length == 0)
			{
				throw new Exception("No properties file given");
			}
			log("Starting "+APPNAME+" "+APPVERSION);
			init(args[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("ERROR during initialization");
		}
		processUserStatuses();
		cleanup();
	}
	
	static void cleanup()
	{
		twitter.shutdown();
		log("Cleaned up");
	}
	
	static void init(String s) throws Exception
	{
		properties.load(new FileInputStream(new File(s)));
		if(properties.size() == 0)
		{
			throw new Exception("No properties in file "+s);
		}
		/* Put auth properties to system.properties and set up users. */
		for(Iterator<Object>iter = properties.keySet().iterator(); iter.hasNext();)
		{
			String k = iter.next().toString();
			if(k.startsWith("twitter4j."))
			{
				System.getProperties().put(k, properties.get(k));
			}
			else if(k.startsWith("uid."))
			{
				String id = k.substring(4);
				userids.add(new Uid(properties.get(k).toString(), properties.get("channel."+id).toString()));
			}
		}
		if(userids.size() == 0)
		{
			throw new Exception("No userids in propertiesfile "+s);
		}
		log("Processing "+Arrays.toString(userids.toArray()));
		String url = properties.get("rssamantha.url").toString();
		if(url == null)
		{
			throw new Exception("No rssamantha.url defined in propertiesfile "+s);
		}
		rssamanthaurl = new URL(url);
		twitter = TwitterFactory.getSingleton();
		log("Connected");
	}
	
	static void processUserStatuses()
	{
		for(Uid u : userids)
		{
			try
			{
				List<Status> statuses = twitter.getUserTimeline(u.uid);
				log("Showing home timeline of "+u.uid);
				for(Status status : statuses) 
				{
					log(status.getUser().getName() + ":" +status.getText()+" ts:"+status.getCreatedAt()+" id:"+status.getId());
					sendItem("["+status.getUser().getName().trim()+"] "+status.getText(), status.getCreatedAt(), "https://twitter.com/"+u.uid+"/status/"+status.getId(), u.channel);
				}
			}
			catch(TwitterException te)
			{
				log("ERROR requesting user "+u);
				te.printStackTrace();
			}
		}
	}
	
	static void sendItem(String title, Date ts, String link, String channel)
	{
		try
		{
			String urlParameters = "source=twitter&title="+URLEncoder.encode(title, "UTF-8")+"&channel="+channel+"&created="+ts.getTime()+"&link="+link;
			log("parameters:"+urlParameters);
			HttpURLConnection connection = (HttpURLConnection) rssamanthaurl.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches (false);
	
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	
			while ((line = reader.readLine()) != null) 
			{
			    System.out.println(line);
			}
			reader.close();
			connection.disconnect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	static void log(String s)
	{
		System.out.println("INFO: "+s);
	}
}

