<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />
  <link media="all" href="/library/skin/neo-default/tool.css" rel="stylesheet" type="text/css" />
  <link media="all" href="./css/nssakura-config.css" rel="stylesheet" type="text/css" />

  <script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
  <script type="text/javascript" src="js/jquery-1.9.1.js"></script>

  <title>Sakai-Spring</title>
</head>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
  <jsp:setProperty name="msgs" property="baseName" value="org.sakaiproject.nssakura.config.messages"/>
</jsp:useBean>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">
<div class="portletBody">
