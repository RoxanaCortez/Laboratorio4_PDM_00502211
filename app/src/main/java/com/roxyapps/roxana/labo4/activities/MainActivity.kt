package com.roxyapps.roxana.labo4.activities

import android.net.Network
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.gson.Gson
import com.roxyapps.roxana.labo4.R
import com.roxyapps.roxana.labo4.adapters.MovieAdapter
import com.roxyapps.roxana.labo4.network.NetworkUtils
import com.roxyapps.roxana.labo4.pojos.Movie
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var viewManager:RecyclerView.LayoutManager

    private var movieList : ArrayList<Movie> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerview()
        initSearchButton()
    }

    fun initRecyclerview(){
        viewManager = LinearLayoutManager(this)

        movieAdapter = MovieAdapter(movieList,{movieItem: Movie -> movieItemCliked(movieItem)})

        movie_list_rv.apply{
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = movieAdapter
        }
    }

    fun initSearchButton() = add_movie_btn.setOnClickListener{
        if(!movie_name_et.text.toString().isEmpty()){
            FetchMovie().execute(movie_name_et.text.toString())
        }
    }
    fun addMovieToList(movie:Movie){
        movieList.add(movie)
        movieAdapter.changeList(movieList)
        Log.d("Number", movieList.size.toString())
    }

    private fun movieItemCliked(item:Movie){

    }

    private inner class FetchMovie: AsyncTask<String,Void,String>(){
        override fun doInBackground(vararg params: String): String {
            if(params.isEmpty())return ""
            val movieName = params[0]
            val movieUrl= NetworkUtils().buildSearUrl(movieName)

            return try{
                NetworkUtils().getResponseFromHttpUrl(movieUrl)
            }catch (e: IOException){
                ""
            }
        }
        override  fun onPostExecute(movieInfo:String){
            super.onPostExecute(movieInfo)
            if(!movieInfo.isEmpty()){
                val movieJson =JSONObject(movieInfo)
                if(movieJson.getString("Response")=="True"){
                    val movie = Gson().fromJson<Movie>(movieInfo, Movie::class.java)
                    addMovieToList(movie)
                }else{
                    Snackbar.make(main_ll, "No existe la pelicula en la base", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
