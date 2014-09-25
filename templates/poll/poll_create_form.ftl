<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "style.ftl">
<title>Poll - New Poll</title>
<#include "jquery.ftl">
<script>
var options = 0;
function addOption() {
        var option = document.createElement("input");
        var type = document.createAttribute("type");
        var name = document.createAttribute("name");
        var id = document.createAttribute("id");
        type.nodeValue = "text";
        name.nodeValue = "option_"+options;
        id.nodeValue = name.nodeValue;
        option.setAttributeNode(type);
        option.setAttributeNode(name);
        var label = document.createElement("label");
        var forL = document.createAttribute("for");
        forL.nodeValue = id.nodeValue;
        var text = document.createTextNode("Option "+(options+1)+":");
        label.setAttributeNode(forL)
        label.appendChild(text)
        $("#options").append(label);
        $("#options").append(option);
        $("#options").append("</br>");
        options++;
}
$(function() {
    $("#more").click(function() {
        addOption();
        addOption();
        addOption();
        addOption();
        addOption();
    });
});
</script>
</head>
<#include "bodystart.ftl">
<#include "/menu.ftl">
<form class="pure-form pure-form-aligned" method="post" action="${action}" enctype="multipart/form-data">
<div class="pure-control-group">
<label for="question">Question:</label><input type="text" name="question" id="question" placeholder="What is your question?" required />
</div>
<div class="pure-control-group">
<label class="css-label lite-green-check" for="enableipfilter">Enable IP-Filter</label><input class="css-checkbox" type="checkbox" id="enableipfilter" name="enableipfilter" checked="checked"/>
</div>
<div class="pure-control-group">
<label for="ipfilter" title="Java Regular Expression, def. matches all ips">IP-Filter:</label><input type="text" name="ipfilter" id="ipfilter" value="\d+\.\d+\.\d+\.\d+" title="only matching ips are allowed to vote" required/>
</div>
<div class="pure-control-group">
<label class="css-label lite-green-check" for="onevoteperip">One Vote per IP</label><input type="checkbox" id="onevoteperip" name="onevoteperip" checked="checked"/>
</div>
<div class="pure-control-group">
<label for="votes" title="How many different options one voter is allowed to choose.">Votes per Person:</label><input type="number" id="votes" name="votes" value="1" required/>
</div>
<div class="pure-control-group" id="options">

</div>
<div class="pure-controls">
<button class="pure-button pure-button-primary" type="submit" name="submit">Submit</button>
<button class="pure-button" type="button" id="more" value="Add More" >More Options</button>
<button class="pure-button" type="reset" name="reset">Reset</button>
</div>
</form>

</body>
</html>
