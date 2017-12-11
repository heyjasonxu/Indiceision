# Indiceision

Indiceision is for the indecisive. The purpose is to facilitate human interaction by removing the analysis paralysis of choosing an activity. Indiceision will help users decide on food locations based on certain preferences like type, price, distance and location.

## Database
The application uses Firebase to handle user authentication and to store data about the restaurants each user visited and whether they liked it or not (this can be viewed in their profile in the application). Firebase is also used to store the number of times each restaurant is suggested, how many of the users that were suggested the restaurant actually visited the restaurant and whether they liked it or not, collected from a notification sent 30 minutes after the user presses go (but pops up immediately after pressing go for the demo).

## Roll a Dice
After signing in, you can shake the phone to 'roll the dice'. You will notice the result restaurant (queried from the Yelp API based on your location using the Google Maps API) changing based on the dice.

## Result Screen
This screen shows the restaurant chosen from the dice roll with information about the restaurant, Yelp's rating, our rating (based on the data stored in Firebase), where it is located, and the ability to share, call or go to that location (which takes you straight to walking navigation in google maps).

## Notification
The response from the notification provides information about the restaurant that is used for the rating and whether or not the user went to the restaurant and liked it (which will show up on their profile)
