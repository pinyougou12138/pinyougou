var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        contentList:[],
        keywords:'',
        entity:{},
        itemCat1List:[],
        itemCat2List:[],
        itemCat3List:[],
        itemCat23:[],
        id1:0,
        id2:0
    },
    methods: {

        /***
         * 二三级目录
         * @param parentId
         */
        findByParentId23:function (parentId) {
            axios.get('/itemCat/findByParentId23/'+parentId+'.shtml').then(function (response) {
                console.log(response.data);
                app.itemCat23=response.data;
            })
        },

        findByParentId3:function (parentId) {
            axios.get('/itemCat/findByParentId/'+parentId+'.shtml').then(function (response) {
                app.itemCat3List=response.data;

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        findByParentId2:function (parentId) {
            axios.get('/itemCat/findByParentId/'+parentId+'.shtml').then(function (response) {
                app.itemCat2List=response.data;

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //获取一级分类的类别的方法
        findByParentId:function (parentId) {
            axios.get('/itemCat/findByParentId/'+parentId+'.shtml').then(function (response) {
                app.itemCat1List=response.data;

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        doSearch:function () {
            window.location.href="http://localhost:18086/search.html?keywords="+encodeURIComponent(this.keywords);
        },

        findByCategoryId:function (categoryId) {
            axios.get('/content/findByCategoryId/'+categoryId+'.shtml').then(function (response) {
                app.contentList = response.data;
            })
        },

        searchList:function (curPage) {
            axios.post('/content/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/content/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/content/findPage.shtml',{params:{
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
            axios.post('/content/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/content/update.shtml',this.entity).then(function (response) {
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
            axios.get('/content/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/content/delete.shtml',this.ids).then(function (response) {
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
        this.findByCategoryId(1);
        this.findByParentId(0);
    }
})
