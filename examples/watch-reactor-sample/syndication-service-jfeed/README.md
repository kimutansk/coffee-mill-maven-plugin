# Syndication Service - JFEED based implementation

This implementation of the syndication service is based on JFeed and on the Google Feed API. It implements the
complete syndication-service API (```FeedReader```).

## Features

* Periodic polling
* Support the Google Feed API to avoid the 'same origin policy'
* Emits event when new feeds are detected

## Usage

### Prerequisites

This component requires JQuery and JFeeds, as well as h-ubu.

    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script type="text/javascript" src="https://raw.github.com/jfhovinne/jFeed/master/build/dist/jquery.jfeed.js"></script>
    <script type="text/javascript" src="https://github.com/downloads/nano-project/h-ubu/hubu-all-min.js"></script>

### Instantiation and Configuration

You then need to create the instance you want to register and configure it:

    hub.registerComponent(new SyndicationService.JFeed.FeedReaderImpl(), {
                    'feed.url' : 'http://news.google.com/?output=atom',
                    'feed.useGoogleFeedAPI' : true,
                    'feed.period' : 60000
       });

* The property ```feed.url``` is mandatory and specifices the feed url.
* The property ```feed.useGoogleFeedAPI``` enables the Google Feed API to avoid the same origin policy issue. It's
disabled by default.
* The property ```feed.period``` enables the periodic polling. It sets the polling period in **ms**. Periodic polling
 is disabled by default. To disable it explicitelty set it to ```-1```.

You can register several instance of this component:

     hub
        .registerComponent(new SyndicationService.JFeed.FeedReaderImpl(), {
                     'feed.url' : 'http://www.lequipe.fr/rss/actu_rss_Jo.xml',
                     'feed.useGoogleFeedAPI' : true,
                     'feed.period' : 10000
        })
        .registerComponent(new SyndicationService.JFeed.FeedReaderImpl(), {
                     'feed.url' : 'http://news.google.com/?output=atom',
                     'feed.useGoogleFeedAPI' : true,
                     'feed.period' : 10000
        }).start();

### Using the FeedReader Service

Once registered and the hub started, the components are retrieving the feed. **Only** when the feed is retrieved and
parsed, the ```SyndicationService.FeedReader``` service is published.

So to require the service, just configure your component to require the ```FeedReader``` services. In the _configure_
 method of your component add:

    hub.requireService({
                        component: this,
                        contract: SyndicationService.FeedReader,
                        field: "reader"
    });

Then, you can use the ```this.reader``` to read feeds:

    var entries = this.reader.getEntries();

If you wants to be notified of new entries, register a listener on the ```org/nano/syndication``` topic. Again in the
 ```configure``` method just add:

    hub.subscribe(this, "org/nano/syndication", function(event) {
                        // Do something with the entry
    });

### Selecting a specific FeedReader service

If you wants a specific ```FeedReader``` service you can filter the services using the 2 service properties published:

* ```org.nano.syndication.feed.title```: the feed title,
* ```org.nano.syndication.feed.url``` : the feed url

On events, you can also filters the received event using the following properties:

* ```feed.url``` : the feed url
* ```feed.title``` : the feed title
