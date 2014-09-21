<nav>
<div class="pure-menu pure-menu-open pure-menu-horizontal topmenu">
<ul id="menu">
    <#list menulinks as link>
    <li><a href="${link.url}">${link.name?html}</a></li>
    </#list>
</ul>
</div>
</nav>