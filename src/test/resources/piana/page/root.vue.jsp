<app name="root"></app>

<template>
    <div style="background-color: #457e58">
        <vmenu></vmenu>
        <h1>Root</h1>
        <router-link to="/book">book</router-link>
        <router-view></router-view>
    </div>
</template>

<script>
    var $app$ = Vue.component('$app$', {
        template: '$template$'
    });
</script>
