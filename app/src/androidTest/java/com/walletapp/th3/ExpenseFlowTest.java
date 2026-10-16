package com.walletapp.th3;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ExpenseFlowTest {
    private void login() {
        onView(withId(R.id.edit_username)).perform(replaceText("admin"));
        onView(withId(R.id.edit_password)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(scrollTo(), click());
    }

    private Activity resumedActivity() {
        Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED);
        assertEquals(1, activities.size());
        return activities.iterator().next();
    }

    private void screenshot(String name) throws Exception {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        SystemClock.sleep(650); // Chờ hoạt ảnh hệ thống ổn định trước khi chụp minh chứng.
        Bitmap bitmap = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        File directory = new File(InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getExternalFilesDir(null), "qa");
        assertTrue(directory.isDirectory() || directory.mkdirs());
        try (FileOutputStream stream = new FileOutputStream(new File(directory, name + ".png"))) {
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream));
        }
        bitmap.recycle();
    }

    @Test
    public void loginRejectsEmptyAndWrongCredentialsThenFinishes() throws Exception {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            screenshot("01-login");
            onView(withId(R.id.button_login)).perform(scrollTo(), click());
            onView(withId(R.id.text_login_error)).check(matches(withText(R.string.login_empty)));
            screenshot("07-login-empty");
            onView(withId(R.id.edit_username)).perform(replaceText("admin"));
            onView(withId(R.id.edit_password)).perform(replaceText("wrong"), closeSoftKeyboard());
            onView(withId(R.id.button_login)).perform(scrollTo(), click());
            onView(withId(R.id.text_login_error)).check(matches(withText(R.string.login_incorrect)));
            screenshot("02-login-error");
            login();
            onView(withId(R.id.text_total)).check(matches(withText("200.000 đ")));
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                assertTrue(resumedActivity() instanceof MainActivity);
                assertTrue(ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.STOPPED).stream()
                        .noneMatch(activity -> activity instanceof LoginActivity));
                resumedActivity().finish();
            });
        }
    }

    @Test
    public void addDetailReturnAndDeleteUpdateTheListAndTotal() throws Exception {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.text_total)).check(matches(withText("200.000 đ")));
            screenshot("03-main");
            onData(anything()).inAdapterView(withId(R.id.list_expenses)).atPosition(2).perform(click());
            screenshot("08-detail-sample");
            onView(withId(R.id.button_return)).perform(scrollTo(), click());
            onView(withId(R.id.button_add_expense)).perform(click());
            screenshot("09-add-empty");
            onView(withId(R.id.edit_expense_name)).perform(replaceText("Ăn trưa"));
            onView(withId(R.id.edit_expense_amount)).perform(replaceText("45000"), closeSoftKeyboard());
            onView(withId(R.id.spinner_category)).perform(click());
            onData(equalTo("Ăn uống")).perform(click());
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                    ((DatePicker) resumedActivity().findViewById(R.id.date_expense)).updateDate(2026, 9, 16));
            onView(withId(R.id.edit_expense_note)).perform(scrollTo(), replaceText("Ăn trưa tại căng tin\nThanh toán tiền mặt"), closeSoftKeyboard());
            screenshot("04-add");
            onView(withId(R.id.button_save_expense)).perform(scrollTo(), click());
            onView(withId(R.id.text_total)).check(matches(withText("245.000 đ")));
            screenshot("10-main-after-add");
            scenario.recreate();
            onView(withId(R.id.text_total)).check(matches(withText("245.000 đ")));
            onData(anything()).inAdapterView(withId(R.id.list_expenses)).atPosition(3).perform(click());
            onView(withId(R.id.text_detail_name)).check(matches(withText("Ăn trưa")));
            onView(withId(R.id.text_detail_amount)).check(matches(withText("45.000 đ")));
            onView(withId(R.id.text_detail_category)).check(matches(withText("Ăn uống")));
            onView(withId(R.id.text_detail_date)).check(matches(withText("16/10/2026")));
            onView(withId(R.id.text_detail_note)).check(matches(withText("Ăn trưa tại căng tin\nThanh toán tiền mặt")));
            screenshot("05-detail");
            onView(withId(R.id.button_return)).perform(scrollTo(), click());
            onView(withId(R.id.text_total)).check(matches(withText("245.000 đ")));
            onData(anything()).inAdapterView(withId(R.id.list_expenses)).atPosition(3).perform(click());
            onView(withId(R.id.button_delete_expense)).perform(scrollTo(), click());
            screenshot("11-delete-confirm");
            onView(withText(R.string.cancel)).inRoot(isDialog()).perform(click());
            onView(withId(R.id.text_detail_name)).check(matches(withText("Ăn trưa")));
            onView(withId(R.id.button_delete_expense)).perform(scrollTo(), click());
            onView(withText(R.string.confirm_delete)).inRoot(isDialog()).perform(click());
            onView(withId(R.id.text_total)).check(matches(withText("200.000 đ")));
            screenshot("06-main-after-delete");
        }
    }

    @Test
    public void addRejectsInvalidInputAndAllowsAnEmptyNote() throws Exception {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.button_add_expense)).perform(click());
            onView(withId(R.id.button_save_expense)).perform(scrollTo(), click());
            onView(withId(R.id.edit_expense_name)).check(matches(hasErrorText(
                    InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.expense_name_required))));
            screenshot("12-add-name-error");
            onView(withId(R.id.edit_expense_name)).perform(replaceText("Khoản chi"), closeSoftKeyboard());
            for (String amount : new String[]{"", "0", "-1", "9999999999999999999999999"}) {
                InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                        ((EditText) resumedActivity().findViewById(R.id.edit_expense_amount)).setText(amount));
                onView(withId(R.id.button_save_expense)).perform(scrollTo(), click());
                onView(withId(R.id.edit_expense_amount)).check(matches(hasErrorText(
                        InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.expense_amount_invalid))));
                if ("0".equals(amount)) {
                    screenshot("13-add-amount-error");
                }
            }
            onView(withId(R.id.edit_expense_amount)).perform(replaceText("10000"), closeSoftKeyboard());
            onView(withId(R.id.button_save_expense)).perform(scrollTo(), click());
            onView(withId(R.id.text_total)).check(matches(withText("210.000 đ")));
            onData(anything()).inAdapterView(withId(R.id.list_expenses)).atPosition(3).perform(click());
            onView(withId(R.id.text_detail_note)).check(matches(withText("")));
            pressBack();
        }
    }

    @Test
    public void cancelAndDeleteAllKeepTheCorrectStateAfterRecreation() throws Exception {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.button_add_expense)).perform(click());
            onView(withId(R.id.button_back)).perform(click());
            onView(withId(R.id.text_total)).check(matches(withText("200.000 đ")));
            onData(anything()).inAdapterView(withId(R.id.list_expenses)).atPosition(2).perform(click());
            onView(withId(R.id.text_detail_note)).check(matches(withText("Sách Lập trình Android")));
            pressBack();
            onView(withId(R.id.text_total)).check(matches(withText("200.000 đ")));
            for (int count = 3; count > 0; count--) {
                onData(anything()).inAdapterView(withId(R.id.list_expenses)).atPosition(0).perform(click());
                onView(withId(R.id.button_delete_expense)).perform(scrollTo(), click());
                onView(withText(R.string.confirm_delete)).inRoot(isDialog()).perform(click());
            }
            onView(withId(R.id.text_total)).check(matches(withText("0 đ")));
            screenshot("14-main-empty");
            scenario.recreate();
            onView(withId(R.id.text_total)).check(matches(withText("0 đ")));
            scenario.onActivity(activity -> assertEquals(0,
                    ((ListView) activity.findViewById(R.id.list_expenses)).getAdapter().getCount()));
            onView(withId(R.id.button_add_expense)).check(matches(isDisplayed()));
        }
    }
}
