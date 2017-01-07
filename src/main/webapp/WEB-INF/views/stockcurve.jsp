<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>经营业绩趋势</title>

    <script src="http://cdn.bootcss.com/jquery/2.0.0/jquery.min.js"></script>
    <script src="http://cdn.bootcss.com/echarts/3.2.2/echarts.min.js"></script>
    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">

    <style type="text/css">
        .chart_container {
            width: 880px;
            height: 450px;
            margin: 50px auto;
            display: none;
        }

        #search_panel {
            width: 530px;
            margin: 250PX auto 50px auto;
        }

        #keyword {
            width: 400px;
        }

        .suggestion-list {
            list-style: none;
            position: absolute;
            width: 400px;
            background-color: #fff;
            z-index: 9999;
            top: 35px;
            left: 124px;
            padding-left: 0px;
            display: none;
            border: 1px solid #C0C0C0;
        }

        .suggestion-list li {
            border: none;
            border-bottom: 1px solid #e3e3e3;
            border-radius: 0;
            padding-top: 2px;
            padding-bottom: 2px;
            margin: 0px;
        }

        .suggestion-list li:hover {
            border: 2px solid #343434;
            background: #cccccc;
            color: #fff;
            cursor: pointer;
        }

        .suggestion-list li p {
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            font-size: 16px;
            color: #000;
            margin: 0px;
        }
    </style>
</head>
<body>
<div id="search_panel" class="input-group">
    <div class="input-group" style="width:420px;">
        <span class="input-group-addon">股票姓名或代码</span>
        <input id="keyword" class="form-control" type="text" placeholder="输入公司名或交易代码，我们将为您自动搜索" value=""/>
    </div>
    <ul class="suggestion-list" id="searchResultPanel" style="display: none;"></ul>
</div>
<div id="toi_chart" class="chart_container"></div>
<div id="mp_chart" class="chart_container"></div>
<div id="sgpr_chart" class="chart_container" style="width:850px;padding-left:20px;"></div>
<div id="trade_chart" class="chart_container" style="width:850px;padding-left:20px;"></div>


