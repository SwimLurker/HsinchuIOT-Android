$(function(){
	$.autoLogin();
	window.page_size=240;//240 2880;
	layout();
	loadMonitor();
});

$.autoLogin = function(){
	var g={
		init:function(){
			g.bindEvent();
		}

		,bindEvent:function(){  
			g.login();
		}

		,login:function(){
			g.getNonce();
		}

		//获取nonce
		,getNonce:function(){
			AJAX({
				url:'/_NBI/get_session_id.lua'
				,success:function(data){
					alert(data);
					alert($('SessionID',data).text());
					window.top.__session_id=$('SessionID',data).text();
					g.toLogin();
				}
			});
		}

		//登录
		,toLogin:function(){
			AJAX({
				url:'/_NBI/login.lua'
				,waiting:$('#waiting')
				,data:{
					username:'hsinchu'
					,mangled_password:hex_md5(hex_md5('hsinchu') + ':' + window.top.__session_id)
					,lang:window.top.Lang
					,timezone:new Date().getTimezoneOffset()
				}
				,success:function(data){
					var username = 'hsinchu';
					var super_user = $('super_user', data).text();
					var permission = $('permission', data).text();
					//var user_id = $('user_id', data).text();
					var admin_domain = $('admin_domain', data).text();
					//var timezone = 0; // 正数为西，负数为东
					var s=g.setLoginCookies(username,window.top.__session_id,admin_domain); //,timezone
					
				}
			});
		}
		
		
		,setLoginCookies:function (username,__session_id,admin_domain,timezone){
			var url='?__session_id='+__session_id+'&username='+username+'&admin_domain='+admin_domain+'&cookie=false';//+'&timezone='+timezone
			return url;
			if(!navigator.cookieEnabled){return url;}
			try{
				localStorage.setItem('IOT_lastuser','hsinchu');
				localStorage.setItem('IOT_lastuserpass','hsinchu');
				localStorage.setItem('IOT_language',window.Lang);
				sessionStorage.setItem('IOT_lang',window.Lang);
				sessionStorage.setItem('IOT_username',username);
				sessionStorage.setItem('IOT___session_id',__session_id);
				sessionStorage.setItem('IOT_admin_domain',admin_domain);
				//sessionStorage.setItem('IOT_timezone',timezone);
				return true;
			}catch(e){
				return url;
			}
		}

	};
	g.init();
	return g;
}

function layout(){
	$('body').layout({ 
			center__paneSelector:"#CHART"
		,	north__paneSelector:"#top"  
		,	north__size:80
		,	north__spacing_open:0
		,	center__onresize_end:replot
	}); 
}

function replot(){
	if(window.chart){
		window.chart.setSize($('#CHART').width(),$('#CHART').height());
	}
}

function loadMonitor(){
	$('#waiting').show();
	var id=getRequest('id');
	NBI.loadMonitor({
		did:[id]
		,page_size:window.page_size*3
		,success:parseMonitor
	});
}

function parseMonitor(data){
	var c={
		CO2:[]
		,Temp:[]
		,Humidity:[]
	}
	var $data=$('NBIResponse > Items > Item',data);
	for(var i=$data.length-1;i>-1;i--){
		var $this=$data[i];
		var n=$('name',$this).text();
		var v=$('value',$this).text()-0;
		var t=$('t',$this).text();
		t = toLocalTime(t);
		t = new Date(t.replace(/\-/g,'/')).getTime();
		c[n].push({x:t,y:v});
	};
	window.DATA=c;
	loadChart(c);
}


function loadM2M(){
	var id=getRequest('id');
	NBI.loadM2M({
		did:[id]
		,success:parseM2M
	});
}


