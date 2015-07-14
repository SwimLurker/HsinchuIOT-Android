$(function(){
	loadM2M();
	bindEvent();
})

function bindEvent(){
	$('body').delegate('.refresh','click',loadM2M);
}


function loadM2M(data){
	var id=getRequest('id');
	NBI.loadM2M({did:[id],success:parseM2M});
}

function parseM2M(data){
	$('NBIResponse > Items > Item',data).each(function(){
		var n=$('name',this).text();
		var v=$('value',this).text()-0;
		var t=$('t',this).text();
		t = toLocalTime(t);
		$('.'+n).html(numberToHTML(v));
		if(n==='CO2')showFace(v);
	});
	window.setTimeout(loadM2M,15000);
}



function showFace(v){
	$('.emotion').hide();
	if(v<=800)$(' .good').show();
	if(v<=1000&&v>800)$(' .soso').show();
	if(v>1000)$(' .bad').show();
}