$(function(){init();})

function init(){
	if(window.g)return;
	window.g=$.autoLogin();
}

$.autoLogin = function(){
	var g={
		init:function(){
			g.bindEvent();
		}

		,bindEvent:function(){  
			setLang();
			var u=navigator.cookieEnabled?localStorage.lastuser:'';
			var p=navigator.cookieEnabled?localStorage.lastuserpass:'';
			$('#username').val(u);
			$('#password').val(u);		
			$('#username').focus().keypress(function(e){
				if(e.which==13){ 
					if($('#password').val()===''){$('#password').focus();}
					else{g.login();}
				}
			});
			$('#password').keypress(function(e){
				if(e.which==13){g.login();}
			}); 
			$('#OK').click(function(){
				g.login();
			});
			$('#langs a').click(function(){
				$('#langs a.selected').removeClass('selected');
				$(this).addClass('selected');
				setLang({lang:$(this).attr('code')});
			});	
			$('#langs a[code="'+window.Lang+'"]').addClass('selected');
		}

		//验证
		,validate:function(){
			var n=$('#username').val().replace(/\s/g,'');
			var p=$('#password').val().replace(/\s/g,'');
			$('#username').val(n);
			$('#password').val(p);
			if(n===''||p===''){
				alert($.l10n.__('alert_username_password_error'));
				return false;
			}
			return true;
		}

		
		,login:function(){
			if(!g.validate())return;
			g.getNonce();
		}

		//获取nonce
		,getNonce:function(){
			AJAX({
				url:'/_NBI/get_session_id.lua'
				,success:function(data){
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
					username:$('#username').val()
					,mangled_password:hex_md5(hex_md5($('#password').val()) + ':' + window.top.__session_id)
					,lang:window.top.Lang
					,timezone:new Date().getTimezoneOffset()
				}
				,success:function(data){
					var username = $('#username').val();
					var super_user = $('super_user', data).text();
					var permission = $('permission', data).text();
					//var user_id = $('user_id', data).text();
					var admin_domain = $('admin_domain', data).text();
					//var timezone = 0; // 正数为西，负数为东
					var s=g.setLoginCookies(username,window.top.__session_id,admin_domain); //,timezone
					if(s===true){
						window.location='main.html'; 
					}else{
						window.location='main.html'+s; 
					}
				}
			});
		}
		
		
		,setLoginCookies:function (username,__session_id,admin_domain,timezone){
			var url='?__session_id='+__session_id+'&username='+username+'&admin_domain='+admin_domain+'&cookie=false';//+'&timezone='+timezone
			return url;
			if(!navigator.cookieEnabled){return url;}
			try{
				localStorage.setItem('IOT_lastuser',$('#username').val());
				localStorage.setItem('IOT_lastuserpass',$('#password').val());
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