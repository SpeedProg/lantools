<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<link rel="stylesheet" href="/html/notices/boardview.css">
<title>Notices - Index</title>
<#include "/jquery.ftl">
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
        <div class="notice-board lantools-content" id="board-${entry.id}">
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
</div>
</body>
</html>