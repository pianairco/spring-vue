<body>
<div>
    <form>
        <h1>{{ message }}</h1>
        <input type="text" v-model="user.firstName" />
        <input type="text" v-model="user.lastName" />
        <button v-on:click="$x$" >ok</button>
    </form>
</div>

<script>
    new Vue({
        data: {
            user: {
                firstName: '',
                lastName: ''
            },
            message: 'Hello To Spring Vue'
        },
        methods: {
            x_success: function (response) {
                this.message = response;
            },
            x_fail: function (error) {

            }
        }
    })
</script>
</body>

<imports>
    <%@ page import="org.springframework.http.RequestEntity" %>
    <%@ page import="org.springframework.http.ResponseEntity" %>
    <%@ page import="java.util.function.Function" %>
    <%@ page import="ir.piana.dev.springvue.core.sql.SQLExecuter" %>
    <%@ page import="java.util.Map" %>
</imports>

<autowireds>
    <%
        SQLExecuter sqlExecuter = null;
    %>
</autowireds>

<services>
    <%
        Function<RequestEntity, ResponseEntity> x = (r) -> {
            sqlExecuter.toString();
            Map body = (Map) r.getBody();
            String firstName = (String)body.get("firstName");
            String lastName = (String)body.get("lastName");
            return ResponseEntity.ok("Hello ".concat(firstName).concat(" ").concat(lastName));
        };
    %>
</services>
