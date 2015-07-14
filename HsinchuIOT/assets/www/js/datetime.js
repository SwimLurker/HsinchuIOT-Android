(function($){
$.addDateTimePicker = function(t,o){
	var g={	
		Months:['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC']
		,Weeks:['SUN','MON','TUE','WED','THU','FRI','SAT']
		,Shortcuts:['Today','Yesterday','ThisWeek','LastWeek','ThisMonth','LastMonth','Last3Days','Last5Days','Last7Days','Last1Weeks','Last2Weeks','Last3Weeks','Last1Month','Last2Month','Last3Month']
		,init:function(){
			g.create();
		}
		,create:function(){
			setLang();
			g.container=document.createElement('div');g.$container=$(g.container);g.$container.addClass('DateTimePicker');
			g.shortcuts=document.createElement('div');g.$shortcuts=$(g.shortcuts);g.shortcuts.className='shortcuts';
			g.calendar=document.createElement('div');g.$calendar=$(g.calendar);g.calendar.className='calendar';
			g.startDiv=document.createElement('div');g.$startDiv=$(g.startDiv);g.startDiv.className='start';
			g.endDiv=document.createElement('div');g.$endDiv=$(g.endDiv);g.endDiv.className='end';
			g.control=document.createElement('div');g.$control=$(g.control);g.control.className='control';
			g.$calendar.append(g.startDiv).append(g.endDiv).append('<span style="clear:both;"></span>');
			g.$container.append(g.shortcuts).append(g.calendar).append(g.control).appendTo('body');
			g.createShortcuts();
			g.createControl();
			g.setRange(o.time);
			g.width=g.$container.width();
			g.height=g.$container.height();
			$(window).resize(g.resize).resize();
		}
		,resize:function(){
			var w=$('body').width(),h=$('body').height();
			if(w<g.width){
				g.$container.css({width:w-g.$container.position().left});
			}else{
				g.$container.css({width:g.width});
			}
			if(h<g.height){
				g.$container.css({height:h-g.$container.position().top});
			}else{
				g.$container.css({width:'auto'});
			}
		}
		,createCanlendar:function(fn){
			var A=[{i:0,c:g.$startDiv,d:g.start},{i:1,c:g.$endDiv,d:g.end}];
			$(A).each(function(){
				var I=this.i,d=this.d,$c=this.c;
				var Y=d.getFullYear(),M=d.getMonth(),D=d.getDate(),W=d.getDay(),h=d.getHours(),m=d.getMinutes(),s=d.getSeconds();
				var T=new Date,_TY=T.getFullYear(),_TM=T.getMonth(),_TD=T.getDate();
				var html=['<table cellpdding="0" cellspacing="0"><thead><tr><th class="year_prev">«</th><th class="month_prev">‹</th><th colspan="3"><span class="month">'+$.l10n.__(g.Months[M])+'</span> <span class="year">'+Y+'</span></th><th class="month_next">›</th><th class="year_next">»</th></tr><tr><th>'+$.l10n.__(g.Weeks[0])+'</th><th>'+$.l10n.__(g.Weeks[1])+'</th><th>'+$.l10n.__(g.Weeks[2])+'</th><th>'+$.l10n.__(g.Weeks[3])+'</th><th>'+$.l10n.__(g.Weeks[4])+'</th><th>'+$.l10n.__(g.Weeks[5])+'</th><th>'+$.l10n.__(g.Weeks[6])+'</th></tr></thead><tbody>'];
				for(i=1;i<=32;i++){
					var _d=new Date(Y+'/'+(M+1)+'/'+i+' '+h+':'+m+':'+s);
					var _Y=_d.getFullYear();
					var _M=_d.getMonth();
					var _D=_d.getDate();
					var _W=_d.getDay();
					if(_M!==M){
						for(var j=_W;j<7;j++){
							html.push('<td></td>');
						}
						html.push('</tr>');
						break;
					}
					if(i===1){
						html.push('<tr>');
						for(var j=0;j<_W;j++){
							html.push('<td></td>');
						}
					}
					html.push('<td class="'+(_Y===Y&&_M===M&&_D===D?'selected ':'')+(_Y===_TY&&_M===_TM&&_D===_TD?'today ':'')+'">'+i+'</td>');
					if(_W===6)html.push('</tr>');
				}
				html.push('</tbody><tfoot><th colspan="7"><input type="number" class="h" value="'+h+'"/> : <input type="number" class="m" value="'+m+'"/> : <input type="number" class="s" value="'+s+'"/></th></tfoot></table>');
				$c.html(html.join(''));
				$c[0].date=d;				
				$('td,.year_prev,.year_next,.month_prev,.month_next,input',$c).mousedown(function(){
					$c[0].date=d;
				});
				$('td',$c).click(function(){
					$('td.selected',$c).removeClass('selected');
					$(this).addClass('selected');
					if(this.innerHTML!==''){
						d.setDate(this.innerHTML-0);
						g.set();
					}
				});
				$('.year_prev',$c).click(function(){
					d.setFullYear(Y-1);
					g.createCanlendar();
				});
				$('.year_next',$c).click(function(){
					d.setFullYear(Y+1);
					g.createCanlendar();
				});
				$('.month_prev',$c).click(function(){
					d.setMonth(M-1);
					g.createCanlendar();
				});
				$('.month_next',$c).click(function(){
					d.setMonth(M+1);
					g.createCanlendar();
				});
				$('input',$c).change(function(e){
					var $this=$(this);
					if(!/\d+/.test($this.val()))$this.val('00');
					if($this.hasClass('h')){
						if($this.val()-0>24)$this.val('00');
						if($this.val()-0<0)$this.val('23');
						d.setHours($this.val()-0);
					}else{
						if($this.val()-0>60)$this.val('00');
						if($this.val()-0<0)$this.val('59');
						if($this.hasClass('m'))d.setMinutes($this.val()-0);
						else d.setSeconds($this.val()-0);
					}
					if($this.val()<10)$this.val('0'+$this.val());				
					g.set();
				});
			});
			g.set();
			if(fn)fn();
		}
		,createShortcuts:function(){
			var html=[];
			var s=g.Shortcuts;
			for(var i=0,l=s.length;i<l;i++){
				var si=s[i];
				html.push('<a time="'+si+'" class="'+si+'" domain="l10n" msgid="'+si+'">'+$.l10n.__(si)+'</a>');
			}
			g.$shortcuts.html(html.join(''));
			$('a',g.$shortcuts).click(function(){g.setRange($(this).attr('time'));});
		}
		,createControl:function(){
			var html='<button domain="l10n" msgid="OK" class="OK">'+$.l10n.__('OK')+'</button> <button domain="l10n" msgid="Close" class="Close">'+$.l10n.__('Close')+'</button>';
			g.$control.html(html);
			$('button.Close',g.$control).click(function(){g.$container.hide();});
			$('button.OK',g.$control).click(g.callback);
		}
		,setRange:function(r){
			g.start=new Date();
			g.end=new Date();
			g.start.setHours(0);
			g.start.setMinutes(0);
			g.start.setSeconds(0);
			g.start.setMilliseconds(0);
			g.end.setHours(0);
			g.end.setMinutes(0);
			g.end.setSeconds(0);
			g.end.setMilliseconds(0);
			switch(r){
				case 'Today':
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Yesterday':
					g.start.setDate(g.start.getDate()-1);
				break;
				case 'ThisWeek':
					var W=g.start.getDay();
					g.start.setDate(g.start.getDate()-W);
					g.end.setDate(g.end.getDate()+(7-W));
				break;
				case 'LastWeek':
					var W=g.start.getDay();
					g.start.setDate(g.start.getDate()-W-7);
					g.end.setDate(g.end.getDate()-W);
				break;
				case 'ThisMonth':
					g.start.setMonth(g.start.getMonth());
					g.start.setDate(1);
					g.end.setMonth(g.start.getMonth()+1);
					g.end.setDate(1);
				break;
				case 'LastMonth':
					g.start.setMonth(g.start.getMonth()-1);
					g.start.setDate(1);
					g.end.setMonth(g.end.getMonth());
					g.end.setDate(1);
				break;
				case 'Last3Days':
					g.start.setDate(g.start.getDate()-2);
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Last5Days':
					g.start.setDate(g.start.getDate()-4);
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Last7Days':
					g.start.setDate(g.start.getDate()-6);
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Last1Weeks':
					var W=g.start.getDay();
					g.start.setDate(g.start.getDate()-W-7);
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Last2Weeks':
					var W=g.start.getDay();
					g.start.setDate(g.start.getDate()-W-14);
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Last3Weeks':
					var W=g.start.getDay();
					g.start.setDate(g.start.getDate()-W-21);
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Last1Month':
					g.start.setMonth(g.start.getMonth()-1);
					g.start.setDate(1);
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Last2Month':
					g.start.setMonth(g.start.getMonth()-2);
					g.start.setDate(1);
					g.end.setDate(g.end.getDate()+1);
				break;
				case 'Last3Month':
					g.start.setMonth(g.start.getMonth()-3);
					g.start.setDate(1);
					g.end.setDate(g.end.getDate()+1);
				break;
			}
			g.createCanlendar(g.callback);
		}
		,set:function(){
			g.start=g.startDiv.date;
			g.end=g.endDiv.date;
			var d=g.start;
			var Y=new Date().getFullYear();
			var s_Y=d.getFullYear();
			var s_M=d.getMonth();s_M=g.pad(s_M+1);
			var s_D=d.getDate();s_D=g.pad(s_D);
			var s_h=d.getHours();s_h=g.pad(s_h);
			var s_m=d.getMinutes();s_m=g.pad(s_m);
			var s_s=d.getSeconds();s_s=g.pad(s_s);
			var html='<div class="DateTime"><button class="choose" domain="l10n" msgid="ChooseTimeSpan">'+$.l10n.__('ChooseTimeSpan')+'</button>';
			html+=' <span class="start"><label class="Y"><b>'+s_Y+'</b><select></select></label>/<label class="M"><b>'+s_M+'</b><select></select></label>/<label class="D"><b>'+s_D+'</b><select></select></label> <label class="h"><b>'+s_h+'</b><select></select></label>:<label class="m"><b>'+s_m+'</b><select></select></label>:<label class="s"><b>'+s_s+'</b><select></select></label></span> - ';
			//html+= '</div>';
			d=g.end;
			var e_Y=d.getFullYear();
			var e_M=d.getMonth();e_M=g.pad(e_M+1);
			var e_D=d.getDate();e_D=g.pad(e_D);
			var e_h=d.getHours();e_h=g.pad(e_h);
			var e_m=d.getMinutes();e_m=g.pad(e_m);
			var e_s=d.getSeconds();e_s=g.pad(e_s);
			html+=' <span class="end"><label class="Y"><b>'+e_Y+'</b><select></select></label>/<label class="M"><b>'+e_M+'</b><select></select></label>/<label class="D"><b>'+e_D+'</b><select></select></label> <label class="h"><b>'+e_h+'</b><select></select></label>:<label class="m"><b>'+e_m+'</b><select></select></label>:<label class="s"><b>'+e_s+'</b><select></select></label></span>';
			html+= '</div>';
			//<span class="calendar"></span>
			$(t).html(html);
			for(var i=Y+1;i>=Y-20;i--){
				$('.DateTime .start .Y select',t).append('<option value="'+i+'" '+(i===s_Y-0?'selected="selected"':'')+'>'+i+'</option>');
				$('.DateTime .end .Y select',t).append('<option value="'+i+'" '+(i===e_Y-0?'selected="selected"':'')+'>'+i+'</option>');
			}
			for(var i=1;i<13;i++){
				$('.DateTime .start .M select',t).append('<option value="'+g.pad(i)+'" '+(i===s_M-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
				$('.DateTime .end .M select',t).append('<option value="'+g.pad(i)+'" '+(i===e_M-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
			}
			for(var i=1;i<32;i++){
				$('.DateTime .start .D select',t).append('<option value="'+g.pad(i)+'" '+(i===s_D-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
				$('.DateTime .end .D select',t).append('<option value="'+g.pad(i)+'" '+(i===e_D-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
			}
			for(var i=0;i<23;i++){
				$('.DateTime .start .h select',t).append('<option value="'+g.pad(i)+'" '+(i===s_h-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
				$('.DateTime .end .h select',t).append('<option value="'+g.pad(i)+'" '+(i===e_h-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
			}
			for(var i=0;i<60;i++){
				$('.DateTime .start .m select',t).append('<option value="'+g.pad(i)+'" '+(i===s_m-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
				$('.DateTime .end .m select',t).append('<option value="'+g.pad(i)+'" '+(i===e_m-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
			}
			for(var i=0;i<60;i++){
				$('.DateTime .start .s select',t).append('<option value="'+g.pad(i)+'" '+(i===s_s-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
				$('.DateTime .end .s select',t).append('<option value="'+g.pad(i)+'" '+(i===e_s-0?'selected="selected"':'')+'>'+g.pad(i)+'</option>');
			}
			$('.DateTime .calendar,.DateTime .choose',t).click(function(){
				g.$container.show().css({left:t.offsetLeft,top:t.offsetTop+20});
			});
			$('.DateTime select',t).change(function(){
				$(this).prev().text(this.value);
			});
			$('.end .Y select',t).change(function(){
				g.end.setYear(this.value);				
				g.createCanlendar();
			});
			$('.end .M select',t).change(function(){
				g.end.setMonth(this.value-1);				
				g.createCanlendar();
			});
			$('.end .D select',t).change(function(){
				g.end.setDate(this.value);				
				g.createCanlendar();
			});
			$('.end .h select',t).change(function(){
				g.end.setHour(this.value);				
				g.createCanlendar();
			});
			$('.end .m select',t).change(function(){
				g.end.setMinute(this.value);				
				g.createCanlendar();
			});
			$('.end .s select',t).change(function(){
				g.end.setSeconds(this.value);				
				g.createCanlendar();
			});
			$('.start .Y select',t).change(function(){
				g.start.setYear(this.value);				
				g.createCanlendar();
			});
			$('.start .M select',t).change(function(){
				g.start.setMonth(this.value-1);				
				g.createCanlendar();
			});
			$('.start .D select',t).change(function(){
				g.start.setDate(this.value);				
				g.createCanlendar();
			});
			$('.start .h select',t).change(function(){
				g.start.setHour(this.value);				
				g.createCanlendar();
			});
			$('.start .m select',t).change(function(){
				g.start.setMinute(this.value);				
				g.createCanlendar();
			});
			$('.start .s select',t).change(function(){
			console.log(this.value);
				g.start.setSeconds(this.value);				
				g.createCanlendar();
			});
		}
		,datetime:function(dash){
			var d=g.start;
			var Y=d.getFullYear();
			var M=d.getMonth();M=g.pad(M+1);
			var D=d.getDate();D=g.pad(D);
			var h=d.getHours();h=g.pad(h);
			var m=d.getMinutes();m=g.pad(m);
			var s=d.getSeconds();s=g.pad(s);
			var sd=Y+'-'+M+'-'+D+' '+h+':'+m+':'+s;
			d=g.end;
			Y=d.getFullYear();
			M=d.getMonth();M=g.pad(M+1);
			D=d.getDate();D=g.pad(D);
			h=d.getHours();h=g.pad(h);
			m=d.getMinutes();m=g.pad(m);
			s=d.getSeconds();s=g.pad(s);
			var ed=Y+'-'+M+'-'+D+' '+h+':'+m+':'+s;
			return {from:sd, to:ed, start:g.start, end:g.end};
		}
		,pad:function(n){
			if(n<10)return '0'+n;
			return n;
		}
		,callback:function(){
			g.$container.hide();
			if(o.callback)o.callback();
		}
	}
	t.datetime=g.datetime;
	g.init();
	return g;
};
$.fn.DateTimePicker = function(o) {
	if(!o)o={};
	return this.each( function() {
		$.addDateTimePicker(this,o);
	});
}; 
})(jQuery);