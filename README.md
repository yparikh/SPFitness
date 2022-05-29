# Fitness & Health - Android App

## About

This app has been created to help users track and manage their fitness and health needs in an intuitive manner. The app was developed using Java on Android Studios with Google Firestore as the NoSQL cloud database. 

## Project Setup


 Open the terminal:
 ```
 $ cd \<required-path>
 $ git clone https://github.com/yparikh/SPFitness.git
 ```
 * In Android Studio, File -> Open -> SPFitness Folder
 * Once project is built, run with your choice of emulator/device using Run -> Run 'app'

## Overview & Features

The android app lets you:
- Create an account to manage your data 
- Input health data to suggest caloric/hydration goal 
- Hydration Features
    - Track hydration by adding amount drank
    - Explore habits with a monthly and weekly progress graph
- Nutrition Features
    - Query and add food you have eaten to the database
    - View macronutrient information based on food entered for the current day
    - Observe previous eating habits with food logs based on date 
- Fitness Features
    - Track steps with a pedometer progress bar
    - Schedule a reminder for workouts
    - Log workouts to chart physical progress  

## Observer Design Pattern
<img src="/readme/observer_pattern.png" align="top" width="300" hspace="10" vspace="10"> 
</br>
This project implements the Observer pattern. This pattern is one of the many Behaviorial Design Patterns and is used as a method to communciate between classes and objects. The Observer pattern utilizes Observables (Subject), an object we want to observe, and Observers that are notified of such changes. For this android app, the ViewModels for each of the fragments are the Observables since the ViewModel classes are used to manage and store UI related data. Each of the fragments are Observers that will observe any chanages in data that occur in their respective ViewModel. This pattern was applied to the project for a few reasons. First the ViewModel insures that the data is persistent and secure when changing among the fragments. The ViewModel will also track any data that the user adds or updates through the fragments and record them to the Firestore Database.

## Screenshots

<img src="/readme/Screenshot_home.png" align="left"
width="200" hspace="10" vspace="10">
<img src="/readme/Screenshot_nutrition.png" align="left"
width="200" hspace="10" vspace="10">
<img src="/readme/Screenshot_hydration.png" align="left"
width="200" hspace="10" vspace="10">
<img src="/readme/Screenshot_Fitness.png" align="left"
width="200" hspace="10" vspace="10">

## Demo

<img src="/readme/Demo_Nutrition.gif" align="left"
width="200" hspace="10" vspace="10">
<img src="/readme/Demo_Hydration.gif" align="left"
width="200" hspace="10" vspace="10">
<img src="/readme/Demo_Fitness.gif" align="left"
width="200" hspace="10" vspace="10">
<br />
## Credits
* Visualization Library - https://github.com/AAChartModel/AAChartKit
* Round Progressbar Library - https://github.com/akexorcist/RoundCornerProgressBar
* Chip Navigation Bar - https://github.com/ismaeldivita/chip-navigation-bar
* Circular Progressbar Library - https://github.com/lopspower/CircularProgressBar
* FoodData Central API - https://fdc.nal.usda.gov/api-guide.html
* Icons - https://icons8.com/
