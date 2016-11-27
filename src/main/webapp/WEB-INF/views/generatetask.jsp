<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@page import="com.orditech.stockanalysis.catcher.enums.TaskTypeEnum" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>后台提交爬虫任务</title>

    <script src="http://cdn.bootcss.com/jquery/2.0.0/jquery.min.js"></script>
    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">

    <style type="text/css">
        .container {
            width: 270px;
            margin: 200px auto;
        }

    </style>
</head>
<body>
<div class="container">
    <div class="input-group" style="margin-bottom:20px;">
        <span class="input-group-addon" style="width:120px;">操作密匙</span>
        <input id="generateLockKey" type="text" class="form-control" style="width:150px;"/>
    </div>
    <div class="input-group">
        <div class="dropdown" style="display:inline;">
            <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown"
                    style="min-width:120px;">
                <span id="catcher" val="">爬虫类型</span>
                <span class="caret"></span>
            </button>
            <ul class="dropdown-menu ul_catcher" role="menu" aria-labelledby="dropdownMenu1" style="margin-top:12px;">
                <%for (TaskTypeEnum item : (Set<TaskTypeEnum>) request.getAttribute ("catcherSet")) { %>
                <li role="presentation" data-code="<%=item.getCode() %>">
                    <a role="menuitem" tabindex="-1" href="#"><%=item.getDesc () %>
                    </a>
                </li>
                <%} %>
            </ul>
        </div>
        <div id="btn_submit" class="btn btn-default">提交任务</div>
    </div>
</div>

<script type="text/javascript">
    $(function () {
        $(".ul_catcher li").click(function (e) {
            $("#catcher").html($(this).find("a").html());
            $("#catcher").attr("val", $(this).data("code"));
        });

        $("#btn_submit").click(function (e) {
            var par = {
                generateLockKey: $("#generateLockKey").val(),
                typeEnumCode: $("#catcher").attr("val")
            };
            $.post("generatetaskcommit.htm", par, function (data, success) {
                if (data == null) {
                    alert("提交成功！");
                    window.location.href = "/stockcurve.htm";
                }
                ;
            })
        });
    })
</script>
</body>
</html>