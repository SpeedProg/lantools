<#include "/layout.ftl">
<#macro body_main>
<p>Poll: <pre>${createdpoll.question?html}</pre> created with ID ${createdpoll.uuid?html}</p>
<#include "/poll/part_polls_list.ftl">
</#macro>
<@display_page/>
