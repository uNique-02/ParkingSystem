import android.content.Context
import android.content.SharedPreferences
import com.example.parkingsystem.data.user.User
import com.google.gson.Gson

object SharedPreferencesUtils {
    private const val PREF_NAME = "parking_system_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val CURRENT_USER_ID = "current_user_id"
    private const val CURRENT_USER = "current_user"

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

    fun setCurrentUser(context: Context, user: User?) { // Change parameter type to User?
        val editor = getPreferences(context).edit()
        val gson = Gson()
        val json = gson.toJson(user)
        editor.putString(CURRENT_USER, json)
        editor.apply()
    }

    fun getCurrentUser(context: Context): User? {
        val gson = Gson()
        val json = getPreferences(context).getString(CURRENT_USER, null)
        return gson.fromJson(json, User::class.java)
    }
}
