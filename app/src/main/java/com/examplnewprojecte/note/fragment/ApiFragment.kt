package com.examplnewprojecte.note.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.examplnewprojecte.note.adapters.PokemonAdapter
import com.examplnewprojecte.note.databinding.FragmentApiBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class ApiFragment : Fragment() {
    private var _binding: FragmentApiBinding? = null
    private val binding get() = _binding!!
    private var apiName: String = ""
    private val TAG = "ApiFragment"
    private lateinit var pokemonAdapter: PokemonAdapter

    companion object {
        private const val ARG_API_NAME = "api_name"

        fun newInstance(apiName: String): ApiFragment {
            return ApiFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_API_NAME, apiName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apiName = it.getString(ARG_API_NAME, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apiTitle.text = apiName
        binding.resultLayout.visibility = View.GONE

        // Thiết lập RecyclerView
        pokemonAdapter = PokemonAdapter(emptyList()) { itemName, itemType ->
            if (itemType == "pokemon") {
                loadPokemonDetails(itemName)
            } else {
                loadTransformationDetails(itemName)
            }
        }
        binding.pokemonRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pokemonAdapter
        }

        binding.callApiButton.setOnClickListener {
            if (apiName == "PokemonAPI") {
                callPokeApi()
            } else if (apiName == "DragonBallAPI") {
                callDragonBallApi()
            } else {
                binding.errorText.text = "API không hợp lệ: $apiName"
                binding.errorText.visibility = View.VISIBLE
            }
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.cancelButton.setOnClickListener {
            binding.resultLayout.visibility = View.GONE
            binding.pokemonRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun callPokeApi() {
        binding.progressBar.visibility = View.VISIBLE
        binding.pokemonRecyclerView.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.resultLayout.visibility = View.GONE

        thread {
            try {
                val url = URL("https://pokeapi.co/api/v2/pokemon?limit=20")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                Log.d(TAG, "PokeAPI HTTP response code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    Log.d(TAG, "PokeAPI response: $response")
                    val json = JSONObject(response)
                    val results = json.getJSONArray("results")
                    val pokemonList = mutableListOf<Triple<String, String, String>>()
                    for (i in 0 until results.length()) {
                        val pokemonObj = results.getJSONObject(i)
                        val name = pokemonObj.getString("name")
                        val detailUrl = pokemonObj.getString("url")
                        pokemonList.add(Triple(name, detailUrl, "pokemon"))
                    }

                    activity?.runOnUiThread {
                        pokemonAdapter.updatePokemonList(pokemonList)
                        binding.progressBar.visibility = View.GONE
                        binding.pokemonRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    throw Exception("HTTP error code: $responseCode")
                }
                connection.disconnect()
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.errorText.text = "Lỗi PokemonAPI: ${e.message}"
                    binding.errorText.visibility = View.VISIBLE
                }
                Log.e(TAG, "Error calling PokeAPI", e)
            }
        }
    }

    private fun callDragonBallApi() {
        binding.progressBar.visibility = View.VISIBLE
        binding.pokemonRecyclerView.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.resultLayout.visibility = View.GONE

        thread {
            try {
                val url = URL("https://dragonball-api.com/api/transformations")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                Log.d(TAG, "DragonBallAPI HTTP response code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    Log.d(TAG, "DragonBallAPI response: $response")
                    val jsonArray = JSONArray(response)
                    val transformationList = mutableListOf<Triple<String, String, String>>()
                    for (i in 0 until jsonArray.length()) {
                        val transformationObj = jsonArray.getJSONObject(i)
                        val name = transformationObj.getString("name")
                        val characterId = if (transformationObj.has("characterId")) {
                            transformationObj.getInt("characterId")
                        } else {
                            -1 // Hoặc giá trị mặc định khác
                        }
                        // Gọi API để lấy tên nhân vật dựa trên characterId
                        val characterName = getCharacterName(characterId.toInt())
                        transformationList.add(Triple(name, characterName, "transformation"))
                    }

                    activity?.runOnUiThread {
                        pokemonAdapter.updatePokemonList(transformationList)
                        binding.progressBar.visibility = View.GONE
                        binding.pokemonRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    throw Exception("HTTP error code: $responseCode")
                }
                connection.disconnect()
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.errorText.text = "Lỗi DragonBallAPI: ${e.message}"
                    binding.errorText.visibility = View.VISIBLE
                }
                Log.e(TAG, "Error calling DragonBallAPI", e)
            }
        }
    }

    private fun loadPokemonDetails(pokemonName: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.resultLayout.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.pokemonRecyclerView.visibility = View.GONE

        thread {
            try {
                val url = URL("https://pokeapi.co/api/v2/pokemon/$pokemonName")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                Log.d(TAG, "Pokémon details HTTP response code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    Log.d(TAG, "Pokémon details response: $response")
                    val json = JSONObject(response)
                    val name = json.getString("name").replaceFirstChar { it.uppercase() }
                    val abilitiesArray = json.getJSONArray("abilities")
                    val abilities = mutableListOf<String>()
                    for (i in 0 until abilitiesArray.length()) {
                        val abilityObj = abilitiesArray.getJSONObject(i).getJSONObject("ability")
                        abilities.add(abilityObj.getString("name"))
                    }
                    val statsArray = json.getJSONArray("stats")
                    val stats = mutableListOf<String>()
                    for (i in 0 until statsArray.length()) {
                        val statObj = statsArray.getJSONObject(i)
                        val statName = statObj.getJSONObject("stat").getString("name")
                        val baseStat = statObj.getInt("base_stat")
                        stats.add("$statName: $baseStat")
                    }
                    val spriteUrl = json.getJSONObject("sprites").getString("front_default")

                    activity?.runOnUiThread {
                        binding.pokemonName.text = name
                        binding.abilitiesText.text = abilities.joinToString(", ")
                        binding.statsText.text = stats.joinToString("\n")
                        Picasso.get().load(spriteUrl).into(binding.pokemonSprite)
                        binding.progressBar.visibility = View.GONE
                        binding.resultLayout.visibility = View.VISIBLE
                    }
                } else {
                    throw Exception("HTTP error code: $responseCode")
                }
                connection.disconnect()
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.errorText.text = "Lỗi tải chi tiết Pokémon: ${e.message}"
                    binding.errorText.visibility = View.VISIBLE
                }
                Log.e(TAG, "Error loading Pokémon details", e)
            }
        }
    }

    private fun loadTransformationDetails(transformationName: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.resultLayout.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.pokemonRecyclerView.visibility = View.GONE

        thread {
            try {
                val url = URL("https://dragonball-api.com/api/transformations")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                Log.d(TAG, "Transformation details HTTP response code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    Log.d(TAG, "Transformation details response: $response")
                    val jsonArray = JSONArray(response)
                    var transformation: JSONObject? = null
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        if (obj.getString("name") == transformationName) {
                            transformation = obj
                            break
                        }
                    }

                    if (transformation != null) {
                        val name = transformation.getString("name")
                        val characterId = transformation.getInt("characterId")
                        val description = transformation.getString("description")
                        val imageUrl = transformation.getString("image")

                        // Lấy tên nhân vật từ characterId
                        val characterName = getCharacterName(characterId)

                        activity?.runOnUiThread {
                            binding.pokemonName.text = name
                            binding.abilitiesText.text = "Nhân vật: $characterName"
                            binding.statsText.text = description
                            Picasso.get().load(imageUrl).into(binding.pokemonSprite)
                            binding.progressBar.visibility = View.GONE
                            binding.resultLayout.visibility = View.VISIBLE
                        }
                    } else {
                        throw Exception("Transformation not found")
                    }
                } else {
                    throw Exception("HTTP error code: $responseCode")
                }
                connection.disconnect()
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.errorText.text = "Lỗi tải chi tiết biến hình: ${e.message}"
                    binding.errorText.visibility = View.VISIBLE
                }
                Log.e(TAG, "Error loading Transformation details", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCharacterName(characterId: Int): String {
        return try {
            val url = URL("https://dragonball-api.com/api/characters/$characterId")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                json.getString("name") // Lấy tên nhân vật
            } else {
                "Unknown Character (HTTP $responseCode)"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching character name for ID $characterId", e)
            "Unknown Character"
        }
    }
}
