var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{goods:{},goodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},itemList:[]},
        ids:[],
        searchEntity:{},
        status:['未审核','已审核','审核未通过','已关闭'],
        itemCatList:[],
        item:{},
        seckillGoods:{},
        startTime:'',
        endTime:'',


        },

    methods: {
        //该方法只要不在生命周期的
        add:function () {

            axios.post('/seckillGoods/add.shtml?startTime='+app.startTime+'&endTime='+app.endTime,this.seckillGoods).then(function (response) {
                console.log(response);
                if(response.data.success){
                    document.location.href='goods.html';
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        findOne:function (id) {
            axios.get('/goods/findOneSKU.shtml?id='+id).then(function (response) {
                app.item=response.data;


               app.seckillGoods.itemId=app.item.id;
                app.seckillGoods.goodsId=app.item.goodsId;
                app.seckillGoods.price=app.item.price;
                app.seckillGoods.sellerId=app.item.sellerId;
                app.seckillGoods.num=app.item.num;
                app.seckillGoods.smallPic=app.item.image;
                app.seckillGoods.title=app.item.title;



            }).catch(function (error) {
                console.log("1231312131321");
            });
        },



    },
    //钩子函数 初始化了事件和
    created: function () {
        var request = this.getUrlParam();
        this.findOne(request.id)
    }

})
