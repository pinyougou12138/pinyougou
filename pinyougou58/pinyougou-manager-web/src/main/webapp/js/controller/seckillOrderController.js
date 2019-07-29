var app = new Vue({
    el:"#app",
    data:{
        pages:15,
        pageNo:1,
        searchEntity:{},
        status:['未审核','已审核','审核未通过','已关闭'],
        orderList:[],
        ids:[]
    },
    methods:{

        /***
         * 查询所有订单
         */
        findPage:function () {
            var that = this;
            axios.get('/order/findPage.shtml',{params:{
                    pageNo:this.pageNo
                }}).then(function (response) {
                //赋值
               app.orderList=response.data.list;
               app.pages=response.data.pages;
               app.pageNo=response.data.pageNum;
            })
        },

        searchList:function (curPage) {
            axios.post('/order/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                app.orderList=response.data.list;
                app.pages=response.data.pages;
                app.pageNo=curPage;
            })
        }
    },

    created:function () {
        this.searchList(1);
    }
})