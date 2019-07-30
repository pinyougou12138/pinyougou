var app = new Vue({
    el: "#histogram",
    data: {
        activeuser:[],

    },
    methods:{

        selectActiveUser(){
            axios("/user/findActiveUsers.shtml").then(function (response) {
                if (response.data.success) {
                    var obj = JSON.parse(response.data.message);
                    var activeuser=[];
                    for (var key in obj) {
                        activeuser.push(obj[key]);
                    }
                    app.activeuser=activeuser;

                    // 基于准备好的dom，初始化echarts实例
                    var histogram = echarts.init(document.getElementById('histogram'));
                    // 指定图表的配置项和数据
                    histogram.setOption({
                        title: {
                            text: '用户月活跃量'
                        },
                        tooltip: {},
                        legend: {
                            data: ['用户月活跃量']
                        },
                        xAxis: {
                            data: ["活跃","不活跃"]
                        },
                        yAxis: {},
                        series: [{
                            name: '活跃量',
                            type: 'bar',
                            data: activeuser
                        }]
                    });
                }
            })


        },
    },
    mounted(){
        this.selectActiveUser();

    },
});