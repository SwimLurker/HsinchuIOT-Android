$(function(){
	bindEvent();
	loadData();
})

function bindEvent(){
	var $$=window.top.$;
	$('html,body').css({width:'100%',height:'100%'});
	$('body')
	.delegate('a.plan','click',function(){
		var id=$(this).attr('_id');
		$$('#tree a#'+id+' .plan').click();
	})
	.delegate('a.info','click',function(){
		var id=$(this).attr('_id');
		$$('#tree a#'+id+' .info').click();
	}).delegate('a.monitor','click',function(){
		var id=$(this).attr('_id');
		$$('#tree a#'+id+' .monitor').click();
	}).delegate('a.realtime','click',function(){
		var id=$(this).attr('_id');
		$$('#tree a#'+id+' .realtime').click();
	}).delegate('a.history','click',function(){
		var id=$(this).attr('_id');
		$$('#tree a#'+id+' .history').click();
		
	}).delegate('a.summary','click',function(){
		var id=$(this).attr('_id');
		$$('#tree a#'+id+' .summary').click();
		
	});
}


function loadData(){
	var $$=window.top.$;
	var id=getRequest('id');
	var data=$$('#tree a#'+id)[0].data;
	var json=data.json;
	setMap({
		zoom:json.zoom
		,minzoom:json.minzoom
		,center:new google.maps.LatLng(json.location[0],json.location[1])
		,bound:new google.maps.LatLngBounds( new google.maps.LatLng(json.bound[0], json.bound[1]),  new google.maps.LatLng(json.bound[2], json.bound[3]) )
	});
	var $buildings=$$('#tree li[ip="'+data.ip+'"] ul a.building');
	var markers=[];
	$buildings.each(function(){
		var d=$$('#tree a#'+this.id)[0].data;
		if(!d.json)return;
		markers.push({
			position:new google.maps.LatLng(d.json.location[0],d.json.location[1])
			,type:d.json.type
			,image:d.json.image
			,title:d.name
			,ip:d.ip
			,id:d.id
		});
	});
	loadMarker(markers);
}


function setMap(o){
	var mapOptions = {
		center: o.center,
		zoom: o.zoom,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	var map = new google.maps.Map(document.body,mapOptions);
	window.map = map;
	
	var minZoomLevel=o.minzoom;
	google.maps.event.addListener(map, 'zoom_changed',  function() {  
		if (map.getZoom() < minZoomLevel) map.setZoom(minZoomLevel);  
	});     
	var strictBounds = o.bound;  
	google.maps.event.addListener(map, 'dragend',  function() {  
		if (strictBounds.contains(map.getCenter())) return;  
		var c = map.getCenter(),  
		x = c.lng(),  
		y = c.lat(),  
		maxX = strictBounds.getNorthEast().lng(),  
		maxY = strictBounds.getNorthEast().lat(),  
		minX = strictBounds.getSouthWest().lng(),  
		minY = strictBounds.getSouthWest().lat();  
		if (x < minX) x = minX;  
		if (x > maxX) x = maxX;  
		if (y < minY) y = minY;  
		if (y > maxY) y = maxY;  
		map.setCenter(new google.maps.LatLng(y, x));  
	}); 
}


function loadMarker(markers){
	for ( var i in markers) { 
		var mi= markers[i];						  
		var marker = new google.maps.Marker( {  
			map: window.map
			,position: mi.position
			,title:  mi.title
			,ip: mi.ip
			,id: mi.id
			,type:mi.type
			,image:mi.image
		}); 
		setMaker(marker);  
	}
}


function setMaker(M){
	google.maps.event.addListener(M, 'click',  function() {
		var c='<img src="images/'+M.image+'"/><h2>'+M.title+'</h2><a _id="'+M.id+'" class="link monitor">'+$.l10n.__('Monitor')+'</a><a _id="'+M.id+'" class="link realtime">'+$.l10n.__('Realtime')+'</a><a _id="'+M.id+'" class="link history">'+$.l10n.__('History')+'</a><a _id="'+M.id+'" class="link summary">'+$.l10n.__('Summary')+'</a><a _id="'+M.id+'" class="link plan">'+$.l10n.__('Guidance')+'</a><a _id="'+M.id+'" class="link info">'+$.l10n.__('Contact')+'</a>';
		if (!window.infowindow) {
			window.infowindow = new google.maps.InfoWindow({
				position:M.position
				,content:c
			});  
		} else{
			window.infowindow.position=M.position;
			window.infowindow.content=c;
		}
		window.infowindow.open(window.map, M);  
	}); 
}
