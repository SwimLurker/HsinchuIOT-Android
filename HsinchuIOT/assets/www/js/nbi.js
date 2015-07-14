function AJAX(o){
	var __DOMAIN='http://hsinchu.enable.mobi';
	if(window.top.__session_id){
		o.data=$.extend({
			__session_id:window.top.__session_id
		},o.data);
	}
	if(o.waiting){$(o.waiting).show();}
	$.ajax({
		url:__DOMAIN+o.url
		,dataType:'xml'
		,cache:false
		,data:o.data
		,complete:function(){
			if(o.waiting){$(o.waiting).hide();}
			if(o.complete)o.complete();
		}
		,success:function(DATA){
			alert(DATA);
			if(checkAPIError(DATA))return;
			if(o.success)o.success(DATA);
		}
		,error:function(){
			alert('error');
			if(o.error)o.error();
		}
	});
}



function mAJAX(o){
	if(o.waiting)$(o.waiting).show();
	var l=o.request.length;
	var c=0;
	for(var i=0;i<l;i++){
		var r=o.request[i];
		r.complete=function(){
			c++;
			if(c>=l){
				if(o.waiting)$(o.waiting).hide();
				if(o.complete)o.complete();
			}
		}
		AJAX(r);
	}
}


function checkAPIError(data){
	var e=$('NBIError',data);
	if(e.length===0)return false;
	var c=$('NBIError Code',data).text();
	var s=$('NBIError String',data).text();
	if(c==='408'){
		window.top.__session_id=null;
		window.top.location='index.html';
		return true;
	}
	alert($.l10n.__('alert_system_error')+':'+c+'\n'+s);
	return true;
}


function toServerTime(d){
	var D=new Date(d.replace(/\-/g,'/'));
	D.setMinutes(D.getMinutes()+window.top.timediffer);
	return dateTimeToString(D,'YYYY-MM-DD hh:mm:ss');
}

function toLocalTime(d){
	var D=new Date(d.replace(/\-/g,'/'));
	D.setMinutes(D.getMinutes()-window.top.timediffer);
	return dateTimeToString(D,'YYYY-MM-DD hh:mm:ss');
}



var NBI={
	loadDomain:function(data){
		AJAX({
			url:'/AdminDomain/_NBI/list.lua'	
			,data:{
				__page_no:1
				,__page_size:10000
				//,__sort:'-id'
				//,pip:data.ip
			}
			,success:data.success
		});
	}
	/*,loadDomainInfo:function(data){
		AJAX({
			url:'/AdminDomain/_NBI/list.lua'	
			,data:{
				__page_no:1
				,__page_size:1
				,ip:data.ip
			}
			,success:data.success
		});
	}
	,loadDomainInfoById:function(data){
		AJAX({
			url:'/AdminDomain/_NBI/list.lua'	
			,data:{
				__page_no:1
				,__page_size:1
				,id: data.id
			}
			,success:data.success
		});
	}*/
	,loadDevice:function(data){
		AJAX({
			url:'/Device/_NBI/list.lua'	
			,data:{
				__page_no:1
				,__page_size:10000
				,__sort:'-id'
				,admin_domain: data.admin_domain
			}
			,success:data.success
		});
	}
	,loadM2M:function(data){
		var d={
			__page_no:1
			//,__page_size:3
			,__column:'did,sensor,name,value,t'
			,__having_max:'id'
			,__group_by:'did,name'//sensor,
			//,'did[0]': data.id
			//t__from:
			,__sort:'-id'
		};
		for(var i=0,l=data.did.length;i<l;i++){
			d['did['+i+']']=data.did[i];
		};
		AJAX({
			url:'/M2M/_NBI/list.lua'
			,data:d
			,success:data.success
		});		
	}
	,loadMonitor:function(data){
		var d={
			__page_no:1
			,__page_size:data.page_size
			,__column:'name,value,t'
			,__group_by:'did,name'
			,__sort:'-id'
		};
		for(var i=0,l=data.did.length;i<l;i++){
			d['did['+i+']']=data.did[i];
		};
		AJAX({
			url:'/M2M/_NBI/list.lua'
			,data:d
			,success:data.success
		});		
	}
	,loadReport:function(data){
		var d={
			__page_no:1
			,__page_size:10000
			,__having_max:'id'
			,__group_by:'did,sensor,name'
			,t__from:toServerTime(data.t__from)
			,t__to:toServerTime(data.t__to)
			,__max:'value'
			,__min:'value'
			,__avg:'value'
		}
		for(var i=0,l=data.did.length;i<l;i++){
			d['did['+i+']']=data.did[i];
		};
		AJAX({
			url:'/M2M/_NBI/report.lua'	
			,data:d
			,success:data.success
		});	
	}
	,loadHistory:function(data){
		var g=data.granularity;
		var e=g.toLowerCase()+'_in_epoch';
		var d={
			__page_no:1
			,__page_size:10000
			,__column:'did,sensor,name,value,'+e
			,__group_by:'did,sensor,name,'+e
			,__sort:'-id'
		};
		d[e+'__from']=toServerTime(data.t__from);
		d[e+'__to']=toServerTime(data.t__to);
		for(var i=0,l=data.did.length;i<l;i++){
			d['did['+i+']']=data.did[i];
		};
		AJAX({
			url:'/M2MAggBy'+g+'/_NBI/list.lua'
			,data:d
			,success:data.success
		});	
	}
	,getDomainFile:function(data){
		AJAX({
			url:'/M2M_DOC/_NBI/view.lua'
			,data:{
				domain_id:data.domain_id
				,file_type:data.file_type
			}
			,success:data.success
		});	
	}
	,isFileExist:function(data){
		$.ajax({
			url:__DOMAIN+data.url
			,type:'HEAD'
			,complete:function(h){
				if(h.status===200){
					if(data.yes)data.yes();
				}else{
					if(data.no)data.no();
				}
			}
		});	
	}
};
