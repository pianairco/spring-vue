<script>
    var app = new Vue({
        el: '#app',
        data: {
            currentRoute: window.location.hash
        },
        created: function () {},
        computed: {
            ViewComponent() {
                if (this.currentRoute === '' || this.currentRoute === '/') {
                    window.history.pushState(null,routes['/'],'/#/');
                    return routes['/'] || NotFound;
                } else if (this.currentRoute.startsWith('#'))
                    return routes[this.currentRoute.substring(1)] || NotFound;
                else
                    return routes[this.currentRoute] || NotFound;
            }
        },
        render(h) {
            return h(this.ViewComponent)
        }
    });
</script>
