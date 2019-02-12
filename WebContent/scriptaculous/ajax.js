function doAjaxRequest(objForm, qs) {
	var url = '../servlet/lebah.servlets.AjaxRequestServlet';
    var theForm = objForm;
	var pars = '_d=_d';
	var count = theForm.elements.length;
	var readparam = false;
	var result = "";
	
	for ( var i=0; i < count; i++) {
	   
	   if ( theForm.elements[i].type != "button" ) {
	   
		   readparam = false;
		   if ( theForm.elements[i].type == "checkbox" || theForm.elements[i].type == "radio" ) {
			  if ( theForm.elements[i].checked ) {
			     readparam = true;
			  } 
		   } else {
		   	  readparam = true;
		   }
		  
		   if ( readparam ) {
		  	   var id = theForm.elements[i].id;
			   var name = theForm.elements[i].name;
			   if ( name == '' ) theForm.elements[i].name = id;
			   if ( id == '' ) theForm.elements[i].id = name;
		       name = theForm.elements[i].name;
			   var value = theForm.elements[i].value;
		       pars = pars + '&' + name + '=' + encodeURIComponent(value);
	       }
       
       }
	}
	pars = pars + '&' + qs;
	//alert(pars);
    new Ajax.Request(url, {
    						method: 'post', 
							parameters: pars,
							contentType: 'application/x-www-form-urlencoded',  
							encoding: 'UTF-8', 
							asynchronous: false,
							evalScripts: true, 
			  				onComplete: function(t) {
			  				  	result = t.responseText;
							},
				            on404: function(t) {
								alert('error 404');
				            },
				            onFailure: function(t) {
								alert('failure get response');
				            }     											
						});
    return result;
}

function doAjaxSave(myForm, actionName, qs) {
	/*
	myForm.command.value = actionName;
	
    var theForm = objForm;
	var pars = '_d=d';
	var count = theForm.elements.length;
	theForm.command.value = actionName;
	for ( var i=0; i < count; i++){
	   var id = theForm.elements[i].id;
	   var name = theForm.elements[i].name;
	   if ( name == '' ) theForm.elements[i].name = id;
	   if ( id == '' ) theForm.elements[i].id = name;
       name = theForm.elements[i].name;
	   var value = theForm.elements[i].value;
       pars = pars + '&' + name + '=' + escape(value);
	}
	*/
	
    myForm.request({
        parameters: qs,
        onFailure: function() { alert('Ajax form submission FAILED!'); },
        onSuccess: function(t) {
            //just saving state
        },
		on404: function(t) {
			alert('error 404');
		} 
    });

}



function doAjaxSubmit(myForm, target, actionName) {
	myForm.command.value = actionName;
	alert(myForm.command.value);
    myForm.request({
        onFailure: function() { alert('Ajax form submission FAILED!'); },
        onSuccess: function(t) {
            document.getElementById(target).innerHTML = t.responseText;
        },
		on404: function(t) {
			alert('error 404');
		} 
    });

}

function doAjaxSubmit(myForm, target, actionName, qs) {
	qs += "&command=" + actionName;
    myForm.request({
        parameters: qs,
        onFailure: function() { alert('Ajax form submission FAILED!'); },
        onSuccess: function(t) {
            document.getElementById(target).innerHTML = t.responseText;
        },
		on404: function(t) {
			alert('error 404');
		} 
    });

}

function doAjaxSubmitChain(myForm, target, actionName, qs, target2, actionName2, qs2) {

	myForm.command.value = actionName;
    myForm.request({
        parameters: qs,
        onFailure: function() { alert('Ajax form submission FAILED!'); },
        onSuccess: function(t) {
            document.getElementById(target).innerHTML = t.responseText;
            doAjaxSubmit(myForm, target2, actionName2, qs2);
        },
		on404: function(t) {
			alert('error 404');
		} 
    });

}

function doAjaxUpdater(objForm, url, target, actionName) {
	onCompleteFlag = 0;
    var theForm = objForm;
	var pars = '_d=d';
	var count = theForm.elements.length;
	theForm.command.value = actionName;
	for ( var i=0; i < count; i++){
	   var id = theForm.elements[i].id;
	   var name = theForm.elements[i].name;
	   if ( name == '' ) theForm.elements[i].name = id;
	   if ( id == '' ) theForm.elements[i].id = name;
       name = theForm.elements[i].name;
	   var value = theForm.elements[i].value;
       //pars = pars + '&' + name + '=' + escape(value);
       pars = pars + '&' + name + '=' + encodeURIComponent(value);
	}
	var result;
    var myAjax = new Ajax.Updater(target, url, {
    							  				method: 'post', 
    							  				parameters: pars,
    							  				evalScripts: true,
    							  				onComplete: function(t) {
    							  				  	result = t.responseText;
            									},
									            on404: function(t) {
													alert('error 404');
									            },
									            onFailure: function(t) {
													alert('failure get response');
									            } 
    							  				});		 
}



