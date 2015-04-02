<!DOCTYPE html>
<head>
<meta charset="UTF-8">
<#include "/style.ftl">
<title>Poll - ${poll.question?html} Result</title>
<#include "/jquery.ftl">
<script src="/html/chart/Chart.min.js"></script>
</head>
<#include "/bodystart.ftl">
<#include "/menu.ftl">
<table class="pure-table">
<thead>
<tr><th>Option<th>Vote Count
<tbody>
<#list poll.options as option>
<tr>
<td style="color: ${poll.colors[option_index*2]}">${option.name?html}
<td style="color: ${poll.colors[option_index*2]}">${option.count?html}
</#list>
</table>
<a href="${engravingvote}">Engraving Vote</a>
<canvas id="chart" width="400" height="400"></canvas>
<script type="text/javascript">
var steps = ${poll.options?size}*2;
var c = 0;
var ctx = document.getElementById("chart").getContext("2d");
var mychart = new Chart(ctx);
var data = [
    <#list poll.options as option>
    {
        label: "${option.name?js_string}",
        value: ${option.count},
        color: "${poll.colors[option_index*2]}",
        highlight: "${poll.colors[option_index*2+1]}"
    },
    </#list>
]
var item = "";
mychart.Pie(data,{
    animateScale: true
});
</script>
</body>
</html>