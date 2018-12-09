<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>marker</title>
</head>
<body>
<#--注释学习ffreemarker-->
<h1>${name}:${message}</h1>
<#assign Name="abc"/>
输出:${Name}
<br>
<#assign info={"a":"张","b":"三"}>
输出:${info.a}, ${info.b};
<br>
<#include "header.ftl">
<hr>
<#assign bool=true>
<#if bool>输出a
<#else>输出b
</#if>
</body>
</html>