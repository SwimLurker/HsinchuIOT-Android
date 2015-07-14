$(function(){ 
	checkSession();
	layout();
	bindEvent();
	loadData();
	loadReport();
})

function layout(){ 
	window.LAYOUT=$('body').layout({ 
			center__paneSelector:'#main'
		,	north__paneSelector:'#top'
		,	north__size:32
		,	north__spacing_open:0
		,	west__paneSelector:'#left'  
		,	west__size:320
		,	west__spacing_open:0
		,	center__maskContents:true
		,	contentSelector:'.data'
	}); 
	window.LAYOUT.addPinBtn('#list', 'west');
	window.LAYOUT.hide('west');
}

function bindEvent(){
	$('#monitor').click(loadMonitor);
	$('#realtime').click(loadRealtime);
	$('#history').click(loadHistory);
	$('#summary').click(loadSummary);
	$('#report').click(loadReport);
	$('#logout').click(toLogout);
	$('#toolbar a').click(function(){
		$('#toolbar a.selected').removeClass('selected');
		$(this).addClass('selected');
	});
	$('.toolbar a').click(function(){
		if(this.id!=='list'){
			window.LAYOUT.hide('west');
		}
	});
	$('#devicelist').delegate('li','click',function(){
		window.LAYOUT.hide('west');
		$('#devicelist li.selected').removeClass('selected');
		$(this).addClass('selected');
		var a=$('#toolbar a.selected')[0];
		loadPage(a.id);
	});
}


function toLogout(){
	window.location='index.html';
}


function loadData(){
	NBI.loadDevice({
		success:parseData
	});
}

function parseData(data){
	$('Items Item',data).each(function(){
		var d={
			dkey:$('dkey',this).text()
			,id:$('id',this).text()
			,admin_domain:$('admin_domain',this).attr('ref_val')
			,name:getName($('admin_domain',this).text())
		};
		var li=document.createElement('li');
		var a=document.createElement('a');
		a.innerHTML=d.name;
		li.data=d;
		li.id=d.id; 
		$(li).append(a).appendTo('#devicelist');
	});
}

function loadMonitor(o){
	loadPage('monitor');
}

function loadRealtime(o){
	loadPage('realtime');
}

function loadHistory(o){
	loadPage('history');
}

function loadSummary(o){
	loadPage('summary');
}

function loadReport(){
	loadPage('report');
}

function loadPage(url){
	if(url==='report'){
		$('#toolbar').hide();
		url=url+'.html?id='+window.admin_domain;
		$('#name').empty();
	}else{
		$('#toolbar').show();
		var li=$('#devicelist li.selected')[0];
		if(!li)return;
		var id=li.data.id
		,dkey=li.data.dkey
		,admindomain=li.data.admin_domain
		,name=li.data.name;
		var url=url+'.html?id='+id+'&dkey='+dkey+'&name='+name+'&admin_domain='+admindomain+'&root='+window.admin_domain;
		$('#name').html(name);
	}
	window.LAYOUT.resizeAll();
	$('#page').attr('src',url);
}

function getName(s){
	var r=s.replace(/\.$/,'');
	r=r.substr(r.lastIndexOf('.')+1);
	return r;
}