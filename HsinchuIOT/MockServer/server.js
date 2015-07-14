var http = require('http')

 var os = require('os')

 var config = readConfig()

 var querystring = require('querystring')

 var encodingMapping = {
 	"text/plain": "utf-8",
 	"text/html": "utf-8",
 	"text/xml": "utf-8",
 	"application/json": "utf-8",
 	"image/png": "binary",
 	"image/jpeg": "binary"
 }

 String.prototype.endWith = function(str){
 	var reg = new RegExp(str + "$");
 	return reg.test(this);
 }

 sleep = function(milliSecond){
 	var start = new Date().getTime() + milliSecond
 	while(new Date().getTime() < start){}
 }

 http.createServer(function (request, response){ 

 	console.log("--received request:" +request.url +", method:" +request.method + " from client:" + request.connection.remoteAddress);
	
	if (request.method == "POST" || request.method == "PUT"){
		if (request.url.endWith(".png")){
			var formidable = require('formidable');
			var form = new formidable.IncomingForm();
			form.parse(request, function(err, fields, files){
				for(var key in files){
					console.log("receive upload file:" + key);
					renameFile(files[key]["path"], "image/" + files[key]["name"]);
					console.log("save file to image/" + files[key]["name"]);
				}
			});
		}else{
			var postData;
			request.addListener("data", function(dataChunk){
				console.log("----request data: " + dataChunk )
				postData += dataChunk
			});

			request.addListener("end", function(dataChunk){
			
				//console.log("----sent data:"+ querystring.parse(postData).text);
			});	
		}
		
		
	}else if (request.method == "GET" || request.method == "DELETE"){
		var url = require('url')
		var params = url.parse(request.url,true).query

		console.log("----request parameter: " + JSON.stringify(params))
	}
 	var methodMapping = config[request.method]
 	if (methodMapping){
 		var urlMapping = methodMapping[request.url]
 		if (urlMapping){
 			console.log("----response code:" + urlMapping.responseCode);
 			console.log("----response header:" + JSON.stringify(urlMapping.headers));

 			response.writeHead(urlMapping.responseCode, urlMapping.headers);

 			var encoding = encodingMapping[urlMapping.headers["Content-Type"]];
 			if(encoding == undefined){
 				encoding = "utf-8";
 			}
 			console.log("---encoding:" + encoding);

 			var responseText = urlMapping["response"];
 			if(responseText ==  undefined) {
 				responseText = readResponseText(urlMapping["responseFile"], encoding);
 			}
 			console.log("----response body:" + responseText)

 			if(urlMapping.delayedMS != undefined){
 				sleep(urlMapping.delayedMS)
 			}
 			response.write(responseText, encoding)
 			response.end();
 		}else{
 			response.writeHead(404,{'Content-Type': "text/plain"});
 			response.end("Not Found");
 
 		}
 	}else{
 		response.writeHead(405,{'Content-Type': "text/plain"});
 		response.end("Method:" +request.method + " Not Allowed");
 	}
 }).listen(8088);

 console.log('Server running at http://10.99.73.148:80/');

 function readConfig(){

 	var result = new Object();
 	var fs = require('fs');
 	return JSON.parse(fs.readFileSync('config.json'));
 }

 function readResponseText(filename, encoding){
 	var fs = require('fs');
 	try{
 		return fs.readFileSync(filename, encoding);
 	}catch(exp){
 		console.error(exp);
 	}
 }

 function renameFile(srcFilename, destFilename){
 	var fs = require('fs');
 	try{
 		fs.rename(srcFilename, destFilename);
 	}catch(exp){
 		console.error(exp);
 	}
 }