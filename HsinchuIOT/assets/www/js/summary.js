$(function(){
	bindEvent();
})

function bindEvent(){
	$('#date').DateTimePicker({time:'Today',callback:loadReport});
	$('#refresh').click(loadReport);
	
}


function loadReport(did){
	var d=$('#date')[0].datetime();
	NBI.loadReport({
		did:[getRequest('id')]
		,t__from:d.from
		,t__to:d.to
		,success:parseReport
	});
}


function parseReport(data){
	$('#data').html('<tr id="d'+getRequest('id')+'"><td class="device">'+getRequest('dkey')+'</td><td class="CO2 max"></td><td class="CO2 min"></td><td class="CO2 avg"></td><td class="Temp max"></td><td class="Temp min"></td><td class="Temp avg"></td><td class="Humidity max"></td><td class="Humidity min"></td><td class="Humidity avg"></td></tr>');
	$('Items Item',data).each(function(){
		var id=$('did',this).attr('ref_val');
		var name=$('name',this).text();
		var max=$('__max_value',this).text()-0;
		var min=$('__min_value',this).text()-0;
		var avg=$('__avg_value',this).text()-0;
		$('#d'+id+' .'+name+'.max').html(max);
		$('#d'+id+' .'+name+'.min').html(min);
		$('#d'+id+' .'+name+'.avg').html(avg);
	});
}