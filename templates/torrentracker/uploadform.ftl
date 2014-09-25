<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<title>Torrent Tracker - Upload</title>
</head>
<#include "/bodystart.ftl">
<#include "/menu.ftl">
<#if uploaded!false>
<#if success>
<p>You uploaded <a href="${tdlurl}${torrent.hexInfoHash}">${torrent.name?html}</a>.</p>
<#else>
<p>Your upload failed, most likely it wasn't a valid torrent file.</p>
</#if>
</#if>
<#if showform!false>
<h2>Upload a Torrent</h2>
<form class="pure-form" action="${action}" method="post" enctype="multipart/form-data">
    <label for="file">Torrent File:</label><input type="file" name="file" id="file" />
    <button class="pure-button pure-button-primary" type="submit" value="Upload">Upload</button>
</form>
</#if>
</body>
</html>