# MarkdownView
[![](https://jitpack.io/v/GrenderG/MarkdownView.svg)](https://jitpack.io/#GrenderG/MarkdownView) [![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.me/grenderg)

WebView implementation supporting Markdown rendering.

<div align="center">
	<img src="https://raw.githubusercontent.com/GrenderG/MarkdownView/master/art/demo.gif">
</div>

## Prerequisites

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

## Dependency

Add this to your module's `build.gradle` file (make sure the version matches the JitPack badge above):

```gradle
dependencies {
	...
	implementation 'com.github.GrenderG:MarkdownView:0.1.2'
}
```

## Basic usage

**NOTE:** You will need to specify INTERNET permission in your project if you want to load Internet resources.


First of all, all the View where you want:
```xml
<es.dmoral.markdownview.MarkdownView
        android:id="@+id/markdown_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

Loading Markdown text:
```java
markdownView.loadFromText("### Loading some Markdown!\nHey there.");
```

Loading Markdown from a File:
```java
markdownView.loadFromFile(new File("path/to/md/file"));
```

Loading Markdown from Android assets:
```java
markdownView.loadFromAssets("path/to/file/in/assets");
```

Loading Markdown from URL:
```java
markdownView.loadFromURL("https://raw.githubusercontent.com/GrenderG/MarkdownView/master/README.md");
```

## Advanced usage

MarkdownView uses `css` files to stylize everything, you can customize them too:
```java
markdownView.setCurrentConfig(new Config(
	"file:///android_asset/custom_css_file.css",
	Config.CssCodeHighlight.MONOKAI_SUBLIME // This can be a custom one too, but there's already added some options.
));
```

Internally, MarkdownView uses an `OkHttpClient` to load files from URLs, you can set a custom one if you want:
```java
Config defaultConfig = Config.getDefaultConfig();
        
defaultConfig.setDefaultOkHttpClient(new OkHttpClient().newBuilder().addInterceptor(
        new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request authenticatedRequest = request.newBuilder()
                        .header("Authorization", "Basic OIxhJGHpbjpvcGVuc2VzYW1l").build();
                return chain.proceed(authenticatedRequest);
            }
        }
).build());

markdownView.setCurrentConfig(defaultConfig);
```

You can also set the margins of the content (in px):
```java
Config defaultConfig = Config.getDefaultConfig();
        
defaultConfig.setDefaultMargin(16);

markdownView.setCurrentConfig(defaultConfig);
```

There's also a rendering listener which will provide you info if there's an error rendering the Markdown and when it has finished rendering (near perfect timing).
```java
markdownView.setOnMarkdownRenderingListener(new MarkdownView.OnMarkdownRenderingListener() {
        @Override
        public void onMarkdownFinishedRendering() {
            // Rendered!
        }

        @Override
        public void onMarkdownRenderError() {
	    // Error rendering
        }
    });
```

## Acknowledgements

This library is **heavily** influenced by [MarkedView-for-Android
](https://github.com/mittsuu/MarkedView-for-Android) from [@mittsuu](https://github.com/mittsuu).

- [Marked](https://github.com/markedjs/marked)
- [highlight.js](https://highlightjs.org/)
- [jQuery](https://jquery.com/)
- [Bootstrap (css style)](http://getbootstrap.com/)
