package com.walletapp.th3;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

/** Giữ nội dung nằm ngoài thanh trạng thái, thanh điều hướng và bàn phím. */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context base) {
        Locale locale = new Locale("vi", "VN");
        Locale.setDefault(locale);
        Configuration configuration = new Configuration(base.getResources().getConfiguration());
        configuration.setLocale(locale);
        super.attachBaseContext(base.createConfigurationContext(configuration));
    }

    protected void applySystemInsets() {
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(getColor(R.color.status_bar)),
                SystemBarStyle.dark(getColor(R.color.navigation_bar)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        View root = findViewById(R.id.main);
        View statusBar = findViewById(R.id.status_bar_space);
        View navigationBar = findViewById(R.id.navigation_bar_space);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets keyboard = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            setHeight(statusBar, bars.top);
            setHeight(navigationBar, bars.bottom);
            view.setPadding(bars.left, 0, bars.right, Math.max(0, keyboard.bottom - bars.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(root);
    }

    private void setHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.height != height) {
            params.height = height;
            view.setLayoutParams(params);
        }
    }
}