function doAjaxUpdater(objForm, url, target, actionName, qs) {
    var theForm = objForm;
	var pars = '_d=_d';
	var count = theForm.elements.length;
	theForm.command.value = actionName;
	var readparam = false;
	
	for ( var i=0; i < count; i++) {
	   
	   if ( theForm.elements[i].type != "button" ) {
	   
		   readparam = false;
		   if ( theForm.elements[i].type == "checkbox" || theForm.elements[i].type == "radio" ) {
			  if ( theForm.elements[i].checked ) {
			     readparam = true;
			  } 
		   } else {
		   	  readparam = true;
		   }
		  
		   if ( readparam ) {
		  	   var id = theForm.elements[i].id;
			   var name = theForm.elements[i].name;
			   if ( name == '' ) theForm.elements[i].name = id;
			   if ( id == '' ) theForm.elements[i].id = name;
		       name = theForm.elements[i].name;
			   var value = theForm.elements[i].value;
		       pars = pars + '&' + name + '=' + encodeURIComponent(value);
	       }
       
       }
	}
	pars = pars + '&' + qs;
	//alert(pars);
    var myAjax = new Ajax.Updater(target, url, {
    											method: 'post', 
    											parameters: pars,
    											contentType: 'application/x-www-form-urlencoded',  
												encoding: 'UTF-8', 
    											evalScripts: true, 
    							  				onComplete: function(t) {
    							  				  	result = t.responseText;
    							  				  	
            									},
									            on404: function(t) {
													alert('error 404');
									            },
									            onFailure: function(t) {
													alert('failure get response');
									            }     											
    											});		 
}



function doAjaxUpdaterChain(objForm, url, target, actionName, qs, target2, actionName2, qs2) {
    var theForm = objForm;
	var pars = '_d=d';
	var count = theForm.elements.length;
	theForm.command.value = actionName;
	var readparam = false;
	
	for ( var i=0; i < count; i++) {
	   readparam = false;
	   if ( theForm.elements[i].type == "checkbox" || theForm.elements[i].type == "radio" ) {
		  if ( theForm.elements[i].checked ) {
		     readparam = true;
		  } 
	   } else {
	   	  readparam = true;
	   }
	  
	   if ( readparam ) {
	  	   var id = theForm.elements[i].id;
		   var name = theForm.elements[i].name;
		   if ( name == '' ) theForm.elements[i].name = id;
		   if ( id == '' ) theForm.elements[i].id = name;
	       name = theForm.elements[i].name;
		   var value = theForm.elements[i].value;
	       //pars = pars + '&' + name + '=' + escape(value);
		   pars = pars + '&' + name + '=' + encodeURIComponent(value);
       }
	}
	pars = pars + '&' + qs;
	
    var myAjax = new Ajax.Updater(target, url, {
    											method: 'post', 
    											parameters: pars,
    											evalScripts: true,
    							  				onComplete: function(t) {
    							  				  	result = t.responseText;
    							  				  	doAjaxUpdater(objForm, url, target2, actionName2, qs2);
            									},
									            on404: function(t) {
													alert('error 404');
									            },
									            onFailure: function(t) {
													alert('failure get response');
									            }     											
    											});		 
}


function numeralsOnly(evt) {
    evt = (evt) ? evt : event;
    var charCode = (evt.charCode) ? evt.charCode : ((evt.keyCode) ? evt.keyCode : 
        ((evt.which) ? evt.which : 0));
    if ( charCode == 46 || charCode== 45) return true;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
    }
    return true;
}
	
function get_page_size() {
    var xScroll, yScroll;

    if (window.innerHeight && window.scrollMaxY) {	
      xScroll = document.body.scrollWidth;
      yScroll = window.innerHeight + window.scrollMaxY;
    } else if (document.body.scrollHeight > document.body.offsetHeight){ // all but Explorer Mac
      xScroll = document.body.scrollWidth;
      yScroll = document.body.scrollHeight;
    } else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
      xScroll = document.body.offsetWidth;
      yScroll = document.body.offsetHeight;
    }

    var windowWidth, windowHeight;
    if (self.innerHeight) {	// all except Explorer
      windowWidth = self.innerWidth;
      windowHeight = self.innerHeight;
    } else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
      windowWidth = document.documentElement.clientWidth;
      windowHeight = document.documentElement.clientHeight;
    } else if (document.body) { // other Explorers
      windowWidth = document.body.clientWidth;
      windowHeight = document.body.clientHeight;
    }	
    
    // for small pages with total height less then height of the viewport
    if(yScroll < windowHeight){
      pageHeight = windowHeight;
    } else { 
      pageHeight = yScroll;
    }

    // for small pages with total width less then width of the viewport
    if(xScroll < windowWidth){	
      pageWidth = windowWidth;
    } else {
      pageWidth = xScroll;
    }

    arrayPageSize = new Array(pageWidth,pageHeight,windowWidth,windowHeight);
    return arrayPageSize;
}
	