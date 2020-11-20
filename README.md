# PROJECT-2020STARTER
Final project starter repo for CS 160, Fall 2020


## Group Members


## Final Video
youtube.com/aLinkToYourVideo


# Front End Documentation

## Java Classes And Corresponsing XML Files with brief description

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
  * Contains a nested LinearLayout with the contact buttons (phone and email) so that if the user does not provide a phone number, the remaining email button will still be centered.
- CreateSwap.java <--> activity_create_swap.xml
  * This view is the form that users fill out to create a swap post.
