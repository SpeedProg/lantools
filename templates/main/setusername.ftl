<#include "/layout.ftl">

<#macro body_main>
<div class="row">
<div class="col-lg-6">
<div class="alert alert-info" role="alert">
You need to map a Username to your ip. Please choose one :).
</div>
<form action="/" method="get" class="pure-form">
<div class="input-group">
<input class="form-control" placeholder="Username" type="text" name="username" id="username"/>
<span class="input-group-btn">
<button type="submit" type="submit" class="btn btn-default">Submit</button>
</span>
</div>
</form>
</div>
</div>
</#macro>

<@display_page/>