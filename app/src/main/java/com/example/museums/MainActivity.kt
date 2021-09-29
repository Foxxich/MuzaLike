package com.example.museums

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.museums.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: AlertDialog
    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)

        val item = savedInstanceState?.getInt("Key")

        dialog = builder.create()
        dialog.show()

        if(item != null) {
            id = item
            loadObject(id)
        } else {
            loadResult()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("Key", id)
    }

    private fun loadResult() {
        id = Random.nextInt(2000, 10145)//(10120,10150)
        loadObject(id)
    }

    private fun loadObject(id: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.artic.edu/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val checkApi = retrofit.create(MuseumAPI::class.java)
        val call = checkApi.getResultExpression(id.toString())
        Log.d("LINK", retrofit.baseUrl().toString())
        Log.d("ID", id.toString())

        call.enqueue(object : Callback<MuseumDTO> {
            override fun onResponse(
                call: Call<MuseumDTO>,
                response: Response<MuseumDTO>
            ) {
                Log.d("API_SUCCESS", response.toString())
                val body = response.body()
                if (body != null) {
                    setInformationAboutObject(body)
                    dialog.dismiss()
                } else {
                    sendRequestAgain(id)
                }
            }

            override fun onFailure(call: Call<MuseumDTO>, t: Throwable) {
                Log.d("ERROR", t.localizedMessage!!)
            }
        })
    }

    private fun sendRequestAgain(id: Int) {
        this.id = id + 10 + Random.nextInt(1, 100)
        loadObject(this.id)
    }

    private fun setInformationAboutObject(body: MuseumDTO) {
        Picasso
            .get()
            .load("https://www.artic.edu/iiif/2/" + body.data.image_id + "/full/843,/0/default.jpg")
            .fit().centerCrop()
            .into(binding.imageView)
        Thread.sleep(1_000)
        findViewById<TextView>(R.id.titleTextView).text = body.data.title
        findViewById<TextView>(R.id.dataTextView).text = body.data.date_display
        findViewById<TextView>(R.id.artistTextView).text = body.data.artist_display
        findViewById<TextView>(R.id.placeOfOriginTextView).text = body.data.place_of_origin
        findViewById<TextView>(R.id.mediumTextView).text = body.data.medium_display
//        val artistId = body.data.artist_id
        var classificationTitle: String = "Empty"
        var styleTitle: String = "Empty"
        try {
            classificationTitle = body.data.classification_title
            styleTitle = body.data.style_title
            if(classificationTitle.contains("\n") || styleTitle.contains("\n")) {
                classificationTitle = classificationTitle.replace("\n", " ")
                styleTitle = styleTitle.replace("\n", " ")
            }
        } catch (e: NullPointerException) {

        }
        findViewById<TextView>(R.id.classificationTextView).text = "Class "+classificationTitle
        //findViewById<TextView>(R.id.styleTextView).text = "Style  "  + styleTitle
        var html = "<p>Utagawa Hiroshige is recognized as a master of the ukiyo-e woodblock printing tradition, having created 8,000 prints of everyday life and landscape in Edo-period Japan with a splendid, saturated ambience. Orphaned at 12, Hiroshige began painting shortly thereafter under the tutelage of Toyohiro of the Utagawa school. His early work of narrow, vertical landscapes picturing thatched houses nestled between cliffs and vignettes of birds perched on flowering branches shows the influence of Chinese scroll painting as well as the previously dominant<a href=\\\"https:\\/\\/www.artic.edu\\/artworks\\/241452\\/monkey-trainers-and-scenes-of-chinese-life\\\" target=\\\"_blank\\\">Kan\\u014d school<\\/a> of Japanese painting.<\\/p><p>Much of Hiroshige\\u2019s work focuses on landscape. Partly inspired by Katsushika Hokusai\\u2019s popular <a href=\\\"https:\\/\\/www.artic.edu\\/collection?q=Thirty-Six%20Views%20of%20Mount%20Fuji\\\" target=\\\"_blank\\\"><em>Thirty-Six Views of Mount Fuji<\\/em><\\/a>, Hiroshige took a softer, less formal approach with his <a href=\\\"https:\\/\\/www.artic.edu\\/collection?q=Fifty-Three%20Stations%20of%20the%20Tokaido\\\" target=\\\"_blank\\\"><em>Fifty-Three Stations of the Tokaido<\\/em><\\/a> (1833\\u201334), completed after traveling that coastal route linking Edo and Kyoto. Mountains grow green and bands of salmon-colored sunrise hang in the mist in prints like <em>Maisaka\\u2014No. 31<\\/em>, where traders and farmers mundanely pass by in the foreground.<\\/p><p>Hiroshige\\u2019s prolific output was somewhat due to his being paid very little per series. Still, this did not deter him, as he receded to Buddhist monkhood in 1856 to complete his brilliant and lasting <a href=\\\"https:\\/\\/www.artic.edu\\/collection?q=One%20Hundred%20Famous%20Views%20of%20Edo\\\" target=\\\"_blank\\\"><em>One Hundred Famous Views of Edo<\\/em><\\/a> (1856\\u201358). He died in 1858, 10 years before Monet, Van Gogh, Whistler, and a host of Impressionist painters became eager collectors of Japanese art. And so Hiroshige\\u2019s surging <em>bokashi<\\/em>, or varied gradient printing, lives on\\u2014visibly influencing artists like Paul Gauguin (see the Art Institute\\u2019s <a href=\\\"https:\\/\\/www.artic.edu\\/artworks\\/27943\\/mahana-no-atua-day-of-the-god\\\" target=\\\"_blank\\\"><em>Mahana no atua<\\/em><\\/a>, 1894) and <a href=\\\"https:\\/\\/www.artic.edu\\/collection?artist_ids=Frank+Lloyd+Wright\\\" target=\\\"_blank\\\">Frank Lloyd Wright<\\/a>.<\\/p>"
        html = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        val unicodeCharsPattern: Pattern = Pattern.compile("\\\\u(\\p{XDigit}{4})")
        val unicodeMatcher: Matcher = unicodeCharsPattern.matcher(html)
        var cleanData: String? = null
        if (unicodeMatcher.find()) {
            cleanData = unicodeMatcher.replaceAll("")
        }

        findViewById<TextView>(R.id.styleTextView).text = "Style  "+cleanData
//        Log.d("MUSEUMS_DATA_OBJECT", body.data.title)
//        Log.d("MUSEUMS_DATA_OBJECT_IMAGE_ID", body.data.image_id)






//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://api.artic.edu/")
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//        Log.d("ARTIST", artistId.toString())
//        val checkApi = retrofit.create(MuseumAPI::class.java)
//        val call = checkApi.getArtistExpression(artistId.toString())
//
//        call.enqueue(object : Callback<ArtistDTO> {
//            override fun onResponse(
//                call: Call<ArtistDTO>,
//                response: Response<ArtistDTO>
//            ) {
//                Log.d("API_SUCCESS", response.toString())
//                val artist = response.body()
//                if (artist != null) {
//                    val artistText = artist.data.description
//                    if(artistText != null)
//                        findViewById<TextView>(R.id.artistInfoTextView).text = "Artist life " + artistText
//                } else {
//                    sendRequestAgain(artistId.toInt())
//                }
//            }
//
//            override fun onFailure(call: Call<ArtistDTO>, t: Throwable) {
//                Log.d("ERROR", t.localizedMessage!!)
//            }
//        })
        //https://api.artic.edu/api/v1/artists/34946 - about artist
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i("UI_INFO", "Selected Item: " + item.title)
        return when (item.itemId) {
            R.id.save_button ->                // do your code
            {
                Toast.makeText(this, "Priority list changed ", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.open_list -> {
                Toast.makeText(this, "Time list changed ASC", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.refresh_button -> {
                Log.i("UI_INFO", "Get new object")
                dialog.show()
                loadResult()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}