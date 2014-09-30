<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<title>Torrent Tracker - Torrent List</title>
</head>
<#include "/bodystart.ftl">
<#include "/menu.ftl">
<h2>Torrent Count: ${torrents?size}</h1>
<div class="pure-menu pure-menu-open">
<ul class="list">
    <#list torrents as torrent>
    <li><a href="${tdlurl}${torrent.hexInfoHash}">${torrent.name?html}.torrent</a> <a href="${basepath}?${param_action}=${a_del_torrent}&${param_torrenthash}=${torrent.hexInfoHash}">delete</a></li>
    </#list>
</ul>
</div>
</body>
</html>