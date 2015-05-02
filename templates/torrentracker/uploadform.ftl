<#include "/layout.ftl">

<#macro head>
<link href="/html/css/fileinput.css" rel="stylesheet">
</#macro>

<#macro body_end>
<script src="/html/js/fileinput.js"></script>
</#macro>

<#macro body_main>
<#if uploaded!false>
<#if success>
<p>You uploaded <a href="${tdlurl}${torrent.hexInfoHash}">${torrent.name?html}</a>.</p>
<#else>
<p>Your upload failed, most likely it wasn't a valid torrent file.<#if msg??> ${msg?html}</#if></p>
</#if>
</#if>
<#if showform!false>
<form class="pure-form" action="${action}" method="post" enctype="multipart/form-data">
	<div class="form-group">
	<label for="file">Torrent File:</label>
	<div class="input-group">
		<input type="text" id="fake_file" class="form-control" placeholder="file..." readonly>
		<span class="input-group-addon btn btn-default btn-file">Browse<input type="file" name="file" id="file"></span>
	</div>
	</div>
    <button class="btn btn-default" type="submit" value="Upload">Upload</button>
</form>
</#if>
</#macro>

<@display_page/>
