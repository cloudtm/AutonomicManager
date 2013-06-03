<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="MyCloud.message"/></title>
    
    <style type="text/css">
    	p {
    		display: inline;
    	}
    </style>
    
    <script type="text/javascript">
    $(document).ready( 
    		
    		function(){
    			Retrieve();    		    			
    		}    		
    );
    
    var Polling = function(){
    	setTimeout(Retrieve, 5000);
    }
    
    var Retrieve = function(){
    	$.ajax({
            type: 'GET',
            crossDomain:true,
            dataType: 'json',
            url: 'http://46.252.152.83:9998/status',
            success: function (data) {
            	Update(data);
            },
            error: function (request, status, error) {
//            	setTimeout(function(){
//            		document.location.reload(true);	
//            	}, 5000);
		console.log(status);
		console.log(error);            	
            },
            complete: Polling
        });    	
    }
    
/**
{
   "status":"WORKING",
   "scale":{
      "small":0,
      "medium":0,
      "large":0,
      "type":"AUTO",
      "method":"ANALYTICAL"
   },
   "replicationProtocol":{
      "protocol":"TWOPC",
      "type":"AUTO",
      "method":"ANALYTICAL"
   },
   "replicationDegree":{
      "degree":2,
      "type":"AUTO",
      "method":"ANALYTICAL"
   },
   "dataPlacement":{
      "type":"AUTO",
      "method":"ANALYTICAL"
   }
}
*/
    
    var Update = function(json) {
    	console.log(json); 
		
		$("span#status").text(json.status);
		
		
		/* SCALE */
		$("p#scale_tuning").text($.trim(json.scale.type) + " + " + $.trim(json.scale.method));
		$("p#scale_conf").text( $.trim(json.scale.small) + "S");
		
		$("p#rep_degree_tuning").text( $.trim(json.replicationDegree.type) + " + " + $.trim(json.replicationDegree.method) );
		$("p#rep_degree_conf").text( $.trim(json.replicationDegree.degree) );
		
		$("p#rep_prot_tuning").text( $.trim(json.replicationProtocol.type) + " + " + $.trim(json.replicationProtocol.method) );
		$("p#rep_prot_conf").text( $.trim(json.replicationProtocol.protocol) );
		
		$("p#placement_tuning").text( $.trim(json.dataPlacement.type) + " + " + $.trim(json.dataPlacement.method) );
		
	};

    </script>
    
</head>

<body>
	<!-- Promo -->
	<div id="col-top"></div>
	<div class="box" id="col">
    
    <div id="ribbon"></div> <!-- /ribbon (design/ribbon.gif) -->
        
    <!-- Screenshot in browser (replace tmp/browser.gif) -->
    <div id="col-browser"></div> 
    
  	<div id="col-text">
        
        <h2><s:property value="message"/></h2>
        <h3>Status: <span style="display: inline;" id="status"></span></h3>
        
        <!-- <h2 id="slogan"><span><s:property value="message"/></span></h2> -->
	   				
	   				
	   				
		<table id="box-table-a" summary="Employee Pay Sheet">
		    <thead>
		    	<tr>
		        	<th scope="col">Autotuned Feature</th>
		            <th scope="col">Status</th>
		            <th scope="col">Current</th>
		            <th scope="col">Optimal</th>
		        </tr>
		    </thead>
		    <tbody>
		    	<tr>
		        	<td>Scale</td>
		            <td><p id="scale_tuning"></p></td>
		            <td><p id="scale_conf"></p></td>
		            <td>OPT</td>
		        </tr>
		        <tr>
		        	<td>Replication Degree</td>
		            <td><p id="rep_degree_tuning"></p></td>
		            <td><p id="rep_degree_conf"></p></td>
		            <td>OPT</td>
		        </tr>
		        <tr>
		        	<td>Protocol Switching</td>
		            <td><p id="rep_prot_tuning"></p></td>
		            <td><p id="rep_prot_conf"></p></td>
		            <td>OPT</td>
		        </tr>
		        <tr>
		        	<td>Data Placement</td>
		            <td><p id="placement_tuning"></p></td>
		            <td>--</td>
		            <td>--</td>
		        </tr>
		    </tbody>
		</table>
			   				
	   				
	   				
	   				
      	<a href="${pageContext.request.contextPath}/registration.jsp">Register</a>
		

	</div> <!-- /col-text -->
    
    </div> <!-- /col -->
    <div id="col-bottom"></div>
    
    <hr class="noscreen">
    <hr class="noscreen">        
</body>
</html>



