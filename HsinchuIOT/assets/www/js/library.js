function getRequest(name) { 
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r!=null) return decodeURI(r[2]);
	return null; 

}

function dateTimeToString(d,t){
	var Y=d.getFullYear();
	var M=d.getMonth()+1;
	var D=d.getDate();
	var h=d.getHours();
	var m=d.getMinutes();
	var s=d.getSeconds();	
	var MM=(M>9)?M:'0'+M;
	var DD=(D>9)?D:'0'+D;
	var hh=(h>9)?h:'0'+h;
	var mm=(m>9)?m:'0'+m;
	var ss=(s>9)?s:'0'+s;
	switch(t){
		case 'YYYYMMDD':
			return Y+''+MM+''+DD;
		break;
		case 'YYYY-MM-DD':
			return Y+'-'+MM+'-'+DD;
		break;
		case 'YYYY-MM-DD hh:mm':
			return Y+'-'+MM+'-'+DD+' '+hh+':'+mm;
		break;
		case 'YYYY-MM-DD hh:mm:ss':
			return Y+'-'+MM+'-'+DD+' '+hh+':'+mm+':'+ss;
		break;
		case 'YYYY/MM/DD':
			return Y+'/'+MM+'/'+DD;
		break;
		case 'YYYY/MM/DD hh:mm':
			return Y+'/'+MM+'/'+DD+' '+hh+':'+mm;
		break;
		case 'YYYY/MM/DD hh:mm:ss':
			return Y+'/'+MM+'/'+DD+' '+hh+':'+mm+':'+ss;
		break;
		case 'hh:mm':
			return hh+':'+mm;
		break;
		case 'hh:mm:ss':
			return hh+':'+mm+':'+ss;
		break;
		default:
			return Y+'-'+MM+'-'+DD+' '+hh+':'+mm+':'+ss;
		break;
	}
}


function getRandom(from,to){
	return Math.floor(Math.random()*(to-from+1)+from);
}



function xmlToString(xml){   
	try {
		var s= new XMLSerializer().serializeToString(xml);
		if(s.indexOf('<?xml')!==0)s='<?xml version="1.0" encoding="UTF-8"?>'+s
		return s;   
	} catch(e){
		return xml.xml;  	
	}
}  

function checkSession(){
	var cookieEnabled=getRequest('cookie')==='false'?false:navigator.cookieEnabled;
	window.__session_id=cookieEnabled?sessionStorage.getItem('IOT___session_id'):getRequest('__session_id');
	window.Lang=cookieEnabled?sessionStorage.getItem('IOT_lang'):getRequest('lang');
	window.username=cookieEnabled?sessionStorage.getItem('IOT_username'):getRequest('username');
	window.admin_domain=cookieEnabled?sessionStorage.getItem('IOT_admin_domain'):getRequest('admin_domain');
	window.timezone=cookieEnabled?sessionStorage.getItem('IOT_timezone'):getRequest('timezone');
	window.timediffer=-480;//new Date().getTimezoneOffset()-window.timezone*60;
	//if(!window.__session_id)window.location='index.html';
}



function numberToHTML(v){
	v=v+'';
	var html=[];
	for(var i=0,l=v.length;i<l;i++){
		var vi=v[i];
		switch (v[i]){
			case '0':	case '1':	case '2':	case '3':	case '4':	case '5':	case '6':	case '7':	case '8':	case '9':
				html.push('<div class="n'+vi+'"></div>');
			break;
			case '-':
				html.push('<div class="n10"></div>');
			break;
			case '.':
				html.push('<div class="n11"></div>');
			break;
		}
		
	}
	return html.join('');
}



function setLang(option){
	if(!$.l10n)return;
	if(!option)option={}; 
	var lang=option.lang;
	var el=option.el;
	if(!lang)lang=navigator.cookieEnabled?localStorage.getItem('language'):null;
	if(!lang)lang=window.top.Lang;
	if(!lang)lang=window.navigator.systemLanguage || window.navigator.language;
	lang=lang.toLowerCase();
	if(lang.indexOf('-')>-1){
		var l=lang.split('-');
		if(l[0]==='zh'){
			if(lang==='zh-tw'||lang==='zh-hk'||lang==='zh-mo'||lang==='zh-hant'){
				lang='zh-tw';
			}else{
				lang==='zh-cn'
			}
		}else{
			lang=l[0];
		}
	}
	window.top.Lang=lang;
	var $domain=(el)?$('[domain="l10n"]',el):$('[domain="l10n"]');
	$domain.l10n({
		dir:'languages'
		,lang:lang
	});
}


window.addEventListener('load',setLang);