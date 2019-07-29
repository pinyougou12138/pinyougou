var app = new Vue({
    el: "#app-content",
    data: {
       totalUsers:''
    },
    methods:{

        countTotalUsers:function () {
            axios.post("/user/countTotalUsers.shtml").then(function (response) {
                if (response.data) {
                    app.totalUsers=response.data.message
                }
            })
        },

    },
    created:function () {
           this.countTotalUsers();
    }
});