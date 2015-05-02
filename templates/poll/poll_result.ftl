<#include "/layout.ftl">

<#macro body_end>
<script src="/html/chart/Chart.min.js"></script>
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
</#macro>

<#macro body_all>
    <div class="container-fluid">
      <div class="row-fluid">
       <div class="col-lg-5">
        <table class="table table-bordered table-hover table-condensed">
			<thead>
			<tr><th>Option<th>Vote Count
			<tbody>
			<#list poll.options as option>
				<tr>
					<td style="color: ${poll.colors[option_index*2]}">${option.name?html}
					<td style="color: ${poll.colors[option_index*2]}">${option.count?html}
			</#list>
		</table>
       </div>
		<div class="col-lg-5">
			<canvas id="chart" width="300" height="300"></canvas>
		</div>
	  </div>	
    </div>
	<div class="row-fluid">
		<a class="btn btn-primary" href="${engravingvote}">Create Engraving Vote</a>
	</div>
</#macro>
<@display_page/>
