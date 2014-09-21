<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<#include "style.ftl">
<title>Voted</title>
</head>
<#include "bodystart.ftl">
<#include "/menu.ftl">
<p>You voted on ${poll.question?html}.<br/><a href="${poll.resultUrl}">Show Result</a></p>
</body>
</html>