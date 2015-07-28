<#include "/layout.ftl">

<#macro head>
<script src="/html/jquery.mini.js"></script>
	<#if (poll.maxVotes > 1)>
<script type="text/javascript">
var checkboxes = undefined;
var max = ${poll.maxVotes};
function checkboxValidate(){
        console.log("Validate");
        var current = checkboxes.filter(':checked').length;
        checkboxes.filter(':not(:checked)').prop('disabled', current >= max);
        $('#votesleft').text('Votes left: '+(max-current));
}
$(function(){
    checkboxes = $('#voteform input[type="checkbox"]');
    checkboxes.change(checkboxValidate);
    $("#voteform").on("reset",function() {
    setTimeout(checkboxValidate,100);});
});
$(checkboxValidate);
</script>
</#if>
</#macro>

<#macro body_main>
<#if poll.restriction == 0>
    <form class="pure-form pure-form-aligned" id="voteform" method="post" action="${action}" enctype="multipart/form-data">
    <fieldset>
    <legend>Poll: ${poll.question?html}</legend>
    <#if (poll.maxVotes > 1)>
    
    <div id="votesleft">Votes left: ${poll.maxVotes?html}</div><br />
    <#list poll.options as option>
    <input class="css-checkbox" type="checkbox" name="option_${option.id}" id="option_${option.id}" value="${option.id}"><label for="option_${option.id}">${option.name?html}</label><br/>
    </#list>
    <#else>
    <#list poll.options as option>
    <input type="radio" name="option" id="option_${option.id}" value="${option.id}"><label for="option_${option.id}">${option.name?html}</label><br/>
    </#list>
    </#if>
    </fieldset>
    <input type="hidden" name="pollid" value="${poll.uuid}"/>
    <button class="pure-button pure-button-primary" type="submit">Submit</button>
    <button class="pure-button" type="reset">Reset</button>
    </form>
<#else>
<p>You are not allowed to vote because
    <#if poll.restriction == 1>
 of the IP-Filter.
    <#elseif poll.restriction == 2>
 you voted allready.
    <#else>
 unknown reason. <!-- this should not happen! -->
    </#if>
    </p>
</#if>
</#macro>
<@display_page/>
