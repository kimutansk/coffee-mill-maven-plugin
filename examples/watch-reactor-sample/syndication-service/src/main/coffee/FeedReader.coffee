###*
@class SyndicationService.FeedReader
@classDesc The Feed Reader contract.
Service defining methods to read a syndicated feed. This service exposed
 methods to read one specific feed. Providers must also publish the following
 properties:

    org.nano.syndication.feed.title: the feed title
    org.nano.syndication.feed.url: the feed url (String)

Providers may also poll the feed for new entries periodically. If new entries are found, the providers must post an
 event on the hub to the topic: **org/nano/syndication**. The event must contains the following properties:

    feed.url: the feed url (String)
    feed.title: the feed title
    entry.title: the entry title
    entry.url: the entry url (String) if exists
    entry.content: the entry content
    entry.date: the entry publication (Date)
    entry.categories: the entry categories if exists
    entry.author: the entry author

Be aware that this class cannot be instantiated directly. It must be implementated by a component and consumed as a service.

###
window.SyndicationService.FeedReader = {

    ###*
    @return {String} the feed title.
    @method
    @name #getTitle
    @memberOf SyndicationService.FeedReader
    ###
    getTitle: ->

    ###*
    @return {String} the feed url.
    @method
    @name #getUrl
    @memberOf SyndicationService.FeedReader
    ###
    getUrl: ->

    ###*
    Gets all entries.
    @method
    @memberOf SyndicationService.FeedReader
    @name #getEntries
    @return {Array of SyndicationService.FeedEntry} the feed entries of the feed or an empty list if the feed has no entry.
    ###
    getEntries : ->

    ###*
    Gets recent entries.
    The number of entries returned by this method depends on the implementation. It may be configurable.
    @method
    @memberOf SyndicationService.FeedReader
    @name #getRecentEntries
    @returns {Array of SyndicationService.FeedEntry} the recent entries of the feed or an empty list if the feed has no entry.
    ###
    getRecentEntries : ->

    ###*
    Gets the last entry, ```null``` if the feed has no entry.
    @method
    @memberOf SyndicationService.FeedReader
    @name #getLastEntry
    @return {SyndicationService.FeedEntry} the last feed entry
    ###
    getLastEntry : ->

}