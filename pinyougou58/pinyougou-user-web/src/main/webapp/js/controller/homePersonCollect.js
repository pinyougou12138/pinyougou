var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        smsCode:'',
        ids:[],
        searchEntity:{},
        cartList:[],
        loginName:""
    },
    methods: {
        /**
         * 查看购物车
         */
        findCartList: function () {
            axios.get('/user/findCartList.shtml').then(function(response){
                app.cartList=response.data;
            })
        },

        //获取用户名
        getName:function(){
          axios.get('/login/name.shtml').then(function(response){
              app.loginName=response.data;
          })
        }
    },
    //钩子函数 初始化了事件和
    created: function () {
        this.getName();
        this.findCartList();
    }

})
