package es.dmoral.markdownview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MarkdownView extends WebView {
    private static final String TAG = MarkdownView.class.getSimpleName();
    private static final String HTML_CONTAINER_PATH = "file:///android_asset/html/container.html";
    private static final String IMAGE_PATTERN = "!\\[(.*)\\]\\((.*)\\)";

    private Config currentConfig;
    private String markdownText;
    private boolean openUrlInBrowser = true;
    private boolean codeScrollEnabled = true;

    private OnMarkdownRenderingListener onMarkdownRenderingListener;

    public MarkdownView(Context context) {
        super(context);
        init();
    }

    public MarkdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        currentConfig = Config.getDefaultConfig();

        getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals(HTML_CONTAINER_PATH)) {
                    renderMarkdown();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isOpenUrlInBrowser()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    public void setCurrentConfig(Config currentConfig) {
        this.currentConfig = currentConfig;
    }

    public void loadFromText(String markdownText) {
        this.markdownText = markdownText;
        post(new Runnable() {
            @Override
            public void run() {
                startRendering();
            }
        });
    }

    public void loadFromUrl(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        currentConfig.getDefaultOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    loadFromText(response.body().string());
                } catch (IOException | NullPointerException e) {
                    if (onMarkdownRenderingListener != null) {
                        onMarkdownRenderingListener.onMarkdownRenderError();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (onMarkdownRenderingListener != null) {
                    onMarkdownRenderingListener.onMarkdownRenderError();
                }
            }
        });
    }

    public void loadFromFile(File markdownFile) {
        String markdownText = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(markdownFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readText;
            StringBuilder stringBuilder = new StringBuilder();
            while ((readText = bufferedReader.readLine()) != null) {
                stringBuilder.append(readText);
                stringBuilder.append("\n");
            }
            fileInputStream.close();
            markdownText = stringBuilder.toString();
        } catch (IOException e) {
            if (onMarkdownRenderingListener != null) {
                onMarkdownRenderingListener.onMarkdownRenderError();
            }
        }
        loadFromText(markdownText);
    }

    public void loadFromAssets(String markdownAssetFilePath) {
        BufferedReader bufferedReader = null;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = getContext().getAssets().open(markdownAssetFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str).append("\n");
            }
            bufferedReader.close();
            loadFromText(stringBuilder.toString());
        } catch (IOException e) {
            if (onMarkdownRenderingListener != null) {
                onMarkdownRenderingListener.onMarkdownRenderError();
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    if (onMarkdownRenderingListener != null) {
                        onMarkdownRenderingListener.onMarkdownRenderError();
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void renderMarkdown() {
        String cleanMarkdownText = imgToBase64(markdownText);
        cleanMarkdownText = escapeText(cleanMarkdownText);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            loadUrl(String.format("javascript:render('%s', %b, '%s', '%s', %d)", cleanMarkdownText, isCodeScrollEnabled(),
                    currentConfig.getCssMarkdown(),
                    currentConfig.getCssCodeHighlight(),
                    currentConfig.getDefaultMargin()));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setVisibility(View.VISIBLE);
                            onMarkdownRenderingListener.onMarkdownFinishedRendering();
                        }
                    });
                }
            }, calculateRenderDelay());
        } else {
            evaluateJavascript(String.format("render('%s', %b, '%s', '%s', %d)", cleanMarkdownText, isCodeScrollEnabled(),
                    currentConfig.getCssMarkdown(), currentConfig.getCssCodeHighlight(), currentConfig.getDefaultMargin()),
                    new ValueCallback<String>() {

                @Override
                public void onReceiveValue(final String string) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    if (Boolean.parseBoolean(string)) {
                                        setVisibility(View.VISIBLE);
                                        if (onMarkdownRenderingListener != null) {
                                            onMarkdownRenderingListener.onMarkdownFinishedRendering();
                                        }
                                    } else if (onMarkdownRenderingListener != null) {
                                        onMarkdownRenderingListener.onMarkdownRenderError();
                                    }
                                }
                            });
                        }
                    }, calculateRenderDelay());
                }
            });
        }
    }

    // This calculates a needed delay in order to fire the render callback correctly.
    private long calculateRenderDelay() {
        return markdownText.length() / 10;
    }

    private void startRendering() {
        setVisibility(View.GONE);
        loadUrl(HTML_CONTAINER_PATH);
    }

    private String escapeText(String textToEscape) {
        String escText = textToEscape.replace("\n", "\\\\n");
        escText = escText.replace("'", "\\\'");
        escText = escText.replace("\r", "");
        return escText;
    }

    private String imgToBase64(String markdownText) {
        Matcher matcher = Pattern.compile(IMAGE_PATTERN).matcher(markdownText);
        if (!matcher.find()) {
            return markdownText;
        }

        String imgPath = matcher.group(2);
        if (isUrlPrefix(imgPath) || !isPathExtensionCheck(imgPath)) {
            return markdownText;
        }

        String baseType = imgExtensionToBaseType(imgPath);
        if (baseType.isEmpty()) {
            return markdownText;
        }

        File file = new File(imgPath);
        byte[] bytes = new byte[(int) file.length()];
        BufferedInputStream bufferedInputStream = null;

        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedInputStream.read(bytes, 0, bytes.length);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException:" + e);
        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e);
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException:" + e);
                }
            }
        }

        String base64Img = baseType + Base64.encodeToString(bytes, Base64.NO_WRAP);
        return markdownText.replace(imgPath, base64Img);
    }

    private boolean isUrlPrefix(String text) {
        return text.startsWith("http://") || text.startsWith("https://");
    }

    private boolean isPathExtensionCheck(String text) {
        return text.endsWith(".png")
                || text.endsWith(".jpg")
                || text.endsWith(".jpeg")
                || text.endsWith(".gif");
    }

    private String imgExtensionToBaseType(String text) {
        if (text.endsWith(".png")) {
            return "data:image/png;base64,";
        } else if (text.endsWith(".jpg") || text.endsWith(".jpeg")) {
            return "data:image/jpg;base64,";
        } else if (text.endsWith(".gif")) {
            return "data:image/gif;base64,";
        } else {
            return "";
        }
    }

    public boolean isCodeScrollEnabled() {
        return codeScrollEnabled;
    }

    public void setCodeScrollEnabled(boolean codeScrollEnabled) {
        this.codeScrollEnabled = codeScrollEnabled;
    }

    @Override
    protected void onDetachedFromWindow() {
        onMarkdownRenderingListener = null;
        super.onDetachedFromWindow();
    }

    public void setOnMarkdownRenderingListener(OnMarkdownRenderingListener onMarkdownRenderingListener) {
        this.onMarkdownRenderingListener = onMarkdownRenderingListener;
    }

    public boolean isOpenUrlInBrowser() {
        return openUrlInBrowser;
    }

    public void setOpenUrlInBrowser(boolean openUrlInBrowser) {
        this.openUrlInBrowser = openUrlInBrowser;
    }

    public interface OnMarkdownRenderingListener {
        void onMarkdownFinishedRendering();

        void onMarkdownRenderError();
    }
}