function parseM2M(data){
	$('NBIResponse > Items > Item',data).each(function(){
		var n=$('name',this).text();
		var v=$('value',this).text()-0;
		var t=$('t',this).text();
		t = toLocalTime(t);
		t = new Date(t.replace(/\-/g,'/')).getTime();
		switch(n){
			case 'Temp':
				c=window.chart.series[0];
			break;
			case 'Humidity':
				c=window.chart.series[1];
			break;
			case 'CO2':
				c=window.chart.series[2];
			break;
		}
		$('#'+n).html(numberToHTML(v));
		c.addPoint([t, v], true, true);//时间是否已经存在，已经存在不加
	});
	$('#waiting').hide();
	window.setTimeout(loadM2M,15000);
}

function loadChart(data){
	Highcharts.setOptions({
		global: {
			useUTC: false
		}
	});
   $('#CHART').highcharts({
        chart: {
            type: 'line'
			//,zoomType: 'xy'
			,backgroundColor:'transparent'
        }
        ,title: {
            text: ''
			,style: {
				color: '#89A54E'
			}
        }
		,plotOptions: {  
            line: {
                 marker: {  
                    enabled: false,  
                    states: {  
                        hover: {  
                            enabled: true
                        }  
                    }  
                }
            }  
        }
        ,xAxis: {
			type: 'datetime'
			,tickPixelInterval: 100
            ,labels: {
				formatter: function(){
					return dateTimeToString(new Date(this.value),'hh:mm:ss');
                }
			}
        }
        ,yAxis:[		
			{
				title: {text: ''}
				,labels: {
					format: '{value}°C'
					,style: {
						color:'#f8941d'
					}
				}
				//,min:10
				//,max:33
				,gridLineWidth: 0
				,lineWidth : 1
				,tickInterval: 1
				,startOnTick: false
				,opposite:true
				,plotLines: [{
                    color: '#55320a'
                    ,dashStyle: 'Dash'
                    ,width: 1
                    ,value: 15
                },{
                    color: '#55320a'
                    ,dashStyle: 'Dash'
                    ,width: 1
                    ,value: 28
                }]
			}
			,{
				title: {text: ''}
				,labels: {
					format: '{value}%'
					,style: {
						color:'#7cc576'
					}
				}
				//,min:0
				//,max:100
				,gridLineWidth:0
				,lineWidth : 1
				,tickInterval: 5
				,startOnTick: false
				,opposite:true
				/*,plotLines: [{
                    color: '#7cc576'
                    ,dashStyle: 'ShortDash'
                    ,width: 1
                    ,value: 40
                },{
                    color: '#7cc576'
                    ,dashStyle: 'ShortDash'
                    ,width: 1
                    ,value: 60
                }]*/
			}
			,{
				title: {text: ''}
				,labels: {
					format: '{value}ppm'
					,style: {
						color:'#448ccb'
					}
				}
				//,min:400
				//,max:1100
				,gridLineWidth: 0
				,lineWidth : 1
				//,tickInterval: 100
				,startOnTick: false
				//,opposite:true
				,plotLines: [{
                    color: '#448ccb'
                    ,dashStyle: 'Dot'
                    ,width: 2
                    ,value: 1000
                }]
			}
        ]
        ,tooltip: {
			crosshairs:[true,true]
			,shared: false
        }
        ,series: [
			{
				name: $.l10n.__('Temperature')
				,color:'#f8941d'
				,yAxis:0
				,tooltip:{
					 xDateFormat: '%Y-%m-%d %H:%M:%S'
					 ,valueSuffix: ' °C'
				}
				,data:data.Temp

			}
			,{
				name: $.l10n.__('Humidity')
				,color:'#7cc576'
				,yAxis:1
				,tooltip: {
					 xDateFormat: '%Y-%m-%d %H:%M:%S'
					 ,valueSuffix: ' %'
				}
				,data:data.Humidity
			}
			,{
				name: $.l10n.__('CO2')
				,color:'#448ccb'
				,yAxis:2
				,tooltip:{
					 xDateFormat: '%Y-%m-%d %H:%M:%S'
					,valueSuffix: ' ppm'
				}
				,data:data.CO2
			}
		]
    });
	window.chart=$('#CHART').highcharts();
	loadM2M();
}