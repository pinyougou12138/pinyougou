var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        sta:['未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'],
        totalOrder:'',
        totalUsers:'',
        searchEntity:{}
    },
    methods: {
        countTotalUsers:function () {
            axios.post("/user/countTotalUsers.shtml").then(function (response) {
                if (response.data) {
                    app.totalUsers=response.data.message
                }
            })
        },

        searchList:function (curPage) {
                axios.post('/orderManger/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/orderManger/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                //将list集合长度赋值为totalOrder
                app.totalOrder=response.data.length;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/orderManger/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },

        update:function () {
            axios.post('/orderManger/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/orderManger/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/orderManger/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);
        this.findAll();
        this.countTotalUsers();
    }

})
