<#macro head_default>
</#macro>

<#macro menu_nav_default>
	<#list menu as entry>
		<#if entry.hassubmenu>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">${entry.name?html} <span class="caret"></span></a>
          <ul class="dropdown-menu" role="menu">
          	<!--<<li><a href="${entry.link}">Home</a></li> -->
          	<#list entry.submenu as dentry>
            	<li><a href="${dentry.link}">${dentry.name?html}</a></li>
            </#list>
          </ul>
        </li>
		<#else>
			<li><a href="${entry.link}">${entry.name?html}</a></li>
		</#if>
	</#list>
</#macro>

<#macro head>
	<@head_default/>
</#macro>

<#macro body_main>
</#macro>

<#macro body_end>
</#macro>

<#macro menu_nav>
	<@menu_nav_default/>
</#macro>

<#macro body_all>
    <div class="container-fluid">
      <div class="row-fluid">
        <div class="col-lg-5">
          <@body_main/>
        </div>
	  </div>	
    </div>
</#macro>
<#macro display_page>
<!DOCTYPE html>
<html>
  <head>
    <title>LanTools | ${title!""}</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="apple-mobile-web-app-capable" content="yes">

    <!-- Bootstrap -->
    <link href="/html/css/bootstrap.min.css" rel="stylesheet" media="screen">
	<@head/>
  </head>
  <body>
    <div class="navbar navbar-default navbar-static-top" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/">LanTools</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav navbar-left" id="main-nav">
          	<@menu_nav/>
          </ul>
        </div>
      </div>
    </div>
    <div class="container-fluid">
	<#if errormsg??>
		<#if errormsg?is_enumerable>
			<#list errormsg as msg>
				<div class="row">
					<div class="alert alert-danger">${msg?html}</div>
				</div>
			</#list>
		<#else>
			<div class="alert alert-danger">${errormsg?html}</div>
		</#if>
	</#if>
	<#if infomsg??>
		<#if infomsg?is_enumerable>
			<#list infomsg as msg>
				<div class="row">
					<div class="alert alert-info">${msg?html}</div>
				</div>
			</#list>
		<#else>
			<div class="row">
				<div class="alert alert-info">${infomsg?html}</div>
			</div>
		</#if>
	</#if>
	<#if successmsg??>
		<#if successmsg?is_enumerable>
			<#list errormsg as msg >
				<div class="row">
					<div class="alert alert-success">${msg?html}</div>
				</div>
			</#list>
		<#else>
			<div class="row">
				<div class="alert alert-success">${successmsg?html}</div>
			</div>
		</#if>
	</#if>
    </div>
    <@body_all/>
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://code.jquery.com/jquery.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="/html/js/bootstrap.min.js"></script>
    <@body_end/>
  </body>
</html>
</#macro>
