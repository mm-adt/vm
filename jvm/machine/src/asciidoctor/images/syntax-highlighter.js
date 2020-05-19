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
  // END: general random tweaks to convert python highlighting to mmlang
  var rewrites = {"op":      ["[",  "["+ospan + "#080" +bold + cspan],      // bold green
                  "infix":   ["",       ospan + "#080" +bold + cspan],      // bold green
                  "vars":    ["[",  "["+ospan + "#080" +bold + cspan],      // bold green
                  "value":   ["",       ospan + "#00D" + cspan],            // light blue
                  "type":    ["",       ospan + "#369" + bold + cspan],     // dark blue
                  "special": ["",       ospan + "#710" + cspan],            // dark red
                  "plain":   ["",       ospan + "#000" + cspan]};           // normal black text
  var items =    {"op":      ["a","branch","neg","get","gte","lte","gt","lt","mult","plus","start",
                              "one","zero","noop","eq","is","id","db","explain","count","fold","map",
                              "split","merge","head","tail","div","minus"],
                  "infix":   ["|"],
                  "vars":    ["a","b","c"],
                  "value":   ["true","false"],
                  "type":    ["_","real","rec","lst","poly","inst","obj","int","str","bool","range","domain"],
                  "special": ["mmlang","q"],
                  "plain":   ["is not"]};
  $.each(items, function(key, values) {
    jQuery.each(values, function(index,element) {
      var fragment = $.escapeSelector(rewrites[key][0]+element);
      $("code:contains('"+fragment+"')").html(function(_, html) {
        return html.replace(new RegExp(fragment,"gi"), rewrites[key][1].replace(new RegExp(token,"gi"),element));
      });
     });
   })
});