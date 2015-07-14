$(function(){
	layout();
	bindEvent();
});

function layout(){
	$('body').layout({ 
			center__paneSelector:'#CHART'
		,	north__paneSelector:'#HEADER' 
		,	north__size:120
		,	north__spacing_open:0
		//,	center__maskContents:true
		,	contentSelector:".data"
		,	center__onresize_end:replot
	});
}

function replot(){
	if(window.chart){
		window.chart.setSize($('#CHART').width(),$('#CHART').height());
	}
}

function bindEvent(){
	$('#date').DateTimePicker({time:'Today',callback:loadData});
	$('#refresh').click(loadData);
	$('#download').click(toDownload);
	$('#granularity a').click(function(){
		$(this).addClass('selected').siblings().removeClass('selected');
		loadData();
	});
}


function loadData(){
	window.CSV=null;
	$('#download').hide();
	var d=$('#date')[0].datetime();
	var s=new Date(d.start),e=new Date(d.end);
	var t=e-s;
	var $g=$('#granularity');
	/*
		时间小于1 Quarter			不可
		时间小于240 Quarter,60小时	Quarter
		时间小于240小时，			Hour
		时间小于240个8小时，		Hours
		时间小于240天，				Day
		时间小于240周，240*7天， 	Week
		时间小于240月，20年			Month
		时间大于240月				不可
	$('a',$g).show();
	
	if(t<15*3600){
		$('.Quarter',$g).removeClass('selected').hide();
	}
	if(t<1*3600000){
		$('.Hour',$g).removeClass('selected').hide();
	}
	if(t<8*3600000){
		$('.Hours',$g).removeClass('selected').hide();
	}
	if(t<1*86400000){
		$('.Day',$g).removeClass('selected').hide();
	}
	if(t<7*86400000){
		$('.Week',$g).removeClass('selected').hide();
	}
	if(t<28*86400000){
		$('.Month',$g).removeClass('selected').hide();
	}
	if($('a.selected',$g).length===0){
		$('a',$g).removeClass('selected');
		//if(t<=15*3600){
			//return;
		//}else if(t<=60*3600000){
			//$('.Quarter',$g).addClass('selected');
		//}
		if(t<=1*3600000){
			return;
		}else if(t<=240*3600000){
			$('.Hour',$g).addClass('selected');
		}else if(t<=240*8*3600000){
			$('.Hours',$g).addClass('selected');
		}else if(t<=240*86400000){
			$('.Day',$g).addClass('selected');
		}else if(t<=240*7*86400000){
			$('.Week',$g).addClass('selected');
		}else{	
			//t<=365*20*86400000
			$('.Month',$g).addClass('selected');
		}
	}
	*/
	var g=$('a.selected',$g).attr('name');
	NBI.loadHistory({
		did:[getRequest('id')]
		,t__from:d.from
		,t__to:d.to
		,granularity:g
		,success:parseHistory
	});
}


function toDownload(){
	saveAs(
		new Blob([window.CSV], {type: 'text/plain;charset=utf-8'})
		,getRequest('title')+'.csv'
	);
}



function parseHistory(data){
	var csv={};
	var c={CO2:[],Temp:[],Humidity:[]};
	var $data=$('NBIResponse Items Item',data);
	for(var i=$data.length-1;i>-1;i--){
		var $this=$data[i];
		var id=$('did',$this).attr('ref_val');
		var name=$('name',$this).text();
		var value=$('value',$this).text();
		var date=$('hour_in_epoch,hours_in_epoch,day_in_epoch,week_in_epoch,month_in_epoch',$this).text();
		date = toLocalTime(date);
		t = new Date(date.replace(/\-/g,'/')).getTime();
		c[name].push({x:t,y:value-0});
		if(!csv[date]){csv[date]={};}
		csv[date][name]=value;
	}
	loadChart(c);
	var _csv=[['Time','CO2','Temperature','Humidity'].join(',')];
	for(var i in csv){
		var I=csv[i];
		_csv.push([i,I.CO2,I.Temp,I.Humidity].join(','));
	}
	window.CSV=_csv.join('\n');
	$('#download').show();
}

function loadChart(data){
	Highcharts.setOptions({
		global: {
			useUTC: false
		}
	});
	var type=getRequest('type');
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
					return dateTimeToString(new Date(this.value-0),'YYYY-MM-DD hh:mm:ss').replace(' ','<br/>');
                }
			}
        }
        ,yAxis:[
			{
				title: {text: ''}
				,labels: {
					format: '{value}ppm'
					,style: {
						color:'#448ccb'
					}
				}
				//,min:0
				,gridLineWidth: 0
				,lineWidth : 1
				//,tickInterval: 200
				,startOnTick: false
				//,opposite:true
				,plotLines: [{
                    color: '#448ccb'
                    ,dashStyle: 'Dot'
                    ,width: 2
                    ,value: 1000
                }]
			}			
			,{
				title: {text: ''}
				,labels: {
					format: '{value}°C'
					,style: {
						color:'#f8941d'
					}
				}
				//,min:0
				//,max:50
				,gridLineWidth: 0
				,lineWidth : 1
				//,tickInterval: 5
				,startOnTick: false
				,opposite:true
				,plotLines: [{
                    color: '#f8941d'
                    ,dashStyle: 'Dash'
                    ,width: 1
                    ,value: 15
                },{
                    color: '#f8941d'
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
				//,tickInterval: 10
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
        ]
        ,tooltip: {
			crosshairs:[true,true]
			,shared: false
        }
        ,series: [
			{
				visible:!type||type==='CO2'
				,name: $.l10n.__('CO2')
				,color:'#448ccb'
				,yAxis:0
				,tooltip:{
					 xDateFormat: '%Y-%m-%d %H:%M:%S'
					,valueSuffix: ' ppm'
				}
				,data:data.CO2
			}
			,{
				visible:!type||type==='Temp'
				,name: $.l10n.__('Temperature')
				,color:'#f8941d'
				,yAxis:1
				,tooltip:{
					 xDateFormat: '%Y-%m-%d %H:%M:%S'
					 ,valueSuffix: ' °C'
				}
				,data:data.Temp

			}
			,{
				visible:!type||type==='Humidity'
				,name: $.l10n.__('Humidity')
				,color:'#7cc576'
				,yAxis:2
				,tooltip: {
					 xDateFormat: '%Y-%m-%d %H:%M:%S'
					 ,valueSuffix: ' %'
				}
				,data:data.Humidity
			}
		]
    });
	window.chart=$('#CHART').highcharts();
}