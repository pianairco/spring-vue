<app name="box"></app>

<template>
<div>
    <vmenu></vmenu>
    <h1>{{ message }}</h1>
</div>
</template>

<script>
    var $app$ = Vue.component('$app$', {
        template: '$template$',
        data: function () {
            return {
                message: 'Hello To Box'
            }
        },
        methods: {
            x: function () {
                axios.post('/action', this.user, {headers: {"action": "$bean$", "activity": "x"}})
                    .then((response) => { this.message = response.data; })
            .catch((err) => { this.message = err; });
            }
        }
    })
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
        <%@ page import="ir.piana.dev.springvue.core.action.Action" %>
    </import>
    <action>
        <%
            class $VUE$ extends Action {
                @Autowired
                SQLExecuter sqlExecuter;

                public Function<RequestEntity, ResponseEntity> x = (r) -> {
                    List<Object> objects = sqlExecuter.executeQuery("select * from users");
                    Map body = (Map) r.getBody();
                    return ResponseEntity.ok("Hello Friend!");
                };
            }
        %>
    </action>
</bean>



