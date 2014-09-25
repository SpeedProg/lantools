<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<title>Poll - Poll Created</title>
<#include "/jquery.ftl">
</head>
<#include "/bodystart.ftl">
<#include "/menu.ftl">
<p>Poll: <pre>${createdpoll.question?html}</pre> created with ID ${createdpoll.uuid?html}</p>
<#include "/poll/part_polls_list.ftl">
</body>
</html>