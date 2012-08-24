###
# The Feed Entry class.
###

###*
Creates Feed Entries.
Implementations may extend / overide. So some usages can be restricted by implementations.
@class SyndicationService.FeedEntry
@classdesc FeedEntry represents feed entries. Feed entries are used to manipulate the entry of the retrieve feeds.
###
window.SyndicationService.FeedEntry =

  class FeedEntry
    ###*
    The feed entry title
    @private
    @name #_title
    @memberof SyndicationService.FeedEntry
    @type String
    ###
    _title : null
    ###*
    The feed entry url
    @private
    @name #_url
    @memberof SyndicationService.FeedEntry
    @type String
    ###
    _url : null
    ###*
    The feed entry author
    @private
    @name #_author
    @memberof SyndicationService.FeedEntry
    @type String
    ###
    _author : null
    ###*
    The feed entry publication date
    @private
    @name #_pubDate
    @memberof SyndicationService.FeedEntry
    @type Date
    ###
    _pubDate : null
    ###*
    The feed entry content
    @private
    @name #_content
    @memberof SyndicationService.FeedEntry
    @type String
    ###
    _content : null
    ###*
    The feed entry categories. (Array of String)
    @private
    @name #_categories
    @memberof SyndicationService.FeedEntry
    @type Array
    ###
    _categories : []

    ###*
    @name #getTitle
    @method
    @memberof SyndicationService.FeedEntry
    @returns {String} the entry title. ```null``` if not set.
    ###
    getTitle: -> @_title

    ###*
    Sets the feed entry title.
    @name #setTitle
    @method
    @memberof SyndicationService.FeedEntry
    @param {String} title the title
    @returns {SyndicationService.FeedEntry} the current feed entry.
    ###
    setTitle: (title) -> @_title = title; return @

    ###*
    @name #getUrl
    @method
    @memberof SyndicationService.FeedEntry
    @returns {String} the entry url, sometimes called _link_. If not set returns ```null```
    ###
    getUrl: -> @_url

    ###*
    Sets the feed entry url / link.
    @name #setUrl
    @method
    @memberof SyndicationService.FeedEntry
    @param {String} url the entry url
    @returns {SyndicationService.FeedEntry} the current feed entry.
    ###
    setUrl: (url) -> @_url = url; return @

    ###*
    @name #getAuthor
    @method
    @memberof SyndicationService.FeedEntry
    @returns {String} the entry author if set, ```null``` otherwise.
    ###
    getAuthor: -> @_author

    ###*
    Sets the feed entry author.
    @name #setAuthor
    @method
    @memberof SyndicationService.FeedEntry
    @param {String} author the entry's author
    @returns {SyndicationService.FeedEntry} the current feed entry.
    ###
    setAuthor: (author) -> @_author = author; return @

    ###*
    @name #getPublicationDate
    @method
    @memberof SyndicationService.FeedEntry
    @returns {Date} the entry publication date if set, ```null``` otherwise.
    ###
    getPublicationDate: -> @_pubDate

    ###*
    Sets the feed entry url / link.
    @name #setPublicationDate
    @method
    @memberof SyndicationService.FeedEntry
    @param {Date} pubDate the publication date
    @returns {SyndicationService.FeedEntry} the current feed entry.
    ###
    setPublicationDate: (pubDate) -> @_pubDate = pubDate; return @

    ###*
    @name #getContent
    @method
    @memberof SyndicationService.FeedEntry
    @returns {String} the entry content if set, ```null``` otherwise.
    ###
    getContent: -> @_content

    ###*
    Sets the feed entry content.
    @name #setContent
    @method
    @memberof SyndicationService.FeedEntry
    @param {String} content the content
    @returns {SyndicationService.FeedEntry} the current feed entry.
    ###
    setContent: (content) -> @_content = content; return @

    ###*
    @name #getCategories
    @method
    @memberof SyndicationService.FeedEntry
    @returns {Array} the entry categories (Array of String), empty if not set.
    ###
    getCategories: -> @_categories

    ###*
    Adds a category to the feed entry.
    Be careful to not introduce duplicates. This method does not check for duplicates.
    @name #addCategory
    @method
    @memberof SyndicationService.FeedEntry
    @param {String} cat the category to add.
    @returns {SyndicationService.FeedEntry} the current feed entry.
    ###
    addCategory: (cat) -> @_categories.push(cat); return @

    ###*
    Sets the feed entry categories.
    @name #setCategories
    @method
    @memberof SyndicationService.FeedEntry
    @param {Array} categories the categories (array of String)
    @returns {SyndicationService.FeedEntry} the current feed entry.
    ###
    setCategories: (categories) ->
      @_categories = if categories? then categories else []
      return @