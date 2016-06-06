package com.izaron.pepperpied;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.InputStream;
import java.text.MessageFormat;

public class CodeActivity extends AppCompatActivity {

    private int currentTheme;
    private String currentCodeTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        WebView webView = (WebView) findViewById(R.id.webView);
        SharedPreferences preferences = getPreferences();

        String fileName = getFileName();
        String convertedFileName = getConvertedFileName();

        setTitle(convertedFileName);

        String codeTheme = getCodeTheme(preferences);
        currentCodeTheme = codeTheme;
        String lineNumbersProperty = getLineNumbersProperty(preferences);
        String sourceCode = readSourceCode(fileName);
        String html = generateHtml(codeTheme, lineNumbersProperty, sourceCode);

        String mime = "text/html";
        String encoding = "utf-8";

        setWebView(webView, codeTheme, html, mime, encoding);

        displayHome();
    }

    boolean changeTheme() {
        SharedPreferences preferences = getPreferences();
        int appTheme = Integer.parseInt(preferences.getString("APP_THEME", "1"));
        if (appTheme == 1)
            setTheme(R.style.WhiteTheme);
        else if (appTheme == 2)
            setTheme(R.style.BlackTheme);
        if (appTheme != currentTheme) {
            currentTheme = appTheme;
            return true;
        } else {
            return false;
        }
    }

    boolean isChangedCodeTheme(SharedPreferences preferences) {
        String codeTheme = getCodeTheme(preferences);
        return !codeTheme.equals(currentCodeTheme);
    }

    SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }

    String getFileName() {
        Bundle extras = getIntent().getExtras();
        return extras.getString("FILE_NAME");
    }

    String getConvertedFileName() {
        Bundle extras = getIntent().getExtras();
        return extras.getString("CONVERTED_FILE_NAME");
    }

    String getCodeTheme(SharedPreferences preferences) {
        return preferences.getString("CODE_THEME", "default");
    }

    String getLineNumbersProperty(SharedPreferences preferences) {
        String lineNumbers = "";
        if (preferences.getBoolean("LINE_NUMBERS_ENABLED", true))
            lineNumbers = "line-numbers";
        return lineNumbers;
    }

    String readSourceCode(String fileName) {
        try {
            InputStream ins = getResources().openRawResource(
                    getResources().getIdentifier(fileName,
                            "raw", getPackageName()));

            byte[] b = new byte[ins.available()];
            ins.read(b);
            String code = new String(b);
            code = code.replace("<", "&lt;").replace(">", "&gt;");
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    String generateHtml(String codeTheme, String lineNumbersProperty, String sourceCode) {
        //return MessageFormat.format("<link href=\"styles/fonts.css\" rel=\"stylesheet\"/><link href=\"styles/{0}.css\" rel=\"stylesheet\"/><script src=\"styles/style.js\"></script> <html><body><table style = \"padding: 0px; margin: 0px;border: none;border-collapse: collapse;\"><tr><td> <pre><code class=\"language-java {1}\" >{2}</td>\n</tr>\n</table></code></pre>  </body></html>", codeTheme, lineNumbersProperty, sourceCode);
        return "<link rel=\"stylesheet\" href=\"styles-hl/vs.css\">\n" +
                "<script src=\"highlight.pack.js\"></script>\n" +
                "<script src=\"highlightjs-line-numbers.js\"></script>\n" +
                "<script>hljs.initHighlightingOnLoad();</script>\n" +
                "<script>hljs.initLineNumbersOnLoad();</script>\n" +
                "<link rel=\"stylesheet\" href=\"lines.css\">\n" +
                "<html><body><table><tr><td>  <pre><code class=\"language-java\" >" + sourceCode + "</td>\n</tr>\n</table></code></pre>  </body></html>";
    }

    @SuppressLint("SetJavaScriptEnabled")
    void setWebView(WebView webView, String codeTheme, String html, String mime, String encoding) {
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.parseColor(getBackgroundColor(codeTheme)));
        webView.loadDataWithBaseURL("file:///android_asset/", html, mime, encoding, null);

        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setDisplayZoomControls(false);
    }

    void displayHome() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    String getBackgroundColor(String codeTheme) {
        switch (codeTheme) {
            // Official themes
            case "default":
                return "#f5f2f0";
            case "dark":
                return "#4d4033";
            case "okaidia":
                return "#272822";
            case "twilight":
                return "#141414";
            case "solarized":
                return "#fdf6e3";
            // Unofficial themes
            case "atomdark":
                return "#1d1f21";
            case "sulphurpool":
                return "#f5f7ff";
            case "visual":
                return "#ffffff";
            case "xonokai":
                return "#2a2a2a";
            case "hopscotch":
                return "#322931";
            case "ghcolors":
                return "#ffffff";
            case "pojoaque":
                return "#181914";
            // Unknown theme
            default:
                return "#ffffff";
        }
    }

    @Override
    public void onResume() {
        if (changeTheme())
            recreate();
        if (isChangedCodeTheme(getPreferences()))
            recreate();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_preferences:
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_code, menu);
        return true;
    }
}
