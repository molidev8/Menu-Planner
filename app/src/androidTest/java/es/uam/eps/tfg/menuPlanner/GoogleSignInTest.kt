package es.uam.eps.tfg.menuPlanner


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class GoogleSignInTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun init() {
        ServiceLocator.resetApplication()
    }

    @Test
    fun googleSignInTest() {
        val fy = onView(
            allOf(
                withText("Sign In"),
                childAtPosition(
                    allOf(
                        withId(R.id.button_google_sign_in),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            5
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        fy.perform(click())

        BaseRobot().assertOnView(withId(R.id.init_info_username_editText),
            ViewAssertions.matches(isDisplayed())
        )


        val textInputEditText = onView(
            allOf(
                withId(R.id.init_info_username_editText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.init_info_username),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText.perform(scrollTo(), replaceText("miguel"), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.init_info_age_editText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.init_info_age),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText2.perform(scrollTo(), replaceText("21"), closeSoftKeyboard())

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.init_info_height_editText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.init_info_height),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText3.perform(replaceText("179"), closeSoftKeyboard())

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.init_info_weight_editText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.init_info_weight),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText4.perform(replaceText("59"), closeSoftKeyboard())

        val materialRadioButton = onView(
            allOf(
                withId(R.id.male_checkbox), withText("Men"),
                childAtPosition(
                    allOf(
                        withId(R.id.fragment_sign_up),
                        childAtPosition(
                            withId(R.id.layout_init),
                            0
                        )
                    ),
                    4
                )
            )
        )
        materialRadioButton.perform(scrollTo(), click())

        val materialRadioButton2 = onView(
            allOf(
                withId(R.id.sedentary_checkbox), withText("Sedentary"),
                childAtPosition(
                    allOf(
                        withId(R.id.fragment_sign_up),
                        childAtPosition(
                            withId(R.id.layout_init),
                            0
                        )
                    ),
                    7
                )
            )
        )
        materialRadioButton2.perform(scrollTo(), click())

        val materialButton = onView(
            allOf(
                withId(R.id.button_next), withText("Next"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    1
                )
            )
        )
        materialButton.perform(scrollTo(), click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
