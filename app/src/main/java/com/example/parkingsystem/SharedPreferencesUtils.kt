import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.parkingsystem.data.business.businessUser
import com.example.parkingsystem.data.user.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow

object SharedPreferencesUtils {
    private const val PREF_NAME = "parking_system_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val CURRENT_USER_ID = "current_user_id"
    private const val CURRENT_USER = "current_user"
    private const val CURRENT_BUSINESS_USER = "current_business_user"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setIsLoggedIn(context: Context, isLoggedIn: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun getIsLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setCurrentUserId(context: Context, userId: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(CURRENT_USER_ID, userId)
        editor.apply()
    }

    fun getCurrentUserId(context: Context): Int {
        return getPreferences(context).getInt(CURRENT_USER_ID, -1)
    }

    fun setCurrentUser(context: Context, user: User? = null, businessUser: businessUser? = null) { // Change parameter type to User?
        val editor = getPreferences(context).edit()
        val gson = Gson()
        lateinit var json: String
        if(user!=null){
            Log.e("ProfileViewModel", "current user !null? " + user.toString())
            json = gson.toJson(user)
        }else{
            json = gson.toJson(businessUser)
        }
        editor.putString(CURRENT_USER, json)
        Log.e("ProfileViewModel", "getCurrentUser SET: " + CURRENT_USER + " " + json)
        Log.e("ProfileViewModel", "getCurrentUser: SET 2 " + getPreferences(context).getString(CURRENT_USER, null))

        editor.apply()
    }

    fun getCurrentUser(context: Context): User? {
        Log.e("ProfileViewModel", "getCurrentUser: " + getPreferences(context).getString(CURRENT_USER, null))
        val gson = Gson()
        val json = getPreferences(context).getString(CURRENT_USER, null)
        if (gson.fromJson(json, businessUser::class.java) == null) {
            return  return gson.fromJson(json, User::class.java)
        }
        return gson.fromJson(json, User::class.java)
    }

    fun setCurrentBusinessUser(context: Context, businessUser: businessUser) { // Change parameter type to User?
        val editor = getPreferences(context).edit()
        val gson = Gson()
        val json = gson.toJson(businessUser)
        editor.putString(CURRENT_BUSINESS_USER, json)
        editor.apply()
    }

    fun getCurrentBusinessUser(context: Context): businessUser? {
        val gson = Gson()
        val json = getPreferences(context).getString(CURRENT_USER, null)
        return gson.fromJson(json, businessUser::class.java)
    }
}
