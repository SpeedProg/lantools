<#include "/layout.ftl">

<#macro body_main>
<ul class="list-group">
	<#list torrents as torrent>
	<li class="list-group-item">
		<a href="${tdlurl}${torrent.hexInfoHash}">${torrent.name?html}.torrent</a> <a href="${basepath}?${param_action}=${a_del_torrent}&${param_torrenthash}=${torrent.hexInfoHash}"><span class="badge"><span class="glyphicon glyphicon-trash" aria-label="Delete"></span></span></a>
	</li>
	</#list>
</ul>
</#macro>

<@display_page/>
