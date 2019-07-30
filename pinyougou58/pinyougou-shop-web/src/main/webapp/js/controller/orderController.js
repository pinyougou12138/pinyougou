var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        time1:'',
        time2:'',
        oder:[],
        sellerOrder:[],
        totalMoney:0,
        startTime:'',
        endTime:'',
        hell:{},
        id:'',
        show:'',
        etime:'',
        ctime:'',
        status:['未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价',],
    },
    methods: {

        kk:function (id) {
            axios.get('/order/kk.shtml?startTime='+app.time1+'&endTime='+app.time2+'&id='+id).then(function (response) {
                app.show=response.data;

            })
        }
        ,
        searchList:function (curPage) {
            axios.post('/goods/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        findxl:function () {
            axios.get('/order/findxl.shtml',{
                params:{
                    startTime:this.startTime,
                    endTime:this.endTime
                }
            }).then(function (response) {

                console.log(response.data)
                app.hello=response.data;

            })

        },

        findByTime:function (curPage) {
            axios.get('/order/findByTime.shtml',{
                params:{
                    time1:this.time1,
                    time2:this.time2
                }
            }).then(function (response) {


                /*app.totalMoney=0;*/
               /* for (var i = 0;i<response.data.list.length;i++){
                  app.totalMoney+=(response.data.list[i].payment);
                }*/
                for (var i = 0;i<response.data.list.length;i++){


                  var  createTime=(response.data.list[i].createTime);
                  var endTime=(response.data.list[i].endTime);
                    app.ctime=app.convertTimeString(createTime)

                    app.etime=app.convertTimeString(endTime)

                }


              app.oder=  response.data.list
                app.convertTimeString(app.oder)
                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;


            })
        },

        findBySellerId:function (curPage) {

            axios.get('/order/findBySellerId.shtml?pageNo='+curPage).then(function (response) {

            for (var i=0;i<response.data.list.length;i++){
                response.data.list[i].createTime=app.convertTimeString(response.data.list[i].createTime);
                response.data.list[i].endTime=app.convertTimeString(response.data.list[i].endTime);

            }


                app.oder = response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            })
        },
          /*发货的方法*/
        shipments:function () {
          axios.post('/order/shipments.shtml',this.ids).then(function (response) {

if (response.data.success){


    window.location.reload();

}


          })
        },


//转换时间格式
        convertTimeString:function (jsondate) {
            if (jsondate!=null){
                jsondate=""+jsondate+"";//因为jsonDate是number型的indexOf会报错
                if (jsondate.indexOf("+") > 0) {
                    jsondate = jsondate.substring(0, jsondate.indexOf("+"));
                }
                else if (jsondate.indexOf("-") > 0) {
                    jsondate = jsondate.substring(0, jsondate.indexOf("-"));
                }
                var date = new Date(parseInt(jsondate, 10));
                var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
                var currentDate = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
                var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
                var minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
                var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
                return date.getFullYear() + "-" + month + "-" + currentDate + " " + hours + ":" + minutes + ":" + seconds;
            }
        },


    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.findBySellerId(1);
        this.searchList(1);


    }

})
