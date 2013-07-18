Synopsis
================================================================================
twittertorssamantha is a helper tool for getting tweets into RSSamantha
feed aggregator. This is done by using OAth via twitter4j library, reading
the user statuses and sending items via HTTP POST to RSSamantha.

See: http://sourceforge.net/projects/rssamantha/
See: http://twitter4j.org/en/index.html
See: https://dev.twitter.com/

Installation 
================================================================================
1) Unzip archive.
2) Adjust properties and wrapper script.
3) Set up cronjob or run manually.

Usage 
================================================================================
>bash twittertorssamantha.bsh

Properties
================================================================================
twitter4j.debug=true
twitter4j.oauth.consumerKey=*******
twitter4j.oauth.consumerSecret=*******
twitter4j.oauth.accessToken=*******
twitter4j.oauth.accessTokenSecret=*******
uid0=twitteruser0
channel.0=rssamanthachannelname
uid.obama=BarackObama
channel.obama=rssamanthachannelname
rssamantha.url=http://host:port/

Contact
================================================================================
<tengcomplex at gmail.com>
