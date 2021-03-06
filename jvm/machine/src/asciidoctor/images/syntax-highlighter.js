/**
 * mmlang Syntax Highlighter that tweaks the CodeRay Python highlighter at HTML load time.
 * Currently uses JQuery to alter the HTML DOM accordingly.
 * This file is loaded from docinfo.html <script src="syntax-highlighter.js"/>
 **/
$(document).ready(function(){
  var token = "XXX"
  var ospan = "<span style='color:"
  var cspan = "'>" + token + "</span>"
  var bcolor = "background-color:hsla(0,100%,100%,0.05)"
  var bold = ";font-weight:bold"
  // BEGIN: general random tweaks to convert python highlighting to mmlang
  $("code[data-lang='python']").attr("data-lang", "mmlang")
  $("span[style='color:#010']").attr("style","color:#710")
  $("span[style='background-color:hsla(0,100%,50%,0.05)']").attr("style",bcolor)
  $("span[style='color:#F00;background-color:#FAA']").attr("style","color:#710;" + bcolor)
  // str dark red
  $("code:contains(\"'\")").html(function(_, html) {
    return html.replace(new RegExp("'(.*?)'","gi"),"<span style='color:#D20'>'$1'</span>");
  });
  // comment light gray
  $("code:contains(\"//\")").html(function(_, html) {
    return html.replace(new RegExp("//(.*)","gi"),"<span style='color:#777'>//$1</span>");
  });
  // END: general random tweaks to convert python highlighting to mmlang
  var rewrites = {"special": ["",       ospan + "#710" + cspan],            // dark red
                  // "str":     ["'",  "\'"+ospan + "#D20" + cspan],            // dark red
                  "op":      ["[",  "["+ospan + "#080" +bold + cspan],      // bold green
                  "tokens":  ["",       ospan + "#000" +bold + cspan],      // bold green
                  "infix":   ["",       ospan + "#080" +bold + cspan],      // bold green
                  "value":   ["",       ospan + "#00D" + cspan],            // light blue
                  "type":    ["",       ospan + "#369" + bold + cspan],     // dark blue

                  "plain":   ["",       ospan + "#000" + cspan]};           // normal black text
  var items =    {"op":      ["state","range","domain",
                              "and","or","combine","branch","neg","get","gte","lte","gt","lt","mult","plus","start",
                              "one","zero","noop","eq","is","id","db","explain","count","fold","map","juxt","barrier",
                              "split","merge","last","head","tail","put","repeat","type",
                              "define","to","from","sum","model","as","a","q","path","empty","lift","swap"],
                  "type":    ["_","real","rec","lst","inst","obj","int","str","bool","poly",
                              "nat","person","vertex","edge","graph"],  // CUSTOM TO DOCS (NOT GENERAL TO mm-ADT)
                  "special": ["mmlang","language error","mm","pg",
                              "social"],        // CUSTOM TO DOCS (NOT GENERAL TO mm-ADT)
                  "tokens":  ["{","}","(",")"],
                  "infix":   ["|","&&",",","+","*","=>","^","π"],
                  "value":   ["true","false"],
                  "plain":   ["is not"]};
  $.each(items, function(key, values) {
    jQuery.each(values, function(index,element) {
      var fragment = $.escapeSelector(rewrites[key][0]+element);
      $("code:contains('"+fragment+"')").html(function(_, html) {
        return html.replace(new RegExp(fragment,"gi"), rewrites[key][1].replace(new RegExp(token,"gi"),element));
      });
     });
   })
   //////////////////
   $("a[href^='https://en.wikipedia.org/wiki']").miniPreview({ prefetch: 'parenthover',width:800,height:400,scale:0.9}); // WIKIPEDIA PAGE PREVIEW
   $("iframe[class='mini-preview-frame']").attr( "style", function( i, value ) { return value + ";margin-top:-75px;margin-left:-160px;width:136%;height:133%;"; });
   $("div[class='mini-preview-wrapper']").attr("style","width: 800px;height:400px;top:22.1px;left:-100px")
});