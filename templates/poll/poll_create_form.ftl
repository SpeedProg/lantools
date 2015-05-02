<#include "/layout.ftl">

<#macro body_end>
<script>
var options = 0;
function addOption() {
		var div = document.createElement("div");
		div.setAttribute("class", "form-group");
        var option = document.createElement("input");
        option.setAttribute("type", "text");
        option.setAttribute("name", "option_"+options);
        option.setAttribute("id", "option_"+options);
        option.setAttribute("class", "form-control");
        var label = document.createElement("label");
        label.setAttribute("for", "option_"+options);
        var text = document.createTextNode("Option "+(options+1)+":");
        label.appendChild(text);

        div.appendChild(label);
        div.appendChild(option);
        
        $("#options").append(div);
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
addOption();
addOption();
addOption();
addOption();
addOption();
</script>
</#macro>

<#macro body_main>
<form method="post" action="${action}" enctype="multipart/form-data">
<div class="form-group">
<label for="question">Question:</label><input class="form-control" type="text" name="question" id="question" placeholder="What is your question?" required />
</div>
<div class="checkbox">
<label for="enableipfilter"><input type="checkbox" id="enableipfilter" name="enableipfilter" checked="checked"/>Enable IP-Filter</label>
</div>
<div class="form-group">
<label for="ipfilter" title="Java Regular Expression, def. matches all ips">IP-Filter:</label><input class="form-control" type="text" name="ipfilter" id="ipfilter" value="\d+\.\d+\.\d+\.\d+" title="only matching ips are allowed to vote" required/>
</div>
<div class="checkbox">
<label for="onevoteperip"><input type="checkbox" id="onevoteperip" name="onevoteperip" checked="checked"/>One Vote per IP</label>
</div>
<div class="form-group">
<label for="votes" title="How many different options one voter is allowed to choose.">Votes per Person:</label><input class="form-control" type="number" id="votes" name="votes" value="1" required/>
</div>
<div id="options">
</div>
<button class="btn btn-primary" type="submit" name="submit">Submit</button>
<button class="btn btn-default" type="button" id="more" value="Add More" >More Options</button>
<button class="btn btn-default" type="reset" name="reset">Reset</button>
</form>
</#macro>
<@display_page/>
