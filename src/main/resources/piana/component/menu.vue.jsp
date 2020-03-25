<app name="vmenu"></app>

<template>
<div>
    <ul>
        <li v-for="(item, index) in menu">
            <v-link v-bind:href="item.route">{{item.title}}</v-link>
        </li>
    </ul>
</div>
</template>

<script>
    Vue.component('$app$', {
        template: '$template$',
        props: {
            menu: {
                type: Array,
                default: function () {
                    return [{
                        title: String,
                        route: String,
                    }]
                }
            }
        },
        methods: {
            x: function () {
                axios.post('/action', this.user, {headers: {"action": "$bean$", "activity": "x"}})
                    .then((response) => { this.message = response.data; })
                    .catch((err) => { this.message = err; });
            }
        }
    });
</script>
