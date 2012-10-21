scope = if global? then global else this

scope.SyndicationService = SyndicationService ? {}
scope.SyndicationService.JFeed = SyndicationService.JFeed ? {}

scope.SyndicationService.JFeed.FeedReaderImpl = class FeedReaderImpl

  _url : null,
  _name : null,
  _useGoogleFeedAPI : false

  _period : -1
  _intervalReference : null
  _async : true

  _hub : null
  ###
  # We register the service ourself as we wait the first retrieval and processing.
  ###
  _reg : null

  _logger : null

  configure: (hub, configuration) ->
    @_url = if configuration["feed.url"]? then configuration["feed.url"] else throw new Extension("feed.url missing in configuration")
    @_name = configuration["feed.name"]  ? configuration["feed.url"]
    @_useGoogleFeedAPI = configuration["feed.useGoogleFeedAPI"] ? false
    @_hub = hub
    @_async = configuration["feed.async"] ? true

    @_logger = new Logger(@_name)
    if (configuration["feed.period"]?) then @_period = configuration["feed.period"]

  start : ->
    @_logger.info("Starting...")
    @_retrieve()
    if (@_period isnt -1)
      @_logger.info("Registering periodic call (" + @_period + ")")
      @_intervalReference = setInterval(@._retrieve, @_period)


  stop : ->
    if @_intervalReference? then clearInterval(@_intervalReference); @_intervalReference = null
    @_reg = null # The service is already unregistered.
    @_feed = null

  _retrieve : =>
    @_logger.info("Retrieving feed : " + @_url)
    self        = @
    if @_useGoogleFeedAPI
      # Delegate to Google API
      apiProtocol = "https"
      apiHost     = apiProtocol + "://ajax.googleapis.com/ajax/services/feed/load"
      apiUrl      = apiHost + "?v=1.0&num=-1&output=json&callback=?&q=" + encodeURIComponent(@_url)

      @_logger.info("Configuring Feed Reader with " + apiUrl)
      @_logger.info("Update Period : " + @_period + ", Asynchronous : " + @_async)

      $.ajax({
        url: apiUrl,
        dataType: 'json',
        async: @_async
      }).done((data) -> self._JSONloaded(data))

    else
      # Load with jfeed
      jQuery.getFeed({
        url: @_url,
        success: (feed) =>
          @._loaded(feed)
      })

  _loaded : (feed) =>
    oldEntries = if @_feed? then HUBU.UTILS.clone(@_feed.items) else []
    @_feed = feed
    @_registerServiceIfNotDoneYet()
    @_detectNewEntries(@_feed, oldEntries)

  _JSONloaded : (data) ->
    oldEntries = if @_feed? then HUBU.UTILS.clone(@_feed.items) else []
    if (not data?  || data.responseStatus is 400  || not data.responseData)
      throw new Exception("Cannot load feed from " + @_url)
    # Parse the responseData
    feed = data.responseData.feed
    @_feed = {
      title: feed.title,
      link: feed.link,
      description: feed.description,
      language: feed.language, # Rarely set
      updated: new Date()
      items: []
    }
    for i in feed.entries
      item = new JFeedItem();
      item.title = i.title;
      item.link = i.link;
      item.description = i.content
      item.updated = new Date(i.publishedDate)
      item.author = i.author
      item.categories = i.categories
      @_feed.items.push(item)
    @_registerServiceIfNotDoneYet()
    @_detectNewEntries(@_feed, oldEntries)

  _registerServiceIfNotDoneYet : ->
    if (@_reg is null)
      @_logger.info("Registering feed reader service for " + @.getTitle())
      @_reg = @_hub.registerService(@, SyndicationService.FeedReader, {
        'org.nano.syndication.feed.title' : @.getTitle(),
        'org.nano.syndication.feed.url' : @.getUrl(),
      })

  _detectNewEntries : (feed, oldEntries) ->
    for item in feed.items when not @_contains(item, oldEntries)
      entry = new scope.SyndicationService.FeedEntry()
        .setTitle(item.title)
        .setUrl(item.link)
        .setPublicationDate(item.updated)
        .setContent(item.description)
        .setCategories(item.categories)
        .setAuthor(item.author)
      @_logger.info("Sending event for new feed entry " + item.title + " from " + @getTitle())
      hub.publish(@, "org/nano/syndication", {
        'feed.url' : @_url,
        'feed.title' : @_feed.title,
        'entry.title' : item.title,
        'entry.url' : item.link,
        'entry.content' : item.content,
        'entry.date' : item.publishDate,
        'entry.categories' : item.categories,
        'entry.author' : item.author,
        'entry' : entry
      })


  _contains: (item, oldEntries) ->
    for entry in oldEntries when (entry.title is item.title)
      if (not entry.updated?) and (not item.updated?) then return true # Both have no date
      if (entry.updated? and item.updated? and item.updated.getTime() is entry.updated.getTime()) then return true # Both have dates and they are equals
    return false

  getComponentName : -> return @_name

  ### Feed Reader Implementation ###

  getTitle: -> return @_feed.title

  getUrl : -> return @_url

  getEntries : ->
    items = @_feed.items
    entries = []
    for item in items
      entries.push(new scope.SyndicationService.FeedEntry()
        .setTitle(item.title)
        .setUrl(item.link)
        .setPublicationDate(item.updated)
        .setContent(item.description)
        .setCategories(item.categories)
        .setAuthor(item.author))
    return entries

  getRecentEntries : -> return @getEntries() #TODO Define what recent mean

  getLastEntry : ->
    return @getEntries()[0] unless @getEntries().length is 0
    return null