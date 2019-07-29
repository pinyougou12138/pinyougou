var app = new Vue({
    el: "#myChart",
    data: {
        sellerIds:[],
        sellers:[],
    },
    methods:{
        drawLine(){
            axios("/user/paymentGroupBySellerId.shtml").then(function (response) {
                if (response.data.success) {
                    var obj = JSON.parse(response.data.message);
                    var sellerIds=[];
                    var sellers=[];
                    for (var key in obj) {
                        sellerIds.push(key);
                        sellers.push({value: obj[key], name: key});
                    }
                    app.sellerIds=sellerIds;
                    app.sellers=sellers;
                    console.log(sellerIds)
                    // 基于准备好的dom，初始化echarts实例
                    var myChart = echarts.init(document.getElementById('myChart'));
                    // 绘制图表
                    myChart.setOption( {
                        title: {
                            text: '商家销售额占比图',
                            subtext: '纯属虚构',
                            x: 'center'
                        },
                        tooltip: {

                            trigger: 'item',
                            formatter: "{a} <br/>{b} : {c} ({d}%)"
                        },
                        legend: {
                            orient: 'vertical',
                            x: 'left',
                            // data: ['穿越火线', '英雄联盟', 'QQ飞车', '魔兽世界', '刺激战场']
                            data: sellerIds
                        },
                        toolbox: {
                            show: true,
                            feature: {
                                mark: {show: true},
                                dataView: {show: true, readOnly: false},
                                magicType: {
                                    show: true,
                                    type: ['pie', 'funnel'],
                                    option: {
                                        funnel: {
                                            x: '25%',
                                            width: '50%',
                                            funnelAlign: 'left',
                                            max: 1548
                                        }
                                    }
                                },
                                restore: {show: true},
                                saveAsImage: {show: true}
                            }
                        },
                        calculable: true,
                        series: [
                            {
                                name: '访问来源',
                                type: 'pie',
                                radius: '55%',
                                center: ['50%', '60%'],
                                data:app.sellers
                                // data: [
                                //     {value: 335, name: '穿越火线'},
                                //     {value: 310, name: '英雄联盟'},
                                //     {value: 234, name: 'QQ飞车'},
                                //     {value: 135, name: '魔兽世界'},
                                //     {value: 1548, name: '刺激战场'}
                                // ]
                            }
                        ]
                    });
                }
            });

        }
    },
    mounted(){
        this.drawLine();
    },
});