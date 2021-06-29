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
import es.uam.eps.tfg.menuPlanner.database.Repository
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EmailSignUpTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun init() {
        ServiceLocator.resetApplication()
    }

    @After
    fun end() {
        (ServiceLocator.repository as Repository).deleteUser()
    }

    @Test
    fun emailSignUpTest() {
        val materialButton = onView(
            allOf(
                withId(R.id.sign_in_with_email), withText("Sign in with email"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.myNavHostFragment),
                        0
                    ),
                    9
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(R.id.button_sign_up), withText("Sign Up"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment_login),
                        4
                    ),
                    1
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.login_email_text_editText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.login_email_text),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText.perform(scrollTo(), replaceText("miguel@gmail.com"), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.login_password_text_editText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.login_password_text),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText2.perform(scrollTo(), replaceText("olivera"), closeSoftKeyboard())

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.login_password_text2_editText),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.login_password_text2),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText3.perform(scrollTo(), replaceText("olivera"), closeSoftKeyboard())

        val materialButton4 = onView(
            allOf(
                withId(R.id.button_next_sign_up), withText("Next"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment_sign_up),
                        5
                    ),
                    0
                )
            )
        )
        materialButton4.perform(scrollTo(), click())

        BaseRobot().assertOnView(withId(R.id.init_info_username_editText),
            ViewAssertions.matches(isDisplayed())
        )

        val textInputEditText4 = onView(
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
        textInputEditText4.perform(scrollTo(), replaceText("miguel"), closeSoftKeyboard())

        val textInputEditText5 = onView(
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
        textInputEditText5.perform(scrollTo(), replaceText("21"), closeSoftKeyboard())

        val textInputEditText6 = onView(
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
        textInputEditText6.perform(replaceText("179"), closeSoftKeyboard())

        val textInputEditText7 = onView(
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
        textInputEditText7.perform(replaceText("59"), closeSoftKeyboard())

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

        val materialButton5 = onView(
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
        materialButton5.perform(scrollTo(), click())
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
