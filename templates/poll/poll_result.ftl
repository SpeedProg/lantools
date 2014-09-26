<!DOCTYPE html>
<html>
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
<td>${option.name?html}
<td>${option.count?html}
</#list>
</table>
<canvas id="chart" width="400" height="400"></canvas>
<script type="text/javascript">
function hsvToRgb(h, s, v){
    var r, g, b;
    var i = Math.floor(h * 6);
    var f = h * 6 - i;
    var p = v * (1 - s);
    var q = v * (1 - f * s);
    var t = v * (1 - (1 - f) * s);
    switch(i % 6){
        case 0: r = v, g = t, b = p; break;
        case 1: r = q, g = v, b = p; break;
        case 2: r = p, g = v, b = t; break;
        case 3: r = p, g = q, b = v; break;
        case 4: r = t, g = p, b = v; break;
        case 5: r = v, g = p, b = q; break;
    }
    return [Math.floor(r * 255), Math.floor(g * 255), Math.floor(b * 255)];
}

function getColor(stepMax, stepC) {
    var color = hsvToRgb((stepC/stepMax), 1, 1);
    return "#"+pad(color[0].toString(16))+pad(color[1].toString(16))+pad(color[2].toString(16));
}

function pad(num) {
  if (num.length < 2) {
    return "0" + num;
  } else {
    return num;
  }
}

var steps = ${poll.options?size}*2;
var c = 0;
var ctx = document.getElementById("chart").getContext("2d");
var mychart = new Chart(ctx);
var data = [
    <#list poll.options as option>
    {
        label: "${option.name?html}",
        value: ${option.count?html},
        color: getColor(steps, c++),
        highlight: getColor(steps, c++)
    },
    </#list>
]
mychart.Pie(data,{
    animateScale: true
});
</script>
</body>
</html>