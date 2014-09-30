<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<link rel="stylesheet" href="/html/notices/boardview.css">
<title>Notices - Boards</title>
<#include "/jquery.ftl">
</head>
<#include "/bodystart.ftl">
<#include "/menu.ftl">
<div class="lantools-content">
    <div class="lantools-header">
    Available Boards
    </div>
    <div class="lantools-subtitle">
    Board Count: ${boards?size}
    </div>
    <div class="notice-board-body">
        <#list boards as board>
        <div class="notice-board lantools-content" id="board-${board.id}">
            <a href="/notices/?action=${a_show_board}&boardid=${board.id}">
                <div class="notice-board-head lantools-header">
                    ${board.name?html}
                </div>
            </a>
            <div class="lantools-subtitle">
                Owner: ${(userMapper.getUser(board.owner).username)!"Unknown"?html}
            </div>
            <div class="notice-board-body">
                ${board.description?html}
            </div>
        </div>
        </#list>
    </div>
    <a href="#" id="showform" onClick="document.getElementById('newboardform').hidden=false;document.getElementById('showform').hidden=true;">Create new Board</a>
    <form id="newboardform" cass="pure-form pure-form-aligned" action="${basepath}?${param_action}=${a_new_board}" method="post" hidden>
    <div class="pure-control-group">
        <label for="name_input">Board Name:</label><input required placeholder="eg. Pizza board" type="text" id="name_input" name="name"/>
    </div>
    <div class="pure-control-group">
        <label for="desc_textarea">Description:</label><textarea required placeholder="Description of the context of this Noticeboard. Max 500 chars." name="desc" maxlength="500"></textarea>
    </div>
    <div class="pure-control">
        <button class="pure-button pure-button-primary" type="submit">Submit</button>
    </div>
    </form>
</div>
</body>
</html>