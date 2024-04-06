# RestaurantFinder
 Restaurant Finder Android App Using OpenAI, Google Maps and Places SDK
 
 ## App Demo


 ## Features
### New Scoring Rating System
Introducing a novel scoring rating system based on the probability of a positive review. The formula for calculating the score (S) is as follows:
```
S = -ln((1 - P)^ln(V))
```

Where:
- S: New score
- P: Normalized score
- V: Number of reviewers

### GPT3.5 Based Summarizer
Utilizing the power of GPT3.5, our app includes a summarizer that parses multiple reviews into a single concise description. By simulating a food reviewer role, it distills varied opinions into a comprehensive overview, enhancing user understanding.

Example Prompt:
```
 prompt: {
 model: gpt-3.5-turbo
 role: You are a food reviewer that summarizes reviews in a short and concise manner
 message:
 I came in here a couple days ago about an hour before closing and truly was skeptical because not everyone can make “good for you” food and it actually taste amazing. I got the Golden Temple Soup with brown rice & it was delicious! Also got the Coconut shake… and THAT is whats going to keep bringing me back! Such a clean atmosphere and definitely my new go-to in the neighborhood.
 
 My absolute favorite vegan vegetarian spot in town, BC Chau Veggie Express is a true gem in Vancouver. Not only is the food incredibly delicious, but the portions are generous, ensuring you leave satisfied every single visit. Every dish is meticulously crafted with the freshest ingredients, and the consistency in quality and taste is simply unmatched. My kids also look forward to dining here, which says a lot about the family-friendly atmosphere and the universally appealing flavors. The staff deserves a shout-out too, always welcoming and attentive, making each dining experience even more special. Highly recommend to anyone seeking a top-notch veggie experience!
 
 4.5* Food | 2.5* Service
 Our much-anticipated visit to this trendy restaurant took an unexpected turn. What was meant to be a delightful dinner became a disappointing experience when a spicy pepper, undisclosed in the dish's description, left my mouth ablaze. Despite my efforts to seek assistance, the staff seemed indifferent to my distress. I wish they had been more present and empathetic, as a small gesture could have made a significant difference.
 Nevertheless, the familiar golden temple dish was enjoyable, and while I couldn't fully taste the dish I ordered, my past experiences suggest their meals are typically delightful. Despite this hiccup, I still appreciate the restaurant's food quality.
 
 The aroma of this beautiful, family run business, with a conscious effort to make all their food healthy is definitely worth visiting. The owner greeted us with a smile and came to help us choose from the menu.
 We ordered the Golden Temple soup which stunned our taste buds, the Nonia Crispy Rolls that were incredible delicious, the chips with sauce and the broth in the Rice Fields Bowl was perfect.
 They even made the effort of making my birthday special. I would definitely bring my friends here.
 
 I ordered the golden temple soup with noodles and this soup is honestly so good. The portion size is also gigantic and I couldn't even finish it! They have crunchy vegetables and yummy noodles with a rich broth. If you order this you'll probably have enough leftovers for another meal.
 Overall I would love to visit again!!

}
```
Example Response:
```
response:{
message:
BC Chau Veggie Express in Vancouver offers delicious and generous vegan and vegetarian options, such as the Golden Temple Soup. Despite a hiccup with undisclosed spice in a dish, the food quality and welcoming atmosphere shine. The Golden Temple Soup continues to impress with its flavors and large portion size.
 }
```

## Running the App

1. **Clone the Repository**
   Clone this repository locally.

2. **Set Up API Keys**
   Ensure you have the following API keys:
   - MAPS_API_KEY: Obtain from Google Cloud Console, ensure Maps and Places APIs are enabled.
   - PLACES_API_KEY: Can be Same as above.
   - OPENAI_API_KEY: API key for OpenAI.

3. **Create Secrets File**
   Create a file named `secrets.properties` in the project directory and paste your API keys inside.



  
