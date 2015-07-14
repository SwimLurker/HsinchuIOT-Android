$(function(){
	$('#domain_id').val(getRequest('id'));
	$('#file_type').val(getRequest('file_type'));
	$('#__session_id').val(window.top.__session_id);
	loadFile();
	//if(getRequest('root')==='true'){
		//$('#document,#upload').remove();
	//}else{
		layout();
		$('#document').change(function(){
			$('#waiting').show();
			$('form').submit();
			$('#document').val('');
		});
	//}
});


function layout(){
	$('body').layout({ 
			center__paneSelector:"#content"
		,	north__paneSelector:"#form"  
		,	north__size:30
		,	north__spacing_open:0
	}); 
}

function loadFile(id){
	NBI.getDomainFile({
		domain_id:$('#domain_id').val()
		,file_type:$('#file_type').val()
		,success:function(data){
			var path=$('FilePath',data).text()+'?_='+new Date().getTime();
			NBI.isFileExist({
				url:path
				,yes:function(){
					window.open(path,'content');
				}
			});
			
		}
	});
}

function callback(json){
	$('#waiting').hide();
	if(json.NBIError){
		if(json.NBIError)alert(json.NBIError.String.$);
	}else{
		if(json.NBIResponse.Status.$==='成功'){
			window.open(json.NBIResponse.FileName.$+'?_='+new Date().getTime(),'content');
		}
	}
}