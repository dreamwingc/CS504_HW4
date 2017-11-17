READ ME FOR HW4 PART TWO

1. Follow the instructions in /IndexBuilderPart1/ReadMe.txt to set the required parameters or documents,
   as well as the required libarys for demo_feeder, IndexBuiderForWebCrawler and WebCrawler

2. Follow the instructions in /IndexBuilderPart1/ReadMe.txt to set MySQL server.

3. Start RabbitMQ before running any programs.

4. Firstly, run demo_feeder to put the ad information query which will be crawled from Amazon later.
	- This information will be send to q_feeds in RabbitMQ's Queue.

5. Secondly, run WebCrawler (stored in WebCrawler_2/) to get the ad from q_feed, and start to crawl the related ads
	- These ads information will be send to q_products in RabbitMQ's Queue.

6. Finally, run IndexBuiderForWebCrawler to get the crawled ads from q_products, and then build forward index to MySQL database: searchads.
   Also, the invert indext will be built to access memcatched.	
   