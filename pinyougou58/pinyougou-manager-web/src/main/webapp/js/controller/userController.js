var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        userStatus:{Y:"正常",N:"封号","1":"正常","0":"封号"},
        searchEntity:{}
    },
    methods: {
        exportUserData:function () {
            axios.post("/user/exportUserData.shtml").then(function (response) {
                if (response.data.success) {
                    //导出成功
                    console.log(response.data.message);
                }
            })

        },
        //冻结用户
        frozenAccount:function () {
            axios.post('/user/frozenAccount.shtml').then(function (response) {
                if (response.data.success) {

                }
            }).catch(function (error) {
                console.log(error.toString())
            });
        },
        //更新用户状态
        updateUserStatus:function (status) {
            console.log(this.ids)
            axios.post('/user/updateUserStatus/'+status+'.shtml',this.ids).then(function (response) {

                if (response.data.success) {
                    app.ids=[];
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log(error.toString())
            });
        },
        searchList:function (curPage) {
            axios.post('/user/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/user/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/user/findPage.shtml',{params:{
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
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/user/add.shtml',this.entity).then(function (response) {
                console.log(response);
                //isSuccess
                if(response.data.success){//result:{ isSuccess,}
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/user/update.shtml',this.entity).then(function (response) {
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
            axios.get('/user/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/user/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        formatDate: function (value) {
            var date = new Date(value);
            var y = date.getFullYear();
            var MM = date.getMonth() + 1;
            MM = MM < 10 ? ('0' + MM) : MM;
            var d = date.getDate();
            d = d < 10 ? ('0' + d) : d;
            var h = date.getHours();
            h = h < 10 ? ('0' + h) : h;
            var m = date.getMinutes();
            m = m < 10 ? ('0' + m) : m;
            var s = date.getSeconds();
            s = s < 10 ? ('0' + s) : s;
            return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;
        }




    },
    //钩子函数 初始化了事件和
    created: function () {
        this.frozenAccount();
        this.searchList(1);


    },
    // filters: {
    //     formatDate(time) {
    //         var date = new Date(time);
    //         return formatDate(date, 'yyyy-MM-dd hh:mm');
    //     }
    // }



})
