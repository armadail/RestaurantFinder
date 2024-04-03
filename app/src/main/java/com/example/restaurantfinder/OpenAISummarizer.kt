package com.example.restaurantfinder

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.AuthorAttribution
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Review

// openai sdk
@OptIn(BetaOpenAI::class)
class OpenAISummarizer(private val openAI: OpenAI) {
    @Throws(NoChoiceAvailableException::class)
    suspend fun sendChatRequest (restaurant : Place): String?{
        val reviewList = restaurant.reviews
        var prompt = "Summarize the following in under 50 words: "
        for (review in reviewList){
            prompt += review.text
        }
        Log.i(TAG,prompt)

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a food reviewer that summarizes reviews in a short and concise manner"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )

        val chatMessage = openAI.chatCompletion(chatCompletionRequest).choices.first().message
            ?: throw NoChoiceAvailableException()



        return chatMessage.content

    }

    fun updatePlaceData(marker: Marker?, description: String) {
        // 1. Get tag
        val place = (marker?.tag as? Place) ?: return
        var updatedReviews = place.reviews.toMutableList()

       //Log.i(TAG1,updatedReviews[0].text.toString())

        val author = AuthorAttribution.builder("GPT3.5 Summary").build()
        val newReview = Review.builder(4.0,author)
            .setText(description)
            .build()
        try {
            updatedReviews.add(0, newReview)
        } catch (e: Exception) {
            Log.e(TAG1, "Failed to add review: ${e.message}")
            // the summerizer failed so we break out of the function
            return
        }

        Log.i(TAG1,updatedReviews[0].text.toString())

        //copy over the old data
        val restaurantBuilder = Place.builder()
            .setId(place.id)
            .setName(place.name)
            .setLatLng(place.latLng)
            .setAddress(place.address)
            .setRating(place.rating)
            .setUserRatingsTotal(place.userRatingsTotal)
            .setReviews(updatedReviews)

        val updatedPlace = restaurantBuilder.build()
        //Log.i(TAG1,updatedPlace.toString())

        marker?.tag = updatedPlace
        return


    }
    companion object {
        private const val TAG = "OpenAISummarizer"
        private const val TAG1 = "UpdatePlaceData"
    }

}
class NoChoiceAvailableException: Exception()