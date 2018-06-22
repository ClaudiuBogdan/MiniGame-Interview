# MiniGame-Interview
###### A simple android application created for a job offer as android developer.

* TODO 1: Create MainActivity
  * TODO 1.1: Get data from the server
      1. Check Internet connectivity.
      2. Create an AsyncTask
      3. Parse JSON from server
  * TODO 1.2: Display data into main screen
  * TODO 1.3: Add click listener and launch game activity
* TODO 2: Create GameActivity and implement the game engine
  * TODO 2.1 Create a chronometer an start it when enter the GameActivity
  * TODO 2.2 Create customized views representing the number and display them in a random order.
      1. Create a TextView with square background and set the text to the number to be ordered.
      2. Enable the views to be clicked and dragged. 
  * TODO 2.3 Create a helper line to insert the number.
  * TODO 2.4 When view is dropped:
      1. Place the view into the proper position and move all the views to fit the new configuration
      2. Check if the new configuration is sorted.
      3. If the numbers are sorted, start the HiscoreActivity   

  * TODO 2.4 Handle screen rotation.

* TODO 3: Create the HiscoreActivity
  * TODO 3.1: Enable the user to insert his/her name
  * TODO 3.2: Register the name end the score in the bord and POST the result to the server  
  * TODO 3.3: Go to MainActivity
