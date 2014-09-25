<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<link rel="stylesheet" href="/html/notices/boardview.css">
<title>Notices - Index</title>
<#include "/jquery.ftl">
<script type="text/javascript">
function deleteNotice(noticeid) {
     window.location.href = "${basepath}?${param_action}=${a_del_notice}&boardid=${board.id}&noticeid="+noticeid;
}
</script>
</head>
<#include "/bodystart.ftl">
<#include "/menu.ftl">
<div class="lantools-content" id="board-${board.id}">
    <div class="lantools-header">
${board.name}
    </div>
    <div class="lantools-subtitle">
Owner: ${board.owner.username} Desc: ${board.description}
    </div>
    <div class="notice-board-body">
        <#list board.entryList as entry>
        <menu type="context" id="cmenu_${entry.id}">
        <menuitem label="Delete Notice" onclick="deleteNotice('${entry.id}');"/>
        </menu>
        <div contextmenu="cmenu_${entry.id}" class="notice-board lantools-content" id="board-${entry.id}">
            <div class="notice-board-head lantools-header">
                ${entry.title}
            </div>
            <div class="lantools-subtitle">
                Author: ${entry.owner.username}
            </div>
            <div class="notice-board-body">
                ${entry.content}
            </div>
        </div>
        </#list>
    </div>
    <a href="#" id="showform" onClick="document.getElementById('newboardform').hidden=false;document.getElementById('showform').hidden=true;">Add a Notice</a>
    <form id="newboardform" cass="pure-form pure-form-aligned" action="${basepath}?${param_action}=${a_new_notice}&${param_boardid}=${board.id}" method="post" hidden>
    <div class="pure-control-group">
        <label for="name_input">Notice Title:</label><input type="text" id="title_input" name="title"/>
    </div>
    <div class="pure-control-group">
        <label for="desc_textarea">Content:</label><textarea required rows="10" placeholder="Notice text..." name="content"></textarea>
    </div>
    <div class="pure-control">
        <button class="pure-button pure-button-primary" type="submit">Submit</button>
    </div>
</form>
</div>
</body>
</html>