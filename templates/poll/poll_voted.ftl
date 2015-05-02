<#include "/layout.ftl">

<#macro body_main>
<p>You voted on ${poll.question?html}.<br/><a href="${poll.resultUrl}">Show Result</a></p>
</#macro>
<@display_page/>
