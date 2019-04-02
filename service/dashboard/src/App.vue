<template>
  <div id="app">
    <router-view />
  </div>
</template>
<script>
export default {
  name: 'App',
};
</script>

<style>
#app {
  font-family: "Roboto", sans-serif;
  height: 100vh;
}

/*--- CSS RESET ---*/

html,body,div,span,
h1,h2,h3,h4,h5,h6,
p,pre,a,img,
small,strong,
u,i,ul,
li,label,
legend,table,
caption,tbody,
tfoot,thead,
tr,th,td,
aside,
canvas,
 {
  margin: 0;
  padding: 0;
  border: 0;
  vertical-align: baseline;
  font-family: "Roboto", sans-serif;
}
/* HTML5 display-role reset for older browsers */
figure,footer,header,
hgroup,menu,nav,section {
  display: block;
}
html,
body {
  height: 100%;
}
body {
  line-height: 1;
}
ol,
ul {
  list-style: none;
}
blockquote,
q {
  quotes: none;
}
blockquote:before,
blockquote:after,
q:before,
q:after {
  content: "";
  content: none;
}
table {
  border-collapse: collapse;
  border-spacing: 0;
}
h1 {
  font-size: 2.5rem;
}
h2 {
  font-size: 1.5rem;
}
</style>
