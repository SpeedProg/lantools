<#list board.entryList as entry>
<menu type="context" id="cmenu_${entry.id}">
   <menuitem label="Delete Notice" onclick="deleteNotice('${entry.id}');"/>
</menu>
<div contextmenu="cmenu_${entry.id}" class="notice-board lantools-content" id="board-${entry.id}">
    <div class="notice-board-head lantools-header">
        ${entry.title?html}  <a href="#" style="color: red;" onclick="deleteNotice('${entry.id}');">X</a>
    </div>
    <div class="lantools-subtitle">
        Author: ${userMapper.getUser(entry.owner).username?html}
    </div>
    <div class="notice-board-body">
        ${entry.content?html}
    </div>
</div>
</#list>