<script type="text/javascript">
    //保存由服务器获取的数据
    var srcData = {};

    /**
     * 获取经营数据
     * @param code 股票代码
     */
    function ajaxGetCurveData(code) {
        $.post(
                'curvedata.htm',
                {code: code},
                function (data, status) {
                    if (status == 'success' && data) {
                        srcData = JSON.parse(data);
                        generateBusinessChart('toi_chart', '营业收入（万元）', srcData['toiMap'], 10000);
                        generateBusinessChart('mp_chart', '净利润（万元）', srcData['mpMap'], 10000);
                        generateBusinessChart('sgpr_chart', '销售毛利率', srcData['sgprMap']);
                        generateTradeChart("trade_chart", '近期交易详情', srcData['tradeList']);
                    } else {
                        alert("抱歉，服务器出错了，没有找到您要的数据！");
                    }
                });
    }

    /**
     * 将原始数据提取到需要展示的数组中
     * @param src 原始数据
     * @param xData 接受运算结果，做为x轴的值
     * @param xDatas 接受运算结果，做为折线的值
     * @param divisor 单位转换除数
     */
    function extraData(src, xData, sDatas, divisor) {
        //提取年份，并默认升序排序
        var tmt = {};
        for (attr in src) {
            tmt[attr.substring(0, 4)] = true;
        }
        var i = 0;
        for (attr in tmt) {
            xData[i++] = attr;
        }
        xData.sort();

        //按季度提取每年的数据，无则返回null
        var quarters = ['03-31', '06-30', '09-30', '12-31'];
        for (var i = 0; i < 4; i++) {
            var data = [];
            for (var j = 0; j < xData.length; j++) {
                var d = src[xData[j] + '-' + quarters[i]];
                data[j] = d == undefined ? null : (divisor ? d['amount'] / divisor : d['amount']);
            }
            sDatas[i] = data;
        }
    }

    /**
     * 绘制业绩chart
     * @param text 图表标题
     * @param srcData 绘图数据
     * @param divisor 单位转换除数
     */
    function generateBusinessChart(container, text, srcData, divisor) {
        var xData = [];
        var sDatas = [];
        extraData(srcData, xData, sDatas, divisor);

        var myChart = echarts.init(document.getElementById(container));

        var option = {
            title: {
                text: text
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['一季度', '二季度', '三季度', '四季度']
            },
            toolbox: {
                feature: {
                    saveAsImage: {}
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    boundaryGap: true,
                    data: xData
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ],
            series: [
                {
                    name: '一季度',
                    type: 'line',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: sDatas[0]
                },
                {
                    name: '二季度',
                    type: 'line',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: sDatas[1]
                },
                {
                    name: '三季度',
                    type: 'line',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: sDatas[2]
                },
                {
                    name: '四季度',
                    type: 'line',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: sDatas[3]
                }
            ]
        };

        myChart.setOption(option);

        $('#' + container).show();
    }

    /**
     * 绘制交易chart
     * @param text 图表标题
     * @param srcData 绘图数据
     * @param divisor 单位转换除数
     */
    function generateTradeChart(container, text, srcData) {
        var xData = [];
        var sDatas = [];

        if (srcData != null && srcData.length > 0) {
            var end = srcData.length - 1;
            sDatas[0] = [];
            sDatas[1] = [];
            for (var i = 0; i <= end; i++) {
                var dt = srcData[end - i];
                xData[i] = dt.date;
                sDatas[0][i] = dt.tradeVolume;
                sDatas[1][i] = dt.endPrice;
            }
        }

        var myChart = echarts.init(document.getElementById(container));

        var option = {
            title: {
                text: text
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['交易量', '收盘价格']
            },
            toolbox: {
                feature: {
                    saveAsImage: {}
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    boundaryGap: true,
                    data: xData
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name: '交易量'
                },
                {
                    type: 'value',
                    name: '收盘价格'
                }
            ],
            series: [
                {
                    name: '交易量',
                    type: 'line',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    data: sDatas[0]
                },
                {
                    name: '收盘价格',
                    type: 'line',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    yAxisIndex: 1,
                    data: sDatas[1]
                }
            ]
        };

        myChart.setOption(option);

        $('#' + container).show();
    }

    /**
     * 根据关键字查询股票信息
     * @param keyword
     */
    function ajaxGetSuggesstStocks(keyword) {
        $.post(
                'stocksearch.htm',
                {keyword: keyword},
                function (data, status) {
                    if (status == 'success') {
                        showSuggesstStocks(JSON.parse(data));
                    } else {
                        showSuggesstStocks(null)
                    }
                });
    }

    /**
     * 展示股票建议列表
     * @param data
     */
    function showSuggesstStocks(list) {
        if (list) {
            var html = '';
            $.each(list, function (i, data) {
                if (!data.name)
                    return;
                html += '<li data_index="' + i + '" stock_name="' + data.name +
                        '" stock_code="' + data.code + '"><p>股票名称：' + data.name + '，交易代码：' + data.code + '</p></li>';
            });
            $('#searchResultPanel').empty();
            $('#searchResultPanel').html(html);
            $('#searchResultPanel li').click(function (e) {
                var index = $(this).attr('data_index');
                var data = list[index];
                $('#keyword').val(data.name);
                //将搜索框上移到固定位置
                if ($('#search_panel').css('margin-top') != '50px') {
                    $('#search_panel').animate({marginTop: '50px'});
                }
                ajaxGetCurveData(data.code);
            });
            $('#searchResultPanel').css("display", "block");
        } else {
            $('#searchResultPanel').css("display", "none");
        }
    }

    $('#keyword').bind('input', function (e) {
        if ($(this).val().length == 0) {
            //获取建议公司格力电器
            ajaxGetSuggesstStocks('000651');
        } else {
            //根据输入获取公司信息
            ajaxGetSuggesstStocks($(this).val());
        }
    });

    $('#keyword').focus(function () {
        $('#keyword').trigger('input');
    });

    $('body').bind('click', function (e) {
        if (!e)
            var e = window.event;
        if (e.srcElement) {
            var a = e.srcElement.getAttribute("id");
        } else {
            var a = e.target.getAttribute("id");
        }
        if (a != 'keyword') {
            $('#searchResultPanel').css("display", "none");
        }
    });
</script>
</body>
</html>