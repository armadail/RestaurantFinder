# RestaurantFinder
 Restaurant Finder Android App Using OpenAI, Google Maps and Places SDK
 
 ## App Demo


 ## Features
 New scoring rating system based off probability of a positive review

    log(e^)

 GPT3.5 based summarizer to parse multiple reviews into a single description
 '''
 example prompt {
    model: "gpt-3.5-turbo"
    role: "You are a food reviewer that summarizes reviews in a short and concise manner"
    message:""
 }

 example response{
    message:" "
 }
'''
 ## Running the App

 1. Clone this repo locally make sure you have the following APIs keys
 Maps and Places API key can be the same, make sure they are enabled in your google maps project

 MAPS_API_KEY=AIz...
 PLACES_API_KEY=AIz...
 OPENAI_API_KEY=sk...

 2. Create a file called secrets.proprerties in the project directory and paste your api key inside


  
