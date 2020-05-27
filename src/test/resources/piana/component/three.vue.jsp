<app name="three"></app>
<template>
<div>
    <h1>{{ message }}</h1>
    <button v-on:click="x()" >ok</button>
</div>
</template>

<script for="component">
    Vue.component('$app$', {
        template: '$template$',
        data: function () {
            return {
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
    })
</script>

<script for="state">
    {
        formValue: {},
        setFormValue: function(formValue) {
            this.formValue = formValue;
        },
        setToForm: function(key, value) {
            this.state.formValue[key] = value;
        },
        getFormValue: function() {
            return this.state.formValue;
        }
    };
</script>

<bean>
    <import>
        <%@ page import="org.springframework.http.RequestEntity" %>
        <%@ page import="org.springframework.http.ResponseEntity" %>
        <%@ page import="java.util.function.Function" %>
        <%@ page import="java.util.Map" %>
        <%@ page import="ir.piana.dev.springvue.core.action.Action" %>
    </import>
    <action>
        <%
            class $VUE$ extends Action {
//                @Autowired
//                SQLExecutor sqlExecuter;

                public Function<RequestEntity, ResponseEntity> x = (r) -> {
//                    List<Object> objects = sqlExecuter.executeQuery("select * from users");
                    Map body = (Map) r.getBody();
                    return ResponseEntity.ok("Good By Friend!");
                };
            }
        %>
    </action>
</bean>



