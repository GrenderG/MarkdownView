package es.dmoral.markdownview;

import okhttp3.OkHttpClient;

public class Config {
    private String cssMarkdown;
    private String cssCodeHighlight;
    private int defaultMargin;
    private OkHttpClient defaultOkHttpClient;

    public Config() {
        // empty constructor
    }

    public Config(String cssMarkdown, String cssCodeHighlight, OkHttpClient okHttpClient, int defaultMargin) {
        this.cssMarkdown = cssMarkdown;
        this.cssCodeHighlight = cssCodeHighlight;
        this.defaultOkHttpClient = okHttpClient;
        this.defaultMargin = defaultMargin;
    }

    public Config(String cssMarkdown, String cssCodeHighlight, OkHttpClient okHttpClient) {
        this.cssMarkdown = cssMarkdown;
        this.cssCodeHighlight = cssCodeHighlight;
        this.defaultOkHttpClient = okHttpClient;
        this.defaultMargin = 0;
    }

    public Config(String cssMarkdown, String cssCodeHighlight) {
        this(cssMarkdown, cssCodeHighlight, new OkHttpClient(), 0);
    }

    public static Config getDefaultConfig() {
        return new Config(
                CssMarkdown.BOOTSTRAP,
                CssCodeHighlight.GITHUB
        );
    }

    public String getCssMarkdown() {
        return cssMarkdown;
    }

    public void setCssMarkdown(String cssMarkdown) {
        this.cssMarkdown = cssMarkdown;
    }

    public String getCssCodeHighlight() {
        return cssCodeHighlight;
    }

    public void setCssCodeHighlight(String cssCodeHighlight) {
        this.cssCodeHighlight = cssCodeHighlight;
    }

    public OkHttpClient getDefaultOkHttpClient() {
        return defaultOkHttpClient;
    }

    public void setDefaultOkHttpClient(OkHttpClient defaultOkHttpClient) {
        this.defaultOkHttpClient = defaultOkHttpClient;
    }

    public int getDefaultMargin() {
        return defaultMargin;
    }

    public void setDefaultMargin(int defaultMargin) {
        this.defaultMargin = defaultMargin;
    }

    public static class CssMarkdown {
        public static final String BOOTSTRAP = "css/bootstrap.css";
    }

    public static class CssCodeHighlight {
        public static final String DARCULA = "css/highlight/darcula.css";
        public static final String GITHUB = "css/highlight/github.css";
        public static final String MONOKAI_SUBLIME = "css/highlight/monokai-sublime.css";
        public static final String SOLARIZED_DARK = "css/highlight/solarized-dark.css";
        public static final String SOLARIZED_LIGHT = "css/highlight/solarized-light.css";
    }
}
