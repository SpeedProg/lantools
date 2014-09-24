<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<link rel="stylesheet" href="/html/notices/boardview.css">
<title>Notices - Board</title>
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
${board.name}
</div></a>
<div class="lantools-subtitle">
Owner: ${board.owner.username}
</div>
<div class="notice-board-body">
${board.description}
</div>
</div>
</#list>
    </div>
</div>
</body>
</html>