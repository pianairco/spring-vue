<app name="vmenu"></app>

<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <a class="navbar-brand" href="#">Simple Shop</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <v-link class="nav-link" href="/login">login</v-link>
                </li>
                <li class="nav-item">
                    <v-link class="nav-link" href="/">Home</v-link>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Products
                    </a>
                    <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                        <v-link class="dropdown-item" href="/proucts/book">Book</v-link>
                        <div class="dropdown-divider"></div>
                        <v-link class="dropdown-item" href="/proucts/box">Box</v-link>
                    </div>
                </li>
            </ul>
        </div>
    </nav>
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
