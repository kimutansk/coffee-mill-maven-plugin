describe("Direct Binding Test Suite", function () {

    afterEach(function () {
        hub.reset();
    });

    it("should get news from lemonde.fr", function () {

        hub
            .registerComponent(new SyndicationService.JFeed.FeedReaderImpl(), {
                'feed.url':"http://www.lemonde.fr/rss/une.xml",
                'feed.useGoogleFeedAPI':true,
                'feed.name':"le monde"
            })
            .start();

        waitsFor(function () {
            return hub.getServiceReference(SyndicationService.FeedReader) !== null;
        }, "Syndication Service lookup never completed", 10000);

        runs(function () {
            var ref = hub.getServiceReference(SyndicationService.FeedReader);
            expect(ref).toBeTruthy();
            var service = hub.getService(hub, ref);

            expect(service.getEntries().length).toBeGreaterThan(0);
            expect(service.getTitle() !== null).toBeTruthy();
            expect(service.getUrl() !== null).toBeTruthy();
            expect(service.getLastEntry() !== null).toBeTruthy();

            var entry = service.getLastEntry();
            expect(entry.getTitle()).toBeTruthy();
        });


    });

    it("should get news from Google using RSS", function () {
        hub.registerComponent(new SyndicationService.JFeed.FeedReaderImpl(), {
            'feed.url':"http://news.google.com/?output=rss",
            'feed.useGoogleFeedAPI':true
        }).start();

        waitsFor(function () {
            return hub.getServiceReference(SyndicationService.FeedReader) !== null;
        }, "Syndication Service lookup never completed", 10000);

        runs(function () {
            var ref = hub.getServiceReference(SyndicationService.FeedReader);
            expect(ref).toBeTruthy();
            var service = hub.getService(hub, ref);

            expect(service.getEntries().length).toBeGreaterThan(0);
            expect(service.getTitle() !== null).toBeTruthy();
            expect(service.getUrl() !== null).toBeTruthy();
            expect(service.getLastEntry() !== null).toBeTruthy();

            var entry = service.getLastEntry();
            expect(entry.getTitle()).toBeTruthy();
            expect(entry.getContent()).toBeTruthy();
        });
    });

    it("should get news from Google using ATOM", function () {
        hub.registerComponent(new SyndicationService.JFeed.FeedReaderImpl(), {
            'feed.url':"http://news.google.com/?output=atom",
            'feed.useGoogleFeedAPI':true
        }).start();

        waitsFor(function () {
            return hub.getServiceReference(SyndicationService.FeedReader) !== null;
        }, "Syndication Service lookup never completed", 10000);

        runs(function () {
            var ref = hub.getServiceReference(SyndicationService.FeedReader);
            expect(ref).toBeTruthy();
            var service = hub.getService(hub, ref);

            expect(service.getEntries().length).toBeGreaterThan(0);
            expect(service.getTitle() !== null).toBeTruthy();
            expect(service.getUrl() !== null).toBeTruthy();
            expect(service.getLastEntry() !== null).toBeTruthy();

            var entry = service.getLastEntry();
            expect(entry.getTitle()).toBeTruthy();
            expect(entry.getContent()).toBeTruthy();
        });

    });

    //TODO Test events, Test new item detection, Test service properties, Test when the feed cannot be loaded.

});