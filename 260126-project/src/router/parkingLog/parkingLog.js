import Entry from '@/views/parkingLog/Entry.vue'
import Exit from '@/views/parkingLog/Exit.vue'

export default [
    {
        path: '/entry',      // 주소창에 localhost:5173/entry 라고 치면
        name: 'entry',
        component: Entry     // Entry.vue 화면을 보여줘라
    },
    {
        path: '/exit',       // 주소창에 localhost:5173/exit 라고 치면
        name: 'exit',
        component: Exit      // Exit.vue 화면을 보여줘라
    }
]