import Faq from "@/views/customer/Faq.vue";
import Notice from "@/views/customer/Notice.vue";
import Voc from "@/views/customer/Voc.vue";

export default [
    {
        path: '/notice',
        name: 'notice',
        component: Notice
    },
    {
        path: '/faq',
        name: 'faq',
        component: Faq
    },
    {
        path: '/voc',
        name: 'voc',
        component: Voc,
        meta: { requiresAuth: true, title: '고객의소리' }
    }
]