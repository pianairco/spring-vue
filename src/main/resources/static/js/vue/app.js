
Vue.component('one', {
    data: function () {
        return {
            user: {
                firstName: '',
                lastName: ''
            },
            message: 'Hello One'
        }
    },
    methods: {
        x: function () {
            axios.post('/action', this.user, {headers: {"action": "one", "activity": "x"}})
                .then((response) => { this.message = response.data; })
                .catch((err) => { this.message = err; });
        }
    },
    template: '<div><h1>{{ message }}</h1><input type="text" v-model="user.firstName" /><input type="text" v-model="user.lastName" /><button v-on:click="x()" >ok</button></div>'
});

Vue.component('two', {
    data: function () {
        return {
            message: 'Hello Two'
        }
    },
    template: '<h1>{{ message }}</h1>'
});