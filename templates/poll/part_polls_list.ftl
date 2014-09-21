<#list polls as poll>
<div class="pure-menu pure-menu-open pure-menu-horizontal">
<style type="text/css" scoped>
.pure-menu .pure-menu-heading {
    text-transform: none;
}
.pure-menu a {
    text-decoration: underline;
    background-color: rgba(179, 233, 177, 1);

}
.pure-menu-heading {
    font-size: 150%;
    border-right: medium solid green;
    text-align: center;
}
.pure-menu a:hover {
    background-color: rgba(60, 164, 56, 1);
}
.pure-menu-heading {
    display: inherit;
}
.pure-menu.pure-menu-horizontal > ul {
    display: inherit;
}
</style>
<div class="pure-menu-heading">${poll.question?html}</div>
<ul>
<li><a href="${poll.voteUrl}">Vote</a></li>
<li><a href="${poll.resultUrl}">Show Result</a></li>
<li><a href="${poll.deleteUrl}">Delete</a></li>
</ul>
</div>
</#list>