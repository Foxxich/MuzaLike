package com.example.museums

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MuseumAPI {
    @GET("/api/v1/artworks/{expression}?fields=id,artist_title,title,artist_display,date_display,main_reference_number,image_id,place_of_origin,medium_display,style_title,classification_title,artist_id")
    fun getResultExpression(@Path("expression") expression: String) : Call<MuseumDTO>

    @GET("/api/v1/artists/{expression}?fields=id,description")
    fun getArtistExpression(@Path("expression") expression: String) : Call<ArtistDTO>
}