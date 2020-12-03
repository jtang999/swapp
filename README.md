# PROJECT-2020STARTER
Final project starter repo for CS 160, Fall 2020


## Group Members


## Final Video
youtube.com/aLinkToYourVideo


# Front End Documentation

## Java Classes And Corresponding XML Files with brief description

Java classes are located at src/main/java/com.example.swap,
XML Files are located at src/main/res/layout

- NearbySwaps.java <--> activity_nearby_swaps.xml
  * This is the home page where users can find swaps near them, filter by type of swap, and search using search terms or filter (tbd).
  * Note: It utilizes the PostCardView, which is a custom view created to display posts as a cardview.
- PostCardView.java <--> post_card_view.xml
  * This is a Card View specially defined to take in post information as a JSONObject and create a new card for the post.
- ProfilePage.java <--> activity_profile_page.xml
  * This is the profile page where users should be able to view their own profile, or the profiles of others.
  * This should include the user's posts, username, and picture as a well as a logout button if they are viewing their own profile.
- ViewSwapActivity.java <--> activity_view_swap.xml
  * This view displays the specific information for a posted swap.
  * The post information itself is contained in a ScrollView.
  * Contains a nested LinearLayout with the contact buttons (phone and email) so that if the user does not provide a phone number, the remaining email button will still be centered. Same goes for the "mark as resolved" and "delete" buttons.
- CreateSwap.java <--> activity_create_swap.xml
  * This view is the form that users fill out to create a swap post.
  * The form itself is contained in a ScrollView for now. Users will have to scroll to fill out the form. The buttons are not contained in the scrollview.
  * Create post / cancel buttons are in a LinearLayout at the bottom.

# Back End Documentation

## User fields (?)
- **email:** String - self explanatory
- **avatar**: String - profile image URL (pulled from Google Account)
- **user_name**: String - also pulled from Google Account
- **post_ids**: Array of strings of user ids - BUT we may not be using this. Can query posts using user_id.

## Post fields
- **LAT**: Number - latitude of the user's location at the time of creating a post
- **LON**: Number - longitude of the user's location at the time of creating a post
- **location**: Text field - use Geolocation API to convert LAT/LON into a city and state. Fill out upon creating a post.
- **contact**: Text field - phone number
- **details**: Text field - general text, user can fill out any extra information about this post.
- **email**: Text field - self explanatory
- **expiration**: Text field - user types in when they need the swap by. ("Needed by" field in the Create Swap view)
- **need**: Text field - what the user needs
- **offer**: Text field - what the user can offer
- **user_id**: Text field - self explanatory
- **status**: Boolean - True if resolved, False if it is still open.
- **creation_time**: Text field - preformatted (ask Andrew)
