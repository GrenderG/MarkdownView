$(function() {

    var rend = new marked.Renderer();

    marked.setOptions({
        langPrefix: '',
        tables: true,
        highlight: function(code) {
            return hljs.highlightAuto(code).value;
        }
    });

    rend.code = function(code, lang, escaped) {
        var lineArray = code.split(/\r\n|\r|\n/);
        var len = 0;
        if (lineArray == null) {
            len = code.length;

        } else {
            $.each(lineArray, function(index, val) {
                if (len < val.length) {
                    len = val.length;
                }
            });
        }

        var code = code.replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');

        if (!lang) {
            return '<pre><code style="' +
                ' display: block; word-wrap: normal; overflow-x: scroll;' +
                ' width: ' + len + 'rem; ' +
                ' -webkit-text-size-adjust: none;' +
                '">' +
                code +
                '\n</code></pre>';
        }

        return '<pre><code class="' +
            lang +
            '" style="' +
            ' display: block; word-wrap: normal; overflow-x: scroll;' +
            ' width: ' + len + 'rem; ' +
            ' -webkit-text-size-adjust: none;' +
            '">' +
            code +
            '\n</code></pre>';
    };

    function escSub(text) {
        var result = text.match(/~+.*?~+/g);
        if (result == null) {
            return text;
        }

        $.each(result, function(index, val) {
            if (val.lastIndexOf('~~', 0) === 0) {
                return true;
            }
            var escapedText = val.replace(/~/, '<sub>');
            escapedText = escapedText.replace(/~/, '</sub>');
            var reg = new RegExp(val, 'g');
            text = text.replace(reg, escapedText);
        });

        return text;
    }

    function escSup(text) {
        var result = text.match(/\^.*?\^/g);
        if (result == null) {
            return text;
        }

        $.each(result, function(index, val) {
            var escapedText = val.replace(/\^/, '<sup>');
            escapedText = escapedText.replace(/\^/, '</sup>');
            val = val.replace(/\^/g, '\\^');
            var reg = new RegExp(val, 'g');
            text = text.replace(reg, escapedText);
        });

        return text;
    }

    render = function setMarkdown(md_text, code_scroll_enabled, base_css, code_highlight_css, margin) {
        if (md_text == "") {
            return false;
        }

        md_text = md_text.replace(/\\n/g, "\n");
        md_text = escSub(md_text);
        md_text = escSup(md_text);

        // markdown html
        var md_html;
        if (code_scroll_enabled) {
            md_html = marked(md_text, {
                renderer: rend
            });
        } else {
            md_html = marked(md_text);
        }

        $('link[href*="_dummy_css_1_"]').replaceWith('<link href="' + base_css + '" type="text/css" rel="stylesheet">');
        $('link[href*="_dummy_css_2_"]').replaceWith('<link href="' + code_highlight_css + '" type="text/css" rel="stylesheet">');
        $('body').css("margin", margin)

        $('#container_body').html(md_html);

        $('pre code').each(function(i, block) {
            hljs.highlightBlock(block);
        });

        return true;
    };
});