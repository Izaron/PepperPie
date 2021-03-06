package com.izaron.pepperpied;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CodeActivity extends AppCompatActivity {

    private int currentTheme;
    private String currentCodeTheme;
    private String currentLineNumbersProperty;
    private String currentFont;

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
        currentLineNumbersProperty = lineNumbersProperty;
        String sourceCode = readSourceCode(fileName);
        String font = getFont(preferences);
        currentFont = font;

        String html = generateHtml(codeTheme, lineNumbersProperty, font, sourceCode);

        String mime = "text/html";
        String encoding = "utf-8";

        setWebView(webView, codeTheme, html, mime, encoding);

        displayHome();
    }

    boolean changeTheme() {
        SharedPreferences preferences = getPreferences();
        int appTheme = Integer.parseInt(preferences.getString("APP_THEME", "0"));
        if (appTheme == 0)
            setTheme(R.style.AppTheme);
        else if (appTheme == 1)
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

    boolean isChangedLineNumbersProperty(SharedPreferences preferences) {
        String lineNumbersProperty = getLineNumbersProperty(preferences);
        return !lineNumbersProperty.equals(currentLineNumbersProperty);
    }

    boolean isChangedFont(SharedPreferences preferences) {
        String font = getFont(preferences);
        return !font.equals(currentFont);
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

    String getFont(SharedPreferences preferences) {
        return preferences.getString("CODE_FONT", "Inconsolata");
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

    String generateHtml(String codeTheme, String lineNumbersProperty, String font, String sourceCode) {
        //return MessageFormat.format("<link href=\"styles/fonts.css\" rel=\"stylesheet\"/><link href=\"styles/{0}.css\" rel=\"stylesheet\"/><script src=\"styles/style.js\"></script> <html><body><table style = \"padding: 0px; margin: 0px;border: none;border-collapse: collapse;\"><tr><td> <pre><code class=\"language-java {1}\" >{2}</td>\n</tr>\n</table></code></pre>  </body></html>", codeTheme, lineNumbersProperty, sourceCode);
        if (!lineNumbersProperty.isEmpty()) {
            return "<link rel=\"stylesheet\" href=\"styles-hl/" + codeTheme + ".css\">\n" +
                    "<script src=\"highlight.pack.js\"></script>\n" +
                    "<script src=\"highlightjs-line-numbers.js\"></script>\n" +
                    "<script>hljs.initHighlightingOnLoad();</script>\n" +
                    "<script>hljs.initLineNumbersOnLoad();</script>\n" +
                    "<link rel=\"stylesheet\" href=\"lines.css\">\n" +
                    "<style type=\"text/css\">.hljs {\n" +
                    "    font-family: \"" + font + "\";\n" +
                    "}\n" +
                    "</style>\n" +
                    "<html><body>  <pre><code class=\"java\" >" + sourceCode + "</code></pre>  </body></html>";
        } else {
            return "<link rel=\"stylesheet\" href=\"styles-hl/" + codeTheme + ".css\">\n" +
                    "<script src=\"highlight.pack.js\"></script>\n" +
                    "<script>hljs.initHighlightingOnLoad();</script>\n" +
                    "<link rel=\"stylesheet\" href=\"lines.css\">\n" +
                    "<style type=\"text/css\">.hljs {\n" +
                    "    padding-left: 0.5em;\n" +
                    "    font-family: \"" + font + "\";\n" +
                    "}\n" +
                    "</style>\n" +
                    "<html><body>  <pre><code class=\"java\" >" + sourceCode + "</code></pre>  </body></html>";
        }
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
        String back = null;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("styles-hl/" + codeTheme + ".css"), "UTF-8"));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (mLine.contains("background:") && back == null) {
                    if (mLine.contains("#")) {
                        String str = mLine.substring(mLine.indexOf('#'));
                        while (str.charAt(0) == ' ')
                            str = str.substring(1);
                        if (str.contains(" "))
                            str = str.substring(0, str.indexOf(' '));
                        while (str.charAt(str.length() - 1) == ';')
                            str = str.substring(0, str.length() - 1);
                        back = str;
                    } else {
                        String str = mLine.substring(mLine.indexOf("background:") + "background:".length());
                        while (str.charAt(0) == ' ')
                            str = str.substring(1);
                        if (str.contains(" "))
                            str = str.substring(0, str.indexOf(' '));
                        while (str.charAt(str.length() - 1) == ';')
                            str = str.substring(0, str.length() - 1);
                        back = str;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (back != null && back.startsWith("#") && back.length() - 1 == 3)
            back += back.substring(1);

        if (back == null)
            back = "#ffffff";

        return back;
    }

    @Override
    public void onResume() {
        if (changeTheme())
            recreate();
        if (isChangedCodeTheme(getPreferences()))
            recreate();
        if (isChangedLineNumbersProperty(getPreferences()))
            recreate();
        if (isChangedFont(getPreferences()))
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
