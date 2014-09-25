<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<title>Overview</title>
<#include "/jquery.ftl">
</head>
<#include "/bodystart.ftl">
<#include "/menu.ftl">
<h2>Existing Modules:</h2>
<div class="pure-menu pure-menu-open">
<ul class="list">
<#list modules as module>
<li><a href="${module.basepath}">${module.name?html}</a></li>
</#list>
</ul>
</div>
</body>
</html>