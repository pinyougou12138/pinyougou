var app = new Vue({
    el: "#app",
    data: { 
        OrderList:[],
        pages:{},
        pageNo:{},
        searchList:{},
        startTime: "",
        endTime: "",
        example:5,
    },
    methods: {
        clickBtn1: function () {
            console.log(this.startTime);
        },
        clickBtn2: function () {
            console.log(this.endTime);
        },
        findMap:function () {
            axios.post("/orderManger/findOderByTime.shtml?startTime="+this.startTime+"&endTime="+this.endTime).then(function (response) {
                app.OrderList= response.data;
                console.log(response.data);
            })
        }
    },
    created:function () {

    }
});