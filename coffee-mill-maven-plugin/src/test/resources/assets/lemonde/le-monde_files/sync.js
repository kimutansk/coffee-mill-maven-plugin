/**
 * Script de publicité appelé en synchrone
 * Mode delayed : juste avant le </body>
 * Mode sync : dans le head
 */

(function()
{
   var global = (function () { return this; }());
   var matches, i, il, keywords, url, oasQueryTemp = [];

   // Pub activée ou non
   global.OAS_active = global.lmd.conf.pub.active;

   // Cas particulier des crawlers google bot
   if( navigator.userAgent.toLowerCase().indexOf('googlebot') !== -1 ){
      global.OAS_active = false;
   }

   if (!global.OAS_active) {
      global.OAS_AD = function(){};
      global.OAS_listpos = '';
      return;
   }

   // Fallback MIA
   if (typeof MIA === "undefined"){ MIA = {}; }
   if (typeof MIA.Pub === "undefined") { MIA.Pub = {}; }
   if (typeof MIA.Pub.OAS === "undefined"){ MIA.Pub.OAS = {}; }

   global.OAS_url = "http://pubs.lemonde.fr/RealMedia/ads/";
   global.OAS_rns = (Math.random() + "").substring(2, 11);

   if(typeof OAS_listpos_temp !== "undefined")
   {
      global.OAS_listpos = OAS_listpos_temp.join(",");
   }
   else
   {
      global.OAS_listpos = "x01,x02,Top,Top1,Top2,Top3,TopRight,Middle,Middle1,Middle2,Middle3,Position1,Position2,Right,Right1,Right2,x93,Bottom1,Bottom2";
   }

   if (lmd.auth_sync && lmd.auth_sync.abonne) {
      global.OAS_listpos = "Top1,Middle,x93";
   }

   global.OAS_AD = function (pos) {
      if (typeof pos === "undefined" || !pos) {
         var arrDiv = document.getElementsByTagName('div');
         pos = arrDiv[arrDiv.length - 1].getAttribute("data-id");
      }
      OAS_RICH(pos);
   };

   // Calcul de OAS_query
   global.OAS_query = "";
   if ((matches = document.location.search.match(/^\?test_ad=([0-9a-zA-Z_-]+)/)))
   {
      global.OAS_query = matches[1];
   }
   else {
      if (typeof global.lmd !== "undefined" &&
          typeof global.lmd.context !== "undefined" &&
          typeof global.lmd.context.item !== "undefined" &&
          global.lmd.context.item !== null &&
          typeof global.lmd.context.item.id !== "undefined" &&
          global.lmd.context.item.id !== null)
      {
         oasQueryTemp.push("item-id-" + global.lmd.context.item.id);
      }

      if (typeof global.lmd !== "undefined" &&
          typeof global.lmd.context !== "undefined" &&
          global.lmd.context.pageType === "Element" &&
          typeof global.lmd.context.rubriques !== "undefined" &&
          global.lmd.context.rubriques !== null)
      {
         il = global.lmd.context.rubriques.length;
         for(i=0;i<il;i++)
         {
            oasQueryTemp.push(global.lmd.context.rubriques[i].url_friendly);
         }
         oasQueryTemp.push("element");
      }
      else
      {
         keywords = global.OAS_sitepage.split("/");
         keywords.shift();

         il = keywords.length;
         for(i=0;i<il;i++)
         {
            oasQueryTemp.push(keywords[i]);
         }
      }

      global.OAS_query = oasQueryTemp.join(",");
   }

   // Inclusion du script OAS

   url = global.OAS_url + "adstream_mjx.ads/" + global.OAS_sitepage + "/1" + global.OAS_rns + "@" + global.OAS_listpos + "?" + global.OAS_query;
   document.write("<scr" + "ipt src=\"" + url + "\"></scr" + "ipt>");

})();

