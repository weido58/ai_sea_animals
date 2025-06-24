import {createRouter, createWebHashHistory} from "vue-router";
import user_list from "../components/user_list.vue"


const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            component: user_list,
            path: "/"
        },
        {
            component: user_list,
            path: "/user_list"
        }
    ]
});
export default router;