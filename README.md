# Geotaur/Neotaur

In the overlay of real and virtual a maze appears. In it roams the meme-descendants of the ancient Minotaur: Geotaur and Neotaur. Put down lures of various strengths(radii) at chosen locations and stand a chance to glimpse these illusive mega-fauna.

**tl;dr**

Geofence/Awareness trigger [sampler](https://en.wikipedia.org/wiki/Sampler_(needlework)) app. Geotaur and Neotaur are identical twins: two flavours differing only in the version of awareness api that is used. The location code is wrapped, somewhat artificially, in the [FenceAccess Interface](https://github.com/maiatoday/Geotaur/blob/master/app/src/main/java/net/maiatoday/geotaur/location/FenceAccess.java) so that the base apps can interact with the location code in the same way.

Also in this sampler app:

* dagger2 dependency injection
* firebase remote config
* firebase analytics
* rudimentary firebase db access (watch this space)
* recycler views with databinding

This is a WIP:
-[ ] fix walk detection
-[ ] anonymous firebase login
-[ ] store geo lures in firebase
-[ ] login convert to google login
-[ ] compound individual quips in multiple notification to one notification
-[ ] make the fence info not display in a toast that disappears

# Getting started
If you want to mess with this example you need to clone it and the submodule.
```
git clone --recursive <ssh or https link for the project>
```
Also you need to get the google-settings.json file from the [firebase console] (https://console.firebase.google.com/) and put it into the app folder.