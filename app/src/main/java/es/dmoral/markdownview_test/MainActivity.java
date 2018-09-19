package es.dmoral.markdownview_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;

import es.dmoral.markdownview.Config;
import es.dmoral.markdownview.MarkdownView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MarkdownView markdownView = findViewById(R.id.markdown_view);
        markdownView.setOnMarkdownRenderingListener(new MarkdownView.OnMarkdownRenderingListener() {
            @Override
            public void onMarkdownFinishedRendering() {
                Toast.makeText(MainActivity.this, "Rendered!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkdownRenderError() {

            }
        });
        markdownView.loadFromText("An h1 header\n" +
                "============\n" +
                "\n" +
                "Paragraphs are separated by a blank line.\n" +
                "\n" +
                "2nd paragraph. *Italic*, **bold**, and `monospace`. Itemized lists\n" +
                "look like:\n" +
                "\n" +
                "  * this one\n" +
                "  * that one\n" +
                "  * the other one\n" +
                "\n" +
                "Note that --- not considering the asterisk --- the actual text\n" +
                "content starts at 4-columns in.\n" +
                "\n" +
                "> Block quotes are\n" +
                "> written like so.\n" +
                ">\n" +
                "> They can span multiple paragraphs,\n" +
                "> if you like.\n" +
                "\n" +
                "Use 3 dashes for an em-dash. Use 2 dashes for ranges (ex., \"it's all\n" +
                "in chapters 12--14\"). Three dots ... will be converted to an ellipsis.\n" +
                "Unicode is supported. ☺\n" +
                "\n" +
                "\n" +
                "\n" +
                "An h2 header\n" +
                "------------\n" +
                "\n" +
                "Here's a numbered list:\n" +
                "\n" +
                " 1. first item\n" +
                " 2. second item\n" +
                " 3. third item\n" +
                "\n" +
                "Note again how the actual text starts at 4 columns in (4 characters\n" +
                "from the left side). Here's a code sample:\n" +
                "\n" +
                "    # Let me re-iterate ...\n" +
                "    for i in 1 .. 10 { do-something(i) }\n" +
                "\n" +
                "As you probably guessed, indented 4 spaces. By the way, instead of\n" +
                "indenting the block, you can use delimited blocks, if you like:\n" +
                "\n" +
                "~~~\n" +
                "define foobar() {\n" +
                "    print \"Welcome to flavor country!\";\n" +
                "}\n" +
                "~~~\n" +
                "\n" +
                "(which makes copying & pasting easier). You can optionally mark the\n" +
                "delimited block for Pandoc to syntax highlight it:\n" +
                "\n" +
                "~~~python\n" +
                "import time\n" +
                "# Quick, count to ten!\n" +
                "for i in range(10):\n" +
                "    # (but not *too* quick)\n" +
                "    time.sleep(0.5)\n" +
                "    print i\n" +
                "~~~\n" +
                "\n" +
                "\n" +
                "\n" +
                "### An h3 header ###\n" +
                "\n" +
                "Now a nested list:\n" +
                "\n" +
                " 1. First, get these ingredients:\n" +
                "\n" +
                "      * carrots\n" +
                "      * celery\n" +
                "      * lentils\n" +
                "\n" +
                " 2. Boil some water.\n" +
                "\n" +
                " 3. Dump everything in the pot and follow\n" +
                "    this algorithm:\n" +
                "\n" +
                "        find wooden spoon\n" +
                "        uncover pot\n" +
                "        stir\n" +
                "        cover pot\n" +
                "        balance wooden spoon precariously on pot handle\n" +
                "        wait 10 minutes\n" +
                "        goto first step (or shut off burner when done)\n" +
                "\n" +
                "    Do not bump wooden spoon or it will fall.\n" +
                "\n" +
                "Notice again how text always lines up on 4-space indents (including\n" +
                "that last line which continues item 3 above).\n" +
                "\n" +
                "Here's a link to [a website](http://foo.bar), to a [local\n" +
                "doc](local-doc.html), and to a [section heading in the current\n" +
                "doc](#an-h2-header). Here's a footnote [^1].\n" +
                "\n" +
                "[^1]: Footnote text goes here.\n" +
                "\n" +
                "Tables can look like this:\n" +
                "\n" +
                "size  material      color\n" +
                "----  ------------  ------------\n" +
                "9     leather       brown\n" +
                "10    hemp canvas   natural\n" +
                "11    glass         transparent\n" +
                "\n" +
                "Table: Shoes, their sizes, and what they're made of\n" +
                "\n" +
                "(The above is the caption for the table.) Pandoc also supports\n" +
                "multi-line tables:\n" +
                "\n" +
                "--------  -----------------------\n" +
                "keyword   text\n" +
                "--------  -----------------------\n" +
                "red       Sunsets, apples, and\n" +
                "          other red or reddish\n" +
                "          things.\n" +
                "\n" +
                "green     Leaves, grass, frogs\n" +
                "          and other things it's\n" +
                "          not easy being.\n" +
                "--------  -----------------------\n" +
                "\n" +
                "A horizontal rule follows.\n" +
                "\n" +
                "***\n" +
                "\n" +
                "Here's a definition list:\n" +
                "\n" +
                "apples\n" +
                "  : Good for making applesauce.\n" +
                "oranges\n" +
                "  : Citrus!\n" +
                "tomatoes\n" +
                "  : There's no \"e\" in tomatoe.\n" +
                "\n" +
                "Again, text is indented 4 spaces. (Put a blank line between each\n" +
                "term/definition pair to spread things out more.)\n" +
                "\n" +
                "Here's a \"line block\":\n" +
                "\n" +
                "| Line one\n" +
                "|   Line too\n" +
                "| Line tree\n" +
                "\n" +
                "and images can be specified like so:\n" +
                "\n" +
                "![](https://www.gannett-cdn.com/-mm-/89934f7b13e7717eb560f3babda84f20895abcd0/c=83-0-724-482/local/-/media/2018/07/17/DetroitFreeP/DetroitFreePress/636674313628993565-GettyImages-684133728.jpg?width=534&height=401&fit=crop)\n" +
                "\n" +
                "And note that you can backslash-escape any punctuation characters\n" +
                "which you wish to be displayed literally, ex.: \\`foo\\`, \\*bar\\*, etc.");
    }
}
