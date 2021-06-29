package es.uam.eps.tfg.menuPlanner.util

import androidx.annotation.StringRes
import es.uam.eps.tfg.menuPlanner.MenuApplication

fun String.capitalizeTitle(): String = split(" ").joinToString(" ") { it.capitalize() }

/**
 * Provides access to string resources in any place of the codebase without needing the context
 */
object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return MenuApplication.instance.getString(stringRes, *formatArgs)
    }
}