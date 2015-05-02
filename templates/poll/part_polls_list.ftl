<#list polls as poll>
<ul class="list-group">
	<li class="list-group-item"><div class="btn-group">
  <button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
    Action <span class="caret"></span>
  </button>
  <ul class="dropdown-menu dropdown-menu-left" role="menu">
    <li><a href="${poll.voteUrl}"><span class="glyphicon glyphicon-ok-circle" aria-hidden="true"></span> Vote</a></li>
    <li><a href="${poll.resultUrl}"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> Show Result</a></li>
    <li><a href="${poll.deleteUrl}"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Delete</a></li>
  </ul>
</div> ${poll.question?html}</li>
</ul>
</#list>
