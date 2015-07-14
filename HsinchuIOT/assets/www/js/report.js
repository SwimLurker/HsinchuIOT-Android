$(function(){
	bindEvent();
	loadDevice();
})

function bindEvent(){
	window.setInterval(function(){
		$('#date').html(dateTimeToString(new Date(),'YYYY-MM-DD hh:mm:ss'));
	},1000);
	$('#refresh').click(loadDevice);
}

function loadDevice(){
	NBI.loadDevice({
		admin_domain:getRequest('id')
		,success:parseDevice
	});
}


function parseDevice(data){
	var did=[];
	var $d=$('#data');
	$d.empty();
	$('Items Item',data).each(function(){
		var d={
			dkey:$('dkey',this).text()
			,id:$('id',this).text()
			,admin_domain:$('admin_domain',this).text()
		}; 
		var p=d.admin_domain.split('.');
		p=p[p.length-2];
		did.push(d.id);
		$d.append('<tr id="d'+d.id+'"><td class="device">'+p+'</td><td class="CO2 realtime"></td><td class="Temp realtime"></td><td class="Humidity realtime"></td><td class="CO2 legal"></td><td class="Temp legal"></td><td class="Humidity legal"></td></tr>')
	});
	loadReport(did);
}

function loadReport(did){
	NBI.loadM2M({
		did:did
		,success:parseM2M
	});
	var now=new Date();
	var y=now.getFullYear();
	var m=now.getMonth()+1;
	var d=now.getDate();
	var h=now.getHours();
	var s,t;
	if(h>=0&&h<8){
		var D=new Date(now);
		now.setDate(d-1);
		y=now.getFullYear();
		m=now.getMonth()+1;
		d=now.getDate();
		h=now.getHours();
		s=y+'-'+m+'-'+d+' '+'00:00:00';
		t=y+'-'+m+'-'+d+' '+'08:00:00';
	}
	if(m<10)m='0'+m;
	if(d<10)d='0'+d;
	if(h>=8&&h<16){
		s=y+'-'+m+'-'+d+' '+'00:00:00';
		t=y+'-'+m+'-'+d+' '+'08:00:00';
	}
	if(h>=16){
		s=y+'-'+m+'-'+d+' '+'08:00:00';
		t=y+'-'+m+'-'+d+' '+'16:00:00';
	}
	$('#8HoursAvgDate').text(s.replace(/\-/g,'/')+' - ' +t.replace(/\-/g,'/'));
	NBI.loadHistory({
		did:did
		,t__from:s
		,t__to:t
		,granularity:'Hours'
		,success:parse8Hours
	});
	now=new Date();
	var S=new Date(now);
	var T=new Date(now);
	S.setHours(now.getHours()-2);
	T.setHours(now.getHours()-1);
	y=S.getFullYear();
	m=S.getMonth()+1;
	d=S.getDate();
	h=S.getHours();
	if(m<10)m='0'+m;
	if(d<10)d='0'+d;
	if(h<10)h='0'+h;
	s=y+'-'+m+'-'+d+' '+h+':00:00';
	y=T.getFullYear();
	m=T.getMonth()+1;
	d=T.getDate();
	h=T.getHours();
	if(m<10)m='0'+m;
	if(d<10)d='0'+d;
	if(h<10)h='0'+h;
	t=y+'-'+m+'-'+d+' '+h+':00:00';	
	$('#1HourAvgDate').text(s.replace(/\-/g,'/')+' - ' +t.replace(/\-/g,'/'));
	NBI.loadHistory({
		did:did
		,t__from:s
		,t__to:t
		,granularity:'Hour'
		,success:parse1Hour
	});
}



function parseM2M(data){
	$('NBIResponse > Items > Item',data).each(function(){
		var name=$('name',this).text();
		var value=$('value',this).text()-0;
		var t=$('t',this).text();
		var id=$('did',this).attr('ref_val');
		$('#d'+id+' .realtime.'+name).html('<span class="'+warning(name,value)+'">'+value+'</span>');
	});
}


function parse8Hours(data){
	$('NBIResponse Items Item',data).each(function(){
		var id=$('did',this).attr('ref_val');
		var name=$('name',this).text();
		var value=$('value',this).text()-0;
		var date=$('hour_in_epoch,8hours_in_epoch,day_in_epoch,week_in_epoch,month_in_epoch',this).text();
		date = toLocalTime(date);
		if(name==='CO2')$('#d'+id+' .legal.'+name).html('<span class="'+warning(name,value)+'">'+value+'</span>');
	});
}

function parse1Hour(data){
	$('NBIResponse Items Item',data).each(function(){
		var id=$('did',this).attr('ref_val');
		var name=$('name',this).text();
		var value=$('value',this).text()-0;
		var date=$('hour_in_epoch,8hours_in_epoch,day_in_epoch,week_in_epoch,month_in_epoch',this).text();
		date = toLocalTime(date);
		if(name!=='CO2')$('#d'+id+' .legal.'+name).html('<span class="'+warning(name,value)+'">'+value+'</span>');
	});
}


function warning(n,v){
	switch(n){
		case 'CO2':
			if(v>1000)return 'red';
			if(v>800)return 'yellow';
			return 'green';
		break;
		case 'Temp':
			if(v<15||v>28)return 'red';
			if((v>=15&&v<16)||(v>27&&v<=28))return 'yellow';
			return 'green';
		break;
		case 'Humidity':
			return 'green';
			/*if(v<40||v>60)return 'red';
			if((v>=40&&v<=45)||(v>=55&&v<=60))return 'yellow';
			return 'green';*/
		break;
	}
}