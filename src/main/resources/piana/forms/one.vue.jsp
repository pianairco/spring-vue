<app name="one"></app>

<template>
<div>
    <h1>{{ message }}</h1>
    <input type="text" v-model="user.firstName" />
    <input type="text" v-model="user.lastName" />
    <button v-on:click="x()" >ok</button>
</div>
</template>

<script>
    Vue.component('$app$', {
        template: '$template$',
        data: function() {
            return {
                user: {
                    firstName: '',
                    lastName: ''
                },
                message: 'Hello To Spring Vue'
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

<bean>
    <import>
        <%@ page import="org.springframework.beans.factory.annotation.Autowired" %>
        <%@ page import="ir.piana.dev.springvue.core.sql.SQLExecuter" %>
        <%@ page import="org.springframework.http.RequestEntity" %>
        <%@ page import="org.springframework.http.ResponseEntity" %>
        <%@ page import="java.util.function.Function" %>
        <%@ page import="java.util.List" %>
        <%@ page import="java.util.Map" %>
        <%@ page import="ir.piana.dev.springvue.action.Action" %>
    </import>
    <action>
        <%
            class $VUE$ extends Action {
                @Autowired
                SQLExecuter sqlExecuter;

                public Function<RequestEntity, ResponseEntity> x = (r) -> {
                    List<Object> objects = sqlExecuter.executeQuery("select * from users");
                    Map body = (Map) r.getBody();
//                    String firstName = (String)body.get("firstName");
//                    String lastName = (String)body.get("lastName");
                    return ResponseEntity.ok("Hello Friend!");
//                    return ResponseEntity.ok("Hello ".concat(firstName).concat(" ").concat(lastName));
                };
            }
        %>
    </action>
</bean>